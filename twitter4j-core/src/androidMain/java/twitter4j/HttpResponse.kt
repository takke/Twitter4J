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

import twitter4j.conf.ConfigurationContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.UnsupportedEncodingException

/**
 * A data class representing HTTP Response
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
abstract class HttpResponse {

    @JvmField
    protected val CONF: HttpClientConfiguration?

    protected constructor() {
        this.CONF = ConfigurationContext.getInstance().getHttpClientConfiguration()
    }

    constructor(conf: HttpClientConfiguration?) {
        this.CONF = conf
    }

    @JvmField
    protected var statusCode: Int = 0

    @JvmField
    protected var responseAsString: String? = null

    @JvmField
    protected var `is`: InputStream? = null

    private var streamConsumed = false

    fun getStatusCode(): Int {
        return statusCode
    }

    abstract fun getResponseHeader(name: String): String?

    abstract fun getResponseHeaderFields(): MutableMap<String, MutableList<String>>?

    /**
     * Returns the response stream.<br></br>
     * This method cannot be called after calling asString() or asDcoument()<br></br>
     * It is suggested to call disconnect() after consuming the stream.
     *
     *
     * Disconnects the internal HttpURLConnection silently.
     *
     * @return response body stream
     * @see .disconnect
     */
    open fun asStream(): InputStream? {
        if (streamConsumed) {
            throw IllegalStateException("Stream has already been consumed.")
        }
        return `is`
    }

    /**
     * Returns the response body as string.<br></br>
     * Disconnects the internal HttpURLConnection silently.
     *
     * @return response body
     * @throws TwitterException when there is any network issue upon response body consumption
     */
    @Throws(TwitterException::class)
    open fun asString(): String {
        if (null == responseAsString) {
            var br: BufferedReader? = null
            var stream: InputStream? = null
            try {
                stream = asStream()
                if (null == stream) {
                    return ""
                }
                br = BufferedReader(InputStreamReader(stream, "UTF-8"))
                val buf = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    buf.append(line).append("\n")
                }
                this.responseAsString = buf.toString()
                logger.debug(responseAsString)
                stream.close()
                streamConsumed = true
            } catch (ioe: IOException) {
                throw TwitterException(ioe.message, ioe)
            } finally {
                if (stream != null) {
                    try {
                        stream.close()
                    } catch (ignore: IOException) {
                    }
                }
                if (br != null) {
                    try {
                        br.close()
                    } catch (ignore: IOException) {
                    }
                }
                disconnectForcibly()
            }
        }
        return responseAsString!!
    }

    private var json: JSONObject? = null

    /**
     * Returns the response body as twitter4j.JSONObject.<br></br>
     * Disconnects the internal HttpURLConnection silently.
     *
     * @return response body as twitter4j.JSONObject
     * @throws TwitterException when the response body is not in JSON Object format
     */
    @Throws(TwitterException::class)
    open fun asJSONObject(): JSONObject {
        if (json == null) {
            try {
                json = JSONObject(asString())
                if (CONF!!.isPrettyDebugEnabled()) {
                    logger.debug(json!!.toString(1))
                } else {
                    logger.debug(
                        if (responseAsString != null) responseAsString
                        else json!!.toString()
                    )
                }
            } catch (jsone: JSONException) {
                if (responseAsString == null) {
                    throw TwitterException(jsone.message, jsone)
                } else {
                    throw TwitterException(jsone.message + ":[" + this.responseAsString + "]", jsone)
                }
            } finally {
                disconnectForcibly()
            }
        }
        return json!!
    }

    private var jsonArray: JSONArray? = null

    /**
     * Returns the response body as twitter4j.JSONArray.<br></br>
     * Disconnects the internal HttpURLConnection silently.
     *
     * @return response body as twitter4j.JSONArray
     * @throws TwitterException when the response body is not in JSON Array format
     */
    @Throws(TwitterException::class)
    open fun asJSONArray(): JSONArray {
        if (jsonArray == null) {
            try {
                jsonArray = JSONArray(asString())
                if (CONF!!.isPrettyDebugEnabled()) {
                    logger.debug(jsonArray!!.toString(1))
                } else {
                    logger.debug(
                        if (responseAsString != null) responseAsString
                        else jsonArray!!.toString()
                    )
                }
            } catch (jsone: JSONException) {
                if (logger.isDebugEnabled()) {
                    throw TwitterException(jsone.message + ":[" + this.responseAsString + "]", jsone)
                } else {
                    throw TwitterException(jsone.message, jsone)
                }
            } finally {
                disconnectForcibly()
            }
        }
        return jsonArray!!
    }

    open fun asReader(): Reader {
        return try {
            BufferedReader(InputStreamReader(`is`, "UTF-8"))
        } catch (uee: UnsupportedEncodingException) {
            InputStreamReader(`is`)
        }
    }

    private fun disconnectForcibly() {
        try {
            disconnect()
        } catch (ignore: Exception) {
        }
    }

    @Throws(IOException::class)
    abstract fun disconnect()

    override fun toString(): String {
        return ("HttpResponse{" +
                "statusCode=" + statusCode +
                ", responseAsString='" + responseAsString + '\'' +
                ", is=" + `is` +
                ", streamConsumed=" + streamConsumed +
                '}')
    }

    companion object {
        private val logger = Logger.getLogger(HttpResponseImpl::class.java)
    }
}
