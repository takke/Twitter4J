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
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.ArrayList

/**
 * A data class representing HTTP Post parameter
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class HttpParameter private constructor(
    val name: String?,
    val value: String?,
    val jsonObject: JSONObject?,
    val file: File?,
    val fileBody: InputStream?
) : Comparable<HttpParameter>, java.io.Serializable {

    constructor(name: String?, value: String?) : this(name, value, null, null, null)

    constructor(jsonObject: JSONObject?) : this(null, null, jsonObject, null, null)

    constructor(name: String?, file: File?) : this(name, null, null, file, null)

    constructor(name: String?, fileName: String, fileBody: InputStream?) : this(name, null, null, File(fileName), fileBody)

    constructor(name: String?, value: Int) : this(name, value.toString(), null, null, null)

    constructor(name: String?, value: Long) : this(name, value.toString(), null, null, null)

    constructor(name: String?, value: Double) : this(name, value.toString(), null, null, null)

    constructor(name: String?, value: Boolean) : this(name, value.toString(), null, null, null)

    fun isFile(): Boolean {
        return file != null
    }

    fun isJson(): Boolean {
        return jsonObject != null
    }

    fun hasFileBody(): Boolean {
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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as HttpParameter

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

    override fun compareTo(o: HttpParameter): Int {
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

    companion object {
        private const val serialVersionUID = 4046908449190454692L

        private const val JPEG = "image/jpeg"
        private const val GIF = "image/gif"
        private const val PNG = "image/png"
        private const val OCTET = "application/octet-stream"

        @JvmStatic
        fun containsJson(params: Array<HttpParameter>): Boolean {
            return params.size == 1 && params[0].isJson()
        }

        @JvmStatic
        fun containsFile(params: Array<HttpParameter>?): Boolean {
            var containsFile = false
            if (null == params) {
                return false
            }
            for (param in params) {
                if (param.isFile()) {
                    containsFile = true
                    break
                }
            }
            return containsFile
        }

        @JvmStatic
        fun containsFile(params: List<HttpParameter>): Boolean {
            var containsFile = false
            for (param in params) {
                if (param.isFile()) {
                    containsFile = true
                    break
                }
            }
            return containsFile
        }

        @JvmStatic
        fun getParameterArray(name: String, value: String): Array<HttpParameter> {
            return arrayOf(HttpParameter(name, value))
        }

        @JvmStatic
        fun getParameterArray(name: String, value: Int): Array<HttpParameter> {
            return getParameterArray(name, value.toString())
        }

        @JvmStatic
        fun getParameterArray(
            name1: String, value1: String,
            name2: String, value2: String
        ): Array<HttpParameter> {
            return arrayOf(
                HttpParameter(name1, value1),
                HttpParameter(name2, value2)
            )
        }

        @JvmStatic
        fun getParameterArray(
            name1: String, value1: Int,
            name2: String, value2: Int
        ): Array<HttpParameter> {
            return getParameterArray(name1, value1.toString(), name2, value2.toString())
        }

        @JvmStatic
        fun encodeParameters(httpParams: Array<HttpParameter>?): String {
            if (null == httpParams) {
                return ""
            }
            val buf = StringBuilder()
            for (j in httpParams.indices) {
                if (httpParams[j].isFile()) {
                    throw IllegalArgumentException("parameter [" + httpParams[j].name + "]should be text")
                }
                if (j != 0) {
                    buf.append("&")
                }
                buf.append(encode(httpParams[j].name))
                    .append("=").append(encode(httpParams[j].value))
            }
            return buf.toString()
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
        fun encode(value: String?): String {
            var encoded: String? = null
            try {
                encoded = URLEncoder.encode(value, "UTF-8")
            } catch (ignore: UnsupportedEncodingException) {
            }
            val enc = encoded!!
            val buf = StringBuilder(enc.length)
            var focus: Char
            var i = 0
            while (i < enc.length) {
                focus = enc[i]
                if (focus == '*') {
                    buf.append("%2A")
                } else if (focus == '+') {
                    buf.append("%20")
                } else if (focus == '%' && (i + 1) < enc.length &&
                    enc[i + 1] == '7' && enc[i + 2] == 'E'
                ) {
                    buf.append('~')
                    i += 2
                } else {
                    buf.append(focus)
                }
                i++
            }
            return buf.toString()
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
        fun decode(value: String): String? {
            var v = value
            v = v.replace("%2A", "*")
            v = v.replace("%2a", "*")
            v = v.replace("%20", " ")

            var decoded: String? = null
            try {
                decoded = URLDecoder.decode(v, "UTF-8")
            } catch (ignore: UnsupportedEncodingException) {
            }

            return decoded
        }

        /**
         * Parses a query string without the leading "?"
         *
         * @param queryParameters a query parameter string, like a=hello&amp;b=world
         * @return decoded parameters
         */
        @JvmStatic
        fun decodeParameters(queryParameters: String): List<HttpParameter> {
            val result: MutableList<HttpParameter> = ArrayList()
            for (pair in queryParameters.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val parts = pair.split("=".toRegex(), limit = 2).toTypedArray()
                if (parts.size == 2) {
                    val name = decode(parts[0])
                    val value = decode(parts[1])
                    if (name != "" && value != "") result.add(HttpParameter(name, value))
                }
            }
            return result
        }
    }
}
