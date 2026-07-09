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

import twitter4j.conf.Configuration

/**
 * A static factory class for Authorization.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.1
 */
object AuthorizationFactory {
    /**
     * @param conf configuration
     * @return authorization instance
     * @since Twitter4J 2.1.11
     */
    @JvmStatic
    fun getInstance(conf: Configuration): Authorization {
        var auth: Authorization? = null
        val consumerKey = conf.oAuthConsumerKey
        val consumerSecret = conf.oAuthConsumerSecret

        if (consumerKey != null && consumerSecret != null) {
            if (conf.isApplicationOnlyAuthEnabled()) {
                val oauth2 = OAuth2Authorization(conf)
                val tokenType = conf.oAuth2TokenType
                val accessToken = conf.oAuth2AccessToken
                if (tokenType != null && accessToken != null) {
                    oauth2.setOAuth2Token(OAuth2Token(tokenType, accessToken))
                }
                auth = oauth2

            } else {
                val oauth: OAuthAuthorization
                oauth = OAuthAuthorization(conf)
                val accessToken = conf.oAuthAccessToken
                val accessTokenSecret = conf.oAuthAccessTokenSecret
                if (accessToken != null && accessTokenSecret != null) {
                    oauth.setOAuthAccessToken(AccessToken(accessToken, accessTokenSecret))
                }
                auth = oauth
            }
        } else {
            val screenName = conf.user
            val password = conf.password
            if (screenName != null && password != null) {
                auth = BasicAuthorization(screenName, password)
            }

            val cookie = conf.cookie
            if (cookie != null) {
                auth = CookieAuthorization(conf)
            }

        }
        if (null == auth) {
            auth = NullAuthorization.getInstance()
        }
        return auth!!
    }
}
