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
 * HTTP パラメータを表す共通抽象（KMP移行 Phase 4-3a）。
 *
 * マルチパート添付用の `file` パラメータが `java.io.File` に依存するため、クラス全体を expect/actual class
 * として分割した。ここには「commonMain / 共通コードが必要とする最小メンバのみ」を宣言する。
 *
 * - JVM(Android): actual は従来の `HttpParameter.kt`。`java.io.File` / `java.io.InputStream` ベースの
 *   ファイルパラメータ（コンストラクタ・`getFile()` / `getFileBody()` / `getContentType()`）を追加メンバとして
 *   温存し、`HttpClientImpl` / `AlternativeHttpClientImpl`（マルチパートPOST）や twitter4j-v2 などの
 *   既存呼び出しを無修正で受け入れる。
 * - iOS(Native): actual は `java.io.File` を持たない最小実装。ファイル本体はパス文字列 + [ByteArray] で表現し、
 *   Phase 4-3b の NSURLSession マルチパート実装の基底とする。
 *
 * 静的なパーセントエンコード等のロジックは [HttpParameterEncoding] に集約し、各 actual companion から委譲する。
 */
expect class HttpParameter : Comparable<HttpParameter>, Serializable {

    val name: String?
    val value: String?
    val jsonObject: JSONObject?

    constructor(name: String?, value: String?)

    constructor(jsonObject: JSONObject?)

    constructor(name: String?, value: Int)

    constructor(name: String?, value: Long)

    constructor(name: String?, value: Double)

    constructor(name: String?, value: Boolean)

    fun isFile(): Boolean

    fun isJson(): Boolean

    fun hasFileBody(): Boolean

    override fun compareTo(o: HttpParameter): Int

    companion object {
        fun containsJson(params: Array<HttpParameter>): Boolean

        fun containsFile(params: Array<HttpParameter>?): Boolean

        fun containsFile(params: List<HttpParameter>): Boolean

        fun getParameterArray(name: String, value: String): Array<HttpParameter>

        fun getParameterArray(name: String, value: Int): Array<HttpParameter>

        fun getParameterArray(
            name1: String, value1: String,
            name2: String, value2: String
        ): Array<HttpParameter>

        fun getParameterArray(
            name1: String, value1: Int,
            name2: String, value2: Int
        ): Array<HttpParameter>

        fun encodeParameters(httpParams: Array<HttpParameter>?): String

        fun encode(value: String?): String

        fun decode(value: String): String?

        fun decodeParameters(queryParameters: String): List<HttpParameter>
    }
}
