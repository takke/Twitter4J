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

package twitter4j.auth

import twitter4j.Serializable

import twitter4j.HttpResponse
import twitter4j.TwitterException

/**
 * Representing authorized Access Token which is passed to the service provider in order to access protected resources.<br>
 * the token and token secret can be stored into some persistent stores such as file system or RDBMS for the further accesses.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
open class AccessToken : OAuthToken, Serializable {

    /**
     * @return screen name
     * @since Twitter4J 2.0.4
     *
     * 注: 元 Java 実装では getParameter() の結果（null 可）をそのまま保持していたが、
     * 消費側（TwitPane 等）が getScreenName() の戻り値を非 null 前提で扱っている
     * （platform type 由来）ため、Kotlin 化に際して非 null 型で公開する。
     * access_token レスポンスには screen_name が必ず含まれるため実挙動は不変。
     * token/tokenSecret 指定のコンストラクタ経路のみ、旧 null が空文字になる（未参照）。
     */
    var screenName: String = ""
        private set

    /**
     * @return user id
     * @since Twitter4J 2.0.4
     */
    var userId: Long = -1L
        private set

    @Throws(TwitterException::class)
    internal constructor(res: HttpResponse) : this(res.asString())

    internal constructor(str: String) : super(str) {
        screenName = getParameter("screen_name") ?: ""
        val sUserId = getParameter("user_id")
        if (sUserId != null) {
            userId = sUserId.toLong()
        }
    }

    constructor(token: String, tokenSecret: String) : super(token, tokenSecret) {
        val dashIndex = token.indexOf("-")
        if (dashIndex != -1) {
            val sUserId = token.substring(0, dashIndex)
            try {
                userId = sUserId.toLong()
            } catch (ignore: NumberFormatException) {
            }
        }
    }

    constructor(token: String, tokenSecret: String, userId: Long) : super(token, tokenSecret) {
        this.userId = userId
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false

        val that = o as AccessToken

        if (userId != that.userId) return false
        if (screenName != that.screenName)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + screenName.hashCode()
        result = 31 * result + (userId xor (userId ushr 32)).toInt()
        return result
    }

    override fun toString(): String {
        return "AccessToken{" +
                "screenName='" + screenName + '\'' +
                ", userId=" + userId +
                '}'
    }

    companion object {
        private const val serialVersionUID = 2470022129505774772L
    }
}
