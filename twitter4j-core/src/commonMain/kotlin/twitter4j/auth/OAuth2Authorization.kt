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

import twitter4j.BASE64Encoder
import twitter4j.HttpClient
import twitter4j.newHttpClient
import twitter4j.HttpParameter
import twitter4j.HttpRequest
import twitter4j.TwitterException
import twitter4j.conf.Configuration

/**
 * @author KOMIYA Atsushi - komiya.atsushi at gmail.com
 * @see <a href="https://dev.twitter.com/docs/auth/application-only-auth">Application-only authentication</a>
 */
class OAuth2Authorization(private val conf: Configuration) : Authorization, Serializable, OAuth2Support {

    private val http: HttpClient

    private var consumerKey: String? = null

    private var consumerSecret: String? = null

    private var token: OAuth2Token? = null

    init {
        setOAuthConsumer(conf.oAuthConsumerKey, conf.oAuthConsumerSecret)
        http = newHttpClient(conf.getHttpClientConfiguration())
    }

    override fun setOAuthConsumer(consumerKey: String?, consumerSecret: String?) {
        this.consumerKey = consumerKey ?: ""
        this.consumerSecret = consumerSecret ?: ""
    }

    @Throws(TwitterException::class)
    override fun getOAuth2Token(): OAuth2Token {
        if (token != null) {
            throw IllegalStateException("OAuth 2 Bearer Token is already available.")
        }

        val params: Array<HttpParameter> = if (conf.getOAuth2Scope() == null) {
            arrayOf(HttpParameter("grant_type", "client_credentials"))
        } else {
            arrayOf(
                HttpParameter("grant_type", "client_credentials"),
                HttpParameter("scope", conf.getOAuth2Scope())
            )
        }

        val res = http.post(conf.getOAuth2TokenURL(), params, this, null)
        if (res.getStatusCode() != 200) {
            throw TwitterException("Obtaining OAuth 2 Bearer Token failed.", res)
        }
        token = OAuth2Token(res)
        return token!!
    }

    override fun setOAuth2Token(oauth2Token: OAuth2Token?) {
        this.token = oauth2Token
    }

    @Throws(TwitterException::class)
    override fun invalidateOAuth2Token() {
        if (token == null) {
            throw IllegalStateException("OAuth 2 Bearer Token is not available.")
        }

        val params = arrayOf(HttpParameter("access_token", token!!.accessToken))

        val _token = token
        var succeed = false

        try {
            token = null

            val res = http.post(conf.getOAuth2InvalidateTokenURL(), params, this, null)
            if (res.getStatusCode() != 200) {
                throw TwitterException("Invalidating OAuth 2 Bearer Token failed.", res)
            }

            succeed = true

        } finally {
            if (!succeed) {
                token = _token
            }
        }
    }

    override fun getAuthorizationHeader(req: HttpRequest): String? {
        if (token == null) {
            // KMP移行 Phase 4-4: java.net.URLEncoder（application/x-www-form-urlencoded）を
            // common の HttpParameter.encode（パーセントエンコード）へ置換。
            // consumerKey / consumerSecret は英数字トークンのため両者のエンコード結果は同一。
            val credentials =
                HttpParameter.encode(consumerKey) +
                        ":" +
                        HttpParameter.encode(consumerSecret)

            return "Basic " + BASE64Encoder.encode(credentials.encodeToByteArray())

        } else {
            return token!!.generateAuthorizationHeader()
        }
    }

    override fun isEnabled(): Boolean {
        return token != null
    }

    override fun getAdditionalHeaders(): Map<String, String>? {
        return null
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is OAuth2Authorization) {
            return false
        }

        val that = obj
        if (if (consumerKey != null) consumerKey != that.consumerKey else that.consumerKey != null) {
            return false
        }
        if (if (consumerSecret != null) consumerSecret != that.consumerSecret else that.consumerSecret != null) {
            return false
        }
        if (if (token != null) token != that.token else that.token != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = consumerKey?.hashCode() ?: 0
        result = 31 * result + (consumerSecret?.hashCode() ?: 0)
        result = 31 * result + (token?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "OAuth2Authorization{" +
                "consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='******************************************\'" +
                ", token=" + (if (token == null) "null" else token.toString()) +
                '}'
    }

    companion object {
        private const val serialVersionUID = -2895232598422218647L
    }
}
