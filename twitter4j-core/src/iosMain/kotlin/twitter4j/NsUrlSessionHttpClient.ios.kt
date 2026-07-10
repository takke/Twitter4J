/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twitter4j

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.Foundation.NSURLResponse
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionDataTask
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.setHTTPBody
import platform.Foundation.setHTTPMethod
import platform.Foundation.setValue
import platform.darwin.DISPATCH_TIME_FOREVER
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_signal
import platform.darwin.dispatch_semaphore_wait
import platform.posix.memcpy
import platform.posix.sleep
import twitter4j.auth.Authorization

/**
 * NSURLSession ベースの同期 [HttpClient] 実装（KMP移行 Phase 4-3b / iOS(Native)）。
 *
 * JVM の `HttpClientBase` + `HttpClientImpl` 相当を、iOS の NSURLSession を用いて再実装したもの。
 * `HttpClientBase` は `java.io` に依存するため継承せず、[HttpClient] IF を直実装している。
 *
 * ## 同期ブリッジ
 * NSURLSession の `dataTaskWithRequest:completionHandler:` は非同期 API である。twitter4j は
 * 「リクエスト → レスポンス」を同期メソッドとして提供する設計のため、`dispatch_semaphore` で
 * completion を待ち合わせて同期化している。completion ハンドラは NSURLSession の（呼び出しスレッドとは
 * 別の）バックグラウンドキュー上で実行されるため、呼び出しスレッド側で semaphore を待っても
 * デッドロックしない（本ライブラリはバックグラウンドスレッドから呼ばれる前提）。
 *
 * Kotlin/Native の新メモリモデル（既定）ではクロージャがキャプチャした `var` をスレッドをまたいで
 * 変更・参照できるため、`freeze()` 等の特別な操作は不要。
 *
 * ## gzip
 * NSURLSession は「アプリが `Accept-Encoding` ヘッダを明示指定しない場合に限り」自動的に
 * `Accept-Encoding: gzip` を付与し、レスポンスを透過的に解凍する。逆に `Accept-Encoding` を手動指定すると
 * 解凍はアプリ責務となる。そのため本実装では（`HttpClientBase` と異なり）`Accept-Encoding: gzip` を
 * 既定ヘッダに載せず、NSURLSession の透過解凍に委ねる。結果、追加の解凍処理は不要。
 */
class NsUrlSessionHttpClient(
    private val conf: HttpClientConfiguration
) : HttpClient {

    private val requestHeaders: MutableMap<String, String> = LinkedHashMap()

    // 一意なマルチパート境界を作るための単調増加カウンタ
    private var multipartSeq: Long = 0

    init {
        // HttpClientBase の既定ヘッダを複製する。
        // ただし Accept-Encoding: gzip は NSURLSession の透過解凍を活かすため意図的に付与しない（上記 gzip 参照）。
        requestHeaders["X-Twitter-Client-Version"] = VERSION
        requestHeaders["X-Twitter-Client-URL"] =
            "http://twitter4j.org/en/twitter4j-" + VERSION + ".xml"
        requestHeaders["X-Twitter-Client"] = "Twitter4J"
        requestHeaders["User-Agent"] = "twitter4j http://twitter4j.org/ /" + VERSION
    }

    override fun addDefaultRequestHeader(name: String, value: String) {
        requestHeaders[name] = value
    }

    override fun getRequestHeaders(): Map<String, String> = requestHeaders

    override fun request(req: HttpRequest): HttpResponse = handleRequest(req)

    override fun request(req: HttpRequest, listener: HttpResponseListener?): HttpResponse {
        return try {
            val res = handleRequest(req)
            listener?.httpResponseReceived(HttpResponseEvent(req, res, null))
            res
        } catch (te: TwitterException) {
            listener?.httpResponseReceived(HttpResponseEvent(req, null, te))
            throw te
        }
    }

    override fun get(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse =
        request(HttpRequest(RequestMethod.GET, url, parameters, authorization, requestHeaders), listener)

    override fun get(url: String): HttpResponse =
        request(HttpRequest(RequestMethod.GET, url, null, null, requestHeaders))

    override fun post(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse =
        request(HttpRequest(RequestMethod.POST, url, parameters, authorization, requestHeaders), listener)

    override fun post(url: String): HttpResponse =
        request(HttpRequest(RequestMethod.POST, url, null, null, requestHeaders))

    override fun delete(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse =
        request(HttpRequest(RequestMethod.DELETE, url, parameters, authorization, requestHeaders), listener)

    override fun delete(url: String): HttpResponse =
        request(HttpRequest(RequestMethod.DELETE, url, null, null, requestHeaders))

    override fun head(url: String): HttpResponse =
        request(HttpRequest(RequestMethod.HEAD, url, null, null, requestHeaders))

    override fun put(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse =
        request(HttpRequest(RequestMethod.PUT, url, parameters, authorization, requestHeaders), listener)

    override fun put(url: String): HttpResponse =
        request(HttpRequest(RequestMethod.PUT, url, null, null, requestHeaders))

    /**
     * JVM の `HttpClientImpl.handleRequest` 相当。リトライループと同期実行 + ステータス判定を行う。
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun handleRequest(req: HttpRequest): HttpResponse {
        val retry = conf.getHttpRetryCount() + 1
        var res: NsUrlSessionHttpResponse? = null
        var lastError: NSError? = null

        var retriedCount = 0
        while (retriedCount < retry) {
            lastError = null
            val (response, networkError) = execute(req)
            if (networkError != null) {
                // 接続タイムアウト / 読み込みタイムアウト / ネットワークエラー相当
                lastError = networkError
                if (retriedCount == conf.getHttpRetryCount()) {
                    throw TwitterException(networkError.localizedDescription, null as Exception?, -1)
                }
            } else {
                res = response
                val responseCode = response!!.getStatusCode()
                // JVM 実装（HttpClientImpl.handleRequest）と同一のステータス判定
                if (responseCode < HttpResponseCode.OK ||
                    (responseCode != HttpResponseCode.FOUND && HttpResponseCode.MULTIPLE_CHOICES <= responseCode)
                ) {
                    if (responseCode == HttpResponseCode.ENHANCE_YOUR_CLAIM ||
                        responseCode == HttpResponseCode.BAD_REQUEST ||
                        responseCode < HttpResponseCode.INTERNAL_SERVER_ERROR ||
                        retriedCount == conf.getHttpRetryCount()
                    ) {
                        throw TwitterException(response.asString(), response)
                    }
                    // INTERNAL_SERVER_ERROR(>=500) の場合のみリトライ
                } else {
                    break
                }
            }

            // 次のリトライまで待機（最終試行後は待たない）
            if (retriedCount < conf.getHttpRetryCount()) {
                val sec = conf.getHttpRetryIntervalSeconds()
                if (sec > 0) {
                    sleep(sec.toUInt())
                }
            }
            retriedCount++
        }

        // ここに到達するのは break 済み（成功）か、リトライ切れの場合。
        // リトライ切れ時に res が null（全て networkError）なら例外を投げる。
        return res ?: throw TwitterException(
            lastError?.localizedDescription ?: "request failed", null as Exception?, -1
        )
    }

    /**
     * 1 回分のリクエストを NSURLSession で同期実行する。
     *
     * @return (レスポンス, ネットワークエラー) のペア。ネットワークエラー時は first=null。
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun execute(req: HttpRequest): Pair<NsUrlSessionHttpResponse?, NSError?> {
        val nsUrl = NSURL(string = req.url)
            ?: throw TwitterException("Invalid URL: ${req.url}", null as Exception?, -1)

        val request = NSMutableURLRequest(uRL = nsUrl)
        request.setHTTPMethod(req.method.name)

        // タイムアウト（JVM は ms、NSURLSession は秒）
        val config = NSURLSessionConfiguration.defaultSessionConfiguration
        val readTimeoutSec = conf.getHttpReadTimeout() / 1000.0
        val connTimeoutSec = conf.getHttpConnectionTimeout() / 1000.0
        if (readTimeoutSec > 0.0) {
            config.timeoutIntervalForRequest = readTimeoutSec
        }
        if (readTimeoutSec > 0.0 || connTimeoutSec > 0.0) {
            // resource 全体のタイムアウトは read/connection の大きい方を採用
            config.timeoutIntervalForResource = maxOf(readTimeoutSec, connTimeoutSec)
        }

        // 認証ヘッダ + 追加ヘッダ（JVM の setHeaders 相当）
        val authorization = req.authorization
        if (authorization != null) {
            val authorizationHeader = authorization.getAuthorizationHeader(req)
            if (authorizationHeader != null) {
                request.setValue(authorizationHeader, forHTTPHeaderField = "Authorization")
                val additionalHeaders = authorization.getAdditionalHeaders()
                if (additionalHeaders != null) {
                    for ((key, value) in additionalHeaders) {
                        request.setValue(value, forHTTPHeaderField = key)
                    }
                }
            }
        }
        // リクエストヘッダ（既定ヘッダ含む）
        req.requestHeaders?.forEach { (key, value) ->
            request.setValue(value, forHTTPHeaderField = key)
        }

        // POST ボディ
        if (req.method == RequestMethod.POST) {
            val params = req.parameters
            if (HttpParameter.containsFile(params)) {
                multipartSeq++
                val boundary = buildMultipartBoundary(multipartSeq.toString())
                request.setValue(
                    "multipart/form-data; boundary=$boundary",
                    forHTTPHeaderField = "Content-Type"
                )
                val body = buildMultipartBody(boundary, toMultipartParts(params!!))
                request.setHTTPBody(body.toNSData())
            } else if (params != null && HttpParameter.containsJson(params)) {
                request.setValue("application/json", forHTTPHeaderField = "Content-Type")
                val postParam = params[0].jsonObject.toString()
                request.setHTTPBody(postParam.encodeToByteArray().toNSData())
            } else {
                request.setValue(
                    "application/x-www-form-urlencoded",
                    forHTTPHeaderField = "Content-Type"
                )
                val postParam = HttpParameter.encodeParameters(params)
                request.setHTTPBody(postParam.encodeToByteArray().toNSData())
            }
        }

        // 同期実行（semaphore で completion を待ち合わせる）
        val session = NSURLSession.sessionWithConfiguration(config)
        val semaphore = dispatch_semaphore_create(0)
        var resultData: NSData? = null
        var resultResponse: NSURLResponse? = null
        var resultError: NSError? = null

        val task: NSURLSessionDataTask = session.dataTaskWithRequest(request) { data, response, error ->
            resultData = data
            resultResponse = response
            resultError = error
            dispatch_semaphore_signal(semaphore)
        }
        task.resume()
        dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER)

        if (resultError != null) {
            return null to resultError
        }

        val httpResponse = resultResponse as? NSHTTPURLResponse
        val statusCode = httpResponse?.statusCode?.toInt() ?: -1
        val headers = httpResponse?.let { extractHeaders(it) } ?: emptyMap()
        val bodyString = resultData?.toByteArray()?.decodeToString() ?: ""
        return NsUrlSessionHttpResponse(statusCode, headers, bodyString) to null
    }

    /**
     * [HttpParameter] 列を、プラットフォーム非依存の [MultipartPart] 列へ還元する。
     */
    private fun toMultipartParts(params: Array<HttpParameter>): List<MultipartPart> {
        val parts = ArrayList<MultipartPart>(params.size)
        for (param in params) {
            if (param.isFile()) {
                parts.add(
                    MultipartPart.FileData(
                        name = param.name ?: "",
                        fileName = param.fileName ?: "",
                        contentType = param.getContentType(),
                        body = param.fileBody ?: ByteArray(0)
                    )
                )
            } else {
                parts.add(MultipartPart.Text(param.name ?: "", param.value ?: ""))
            }
        }
        return parts
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun extractHeaders(response: NSHTTPURLResponse): Map<String, String> {
        val result = LinkedHashMap<String, String>()
        for ((key, value) in response.allHeaderFields) {
            val k = key as? String ?: continue
            result[k] = value?.toString() ?: ""
        }
        return result
    }

    companion object {
        // Phase 4-4 で Version を commonMain へ移動したため、common の Version.version を直接参照する。
        private val VERSION = Version.version
    }
}

/**
 * NSURLSession の応答（ステータスコード / ヘッダ / ボディ）を保持する [HttpResponse] 具象実装。
 */
class NsUrlSessionHttpResponse(
    statusCode: Int,
    private val headers: Map<String, String>,
    body: String
) : HttpResponse() {

    init {
        this.statusCode = statusCode
        this.responseAsString = body
    }

    /**
     * HTTP ヘッダは名前が大文字小文字非依存（RFC 7230）。NSHTTPURLResponse の allHeaderFields は
     * サーバ返却時の綴りを保持するため、ここで大文字小文字を無視して照合する。
     */
    override fun getResponseHeader(name: String): String? {
        headers[name]?.let { return it }
        for ((key, value) in headers) {
            if (key.equals(name, ignoreCase = true)) {
                return value
            }
        }
        return null
    }

    override fun getResponseHeaderFields(): MutableMap<String, MutableList<String>> {
        val result = LinkedHashMap<String, MutableList<String>>()
        for ((key, value) in headers) {
            result[key] = mutableListOf(value)
        }
        return result
    }
}

/**
 * [ByteArray] を [NSData] へ変換する。
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.convert())
    }
}

/**
 * [NSData] を [ByteArray] へ変換する。
 */
@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val result = ByteArray(size)
    result.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}
