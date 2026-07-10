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
import kotlin.jvm.Transient

abstract class OAuthToken : Serializable {

    val token: String
    val tokenSecret: String

    // KMP移行 Phase 4-2:
    // 署名鍵のキャッシュ。従来は javax.crypto の SecretKeySpec を保持していたが、
    // HMAC-SHA1 を純Kotlin実装(HmacSha1)へ切り替えたため、鍵バイト列そのものをキャッシュする。
    @Transient
    internal var secretKeyBytes: ByteArray? = null
    private var responseStr: Array<String>? = null

    constructor(token: String?, tokenSecret: String?) {
        if (token == null)
            throw IllegalArgumentException("Token can't be null")
        if (tokenSecret == null)
            throw IllegalArgumentException("TokenSecret can't be null")
        this.token = token
        this.tokenSecret = tokenSecret
    }

    @Throws(TwitterException::class)
    constructor(response: HttpResponse) : this(response.asString())

    constructor(string: String) {
        responseStr = string.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        tokenSecret = getParameter("oauth_token_secret")!!
        token = getParameter("oauth_token")!!
    }

    fun getParameter(parameter: String): String? {
        var value: String? = null
        for (str in responseStr!!) {
            if (str.startsWith(parameter + '=')) {
                value = str.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim()
                break
            }
        }
        return value
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is OAuthToken) return false

        val that = o

        if (token != that.token) return false
        if (tokenSecret != that.tokenSecret) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + tokenSecret.hashCode()
        return result
    }

    override fun toString(): String {
        return "OAuthToken{" +
                "token='" + token + '\'' +
                ", tokenSecret='" + tokenSecret + '\'' +
                ", secretKeyBytes=" + secretKeyBytes +
                '}'
    }

    companion object {
        private const val serialVersionUID = -7841506492508140600L
    }
}
