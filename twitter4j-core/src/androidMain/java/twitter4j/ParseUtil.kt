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

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.LinkedBlockingQueue

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
            try {
                returnValue = URLDecoder.decode(returnValue, "UTF-8")
            } catch (ignore: UnsupportedEncodingException) {
            }
        }
        return returnValue
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

    private val formatMapQueue: MutableMap<String, LinkedBlockingQueue<SimpleDateFormat>> = HashMap()

    @JvmStatic
    @Throws(TwitterException::class)
    fun getDate(dateString: String, format: String): Date {
        var simpleDateFormats = formatMapQueue[format]
        if (simpleDateFormats == null) {
            simpleDateFormats = LinkedBlockingQueue()
            formatMapQueue[format] = simpleDateFormats
        }
        var sdf = simpleDateFormats.poll()
        if (null == sdf) {
            sdf = SimpleDateFormat(format, Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
        try {
            return sdf.parse(dateString)
        } catch (pe: ParseException) {
            throw TwitterException("Unexpected date format($dateString) returned from twitter.com", pe)
        } finally {
            try {
                simpleDateFormats.put(sdf)
            } catch (ignore: InterruptedException) {
                // the size of LinkedBlockingQueue is Integer.MAX by default.
                // there is no need to concern about this situation
            }
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
