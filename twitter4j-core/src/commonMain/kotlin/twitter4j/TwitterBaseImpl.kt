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

import twitter4j.auth.AccessToken
import twitter4j.auth.Authorization
import twitter4j.auth.AuthorizationFactory
import twitter4j.auth.BasicAuthorization
import twitter4j.auth.NullAuthorization
import twitter4j.auth.OAuth2Authorization
import twitter4j.auth.OAuth2Support
import twitter4j.auth.OAuth2Token
import twitter4j.auth.OAuthAuthorization
import twitter4j.auth.OAuthSupport
import twitter4j.auth.RequestToken
import twitter4j.conf.Configuration
import kotlin.jvm.JvmField
import kotlin.jvm.Transient

/**
 * Base class of Twitter / AsyncTwitter / TwitterStream supports OAuth.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
// TwitPane(shared/core_tw 等の別モジュール package twitter4j コード)や twitter4j-v2 バイナリが
// http / auth / conf フィールドを直接参照するため、旧Javaの package-private 相当を
// @JvmField 付き public フィールドとして公開する（バイトコード上のフィールド名を維持）。
abstract class TwitterBaseImpl internal constructor(conf: Configuration, auth: Authorization?) :
    TwitterBase, Serializable, OAuthSupport, OAuth2Support, HttpResponseListener {

    @JvmField
    var conf: Configuration

    @Transient
    private var _screenName: String? = null

    @Transient
    private var _id: Long = 0

    @JvmField
    @Transient
    var http: HttpClient

    lateinit var factory: ObjectFactory

    @JvmField
    var auth: Authorization

    init {
        this.conf = conf
        var resolvedAuth = auth
        if (null == resolvedAuth) {
            // try to populate OAuthAuthorization if available in the configuration
            val consumerKey = conf.oAuthConsumerKey
            val consumerSecret = conf.oAuthConsumerSecret
            // try to find oauth tokens in the configuration
            if (consumerKey != null && consumerSecret != null) {
                if (conf.isApplicationOnlyAuthEnabled()) {
                    val oauth2 = OAuth2Authorization(conf)
                    val tokenType = conf.oAuth2TokenType
                    val accessToken = conf.oAuth2AccessToken
                    if (tokenType != null && accessToken != null) {
                        oauth2.setOAuth2Token(OAuth2Token(tokenType, accessToken))
                    }
                    resolvedAuth = oauth2
                } else {
                    val oauth = OAuthAuthorization(conf)
                    val accessToken = conf.oAuthAccessToken
                    val accessTokenSecret = conf.oAuthAccessTokenSecret
                    if (accessToken != null && accessTokenSecret != null) {
                        oauth.setOAuthAccessToken(AccessToken(accessToken, accessTokenSecret))
                    }
                    resolvedAuth = oauth
                }
            } else {
                resolvedAuth = NullAuthorization.getInstance()
            }
        }
        this.auth = resolvedAuth
        http = newHttpClient(conf.getHttpClientConfiguration())
        setFactory()
    }

    internal fun setFactory() {
        factory = JSONImplFactory(conf)
    }

    @Throws(TwitterException::class, IllegalStateException::class)
    override fun getScreenName(): String {
        if (!auth.isEnabled()) {
            throw IllegalStateException(
                "Neither user ID/password combination nor OAuth consumer key/secret combination supplied"
            )
        }
        if (null == _screenName) {
            if (auth is BasicAuthorization) {
                _screenName = (auth as BasicAuthorization).userId
                if (_screenName!!.contains("@")) {
                    _screenName = null
                }
            }
            if (null == _screenName) {
                // retrieve the screen name if this instance is authenticated with OAuth or email address
                fillInIDAndScreenName()
            }
        }
        return _screenName!!
    }

    @Throws(TwitterException::class, IllegalStateException::class)
    override fun getId(): Long {
        if (!auth.isEnabled()) {
            throw IllegalStateException(
                "Neither user ID/password combination nor OAuth consumer key/secret combination supplied"
            )
        }
        if (0L == _id) {
            fillInIDAndScreenName()
        }
        // retrieve the screen name if this instance is authenticated with OAuth or email address
        return _id
    }

    @Throws(TwitterException::class)
    internal fun fillInIDAndScreenName(): User {
        return fillInIDAndScreenName(null)
    }

    @Throws(TwitterException::class)
    internal fun fillInIDAndScreenName(parameters: Array<HttpParameter>?): User {
        ensureAuthorizationEnabled()
        val user = UserJSONImpl(http.get(conf.getRestBaseURL() + "account/verify_credentials.json", parameters, auth, this), conf)
        this._screenName = user.screenName
        this._id = user.id
        return user
    }

    override fun httpResponseReceived(event: HttpResponseEvent) {
    }

    override val authorization: Authorization
        get() = auth

    override val configuration: Configuration
        get() = this.conf

    fun ensureAuthorizationEnabled() {
        if (!auth.isEnabled()) {
            throw IllegalStateException(
                "Authentication credentials are missing. $WWW_DETAILS"
            )
        }
    }

    fun ensureOAuthEnabled() {
        if (auth !is OAuthAuthorization) {
            throw IllegalStateException(
                "OAuth required. Authentication credentials are missing. $WWW_DETAILS"
            )
        }
    }

    // KMP移行 Phase 4-4: writeObject/readObject（Javaシリアライズのカスタムフック）は
    // JVM のオブジェクトシリアライズ機構専用であり、Twitter インスタンスを Java シリアライズする経路は
    // TwitPane に存在しないため削除した。http は @Transient のため元々シリアライズ対象外で、
    // 復元時に再生成していただけであり、挙動上の影響はない。


    // methods declared in OAuthSupport interface

    @JvmSynchronized
    override fun setOAuthConsumer(consumerKey: String?, consumerSecret: String?) {
        if (null == consumerKey) {
            throw NullPointerException("consumer key is null")
        }
        if (null == consumerSecret) {
            throw NullPointerException("consumer secret is null")
        }
        if (auth is NullAuthorization) {
            if (conf.isApplicationOnlyAuthEnabled()) {
                val oauth2 = OAuth2Authorization(conf)
                oauth2.setOAuthConsumer(consumerKey, consumerSecret)
                auth = oauth2
            } else {
                val oauth = OAuthAuthorization(conf)
                oauth.setOAuthConsumer(consumerKey, consumerSecret)
                auth = oauth
            }
        } else if (auth is BasicAuthorization) {
            val xauth = XAuthAuthorization(auth as BasicAuthorization)
            xauth.setOAuthConsumer(consumerKey, consumerSecret)
            auth = xauth
        } else if (auth is OAuthAuthorization || auth is OAuth2Authorization) {
            throw IllegalStateException("consumer key/secret pair already set.")
        }
    }

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(): RequestToken {
        return getOAuthRequestToken(null)
    }

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(callbackURL: String?): RequestToken {
        return getOAuth().getOAuthRequestToken(callbackURL)
    }

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(callbackURL: String?, xAuthAccessType: String?): RequestToken {
        return getOAuth().getOAuthRequestToken(callbackURL, xAuthAccessType)
    }

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(callbackURL: String?, xAuthAccessType: String?, xAuthMode: String?): RequestToken {
        return getOAuth().getOAuthRequestToken(callbackURL, xAuthAccessType, xAuthMode)
    }

    /**
     * {@inheritDoc}
     * Basic authenticated instance of this class will try acquiring an AccessToken using xAuth.<br>
     * In order to get access acquire AccessToken using xAuth, you must apply by sending an email to <a href="mailto:api@twitter.com">api@twitter.com</a> all other applications will receive an HTTP 401 error.  Web-based applications will not be granted access, except on a temporary basis for when they are converting from basic-authentication support to full OAuth support.<br>
     * Storage of Twitter usernames and passwords is forbidden. By using xAuth, you are required to store only access tokens and access token secrets. If the access token expires or is expunged by a user, you must ask for their login and password again before exchanging the credentials for an access token.
     *
     * @throws TwitterException When Twitter service or network is unavailable, when the user has not authorized, or when the client application is not permitted to use xAuth
     * @see <a href="https://dev.twitter.com/docs/oauth/xauth">xAuth | Twitter Developers</a>
     */
    @JvmSynchronized
    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(): AccessToken {
        var auth: Authorization = this.auth
        val oauthAccessToken: AccessToken
        if (auth is BasicAuthorization) {
            val basicAuth = auth
            auth = AuthorizationFactory.getInstance(conf)
            if (auth is OAuthAuthorization) {
                this.auth = auth
                oauthAccessToken = auth.getOAuthAccessToken(basicAuth.userId, basicAuth.password)
            } else {
                throw IllegalStateException("consumer key / secret combination not supplied.")
            }
        } else {
            if (auth is XAuthAuthorization) {
                val xauth = auth
                this.auth = xauth
                val oauthAuth = OAuthAuthorization(conf)
                oauthAuth.setOAuthConsumer(xauth.consumerKey, xauth.consumerSecret)
                oauthAccessToken = oauthAuth.getOAuthAccessToken(xauth.getUserId(), xauth.getPassword())
            } else {
                oauthAccessToken = getOAuth().getOAuthAccessToken()
            }
        }
        _screenName = oauthAccessToken.screenName
        _id = oauthAccessToken.userId
        return oauthAccessToken
    }


    @JvmSynchronized
    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(oauthVerifier: String?): AccessToken {
        val oauthAccessToken = getOAuth().getOAuthAccessToken(oauthVerifier)
        _screenName = oauthAccessToken.screenName
        return oauthAccessToken
    }

    @JvmSynchronized
    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(requestToken: RequestToken?): AccessToken {
        val oauth = getOAuth()
        val oauthAccessToken = oauth.getOAuthAccessToken(requestToken)
        _screenName = oauthAccessToken.screenName
        return oauthAccessToken
    }

    @JvmSynchronized
    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(requestToken: RequestToken?, oauthVerifier: String?): AccessToken {
        return getOAuth().getOAuthAccessToken(requestToken, oauthVerifier)
    }

    @JvmSynchronized
    override fun setOAuthAccessToken(accessToken: AccessToken?) {
        getOAuth().setOAuthAccessToken(accessToken)
    }

    @JvmSynchronized
    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(screenName: String?, password: String?): AccessToken {
        return getOAuth().getOAuthAccessToken(screenName, password)
    }
    /* OAuth support methods */

    private fun getOAuth(): OAuthSupport {
        if (auth !is OAuthSupport) {
            throw IllegalStateException(
                "OAuth consumer key/secret combination not supplied"
            )
        }
        return auth as OAuthSupport
    }

    @JvmSynchronized
    @Throws(TwitterException::class)
    override fun getOAuth2Token(): OAuth2Token {
        return getOAuth2().getOAuth2Token()
    }

    override fun setOAuth2Token(oauth2Token: OAuth2Token?) {
        getOAuth2().setOAuth2Token(oauth2Token)
    }

    @JvmSynchronized
    @Throws(TwitterException::class)
    override fun invalidateOAuth2Token() {
        getOAuth2().invalidateOAuth2Token()
    }

    private fun getOAuth2(): OAuth2Support {
        if (auth !is OAuth2Support) {
            throw IllegalStateException(
                "OAuth consumer key/secret combination not supplied"
            )
        }
        return auth as OAuth2Support
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is TwitterBaseImpl) return false

        val that = o

        if (auth != that.auth) return false
        if (conf != that.conf) return false
        if (http != that.http) return false

        return true
    }

    override fun hashCode(): Int {
        var result = conf.hashCode()
        result = 31 * result + http.hashCode()
        result = 31 * result + auth.hashCode()
        return result
    }

    override fun toString(): String {
        return "TwitterBase{" +
            "conf=" + conf +
            ", http=" + http +
            ", auth=" + auth +
            '}'
    }

    companion object {
        private const val WWW_DETAILS = "See http://twitter4j.org/en/configuration.html for details. See and register at http://apps.twitter.com/"
        private const val serialVersionUID = -7824361938865528554L
    }
}
