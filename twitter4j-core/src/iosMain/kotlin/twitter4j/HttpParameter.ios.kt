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
 * [HttpParameter] の iOS(Native) actual 実装（KMP移行 Phase 4-3a）。
 *
 * `java.io.File` は Native に存在しないため、ファイル本体はファイル名 [fileName] と本体バイト列 [fileBody] で
 * 表現する。これらは Phase 4-3b の NSURLSession マルチパート実装が参照する想定の追加メンバである。
 * 静的ロジックは commonMain の [HttpParameterEncoding] へ委譲する。
 */
actual class HttpParameter private constructor(
    actual val name: String?,
    actual val value: String?,
    actual val jsonObject: JSONObject?,
    val fileName: String?,
    val fileBody: ByteArray?
) : Comparable<HttpParameter>, Serializable {

    actual constructor(name: String?, value: String?) : this(name, value, null, null, null)

    actual constructor(jsonObject: JSONObject?) : this(null, null, jsonObject, null, null)

    /**
     * iOS 固有: マルチパート添付用（ファイル名 + 本体バイト列）。JVM の `File` コンストラクタ相当。
     */
    constructor(name: String?, fileName: String, fileBody: ByteArray?) : this(name, null, null, fileName, fileBody)

    actual constructor(name: String?, value: Int) : this(name, value.toString(), null, null, null)

    actual constructor(name: String?, value: Long) : this(name, value.toString(), null, null, null)

    actual constructor(name: String?, value: Double) : this(name, value.toString(), null, null, null)

    actual constructor(name: String?, value: Boolean) : this(name, value.toString(), null, null, null)

    actual fun isFile(): Boolean {
        return fileName != null
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
        val name = fileName!!
        val index = name.lastIndexOf(".")
        if (-1 == index) {
            // no extension
            contentType = OCTET
        } else {
            val extensions = name.substring(name.lastIndexOf(".") + 1).lowercase()
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
        if (other == null || this::class != other::class) return false

        val that = other as HttpParameter

        if (if (name != null) name != that.name else that.name != null) return false
        if (if (value != null) value != that.value else that.value != null) return false
        if (if (jsonObject != null) jsonObject != that.jsonObject else that.jsonObject != null) return false
        if (if (fileName != null) fileName != that.fileName else that.fileName != null) return false
        return if (fileBody != null) fileBody.contentEquals(that.fileBody) else that.fileBody == null
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (jsonObject?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (fileBody?.contentHashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("HttpParameter{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", jsonObject=" + jsonObject +
                ", fileName=" + fileName +
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

        actual fun containsJson(params: Array<HttpParameter>): Boolean {
            return httpContainsJson(params)
        }

        actual fun containsFile(params: Array<HttpParameter>?): Boolean {
            return httpContainsFile(params)
        }

        actual fun containsFile(params: List<HttpParameter>): Boolean {
            return httpContainsFile(params)
        }

        actual fun getParameterArray(name: String, value: String): Array<HttpParameter> {
            return httpGetParameterArray(name, value)
        }

        actual fun getParameterArray(name: String, value: Int): Array<HttpParameter> {
            return httpGetParameterArray(name, value.toString())
        }

        actual fun getParameterArray(
            name1: String, value1: String,
            name2: String, value2: String
        ): Array<HttpParameter> {
            return httpGetParameterArray(name1, value1, name2, value2)
        }

        actual fun getParameterArray(
            name1: String, value1: Int,
            name2: String, value2: Int
        ): Array<HttpParameter> {
            return httpGetParameterArray(name1, value1.toString(), name2, value2.toString())
        }

        actual fun encodeParameters(httpParams: Array<HttpParameter>?): String {
            return httpEncodeParameters(httpParams)
        }

        actual fun encode(value: String?): String {
            return httpPercentEncode(value)
        }

        actual fun decode(value: String): String? {
            return httpDecodeValue(value)
        }

        actual fun decodeParameters(queryParameters: String): List<HttpParameter> {
            return httpDecodeParameters(queryParameters)
        }
    }
}
