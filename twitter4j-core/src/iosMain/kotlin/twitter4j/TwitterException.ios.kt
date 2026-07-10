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

/**
 * [TwitterException] の iOS(Native) actual 実装（KMP移行 Phase 4-1）。
 *
 * JVM 版と異なり以下を簡略化している:
 * - `ExceptionDiagnosis`（JVMスタックトレースのハッシュ化）相当は持たない。
 * - `rateLimitStatus` はレスポンスヘッダ解析（JVMの JSONImplFactory 依存）を行わず null を返す。
 *
 * 共通コード（JSONImpl 群 / ParseUtil）が参照する最小メンバのみを実装する。
 */
actual open class TwitterException : Exception {

    // expect 側は read-only の `val` として宣言しているため、内部可変は private backing field で保持する。
    private var _statusCode = -1
    actual val statusCode: Int
        get() = _statusCode
    private var _errorCode = -1
    actual val errorCode: Int
        get() = _errorCode
    private var _errorMessage: String? = null
    actual val errorMessage: String?
        get() = _errorMessage

    private var response: HttpResponse? = null

    actual constructor(message: String?, cause: Throwable?) : super(message, cause) {
        decode(message)
    }

    actual constructor(message: String?) : this(message, null as Throwable?)

    actual constructor(cause: Exception) : this(cause.message, cause)

    actual constructor(message: String?, res: HttpResponse) : this(message) {
        response = res
        this._statusCode = res.getStatusCode()
    }

    actual constructor(message: String?, cause: Exception?, statusCode: Int) : this(message, cause as Throwable?) {
        this._statusCode = statusCode
    }

    actual constructor(message: String?, errorCode: Int) : this(message) {
        this._errorMessage = message
        this._errorCode = errorCode
    }

    // JVM版と異なり ExceptionDiagnosis / getCause() による装飾は行わず、最小限の合成に留める。
    actual override val message: String
        get() {
            val em = _errorMessage
            return if (em != null && _errorCode != -1) {
                "message - $em\ncode - $_errorCode"
            } else {
                super.message ?: ""
            }
        }

    private fun decode(str: String?) {
        if (str != null && str.startsWith("{")) {
            try {
                val json = JSONObject(str)
                if (!json.isNull("errors")) {
                    val error = json.getJSONArray("errors").getJSONObject(0)
                    this._errorMessage = error.getString("message")
                    this._errorCode = error.optInt("code", -1)
                }
            } catch (ignore: JSONException) {
            }
        }
    }

    actual fun getResponseHeader(name: String): String? {
        val res = response ?: return null
        val header = res.getResponseHeaderFields()?.get(name)
        return if (header != null && header.isNotEmpty()) header[0] else null
    }

    actual fun resourceNotFound(): Boolean {
        return statusCode == HttpResponseCode.NOT_FOUND
    }

    // iOS ではレスポンスヘッダからのレート制限解析（JVMの JSONImplFactory 依存）を行わないため null を返す。
    actual val rateLimitStatus: RateLimitStatus?
        get() = null

    actual val accessLevel: Int
        get() = TwitterResponse.NONE
}
