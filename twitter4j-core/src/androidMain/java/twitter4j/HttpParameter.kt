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

import java.io.File
import java.io.InputStream

/**
 * A data class representing HTTP Post parameter
 *
 * KMP移行 Phase 4-3a: commonMain の expect class [HttpParameter] に対する JVM(Android) actual。
 * `java.io.File` / `java.io.InputStream` ベースのファイルパラメータ（[file] / [fileBody] /
 * [getContentType]）は本 actual 固有の追加メンバとして温存し、`HttpClientImpl` /
 * `AlternativeHttpClientImpl`（マルチパートPOST）や twitter4j-v2 の既存呼び出しを無修正で受け入れる。
 * 静的ロジックは commonMain の [HttpParameterEncoding] へ委譲する。
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
actual class HttpParameter private constructor(
    actual val name: String?,
    actual val value: String?,
    actual val jsonObject: JSONObject?,
    val file: File?,
    val fileBody: InputStream?
) : Comparable<HttpParameter>, Serializable {

    actual constructor(name: String?, value: String?) : this(name, value, null, null, null)

    actual constructor(jsonObject: JSONObject?) : this(null, null, jsonObject, null, null)

    constructor(name: String?, file: File?) : this(name, null, null, file, null)

    constructor(name: String?, fileName: String, fileBody: InputStream?) : this(name, null, null, File(fileName), fileBody)

    actual constructor(name: String?, value: Int) : this(name, value.toString(), null, null, null)

    actual constructor(name: String?, value: Long) : this(name, value.toString(), null, null, null)

    actual constructor(name: String?, value: Double) : this(name, value.toString(), null, null, null)

    actual constructor(name: String?, value: Boolean) : this(name, value.toString(), null, null, null)

    actual fun isFile(): Boolean {
        return file != null
    }

    actual fun isJson(): Boolean {
        return jsonObject != null
    }

    actual fun hasFileBody(): Boolean {
        return fileBody != null
    }

    /**
     * @return content-type
     */
    fun getContentType(): String {
        if (!isFile()) {
            throw IllegalStateException("not a file")
        }
        val contentType: String
        var extensions = file!!.name
        val index = extensions.lastIndexOf(".")
        if (-1 == index) {
            // no extension
            contentType = OCTET
        } else {
            extensions = extensions.substring(extensions.lastIndexOf(".") + 1).lowercase()
            contentType = if (extensions.length == 3) {
                when (extensions) {
                    "gif" -> GIF
                    "png" -> PNG
                    "jpg" -> JPEG
                    else -> OCTET
                }
            } else if (extensions.length == 4) {
                if ("jpeg" == extensions) {
                    JPEG
                } else {
                    OCTET
                }
            } else {
                OCTET
            }
        }
        return contentType
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as HttpParameter

        if (if (name != null) name != that.name else that.name != null) return false
        if (if (value != null) value != that.value else that.value != null) return false
        if (if (jsonObject != null) jsonObject != that.jsonObject else that.jsonObject != null) return false
        if (if (file != null) file != that.file else that.file != null) return false
        return if (fileBody != null) fileBody == that.fileBody else that.fileBody == null
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (jsonObject?.hashCode() ?: 0)
        result = 31 * result + (file?.hashCode() ?: 0)
        result = 31 * result + (fileBody?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("HttpParameter{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", jsonObject=" + jsonObject +
                ", file=" + file +
                ", fileBody=" + fileBody +
                '}')
    }

    actual override fun compareTo(o: HttpParameter): Int {
        var compared = 0
        if (name != null) {
            compared = name.compareTo(o.name!!)
        }
        if (0 == compared) {
            if (value != null) {
                compared = value.compareTo(o.value!!)
            }
        }
        return compared
    }

    actual companion object {
        private const val serialVersionUID = 4046908449190454692L

        private const val JPEG = "image/jpeg"
        private const val GIF = "image/gif"
        private const val PNG = "image/png"
        private const val OCTET = "application/octet-stream"

        @JvmStatic
        actual fun containsJson(params: Array<HttpParameter>): Boolean {
            return httpContainsJson(params)
        }

        @JvmStatic
        actual fun containsFile(params: Array<HttpParameter>?): Boolean {
            return httpContainsFile(params)
        }

        @JvmStatic
        actual fun containsFile(params: List<HttpParameter>): Boolean {
            return httpContainsFile(params)
        }

        @JvmStatic
        actual fun getParameterArray(name: String, value: String): Array<HttpParameter> {
            return httpGetParameterArray(name, value)
        }

        @JvmStatic
        actual fun getParameterArray(name: String, value: Int): Array<HttpParameter> {
            return httpGetParameterArray(name, value.toString())
        }

        @JvmStatic
        actual fun getParameterArray(
            name1: String, value1: String,
            name2: String, value2: String
        ): Array<HttpParameter> {
            return httpGetParameterArray(name1, value1, name2, value2)
        }

        @JvmStatic
        actual fun getParameterArray(
            name1: String, value1: Int,
            name2: String, value2: Int
        ): Array<HttpParameter> {
            return httpGetParameterArray(name1, value1.toString(), name2, value2.toString())
        }

        @JvmStatic
        actual fun encodeParameters(httpParams: Array<HttpParameter>?): String {
            return httpEncodeParameters(httpParams)
        }

        /**
         * @param value string to be encoded
         * @return encoded string
         * @see [OAuth / TestCases](http://wiki.oauth.net/TestCases)
         *
         * @see [Space encoding - OAuth | Google Groups](http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding.9d79b698ab217df2)
         *
         * @see [RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax - 2.1. Percent-Encoding](http://tools.ietf.org/html/rfc3986.section-2.1)
         */
        @JvmStatic
        actual fun encode(value: String?): String {
            return httpPercentEncode(value)
        }

        /**
         * @param value string to be decoded. The natural opposite of encode() above.
         * @return encoded string
         * @see [OAuth / TestCases](http://wiki.oauth.net/TestCases)
         *
         * @see [Space encoding - OAuth | Google Groups](http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding.9d79b698ab217df2)
         *
         * @see [RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax - 2.1. Percent-Encoding](http://tools.ietf.org/html/rfc3986.section-2.1)
         */
        @JvmStatic
        actual fun decode(value: String): String? {
            return httpDecodeValue(value)
        }

        /**
         * Parses a query string without the leading "?"
         *
         * @param queryParameters a query parameter string, like a=hello&amp;b=world
         * @return decoded parameters
         */
        @JvmStatic
        actual fun decodeParameters(queryParameters: String): List<HttpParameter> {
            return httpDecodeParameters(queryParameters)
        }
    }
}
