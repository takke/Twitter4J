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

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.Collections
import java.util.Random

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import twitter4j.BASE64Encoder
import twitter4j.HttpClient
import twitter4j.HttpClientFactory
import twitter4j.HttpParameter
import twitter4j.HttpRequest
import twitter4j.Logger
import twitter4j.TwitterException
import twitter4j.conf.Configuration

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see <a href="http://oauth.net/core/1.0a/">OAuth Core 1.0a</a>
 */
class OAuthAuthorization(private val conf: Configuration) : Authorization, Serializable, OAuthSupport {

    private var consumerKey: String? = ""
    private var consumerSecret: String? = null

    private var realm: String? = null

    private var oauthToken: OAuthToken? = null

    // constructors

    /**
     * @param conf configuration
     */
    init {
        http = HttpClientFactory.getInstance(conf.getHttpClientConfiguration())
        setOAuthConsumer(conf.oAuthConsumerKey, conf.oAuthConsumerSecret)
        if (conf.oAuthAccessToken != null && conf.oAuthAccessTokenSecret != null) {
            setOAuthAccessToken(AccessToken(conf.oAuthAccessToken!!, conf.oAuthAccessTokenSecret!!))
        }
    }

    // implementations for Authorization
    override fun getAuthorizationHeader(req: HttpRequest): String? {
        return generateAuthorizationHeader(req.method.name, req.url, req.parameters, oauthToken)
    }

    private fun ensureTokenIsAvailable() {
        if (null == oauthToken) {
            throw IllegalStateException("No Token available.")
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun isEnabled(): Boolean {
        return oauthToken != null && oauthToken is AccessToken
    }

    override fun getAdditionalHeaders(): Map<String, String>? {
        return null
    }

    // implementation for OAuthSupport interface

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(): RequestToken {
        return getOAuthRequestToken(null, null, null)
    }

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(callbackURL: String?): RequestToken {
        return getOAuthRequestToken(callbackURL, null, null)
    }

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(callbackURL: String?, xAuthAccessType: String?): RequestToken {
        return getOAuthRequestToken(callbackURL, xAuthAccessType, null)
    }

    @Throws(TwitterException::class)
    override fun getOAuthRequestToken(callbackURL: String?, xAuthAccessType: String?, xAuthMode: String?): RequestToken {
        if (oauthToken is AccessToken) {
            throw IllegalStateException("Access token already available.")
        }
        val params = ArrayList<HttpParameter>()
        if (callbackURL != null) {
            params.add(HttpParameter("oauth_callback", callbackURL))
        }
        if (xAuthAccessType != null) {
            params.add(HttpParameter("x_auth_access_type", xAuthAccessType))
        }
        if (xAuthMode != null) {
            params.add(HttpParameter("x_auth_mode", xAuthMode))
        }
        oauthToken = RequestToken(http!!.post(conf.getOAuthRequestTokenURL(), params.toTypedArray(), this, null), this)
        return oauthToken as RequestToken
    }

    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(): AccessToken {
        ensureTokenIsAvailable()
        if (oauthToken is AccessToken) {
            return oauthToken as AccessToken
        }
        oauthToken = AccessToken(http!!.post(conf.getOAuthAccessTokenURL(), null, this, null))
        return oauthToken as AccessToken
    }

    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(oauthVerifier: String?): AccessToken {
        ensureTokenIsAvailable()
        oauthToken = AccessToken(
            http!!.post(
                conf.getOAuthAccessTokenURL(),
                arrayOf(HttpParameter("oauth_verifier", oauthVerifier)), this, null
            )
        )
        return oauthToken as AccessToken
    }

    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(requestToken: RequestToken?): AccessToken {
        this.oauthToken = requestToken
        return getOAuthAccessToken()
    }

    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(requestToken: RequestToken?, oauthVerifier: String?): AccessToken {
        this.oauthToken = requestToken
        return getOAuthAccessToken(oauthVerifier)
    }

    @Throws(TwitterException::class)
    override fun getOAuthAccessToken(screenName: String?, password: String?): AccessToken {
        try {
            var url = conf.getOAuthAccessTokenURL()
            if (0 == url.indexOf("http://")) {
                // SSL is required
                // @see https://dev.twitter.com/docs/oauth/xauth
                url = "https://" + url.substring(7)
            }
            oauthToken = AccessToken(
                http!!.post(
                    url, arrayOf(
                        HttpParameter("x_auth_username", screenName),
                        HttpParameter("x_auth_password", password),
                        HttpParameter("x_auth_mode", "client_auth")
                    ), this, null
                )
            )
            return oauthToken as AccessToken
        } catch (te: TwitterException) {
            throw TwitterException("The screen name / password combination seems to be invalid.", te, te.statusCode)
        }
    }

    override fun setOAuthAccessToken(accessToken: AccessToken?) {
        this.oauthToken = accessToken
    }

    /**
     * Sets the OAuth realm
     *
     * @param realm OAuth realm
     * @since Twitter 2.1.4
     */
    fun setOAuthRealm(realm: String?) {
        this.realm = realm
    }


    internal fun generateAuthorizationHeader(method: String, url: String, params: Array<HttpParameter>?, nonce: String, timestamp: String, otoken: OAuthToken?): String {
        val paramsNonNull = params ?: arrayOf()
        val oauthHeaderParams = ArrayList<HttpParameter>(5)
        oauthHeaderParams.add(HttpParameter("oauth_consumer_key", consumerKey))
        oauthHeaderParams.add(OAUTH_SIGNATURE_METHOD)
        oauthHeaderParams.add(HttpParameter("oauth_timestamp", timestamp))
        oauthHeaderParams.add(HttpParameter("oauth_nonce", nonce))
        oauthHeaderParams.add(HttpParameter("oauth_version", "1.0"))
        if (otoken != null) {
            oauthHeaderParams.add(HttpParameter("oauth_token", otoken.token))
        }
        val signatureBaseParams = ArrayList<HttpParameter>(oauthHeaderParams.size + paramsNonNull.size)
        signatureBaseParams.addAll(oauthHeaderParams)
        if (!HttpParameter.containsFile(paramsNonNull)) {
            signatureBaseParams.addAll(toParamList(paramsNonNull))
        }
        parseGetParameters(url, signatureBaseParams)
        val base = StringBuilder(method).append("&")
            .append(HttpParameter.encode(constructRequestURL(url))).append("&")
        base.append(HttpParameter.encode(normalizeRequestParameters(signatureBaseParams)))
        val oauthBaseString = base.toString()
        logger.debug("OAuth base string: ", oauthBaseString)
        val signature = generateSignature(oauthBaseString, otoken)
        logger.debug("OAuth signature: ", signature)

        oauthHeaderParams.add(HttpParameter("oauth_signature", signature))

        // http://oauth.net/core/1.0/#rfc.section.9.1.1
        if (realm != null) {
            oauthHeaderParams.add(HttpParameter("realm", realm))
        }
        return "OAuth " + encodeParameters(oauthHeaderParams, ",", true)
    }

    private fun parseGetParameters(url: String, signatureBaseParams: MutableList<HttpParameter>) {
        val queryStart = url.indexOf("?")
        if (-1 != queryStart) {
            val queryStrs = url.substring(queryStart + 1).split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            try {
                for (query in queryStrs) {
                    val split = query.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (split.size == 2) {
                        signatureBaseParams.add(
                            HttpParameter(
                                URLDecoder.decode(split[0], "UTF-8"),
                                URLDecoder.decode(split[1], "UTF-8")
                            )
                        )
                    } else {
                        signatureBaseParams.add(
                            HttpParameter(URLDecoder.decode(split[0], "UTF-8"), "")
                        )
                    }
                }
            } catch (ignore: UnsupportedEncodingException) {
            }

        }

    }

    /**
     * @return generated authorization header
     * @see <a href="http://oauth.net/core/1.0a/#rfc.section.5.4.1">OAuth Core - 5.4.1.  Authorization Header</a>
     */
    internal fun generateAuthorizationHeader(method: String, url: String, params: Array<HttpParameter>?, token: OAuthToken?): String {
        val timestamp = System.currentTimeMillis() / 1000
        val nonce = timestamp + RAND.nextInt()
        return generateAuthorizationHeader(method, url, params, nonce.toString(), timestamp.toString(), token)
    }

    fun generateOAuthSignatureHttpParams(method: String, url: String): List<HttpParameter> {
        val timestamp = System.currentTimeMillis() / 1000
        val nonce = timestamp + RAND.nextInt()

        val oauthHeaderParams = ArrayList<HttpParameter>(5)
        oauthHeaderParams.add(HttpParameter("oauth_consumer_key", consumerKey))
        oauthHeaderParams.add(OAUTH_SIGNATURE_METHOD)
        oauthHeaderParams.add(HttpParameter("oauth_timestamp", timestamp))
        oauthHeaderParams.add(HttpParameter("oauth_nonce", nonce))
        oauthHeaderParams.add(HttpParameter("oauth_version", "1.0"))
        if (oauthToken != null) {
            oauthHeaderParams.add(HttpParameter("oauth_token", oauthToken!!.token))
        }

        val signatureBaseParams = ArrayList<HttpParameter>(oauthHeaderParams.size)
        signatureBaseParams.addAll(oauthHeaderParams)
        parseGetParameters(url, signatureBaseParams)

        val base = StringBuilder(method).append("&")
            .append(HttpParameter.encode(constructRequestURL(url))).append("&")
        base.append(HttpParameter.encode(normalizeRequestParameters(signatureBaseParams)))

        val oauthBaseString = base.toString()
        val signature = generateSignature(oauthBaseString, oauthToken)

        oauthHeaderParams.add(HttpParameter("oauth_signature", signature))

        return oauthHeaderParams
    }

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data  the data to be signed
     * @param token the token
     * @return signature
     * @see <a href="http://oauth.net/core/1.0a/#rfc.section.9.2.1">OAuth Core - 9.2.1.  Generating Signature</a>
     */
    internal fun generateSignature(data: String, token: OAuthToken?): String {
        val byteHMAC: ByteArray
        try {
            val mac = Mac.getInstance(HMAC_SHA1)
            val spec: SecretKeySpec
            if (null == token) {
                val oauthSignature = HttpParameter.encode(consumerSecret) + "&"
                spec = SecretKeySpec(oauthSignature.toByteArray(), HMAC_SHA1)
            } else {
                var s = token.secretKeySpec
                if (null == s) {
                    val oauthSignature = HttpParameter.encode(consumerSecret) + "&" + HttpParameter.encode(token.tokenSecret)
                    s = SecretKeySpec(oauthSignature.toByteArray(), HMAC_SHA1)
                    token.secretKeySpec = s
                }
                spec = s
            }
            mac.init(spec)
            byteHMAC = mac.doFinal(data.toByteArray())
        } catch (ike: InvalidKeyException) {
            logger.error("Failed initialize \"Message Authentication Code\" (MAC)", ike)
            throw AssertionError(ike)
        } catch (nsae: NoSuchAlgorithmException) {
            logger.error("Failed to get HmacSHA1 \"Message Authentication Code\" (MAC)", nsae)
            throw AssertionError(nsae)
        }
        return BASE64Encoder.encode(byteHMAC)
    }

    internal fun generateSignature(data: String): String {
        return generateSignature(data, null)
    }


    override fun setOAuthConsumer(consumerKey: String?, consumerSecret: String?) {
        this.consumerKey = if (consumerKey != null) consumerKey else ""
        this.consumerSecret = if (consumerSecret != null) consumerSecret else ""
    }


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is OAuthSupport) return false

        val that = o as OAuthAuthorization

        if (if (consumerKey != null) consumerKey != that.consumerKey else that.consumerKey != null)
            return false
        if (if (consumerSecret != null) consumerSecret != that.consumerSecret else that.consumerSecret != null)
            return false
        if (if (oauthToken != null) oauthToken != that.oauthToken else that.oauthToken != null)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = consumerKey?.hashCode() ?: 0
        result = 31 * result + (consumerSecret?.hashCode() ?: 0)
        result = 31 * result + (oauthToken?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "OAuthAuthorization{" +
                "consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='******************************************\'" +
                ", oauthToken=" + oauthToken +
                '}'
    }

    companion object {
        private const val serialVersionUID = -886869424811858868L

        @Transient
        private var http: HttpClient? = null

        private const val HMAC_SHA1 = "HmacSHA1"
        private val OAUTH_SIGNATURE_METHOD = HttpParameter("oauth_signature_method", "HMAC-SHA1")
        private val logger = Logger.getLogger(OAuthAuthorization::class.java)

        private val RAND = Random()

        /**
         * The request parameters are collected, sorted and concatenated into a normalized string.
         *
         * @param params parameters to be normalized and concatenated
         * @return normalized and concatenated parameters
         * @see <a href="http://oauth.net/core/1.0#rfc.section.9.1.1">OAuth Core - 9.1.1.  Normalize Request Parameters</a>
         */
        @JvmStatic
        internal fun normalizeRequestParameters(params: Array<HttpParameter>): String {
            return normalizeRequestParameters(toParamList(params))
        }

        private fun normalizeRequestParameters(params: MutableList<HttpParameter>): String {
            Collections.sort(params)
            return encodeParameters(params)
        }

        private fun toParamList(params: Array<HttpParameter>): MutableList<HttpParameter> {
            val paramList = ArrayList<HttpParameter>(params.size)
            paramList.addAll(params.asList())
            return paramList
        }

        /**
         * @param httpParams parameters to be encoded and concatenated
         * @return encoded string
         * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
         * @see <a href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space encoding - OAuth | Google Groups</a>
         */
        @JvmStatic
        fun encodeParameters(httpParams: List<HttpParameter>): String {
            return encodeParameters(httpParams, "&", false)
        }

        @JvmStatic
        fun encodeParameters(httpParams: List<HttpParameter>, splitter: String, quot: Boolean): String {
            val buf = StringBuilder()
            for (param in httpParams) {
                if (!param.isFile() && !param.isJson()) {
                    if (buf.length != 0) {
                        if (quot) {
                            buf.append("\"")
                        }
                        buf.append(splitter)
                    }
                    buf.append(HttpParameter.encode(param.name)).append("=")
                    if (quot) {
                        buf.append("\"")
                    }
                    buf.append(HttpParameter.encode(param.value))
                }
            }
            if (buf.length != 0) {
                if (quot) {
                    buf.append("\"")
                }
            }
            return buf.toString()
        }

        /**
         * The Signature Base String includes the request absolute URL, tying the signature to a specific endpoint.
         *
         * @param url the url to be normalized
         * @return the Signature Base String
         * @see <a href="http://oauth.net/core/1.0#rfc.section.9.1.2">OAuth Core - 9.1.2.  Construct Request URL</a>
         */
        @JvmStatic
        internal fun constructRequestURL(url: String): String {
            var u = url
            val index = u.indexOf("?")
            if (-1 != index) {
                u = u.substring(0, index)
            }
            val slashIndex = u.indexOf("/", 8)
            var baseURL = u.substring(0, slashIndex).lowercase()
            val colonIndex = baseURL.indexOf(":", 8)
            if (-1 != colonIndex) {
                // url contains port number
                if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
                    // http default port 80 MUST be excluded
                    baseURL = baseURL.substring(0, colonIndex)
                } else if (baseURL.startsWith("https://") && baseURL.endsWith(":443")) {
                    // http default port 443 MUST be excluded
                    baseURL = baseURL.substring(0, colonIndex)
                }
            }
            u = baseURL + u.substring(slashIndex)

            return u
        }
    }
}
