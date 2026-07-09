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

import java.io.ObjectStreamException
import java.io.Serializable
import java.net.Proxy
import java.util.Properties

import twitter4j.HttpClientConfiguration
import twitter4j.Logger

/**
 * Configuration base class with default settings.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
internal open class ConfigurationBase constructor() : Configuration, java.io.Serializable {
    private var debug = false
    override var user: String? = null
    override var password: String? = null
    private var httpConf: HttpClientConfiguration = MyHttpClientConfiguration(
        null, // proxy host
        null, // proxy user
        null, // proxy password
        -1, // proxy port
        false, // proxy socks
        20000, // connection timeout
        120000, // read timeout
        false, // pretty debug
        true // gzip enabled
    )

    private var httpStreamingReadTimeout = 40 * 1000
    private var httpRetryCount = 0
    private var httpRetryIntervalSeconds = 5

    override var cookie: String? = null
    override var bearerToken: String? = null

    override var oAuthConsumerKey: String? = null
    override var oAuthConsumerSecret: String? = null
    override var oAuthAccessToken: String? = null
    override var oAuthAccessTokenSecret: String? = null
    override var oAuth2TokenType: String? = null
    override var oAuth2AccessToken: String? = null
    private var oAuth2Scope: String? = null
    private var oAuthRequestTokenURL = "https://api.twitter.com/oauth/request_token"
    private var oAuthAuthorizationURL = "https://api.twitter.com/oauth/authorize"
    private var oAuthAccessTokenURL = "https://api.twitter.com/oauth/access_token"
    private var oAuthAuthenticationURL = "https://api.twitter.com/oauth/authenticate"
    private var oAuth2TokenURL = "https://api.twitter.com/oauth2/token"
    private var oAuth2InvalidateTokenURL = "https://api.twitter.com/oauth2/invalidate_token"

    private var restBaseURL = "https://api.twitter.com/1.1/"
    private var streamBaseURL = "https://stream.twitter.com/1.1/"
    private var userStreamBaseURL = "https://userstream.twitter.com/1.1/"
    private var siteStreamBaseURL = "https://sitestream.twitter.com/1.1/"
    private var uploadBaseURL = "https://upload.twitter.com/1.1/"

    private var asyncNumThreads = 1

    private var contributingTo = -1L

    private var includeMyRetweetEnabled = true
    private var includeEntitiesEnabled = true
    private var trimUserEnabled = false
    private var includeExtAltTextEnabled = true
    private var tweetModeExtended = true
    private var includeExtEditControl = true
    private var includeEmailEnabled = false

    private var jsonStoreEnabled = false

    private var mbeanEnabled = false

    private var userStreamRepliesAllEnabled = false
    private var userStreamWithFollowingsEnabled = true
    private var stallWarningsEnabled = true

    private var applicationOnlyAuthEnabled = false

    private var mediaProvider = "TWITTER"
    private var mediaProviderAPIKey: String? = null
    private var mediaProviderParameters: Properties? = null
    private var daemonEnabled = true

    private var streamThreadName = ""

    inner class MyHttpClientConfiguration(
        private val httpProxyHost: String?,
        private val httpProxyUser: String?,
        private val httpProxyPassword: String?,
        private val httpProxyPort: Int,
        private val httpProxySocks: Boolean,
        private val httpConnectionTimeout: Int,
        private val httpReadTimeout: Int,
        private val prettyDebug: Boolean,
        private val gzipEnabled: Boolean
    ) : HttpClientConfiguration, Serializable {

        override fun getHttpProxyHost(): String? {
            return httpProxyHost
        }

        override fun getHttpProxyPort(): Int {
            return httpProxyPort
        }

        override fun getHttpProxyUser(): String? {
            return httpProxyUser
        }

        override fun getHttpProxyPassword(): String? {
            return httpProxyPassword
        }

        override fun isHttpProxySocks(): Boolean {
            return httpProxySocks
        }

        override fun getHttpConnectionTimeout(): Int {
            return httpConnectionTimeout
        }

        override fun getHttpReadTimeout(): Int {
            return httpReadTimeout
        }

        override fun getHttpRetryCount(): Int {
            return httpRetryCount
        }

        override fun getHttpRetryIntervalSeconds(): Int {
            return httpRetryIntervalSeconds
        }

        override fun isPrettyDebugEnabled(): Boolean {
            return prettyDebug
        }

        override fun isGZIPEnabled(): Boolean {
            return gzipEnabled
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false

            val that = o as MyHttpClientConfiguration

            if (gzipEnabled != that.gzipEnabled) return false
            if (httpProxySocks != that.httpProxySocks) return false
            if (httpConnectionTimeout != that.httpConnectionTimeout) return false
            if (httpProxyPort != that.httpProxyPort) return false
            if (httpProxySocks != that.httpProxySocks) return false
            if (httpReadTimeout != that.httpReadTimeout) return false
            if (prettyDebug != that.prettyDebug) return false
            if (httpProxyHost != that.httpProxyHost) return false
            if (httpProxyPassword != that.httpProxyPassword) return false
            if (httpProxyUser != that.httpProxyUser) return false

            return true
        }

        override fun hashCode(): Int {
            var result = httpProxyHost?.hashCode() ?: 0
            result = 31 * result + (httpProxyUser?.hashCode() ?: 0)
            result = 31 * result + (httpProxyPassword?.hashCode() ?: 0)
            result = 31 * result + httpProxyPort
            result = 31 * result + (if (httpProxySocks) 1 else 0)
            result = 31 * result + httpConnectionTimeout
            result = 31 * result + httpReadTimeout
            result = 31 * result + (if (prettyDebug) 1 else 0)
            result = 31 * result + (if (gzipEnabled) 1 else 0)
            return result
        }

        override fun toString(): String {
            return "MyHttpClientConfiguration{" +
                    "httpProxyHost='" + httpProxyHost + '\'' +
                    ", httpProxyUser='" + httpProxyUser + '\'' +
                    ", httpProxyPassword='" + httpProxyPassword + '\'' +
                    ", httpProxyPort=" + httpProxyPort +
                    ", proxyType=" + (if (httpProxySocks) Proxy.Type.SOCKS else Proxy.Type.HTTP) +
                    ", httpConnectionTimeout=" + httpConnectionTimeout +
                    ", httpReadTimeout=" + httpReadTimeout +
                    ", prettyDebug=" + prettyDebug +
                    ", gzipEnabled=" + gzipEnabled +
                    '}'
        }
    }


    fun dumpConfiguration() {
        val log = Logger.getLogger(ConfigurationBase::class.java)
        if (debug) {
            val fields = ConfigurationBase::class.java.declaredFields
            for (field in fields) {
                try {
                    val value = field.get(this)
                    var strValue = value?.toString() ?: "null"
                    if (value != null && field.name.matches(Regex("oAuthConsumerSecret|oAuthAccessTokenSecret|password"))) {
                        strValue = value.toString().replace(Regex("."), "*")
                    }
                    log.debug(field.name + ": " + strValue)
                } catch (ignore: IllegalAccessException) {
                }
            }
        }
    }

    override fun isDebugEnabled(): Boolean {
        return debug
    }

    fun setDebug(debug: Boolean) {
        this.debug = debug
    }

    override fun getHttpClientConfiguration(): HttpClientConfiguration {
        return httpConf
    }

    fun setPrettyDebugEnabled(prettyDebug: Boolean) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            httpConf.getHttpProxyUser(),
            httpConf.getHttpProxyPassword(),
            httpConf.getHttpProxyPort(),
            httpConf.isHttpProxySocks(),
            httpConf.getHttpConnectionTimeout(),
            httpConf.getHttpReadTimeout(),
            prettyDebug, httpConf.isGZIPEnabled()
        )
    }

    fun setGZIPEnabled(gzipEnabled: Boolean) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            httpConf.getHttpProxyUser(),
            httpConf.getHttpProxyPassword(),
            httpConf.getHttpProxyPort(),
            httpConf.isHttpProxySocks(),
            httpConf.getHttpConnectionTimeout(),
            httpConf.getHttpReadTimeout(),
            httpConf.isPrettyDebugEnabled(), gzipEnabled
        )
    }

    // methods for HttpClientConfiguration

    fun setHttpProxyHost(proxyHost: String?) {
        httpConf = MyHttpClientConfiguration(
            proxyHost,
            httpConf.getHttpProxyUser(),
            httpConf.getHttpProxyPassword(),
            httpConf.getHttpProxyPort(),
            httpConf.isHttpProxySocks(),
            httpConf.getHttpConnectionTimeout(),
            httpConf.getHttpReadTimeout(),
            httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        )
    }

    fun setHttpProxyUser(proxyUser: String?) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            proxyUser,
            httpConf.getHttpProxyPassword(),
            httpConf.getHttpProxyPort(),
            httpConf.isHttpProxySocks(),
            httpConf.getHttpConnectionTimeout(),
            httpConf.getHttpReadTimeout(),
            httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        )
    }

    fun setHttpProxyPassword(proxyPassword: String?) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            httpConf.getHttpProxyUser(),
            proxyPassword,
            httpConf.getHttpProxyPort(),
            httpConf.isHttpProxySocks(),
            httpConf.getHttpConnectionTimeout(),
            httpConf.getHttpReadTimeout(),
            httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        )
    }

    fun setHttpProxyPort(proxyPort: Int) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            httpConf.getHttpProxyUser(),
            httpConf.getHttpProxyPassword(),
            proxyPort,
            httpConf.isHttpProxySocks(),
            httpConf.getHttpConnectionTimeout(),
            httpConf.getHttpReadTimeout(),
            httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        )
    }

    fun setHttpProxySocks(isSocksProxy: Boolean) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            httpConf.getHttpProxyUser(),
            httpConf.getHttpProxyPassword(),
            httpConf.getHttpProxyPort(),
            isSocksProxy,
            httpConf.getHttpConnectionTimeout(),
            httpConf.getHttpReadTimeout(),
            httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        )
    }

    fun setHttpConnectionTimeout(connectionTimeout: Int) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            httpConf.getHttpProxyUser(),
            httpConf.getHttpProxyPassword(),
            httpConf.getHttpProxyPort(),
            httpConf.isHttpProxySocks(),
            connectionTimeout,
            httpConf.getHttpReadTimeout(),
            httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        )
    }

    fun setHttpReadTimeout(readTimeout: Int) {
        httpConf = MyHttpClientConfiguration(
            httpConf.getHttpProxyHost(),
            httpConf.getHttpProxyUser(),
            httpConf.getHttpProxyPassword(),
            httpConf.getHttpProxyPort(),
            httpConf.isHttpProxySocks(),
            httpConf.getHttpConnectionTimeout(),
            readTimeout,
            httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        )
    }

    override fun getHttpStreamingReadTimeout(): Int {
        return httpStreamingReadTimeout
    }

    fun setHttpStreamingReadTimeout(httpStreamingReadTimeout: Int) {
        this.httpStreamingReadTimeout = httpStreamingReadTimeout
    }

    fun setHttpRetryCount(retryCount: Int) {
        this.httpRetryCount = retryCount
    }

    fun setHttpRetryIntervalSeconds(retryIntervalSeconds: Int) {
        this.httpRetryIntervalSeconds = retryIntervalSeconds
    }

    // oauth related setter/getters

    override fun getOAuth2Scope(): String? {
        return oAuth2Scope
    }

    fun setOAuth2Scope(oAuth2Scope: String?) {
        this.oAuth2Scope = oAuth2Scope
    }

    override fun getAsyncNumThreads(): Int {
        return asyncNumThreads
    }

    fun setAsyncNumThreads(asyncNumThreads: Int) {
        this.asyncNumThreads = asyncNumThreads
    }

    override fun getContributingTo(): Long {
        return contributingTo
    }

    fun setContributingTo(contributingTo: Long) {
        this.contributingTo = contributingTo
    }

    override fun getRestBaseURL(): String {
        return restBaseURL
    }

    fun setRestBaseURL(restBaseURL: String) {
        this.restBaseURL = restBaseURL
    }

    override fun getUploadBaseURL(): String {
        return uploadBaseURL
    }

    fun setUploadBaseURL(uploadBaseURL: String) {
        this.uploadBaseURL = uploadBaseURL
    }

    override fun getStreamBaseURL(): String {
        return streamBaseURL
    }

    fun setStreamBaseURL(streamBaseURL: String) {
        this.streamBaseURL = streamBaseURL
    }

    override fun getUserStreamBaseURL(): String {
        return userStreamBaseURL
    }

    fun setUserStreamBaseURL(userStreamBaseURL: String) {
        this.userStreamBaseURL = userStreamBaseURL
    }

    override fun getSiteStreamBaseURL(): String {
        return siteStreamBaseURL
    }

    fun setSiteStreamBaseURL(siteStreamBaseURL: String) {
        this.siteStreamBaseURL = siteStreamBaseURL
    }

    override fun getOAuthRequestTokenURL(): String {
        return oAuthRequestTokenURL
    }

    fun setOAuthRequestTokenURL(oAuthRequestTokenURL: String) {
        this.oAuthRequestTokenURL = oAuthRequestTokenURL
    }

    override fun getOAuthAuthorizationURL(): String {
        return oAuthAuthorizationURL
    }

    fun setOAuthAuthorizationURL(oAuthAuthorizationURL: String) {
        this.oAuthAuthorizationURL = oAuthAuthorizationURL
    }

    override fun getOAuthAccessTokenURL(): String {
        return oAuthAccessTokenURL
    }

    fun setOAuthAccessTokenURL(oAuthAccessTokenURL: String) {
        this.oAuthAccessTokenURL = oAuthAccessTokenURL
    }

    override fun getOAuthAuthenticationURL(): String {
        return oAuthAuthenticationURL
    }

    fun setOAuthAuthenticationURL(oAuthAuthenticationURL: String) {
        this.oAuthAuthenticationURL = oAuthAuthenticationURL
    }

    override fun getOAuth2TokenURL(): String {
        return oAuth2TokenURL
    }

    fun setOAuth2TokenURL(oAuth2TokenURL: String) {
        this.oAuth2TokenURL = oAuth2TokenURL
    }

    override fun getOAuth2InvalidateTokenURL(): String {
        return oAuth2InvalidateTokenURL
    }

    fun setOAuth2InvalidateTokenURL(oAuth2InvalidateTokenURL: String) {
        this.oAuth2InvalidateTokenURL = oAuth2InvalidateTokenURL
    }

    override fun isIncludeEntitiesEnabled(): Boolean {
        return includeEntitiesEnabled
    }

    fun setIncludeEntitiesEnabled(includeEntitiesEnabled: Boolean) {
        this.includeEntitiesEnabled = includeEntitiesEnabled
    }

    override fun isIncludeMyRetweetEnabled(): Boolean {
        return this.includeMyRetweetEnabled
    }

    fun setIncludeMyRetweetEnabled(enabled: Boolean) {
        this.includeMyRetweetEnabled = enabled
    }

    override fun isTrimUserEnabled(): Boolean {
        return this.trimUserEnabled
    }

    override fun isIncludeExtAltTextEnabled(): Boolean {
        return this.includeExtAltTextEnabled
    }

    override fun isTweetModeExtended(): Boolean {
        return this.tweetModeExtended
    }

    override fun isIncludeExtEditControl(): Boolean {
        return this.includeExtEditControl
    }

    override fun isDaemonEnabled(): Boolean {
        return daemonEnabled
    }

    fun setDaemonEnabled(daemonEnabled: Boolean) {
        this.daemonEnabled = daemonEnabled
    }

    override fun isIncludeEmailEnabled(): Boolean {
        return includeEmailEnabled
    }

    fun setIncludeEmailEnabled(includeEmailEnabled: Boolean) {
        this.includeEmailEnabled = includeEmailEnabled
    }

    fun setTrimUserEnabled(enabled: Boolean) {
        this.trimUserEnabled = enabled
    }

    fun setIncludeExtAltTextEnabled(enabled: Boolean) {
        this.includeExtAltTextEnabled = enabled
    }

    fun setTweetModeExtended(enabled: Boolean) {
        this.tweetModeExtended = enabled
    }

    fun setIncludeExtEditControl(enabled: Boolean) {
        this.includeExtEditControl = enabled
    }

    override val isJSONStoreEnabled: Boolean
        get() = this.jsonStoreEnabled

    fun setJSONStoreEnabled(enabled: Boolean) {
        this.jsonStoreEnabled = enabled
    }

    override fun isMBeanEnabled(): Boolean {
        return this.mbeanEnabled
    }

    fun setMBeanEnabled(enabled: Boolean) {
        this.mbeanEnabled = enabled
    }

    override fun isUserStreamRepliesAllEnabled(): Boolean {
        return this.userStreamRepliesAllEnabled
    }

    override fun isUserStreamWithFollowingsEnabled(): Boolean {
        return this.userStreamWithFollowingsEnabled
    }

    fun setUserStreamRepliesAllEnabled(enabled: Boolean) {
        this.userStreamRepliesAllEnabled = enabled
    }

    fun setUserStreamWithFollowingsEnabled(enabled: Boolean) {
        this.userStreamWithFollowingsEnabled = enabled
    }

    override fun isStallWarningsEnabled(): Boolean {
        return stallWarningsEnabled
    }

    fun setStallWarningsEnabled(stallWarningsEnabled: Boolean) {
        this.stallWarningsEnabled = stallWarningsEnabled
    }

    override fun isApplicationOnlyAuthEnabled(): Boolean {
        return applicationOnlyAuthEnabled
    }

    fun setApplicationOnlyAuthEnabled(applicationOnlyAuthEnabled: Boolean) {
        this.applicationOnlyAuthEnabled = applicationOnlyAuthEnabled
    }

    override fun getMediaProvider(): String {
        return this.mediaProvider
    }

    fun setMediaProvider(mediaProvider: String) {
        this.mediaProvider = mediaProvider
    }

    override fun getMediaProviderAPIKey(): String? {
        return this.mediaProviderAPIKey
    }

    fun setMediaProviderAPIKey(mediaProviderAPIKey: String?) {
        this.mediaProviderAPIKey = mediaProviderAPIKey
    }

    override fun getMediaProviderParameters(): Properties? {
        return this.mediaProviderParameters
    }

    fun setMediaProviderParameters(props: Properties?) {
        this.mediaProviderParameters = props
    }

    override fun getStreamThreadName(): String {
        return this.streamThreadName
    }

    fun setStreamThreadName(streamThreadName: String) {
        this.streamThreadName = streamThreadName
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as ConfigurationBase

        if (debug != that.debug) return false
        if (httpStreamingReadTimeout != that.httpStreamingReadTimeout) return false
        if (httpRetryCount != that.httpRetryCount) return false
        if (httpRetryIntervalSeconds != that.httpRetryIntervalSeconds) return false
        if (asyncNumThreads != that.asyncNumThreads) return false
        if (contributingTo != that.contributingTo) return false
        if (includeMyRetweetEnabled != that.includeMyRetweetEnabled) return false
        if (includeEntitiesEnabled != that.includeEntitiesEnabled) return false
        if (trimUserEnabled != that.trimUserEnabled) return false
        if (includeExtAltTextEnabled != that.includeExtAltTextEnabled) return false
        if (tweetModeExtended != that.tweetModeExtended) return false
        if (includeExtEditControl != that.includeExtEditControl) return false
        if (includeEmailEnabled != that.includeEmailEnabled) return false
        if (jsonStoreEnabled != that.jsonStoreEnabled) return false
        if (mbeanEnabled != that.mbeanEnabled) return false
        if (userStreamRepliesAllEnabled != that.userStreamRepliesAllEnabled) return false
        if (userStreamWithFollowingsEnabled != that.userStreamWithFollowingsEnabled) return false
        if (stallWarningsEnabled != that.stallWarningsEnabled) return false
        if (applicationOnlyAuthEnabled != that.applicationOnlyAuthEnabled) return false
        if (daemonEnabled != that.daemonEnabled) return false
        if (user != that.user) return false
        if (password != that.password) return false
        if (httpConf != that.httpConf) return false
        if (oAuthConsumerKey != that.oAuthConsumerKey) return false
        if (oAuthConsumerSecret != that.oAuthConsumerSecret) return false
        if (oAuthAccessToken != that.oAuthAccessToken) return false
        if (oAuthAccessTokenSecret != that.oAuthAccessTokenSecret) return false
        if (oAuth2TokenType != that.oAuth2TokenType) return false
        if (oAuth2AccessToken != that.oAuth2AccessToken) return false
        if (oAuth2Scope != that.oAuth2Scope) return false
        if (oAuthRequestTokenURL != that.oAuthRequestTokenURL) return false
        if (oAuthAuthorizationURL != that.oAuthAuthorizationURL) return false
        if (oAuthAccessTokenURL != that.oAuthAccessTokenURL) return false
        if (oAuthAuthenticationURL != that.oAuthAuthenticationURL) return false
        if (oAuth2TokenURL != that.oAuth2TokenURL) return false
        if (oAuth2InvalidateTokenURL != that.oAuth2InvalidateTokenURL) return false
        if (restBaseURL != that.restBaseURL) return false
        if (streamBaseURL != that.streamBaseURL) return false
        if (userStreamBaseURL != that.userStreamBaseURL) return false
        if (siteStreamBaseURL != that.siteStreamBaseURL) return false
        if (uploadBaseURL != that.uploadBaseURL) return false
        if (mediaProvider != that.mediaProvider) return false
        if (mediaProviderAPIKey != that.mediaProviderAPIKey) return false
        if (mediaProviderParameters != that.mediaProviderParameters) return false
        return streamThreadName == that.streamThreadName
    }

    override fun hashCode(): Int {
        var result = if (debug) 1 else 0
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + httpConf.hashCode()
        result = 31 * result + httpStreamingReadTimeout
        result = 31 * result + httpRetryCount
        result = 31 * result + httpRetryIntervalSeconds
        result = 31 * result + (oAuthConsumerKey?.hashCode() ?: 0)
        result = 31 * result + (oAuthConsumerSecret?.hashCode() ?: 0)
        result = 31 * result + (oAuthAccessToken?.hashCode() ?: 0)
        result = 31 * result + (oAuthAccessTokenSecret?.hashCode() ?: 0)
        result = 31 * result + (oAuth2TokenType?.hashCode() ?: 0)
        result = 31 * result + (oAuth2AccessToken?.hashCode() ?: 0)
        result = 31 * result + (oAuth2Scope?.hashCode() ?: 0)
        result = 31 * result + (oAuthRequestTokenURL?.hashCode() ?: 0)
        result = 31 * result + (oAuthAuthorizationURL?.hashCode() ?: 0)
        result = 31 * result + (oAuthAccessTokenURL?.hashCode() ?: 0)
        result = 31 * result + (oAuthAuthenticationURL?.hashCode() ?: 0)
        result = 31 * result + (oAuth2TokenURL?.hashCode() ?: 0)
        result = 31 * result + (oAuth2InvalidateTokenURL?.hashCode() ?: 0)
        result = 31 * result + (restBaseURL?.hashCode() ?: 0)
        result = 31 * result + (streamBaseURL?.hashCode() ?: 0)
        result = 31 * result + (userStreamBaseURL?.hashCode() ?: 0)
        result = 31 * result + (siteStreamBaseURL?.hashCode() ?: 0)
        result = 31 * result + (uploadBaseURL?.hashCode() ?: 0)
        result = 31 * result + asyncNumThreads
        result = 31 * result + (contributingTo xor (contributingTo ushr 32)).toInt()
        result = 31 * result + (if (includeMyRetweetEnabled) 1 else 0)
        result = 31 * result + (if (includeEntitiesEnabled) 1 else 0)
        result = 31 * result + (if (trimUserEnabled) 1 else 0)
        result = 31 * result + (if (includeExtAltTextEnabled) 1 else 0)
        result = 31 * result + (if (tweetModeExtended) 1 else 0)
        result = 31 * result + (if (includeExtEditControl) 1 else 0)
        result = 31 * result + (if (includeEmailEnabled) 1 else 0)
        result = 31 * result + (if (jsonStoreEnabled) 1 else 0)
        result = 31 * result + (if (mbeanEnabled) 1 else 0)
        result = 31 * result + (if (userStreamRepliesAllEnabled) 1 else 0)
        result = 31 * result + (if (userStreamWithFollowingsEnabled) 1 else 0)
        result = 31 * result + (if (stallWarningsEnabled) 1 else 0)
        result = 31 * result + (if (applicationOnlyAuthEnabled) 1 else 0)
        result = 31 * result + (mediaProvider?.hashCode() ?: 0)
        result = 31 * result + (mediaProviderAPIKey?.hashCode() ?: 0)
        result = 31 * result + (mediaProviderParameters?.hashCode() ?: 0)
        result = 31 * result + (if (daemonEnabled) 1 else 0)
        result = 31 * result + (streamThreadName?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ConfigurationBase{" +
                "debug=" + debug +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", httpConf=" + httpConf +
                ", httpStreamingReadTimeout=" + httpStreamingReadTimeout +
                ", httpRetryCount=" + httpRetryCount +
                ", httpRetryIntervalSeconds=" + httpRetryIntervalSeconds +
                ", oAuthConsumerKey='" + oAuthConsumerKey + '\'' +
                ", oAuthConsumerSecret='" + oAuthConsumerSecret + '\'' +
                ", oAuthAccessToken='" + oAuthAccessToken + '\'' +
                ", oAuthAccessTokenSecret='" + oAuthAccessTokenSecret + '\'' +
                ", oAuth2TokenType='" + oAuth2TokenType + '\'' +
                ", oAuth2AccessToken='" + oAuth2AccessToken + '\'' +
                ", oAuth2Scope='" + oAuth2Scope + '\'' +
                ", oAuthRequestTokenURL='" + oAuthRequestTokenURL + '\'' +
                ", oAuthAuthorizationURL='" + oAuthAuthorizationURL + '\'' +
                ", oAuthAccessTokenURL='" + oAuthAccessTokenURL + '\'' +
                ", oAuthAuthenticationURL='" + oAuthAuthenticationURL + '\'' +
                ", oAuth2TokenURL='" + oAuth2TokenURL + '\'' +
                ", oAuth2InvalidateTokenURL='" + oAuth2InvalidateTokenURL + '\'' +
                ", restBaseURL='" + restBaseURL + '\'' +
                ", streamBaseURL='" + streamBaseURL + '\'' +
                ", userStreamBaseURL='" + userStreamBaseURL + '\'' +
                ", siteStreamBaseURL='" + siteStreamBaseURL + '\'' +
                ", uploadBaseURL='" + uploadBaseURL + '\'' +
                ", asyncNumThreads=" + asyncNumThreads +
                ", contributingTo=" + contributingTo +
                ", includeMyRetweetEnabled=" + includeMyRetweetEnabled +
                ", includeEntitiesEnabled=" + includeEntitiesEnabled +
                ", trimUserEnabled=" + trimUserEnabled +
                ", includeExtAltTextEnabled=" + includeExtAltTextEnabled +
                ", tweetModeExtended=" + tweetModeExtended +
                ", includeExtEditControl=" + includeExtEditControl +
                ", includeEmailEnabled=" + includeEmailEnabled +
                ", jsonStoreEnabled=" + jsonStoreEnabled +
                ", mbeanEnabled=" + mbeanEnabled +
                ", userStreamRepliesAllEnabled=" + userStreamRepliesAllEnabled +
                ", userStreamWithFollowingsEnabled=" + userStreamWithFollowingsEnabled +
                ", stallWarningsEnabled=" + stallWarningsEnabled +
                ", applicationOnlyAuthEnabled=" + applicationOnlyAuthEnabled +
                ", mediaProvider='" + mediaProvider + '\'' +
                ", mediaProviderAPIKey='" + mediaProviderAPIKey + '\'' +
                ", mediaProviderParameters=" + mediaProviderParameters +
                ", daemonEnabled=" + daemonEnabled +
                ", streamThreadName='" + streamThreadName + '\'' +
                '}'
    }

    // assures equality after deserialization
    @Throws(ObjectStreamException::class)
    protected open fun readResolve(): Any {
        return getInstance(this)
    }

    fun cacheInstance() {
        cacheInstance(this)
    }

    companion object {
        private const val serialVersionUID = 6175546394599249696L

        @JvmStatic
        fun fixURL(useSSL: Boolean, url: String?): String? {
            if (null == url) {
                return null
            }
            val index = url.indexOf("://")
            require(-1 != index) { "url should contain '://'" }
            val hostAndLater = url.substring(index + 3)
            return if (useSSL) {
                "https://$hostAndLater"
            } else {
                "http://$hostAndLater"
            }
        }

        private val instances = ArrayList<ConfigurationBase>()

        private fun cacheInstance(conf: ConfigurationBase) {
            if (!instances.contains(conf)) {
                instances.add(conf)
            }
        }

        private fun getInstance(configurationBase: ConfigurationBase): ConfigurationBase {
            val index = instances.indexOf(configurationBase)
            return if (index == -1) {
                instances.add(configurationBase)
                configurationBase
            } else {
                instances[index]
            }
        }
    }
}
