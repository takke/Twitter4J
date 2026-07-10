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

package twitter4j.conf

import twitter4j.Properties

/**
 * A builder that can be used to construct a twitter4j configuration with desired settings.  This
 * builder has sensible defaults such that {@code new ConfigurationBuilder().build()} would create a
 * usable configuration.  This configuration builder is useful for clients that wish to configure
 * twitter4j in unit tests or from command line flags for example.
 *
 * @author John Sirois - john.sirois at gmail.com
 */
class ConfigurationBuilder {

    // KMP移行 Phase 3-2 で PropertyConfiguration を削除したため、
    // twitter4j.properties / System property の読み込みは行わず、デフォルト値の ConfigurationBase を使用する。
    private var configurationBean: ConfigurationBase? = ConfigurationBase()

    fun setPrettyDebugEnabled(prettyDebugEnabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setPrettyDebugEnabled(prettyDebugEnabled)
        return this
    }

    fun setGZIPEnabled(gzipEnabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setGZIPEnabled(gzipEnabled)
        return this
    }

    fun setDebugEnabled(debugEnabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setDebug(debugEnabled)
        return this
    }

    fun setApplicationOnlyAuthEnabled(applicationOnlyAuthEnabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setApplicationOnlyAuthEnabled(applicationOnlyAuthEnabled)
        return this
    }

    fun setUser(user: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.user = user
        return this
    }

    fun setPassword(password: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.password = password
        return this
    }

    fun setHttpProxyHost(httpProxyHost: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpProxyHost(httpProxyHost)
        return this
    }

    fun setHttpProxyUser(httpProxyUser: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpProxyUser(httpProxyUser)
        return this
    }

    fun setHttpProxyPassword(httpProxyPassword: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpProxyPassword(httpProxyPassword)
        return this
    }

    fun setHttpProxyPort(httpProxyPort: Int): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpProxyPort(httpProxyPort)
        return this
    }

    fun setHttpProxySocks(httpProxySocks: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpProxySocks(httpProxySocks)
        return this
    }

    fun setHttpConnectionTimeout(httpConnectionTimeout: Int): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpConnectionTimeout(httpConnectionTimeout)
        return this
    }

    fun setHttpReadTimeout(httpReadTimeout: Int): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpReadTimeout(httpReadTimeout)
        return this
    }

    fun setHttpStreamingReadTimeout(httpStreamingReadTimeout: Int): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpStreamingReadTimeout(httpStreamingReadTimeout)
        return this
    }

    fun setHttpRetryCount(httpRetryCount: Int): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpRetryCount(httpRetryCount)
        return this
    }

    fun setHttpRetryIntervalSeconds(httpRetryIntervalSeconds: Int): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setHttpRetryIntervalSeconds(httpRetryIntervalSeconds)
        return this
    }

    fun setCookie(cookie: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.cookie = cookie
        return this
    }

    fun setBearerToken(bearerToken: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.bearerToken = bearerToken
        return this
    }

    fun setOAuthConsumerKey(oAuthConsumerKey: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.oAuthConsumerKey = oAuthConsumerKey
        return this
    }

    fun setOAuthConsumerSecret(oAuthConsumerSecret: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.oAuthConsumerSecret = oAuthConsumerSecret
        return this
    }

    fun setOAuthAccessToken(oAuthAccessToken: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.oAuthAccessToken = oAuthAccessToken
        return this
    }

    fun setOAuthAccessTokenSecret(oAuthAccessTokenSecret: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.oAuthAccessTokenSecret = oAuthAccessTokenSecret
        return this
    }

    fun setOAuth2TokenType(oAuth2TokenType: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.oAuth2TokenType = oAuth2TokenType
        return this
    }

    fun setOAuth2AccessToken(oAuth2AccessToken: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.oAuth2AccessToken = oAuth2AccessToken
        return this
    }

    fun setOAuth2Scope(oAuth2Scope: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setOAuth2Scope(oAuth2Scope)
        return this
    }

    fun setOAuthRequestTokenURL(oAuthRequestTokenURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setOAuthRequestTokenURL(oAuthRequestTokenURL)
        return this
    }

    fun setOAuthAuthorizationURL(oAuthAuthorizationURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setOAuthAuthorizationURL(oAuthAuthorizationURL)
        return this
    }

    fun setOAuthAccessTokenURL(oAuthAccessTokenURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setOAuthAccessTokenURL(oAuthAccessTokenURL)
        return this
    }

    fun setOAuthAuthenticationURL(oAuthAuthenticationURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setOAuthAuthenticationURL(oAuthAuthenticationURL)
        return this
    }

    fun setOAuth2TokenURL(oAuth2TokenURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setOAuth2TokenURL(oAuth2TokenURL)
        return this
    }

    fun setOAuth2InvalidateTokenURL(invalidateTokenURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setOAuth2InvalidateTokenURL(invalidateTokenURL)
        return this
    }

    fun setRestBaseURL(restBaseURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setRestBaseURL(restBaseURL)
        return this
    }

    fun setUploadBaseURL(uploadBaseURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setUploadBaseURL(uploadBaseURL)
        return this
    }

    fun setStreamBaseURL(streamBaseURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setStreamBaseURL(streamBaseURL)
        return this
    }

    fun setUserStreamBaseURL(userStreamBaseURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setUserStreamBaseURL(userStreamBaseURL)
        return this
    }

    fun setSiteStreamBaseURL(siteStreamBaseURL: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setSiteStreamBaseURL(siteStreamBaseURL)
        return this
    }

    fun setAsyncNumThreads(asyncNumThreads: Int): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setAsyncNumThreads(asyncNumThreads)
        return this
    }

    fun setDaemonEnabled(daemonEnabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setDaemonEnabled(daemonEnabled)
        return this
    }

    fun setContributingTo(contributingTo: Long): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setContributingTo(contributingTo)
        return this
    }

    fun setTrimUserEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setTrimUserEnabled(enabled)
        return this
    }

    fun setIncludeExtAltTextEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setIncludeExtAltTextEnabled(enabled)
        return this
    }

    fun setTweetModeExtended(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setTweetModeExtended(enabled)
        return this
    }

    fun setIncludeExtEditControl(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setIncludeExtEditControl(enabled)
        return this
    }

    fun setIncludeMyRetweetEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setIncludeMyRetweetEnabled(enabled)
        return this
    }

    fun setIncludeEntitiesEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setIncludeEntitiesEnabled(enabled)
        return this
    }

    fun setIncludeEmailEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setIncludeEmailEnabled(enabled)
        return this
    }

    fun setJSONStoreEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setJSONStoreEnabled(enabled)
        return this
    }

    fun setMBeanEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setMBeanEnabled(enabled)
        return this
    }

    fun setUserStreamRepliesAllEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setUserStreamRepliesAllEnabled(enabled)
        return this
    }

    fun setUserStreamWithFollowingsEnabled(enabled: Boolean): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setUserStreamWithFollowingsEnabled(enabled)
        return this
    }

    fun setMediaProvider(mediaProvider: String): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setMediaProvider(mediaProvider)
        return this
    }

    fun setMediaProviderAPIKey(mediaProviderAPIKey: String?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setMediaProviderAPIKey(mediaProviderAPIKey)
        return this
    }

    fun setMediaProviderParameters(props: Properties?): ConfigurationBuilder {
        checkNotBuilt()
        configurationBean!!.setMediaProviderParameters(props)
        return this
    }

    fun build(): Configuration {
        checkNotBuilt()
        configurationBean!!.cacheInstance()
        try {
            return configurationBean!!
        } finally {
            configurationBean = null
        }
    }

    private fun checkNotBuilt() {
        checkNotNull(configurationBean) { "Cannot use this builder any longer, build() has already been called" }
    }
}
