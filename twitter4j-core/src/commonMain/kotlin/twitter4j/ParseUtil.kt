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

@file:OptIn(ExperimentalTime::class)

package twitter4j

import kotlin.jvm.JvmStatic
import kotlin.time.ExperimentalTime

/**
 * A tiny parse utility class.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
object ParseUtil {

    @JvmStatic
    fun getUnescapedString(str: String, json: JSONObject?): String? {
        return HTMLEntity.unescape(getRawString(str, json))
    }

    @JvmStatic
    fun getRawString(name: String, json: JSONObject?): String? {
        return try {
            // 元実装(Java)は json が platform 型で、null が渡されると json.isNull で NPE を送出し
            // 下部の catch(Exception) で null を返していた。挙動維持のため null を明示的に許容する。
            if (json == null || json.isNull(name)) {
                null
            } else {
                json.getString(name)
            }
        } catch (jsone: JSONException) {
            null
        } catch (ex: Exception) {
            null
        }
    }

    @JvmStatic
    fun getURLDecodedString(name: String, json: JSONObject?): String? {
        var returnValue = getRawString(name, json)
        if (returnValue != null) {
            returnValue = urlDecode(returnValue)
        }
        return returnValue
    }

    /**
     * `java.net.URLDecoder.decode(value, "UTF-8")` 相当の純 Kotlin 実装（commonMain 用）。
     *
     * - `'+'` はスペースに変換する
     * - `%XX`（16進2桁）はバイト値としてデコードし、連続する `%XX` をまとめて UTF-8 バイト列として復元する
     * - それ以外の文字はそのまま出力する
     *
     * 元実装は `UnsupportedEncodingException`（UTF-8 では発生しない）のみ catch していたため、
     * 挙動を維持するために正常系のみを扱う（Twitter API から渡される値は整形済みの前提）。
     */
    internal fun urlDecode(value: String): String {
        // '%' も '+' も含まれない場合はそのまま返す（高速化 & 挙動維持）
        if (value.indexOf('%') < 0 && value.indexOf('+') < 0) {
            return value
        }
        val sb = StringBuilder(value.length)
        val bytes = ArrayList<Byte>()
        var i = 0
        val length = value.length
        while (i < length) {
            val c = value[i]
            when (c) {
                '+' -> {
                    // 蓄積済みバイト列があれば UTF-8 として確定させる
                    if (bytes.isNotEmpty()) {
                        sb.append(bytes.toByteArray().decodeToString())
                        bytes.clear()
                    }
                    sb.append(' ')
                    i++
                }

                '%' -> {
                    // `%XX` を 1 バイトとして蓄積（連続分をまとめて後で UTF-8 デコード）
                    val hi = hexValue(value[i + 1])
                    val lo = hexValue(value[i + 2])
                    bytes.add(((hi shl 4) or lo).toByte())
                    i += 3
                }

                else -> {
                    if (bytes.isNotEmpty()) {
                        sb.append(bytes.toByteArray().decodeToString())
                        bytes.clear()
                    }
                    sb.append(c)
                    i++
                }
            }
        }
        if (bytes.isNotEmpty()) {
            sb.append(bytes.toByteArray().decodeToString())
        }
        return sb.toString()
    }

    /**
     * 16進文字（0-9, a-f, A-F）を数値に変換する。
     */
    private fun hexValue(c: Char): Int = when (c) {
        in '0'..'9' -> c - '0'
        in 'a'..'f' -> c - 'a' + 10
        in 'A'..'F' -> c - 'A' + 10
        else -> throw IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - $c")
    }

    @JvmStatic
    @Throws(TwitterException::class)
    fun parseTrendsDate(asOfStr: String): Date {
        return when (asOfStr.length) {
            10 -> Date(asOfStr.toLong() * 1000)
            20 -> getDate(asOfStr, "yyyy-MM-dd'T'HH:mm:ss'Z'")
            else -> getDate(asOfStr, "EEE, d MMM yyyy HH:mm:ss z")
        }
    }

    @JvmStatic
    @Throws(TwitterException::class)
    fun getDate(name: String, json: JSONObject?): Date? {
        return getDate(name, json, "EEE MMM d HH:mm:ss z yyyy")
    }

    @JvmStatic
    @Throws(TwitterException::class)
    fun getDate(name: String, json: JSONObject?, format: String): Date? {
        val dateStr = getUnescapedString(name, json)
        return if ("null" == dateStr || null == dateStr) {
            null
        } else {
            getDate(dateStr, format)
        }
    }

    @JvmStatic
    @Throws(TwitterException::class)
    fun getDate(dateString: String, format: String): Date {
        return try {
            val instant = TwitterDateParser.parse(dateString, format)
            Date(instant.toEpochMilliseconds())
        } catch (e: TwitterException) {
            throw e
        } catch (e: Exception) {
            throw TwitterException("Unexpected date format($dateString) returned from twitter.com", e)
        }
    }

    @JvmStatic
    fun getInt(name: String, json: JSONObject?): Int {
        return getInt(getRawString(name, json))
    }

    @JvmStatic
    fun getInt(str: String?): Int {
        return if (null == str || "" == str || "null" == str) {
            -1
        } else {
            try {
                str.toInt()
            } catch (nfe: NumberFormatException) {
                // workaround for the API side issue http://issue.twitter4j.org/youtrack/issue/TFJ-484
                -1
            }
        }
    }

    @JvmStatic
    fun getLong(name: String, json: JSONObject?): Long {
        return getLong(getRawString(name, json))
    }

    @JvmStatic
    fun getLong(str: String?): Long {
        return if (null == str || "" == str || "null" == str) {
            -1
        } else {
            // some count over 100 will be expressed as "100+"
            if (str.endsWith("+")) {
                str.substring(0, str.length - 1).toLong() + 1
            } else {
                str.toLong()
            }
        }
    }

    @JvmStatic
    fun getDouble(name: String, json: JSONObject?): Double {
        val str2 = getRawString(name, json)
        return if (null == str2 || "" == str2 || "null" == str2) {
            -1.0
        } else {
            str2.toDouble()
        }
    }

    @JvmStatic
    fun getBoolean(name: String, json: JSONObject?): Boolean {
        val str = getRawString(name, json)
        if (null == str || "null" == str) {
            return false
        }
        return str.toBoolean()
    }

    @JvmStatic
    fun toAccessLevel(res: HttpResponse?): Int {
        if (null == res) {
            return -1
        }
        val xAccessLevel = res.getResponseHeader("X-Access-Level")
        val accessLevel: Int
        if (null == xAccessLevel) {
            accessLevel = TwitterResponse.NONE
        } else {
            // https://dev.twitter.com/pages/application-permission-model-faq#how-do-we-know-what-the-access-level-of-a-user-token-is
            accessLevel = when (xAccessLevel.length) {
                // "read" (Read-only)
                4 -> TwitterResponse.READ
                10 -> // "read-write" (Read & Write)
                    TwitterResponse.READ_WRITE
                25 -> // "read-write-directmessages" (Read, Write, & Direct Message)
                    TwitterResponse.READ_WRITE_DIRECTMESSAGES
                26 -> // "read-write-privatemessages" (Read, Write, & Direct Message)
                    TwitterResponse.READ_WRITE_DIRECTMESSAGES
                else ->
                    // unknown access level;
                    TwitterResponse.NONE
            }
        }
        return accessLevel
    }
}
