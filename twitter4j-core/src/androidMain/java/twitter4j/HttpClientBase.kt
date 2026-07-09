package twitter4j

import twitter4j.auth.Authorization
import java.io.DataOutputStream
import java.io.IOException
import java.util.HashMap

abstract class HttpClientBase(
    @JvmField
    protected val CONF: HttpClientConfiguration
) : HttpClient, Serializable {

    private val requestHeaders: MutableMap<String, String> = HashMap()

    init {
        requestHeaders.put("X-Twitter-Client-Version", Version.version)
        requestHeaders.put("X-Twitter-Client-URL", "http://twitter4j.org/en/twitter4j-" + Version.version + ".xml")
        requestHeaders.put("X-Twitter-Client", "Twitter4J")
        requestHeaders.put("User-Agent", "twitter4j http://twitter4j.org/ /" + Version.version)
        if (CONF.isGZIPEnabled()) {
            requestHeaders.put("Accept-Encoding", "gzip")
        }
    }

    protected fun isProxyConfigured(): Boolean {
        return CONF.getHttpProxyHost() != null && CONF.getHttpProxyHost() != ""
    }

    @Throws(IOException::class)
    fun write(out: DataOutputStream, outStr: String) {
        out.writeBytes(outStr)
        logger.debug(outStr)
    }

    override fun getRequestHeaders(): Map<String, String> {
        return requestHeaders
    }

    override fun addDefaultRequestHeader(name: String, value: String) {
        requestHeaders.put(name, value)
    }

    @Throws(TwitterException::class)
    final override fun request(req: HttpRequest): HttpResponse {
        return handleRequest(req)
    }

    @Throws(TwitterException::class)
    final override fun request(req: HttpRequest, listener: HttpResponseListener?): HttpResponse {
        try {
            val res = handleRequest(req)
            if (listener != null) {
                listener.httpResponseReceived(HttpResponseEvent(req, res, null))
            }
            return res
        } catch (te: TwitterException) {
            if (listener != null) {
                listener.httpResponseReceived(HttpResponseEvent(req, null, te))
            }
            throw te
        }
    }

    @Throws(TwitterException::class)
    protected abstract fun handleRequest(req: HttpRequest): HttpResponse

    @Throws(TwitterException::class)
    override fun get(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse {
        return request(HttpRequest(RequestMethod.GET, url, parameters, authorization, this.requestHeaders), listener)
    }

    @Throws(TwitterException::class)
    override fun get(url: String): HttpResponse {
        return request(HttpRequest(RequestMethod.GET, url, null, null, this.requestHeaders))
    }

    @Throws(TwitterException::class)
    override fun post(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse {
        return request(HttpRequest(RequestMethod.POST, url, parameters, authorization, this.requestHeaders), listener)
    }

    @Throws(TwitterException::class)
    override fun post(url: String): HttpResponse {
        return request(HttpRequest(RequestMethod.POST, url, null, null, this.requestHeaders))
    }

    @Throws(TwitterException::class)
    override fun delete(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse {
        return request(HttpRequest(RequestMethod.DELETE, url, parameters, authorization, this.requestHeaders), listener)
    }

    @Throws(TwitterException::class)
    override fun delete(url: String): HttpResponse {
        return request(HttpRequest(RequestMethod.DELETE, url, null, null, this.requestHeaders))
    }

    @Throws(TwitterException::class)
    override fun head(url: String): HttpResponse {
        return request(HttpRequest(RequestMethod.HEAD, url, null, null, this.requestHeaders))
    }

    @Throws(TwitterException::class)
    override fun put(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse {
        return request(HttpRequest(RequestMethod.PUT, url, parameters, authorization, this.requestHeaders), listener)
    }

    @Throws(TwitterException::class)
    override fun put(url: String): HttpResponse {
        return request(HttpRequest(RequestMethod.PUT, url, null, null, this.requestHeaders))
    }

    companion object {
        private val logger = Logger.getLogger(HttpClientBase::class.java)

        private const val serialVersionUID = -8016974810651763053L
    }
}
