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
import twitter4j.JSONException
import twitter4j.JSONObject
import twitter4j.TwitterException

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

class OAuth2Token : Serializable {

    var tokenType: String? = null
        private set

    var accessToken: String? = null
        private set

    @Throws(TwitterException::class)
    internal constructor(res: HttpResponse) {
        val json = res.asJSONObject()
        tokenType = getRawString("token_type", json)
        try {
            accessToken = URLDecoder.decode(getRawString("access_token", json), "UTF-8")
        } catch (ignore: UnsupportedEncodingException) {
        }
    }

    constructor(tokenType: String?, accessToken: String?) {
        this.tokenType = tokenType
        this.accessToken = accessToken
    }

    internal fun generateAuthorizationHeader(): String {
        var encoded = ""
        try {
            encoded = URLEncoder.encode(accessToken, "UTF-8")
        } catch (ignore: UnsupportedEncodingException) {
        }
        return "Bearer $encoded"
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is OAuth2Token) {
            return false
        }

        val that = obj
        if (if (tokenType != null) tokenType != that.tokenType else that.tokenType != null) {
            return false
        }
        if (if (accessToken != null) accessToken != that.accessToken else that.accessToken != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = tokenType?.hashCode() ?: 0
        result = 31 * result + (accessToken?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "OAuth2Token{" +
                "tokenType='" + tokenType + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = -8985359441959903216L

        private fun getRawString(name: String, json: JSONObject): String? {
            return try {
                if (json.isNull(name)) {
                    null
                } else {
                    json.getString(name)
                }
            } catch (jsone: JSONException) {
                null
            }
        }
    }
}
