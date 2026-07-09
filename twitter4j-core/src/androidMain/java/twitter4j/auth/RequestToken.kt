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

import twitter4j.HttpResponse
import twitter4j.TwitterException
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationContext

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 *         representing unauthorized Request Token which is passed to the service provider when acquiring the authorized Access Token
 */
class RequestToken : OAuthToken, java.io.Serializable {
    private val conf: Configuration
    private var oauth: OAuthSupport? = null

    @Throws(TwitterException::class)
    internal constructor(res: HttpResponse, oauth: OAuthSupport?) : super(res) {
        conf = ConfigurationContext.getInstance()
        this.oauth = oauth
    }

    constructor(token: String?, tokenSecret: String?) : super(token, tokenSecret) {
        conf = ConfigurationContext.getInstance()
    }

    internal constructor(token: String?, tokenSecret: String?, oauth: OAuthSupport?) : super(token, tokenSecret) {
        conf = ConfigurationContext.getInstance()
        this.oauth = oauth
    }

    /**
     * @return authorization URL
     * since Twitter4J 2.0.0
     */
    val authorizationURL: String
        get() = conf.getOAuthAuthorizationURL() + "?oauth_token=" + token

    /**
     * @return authentication URL
     * since Twitter4J 2.0.10
     */
    val authenticationURL: String
        get() = conf.getOAuthAuthenticationURL() + "?oauth_token=" + token

    companion object {
        private const val serialVersionUID = -8806439091674811734L
    }
}
