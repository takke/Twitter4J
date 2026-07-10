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

import twitter4j.HttpRequest
import twitter4j.conf.Configuration

/**
 * Cookie Authorization
 */
class CookieAuthorization(conf: Configuration) : Authorization, Serializable {
//    private final Configuration conf;
//    private transient static HttpClient http;

    private val rawCookie: String?
    private val bearerToken: String?

    // constructors

    /**
     * @param conf configuration
     */
    init {

//        this.conf = conf;
//        http = HttpClientFactory.getInstance(conf.getHttpClientConfiguration());

        rawCookie = conf.cookie
        bearerToken = conf.bearerToken
    }

    // implementations for Authorization
    override fun getAuthorizationHeader(req: HttpRequest): String? {
        return "Bearer $bearerToken"
    }

    /**
     * {@inheritDoc}
     */
    override fun isEnabled(): Boolean {
        return rawCookie != null
    }

    override fun getAdditionalHeaders(): Map<String, String>? {
        val map = HashMap<String, String?>()

        val cookieParts = rawCookie!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var csrfToken: String? = null
        var auth_token: String? = null
        for (cookiePart in cookieParts) {
            val cookiePartParts = cookiePart.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (cookiePartParts.size == 2) {
                val key = cookiePartParts[0].trim()
                val value = cookiePartParts[1].trim()
                if (key == "ct0") {
                    csrfToken = value
                    break
                }
                if (key == "auth_token") {
                    auth_token = value
                }
            }
        }

        map["x-twitter-auth-type"] = "OAuth2Session"
        map["x-csrf-token"] = csrfToken    // ct0
        map["cookie"] = rawCookie          // "ct0=${this.csrfToken}; auth_token=${this.AuthToken}"

//        map.put("cookie", "ct0=" + csrfToken + "; auth_token=" + auth_token);

        @Suppress("UNCHECKED_CAST")
        return map as Map<String, String>
    }

    /*package*/

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || this::class != o::class) return false
        val that = o as CookieAuthorization
        return rawCookie == that.rawCookie
    }

    override fun hashCode(): Int {
        // KMP移行 Phase 4-4: java.util.Objects.hash(single) と同値（Arrays.hashCode 相当）。
        return 31 + (rawCookie?.hashCode() ?: 0)
    }

    override fun toString(): String {
        return "CookieAuthorization{" +
                "rawCookie='" + rawCookie + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = -886869424811858968L
    }
}
