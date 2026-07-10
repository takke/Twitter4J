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
 * [HttpParameter] のパーセントエンコード / パラメータ組み立てロジック（KMP移行 Phase 4-3a）。
 *
 * [HttpParameter] は `java.io.File` 依存のため expect/actual class として各プラットフォームに分割したが、
 * その companion の静的ロジック（encode / decode / encodeParameters / decodeParameters / containsFile 等）は
 * プラットフォーム非依存の純ロジックである。二重実装を避けるため、実体を本ファイルの internal 関数へ集約し、
 * 各 actual companion はここへ委譲する。
 *
 * `encode`（パーセントエンコード）は元の `java.net.URLEncoder.encode(value, "UTF-8")` +
 * 事後変換（`*`→`%2A` / `+`→`%20` / `%7E`→`~`）と1バイトも変わらない挙動になるよう、
 * RFC 3986 の unreserved 集合（`A-Za-z0-9-_.~`）以外を UTF-8 バイト列として大文字16進で
 * パーセントエンコードする純 Kotlin 実装に置き換えた（OAuth 署名の同値は OAuthSignatureTest で担保）。
 */

private const val HTTP_ENCODE_HEX = "0123456789ABCDEF"

/**
 * `URLEncoder.encode(value, "UTF-8")` + OAuth 用事後変換 相当のパーセントエンコード。
 */
internal fun httpPercentEncode(value: String?): String {
    if (value == null) {
        // 元実装は null で NPE となるが、実運用では null は渡らない。挙動発散を避けつつ安全側に倒し "" を返す。
        return ""
    }
    val buf = StringBuilder(value.length)
    val bytes = value.encodeToByteArray() // UTF-8
    for (b in bytes) {
        val c = b.toInt() and 0xFF
        val ch = c.toChar()
        if (ch in 'A'..'Z' || ch in 'a'..'z' || ch in '0'..'9' ||
            ch == '-' || ch == '_' || ch == '.' || ch == '~'
        ) {
            buf.append(ch)
        } else {
            buf.append('%')
            buf.append(HTTP_ENCODE_HEX[(c shr 4) and 0x0F])
            buf.append(HTTP_ENCODE_HEX[c and 0x0F])
        }
    }
    return buf.toString()
}

/**
 * [httpPercentEncode] の対になるデコード（`HttpParameter.decode` 相当）。
 */
internal fun httpDecodeValue(value: String): String? {
    var v = value
    v = v.replace("%2A", "*")
    v = v.replace("%2a", "*")
    v = v.replace("%20", " ")
    return ParseUtil.urlDecode(v)
}

internal fun httpEncodeParameters(httpParams: Array<HttpParameter>?): String {
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
        buf.append(httpPercentEncode(httpParams[j].name))
            .append("=").append(httpPercentEncode(httpParams[j].value))
    }
    return buf.toString()
}

internal fun httpDecodeParameters(queryParameters: String): List<HttpParameter> {
    val result: MutableList<HttpParameter> = ArrayList()
    for (pair in queryParameters.split("&").dropLastWhile { it.isEmpty() }) {
        val parts = pair.split("=", limit = 2)
        if (parts.size == 2) {
            val name = httpDecodeValue(parts[0])
            val value = httpDecodeValue(parts[1])
            if (name != "" && value != "") result.add(HttpParameter(name, value))
        }
    }
    return result
}

internal fun httpContainsJson(params: Array<HttpParameter>): Boolean {
    return params.size == 1 && params[0].isJson()
}

internal fun httpContainsFile(params: Array<HttpParameter>?): Boolean {
    if (null == params) {
        return false
    }
    for (param in params) {
        if (param.isFile()) {
            return true
        }
    }
    return false
}

internal fun httpContainsFile(params: List<HttpParameter>): Boolean {
    for (param in params) {
        if (param.isFile()) {
            return true
        }
    }
    return false
}

internal fun httpGetParameterArray(name: String, value: String): Array<HttpParameter> {
    return arrayOf(HttpParameter(name, value))
}

internal fun httpGetParameterArray(
    name1: String, value1: String,
    name2: String, value2: String
): Array<HttpParameter> {
    return arrayOf(
        HttpParameter(name1, value1),
        HttpParameter(name2, value2)
    )
}
