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

/**
 * An exception class that will be thrown when TwitterAPI calls are failed.<br></br>
 * In case the Twitter server returned HTTP error code, you can get the HTTP status code using getStatusCode() method.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
actual open class TwitterException : Exception, TwitterResponse, HttpResponseCode {
    // expect 側は read-only の `val` として宣言しているため、内部可変は private backing field で保持する。
    // JVMの公開シグネチャ（getStatusCode() 等）は従来通り維持される。
    private var _statusCode = -1
    actual val statusCode: Int
        get() = _statusCode
    private var _errorCode = -1
    actual val errorCode: Int
        get() = _errorCode
    private var exceptionDiagnosis: ExceptionDiagnosis? = null
    private var response: HttpResponse? = null
    private var _errorMessage: String? = null
    actual val errorMessage: String?
        get() = _errorMessage

    private var nested = false

    actual constructor(message: String?, cause: Throwable?) : super(message, cause) {
        decode(message)
    }

    actual constructor(message: String?) : this(message, null as Throwable?)

    actual constructor(cause: Exception) : this(cause.message, cause) {
        if (cause is TwitterException) {
            cause.setNested()
        }
    }

    actual constructor(message: String?, res: HttpResponse) : this(message) {
        response = res
        this._statusCode = res.getStatusCode()
    }

    actual constructor(message: String?, cause: Exception?, statusCode: Int) : this(message, cause as Throwable?) {
        this._statusCode = statusCode
    }

    actual constructor(message: String?, errorCode: Int) : this(message) {
        this._errorMessage = message
        this._errorCode = errorCode
    }

    actual override val message: String
        get() {
            val value = StringBuilder()
            if (errorMessage != null && errorCode != -1) {
                value.append("message - ").append(errorMessage)
                    .append("\n")
                value.append("code - ").append(errorCode)
                    .append("\n")
            } else {
                value.append(super.message)
            }
            return if (statusCode != -1) {
                getCause(statusCode) + "\n" + value.toString()
            } else {
                value.toString()
            }
        }

    private fun decode(str: String?) {
        if (str != null && str.startsWith("{")) {
            try {
                val json = JSONObject(str)
                if (!json.isNull("errors")) {
                    val error = json.getJSONArray("errors").getJSONObject(0)
                    this._errorMessage = error.getString("message")
                    this._errorCode = ParseUtil.getInt("code", error)
                }
            } catch (ignore: JSONException) {
            }
        }
    }

    actual fun getResponseHeader(name: String): String? {
        var value: String? = null
        val res = response
        if (res != null) {
            val header = res.getResponseHeaderFields()!![name]
            if (header!!.size > 0) {
                value = header[0]
            }
        }
        return value
    }

    actual override val rateLimitStatus: RateLimitStatus?
        get() {
            if (null == response) {
                return null
            }
            return JSONImplFactory.createRateLimitStatusFromResponseHeader(response!!)
        }

    actual override val accessLevel: Int
        get() = ParseUtil.toAccessLevel(response)

    /**
     * Returns int value of "Retry-After" response header (Search API) or seconds_until_reset (REST API).
     * An application that exceeds the rate limitations of the Search API will receive HTTP 420 response codes to requests. It is a best
     * practice to watch for this error condition and honor the Retry-After header that instructs the application when it is safe to
     * continue. The Retry-After header's value is the number of seconds your application should wait before submitting another query (for
     * example: Retry-After: 67).<br></br>
     * Check if getStatusCode() == 503 before calling this method to ensure that you are actually exceeding rate limitation with query
     * apis.<br></br>
     *
     * @return instructs the application when it is safe to continue in seconds
     * @see <a href="https://dev.twitter.com/docs/rate-limiting">Rate Limiting | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    fun getRetryAfter(): Int {
        var retryAfter = -1
        if (this.statusCode == 400) {
            val rateLimitStatus = this.rateLimitStatus
            if (rateLimitStatus != null) {
                retryAfter = rateLimitStatus.secondsUntilReset
            }
        } else if (this.statusCode == HttpResponseCode.ENHANCE_YOUR_CLAIM) {
            try {
                val retryAfterStr = response!!.getResponseHeader("Retry-After")
                if (retryAfterStr != null) {
                    retryAfter = retryAfterStr.toInt()
                }
            } catch (ignore: NumberFormatException) {
            }
        }
        return retryAfter
    }

    /**
     * Tests if the exception is caused by network issue
     *
     * @return if the exception is caused by network issue
     * @since Twitter4J 2.1.2
     */
    val isCausedByNetworkIssue: Boolean
        get() = cause is java.io.IOException

    /**
     * Tests if the exception is caused by rate limitation exceed
     *
     * @return if the exception is caused by rate limitation exceed
     * @see <a href="https://dev.twitter.com/docs/rate-limiting">Rate Limiting | Twitter Developers</a>
     * @since Twitter4J 2.1.2
     */
    fun exceededRateLimitation(): Boolean {
        return (statusCode == 400 && rateLimitStatus != null) // REST API
                || (statusCode == HttpResponseCode.ENHANCE_YOUR_CLAIM) // Streaming API
                || (statusCode == HttpResponseCode.TOO_MANY_REQUESTS) // API 1.1
    }

    /**
     * Tests if the exception is caused by non-existing resource
     *
     * @return if the exception is caused by non-existing resource
     * @since Twitter4J 2.1.2
     */
    actual fun resourceNotFound(): Boolean {
        return statusCode == HttpResponseCode.NOT_FOUND
    }

    /**
     * Returns a hexadecimal representation of this exception stacktrace.<br></br>
     * An exception code is a hexadecimal representation of the stacktrace which enables it easier to Google known issues.<br></br>
     * Format : XXXXXXXX:YYYYYYYY[ XX:YY]<br></br>
     * Where XX is a hash code of stacktrace without line number<br></br>
     * YY is a hash code of stacktrace excluding line number<br></br>
     * [-XX:YY] will appear when this instance a root cause
     *
     * @return a hexadecimal representation of this exception stacktrace
     */
    fun getExceptionCode(): String {
        return getExceptionDiagnosis().asHexString()
    }

    private fun getExceptionDiagnosis(): ExceptionDiagnosis {
        if (null == exceptionDiagnosis) {
            exceptionDiagnosis = ExceptionDiagnosis(this, FILTER)
        }
        return exceptionDiagnosis!!
    }

    private fun setNested() {
        nested = true
    }

    /**
     * Tests if error message from the API is available
     *
     * @return true if error message from the API is available
     * @since Twitter4J 2.2.3
     */
    val isErrorMessageAvailable: Boolean
        get() = errorMessage != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TwitterException

        if (errorCode != that.errorCode) return false
        if (nested != that.nested) return false
        if (statusCode != that.statusCode) return false
        if (errorMessage != that.errorMessage) return false
        if (exceptionDiagnosis != that.exceptionDiagnosis) return false
        if (response != that.response) return false

        return true
    }

    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + errorCode
        result = 31 * result + (exceptionDiagnosis?.hashCode() ?: 0)
        result = 31 * result + (response?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (if (nested) 1 else 0)
        return result
    }

    override fun toString(): String {
        return message + (if (nested) "" else "\nRelevant discussions can be found on the Internet at:\n"
                + "\thttp://www.google.co.jp/search?q=" + getExceptionDiagnosis().getStackLineHashAsHex()
                + " or\n\thttp://www.google.co.jp/search?q=" + getExceptionDiagnosis().getLineNumberHashAsHex()) +
                "\nTwitterException{" + (if (nested) "" else "exceptionCode=[" + getExceptionCode() + "], ") +
                "statusCode=" + statusCode +
                ", message=" + errorMessage +
                ", code=" + errorCode +
                ", retryAfter=" + getRetryAfter() +
                ", rateLimitStatus=" + rateLimitStatus +
                ", version=" + Version.version +
                '}'
    }

    companion object {
        private const val serialVersionUID = 6006561839051121336L

        private val FILTER = arrayOf("twitter4j")

        private fun getCause(statusCode: Int): String {
            val cause: String
            // https://dev.twitter.com/docs/error-codes-responses
            when (statusCode) {
                HttpResponseCode.NOT_MODIFIED -> cause = "There was no new data to return."
                HttpResponseCode.BAD_REQUEST -> cause = "The request was invalid. An accompanying error message will explain why. This is the status code will be returned during version 1.0 rate limiting(https://dev.twitter.com/pages/rate-limiting). In API v1.1, a request without authentication is considered invalid and you will get this response."
                HttpResponseCode.UNAUTHORIZED -> cause = "Authentication credentials (https://dev.twitter.com/pages/auth) were missing or incorrect. Ensure that you have set valid consumer key/secret, access token/secret, and the system clock is in sync."
                HttpResponseCode.FORBIDDEN -> cause = "The request is understood, but it has been refused. An accompanying error message will explain why. This code is used when requests are being denied due to update limits (https://support.twitter.com/articles/15364-about-twitter-limits-update-api-dm-and-following)."
                HttpResponseCode.NOT_FOUND -> cause = "The URI requested is invalid or the resource requested, such as a user, does not exists. Also returned when the requested format is not supported by the requested method."
                HttpResponseCode.NOT_ACCEPTABLE -> cause = "Returned by the Search API when an invalid format is specified in the request.\n" +
                        "Returned by the Streaming API when one or more of the parameters are not suitable for the resource. The track parameter, for example, would throw this error if:\n" +
                        " The track keyword is too long or too short.\n" +
                        " The bounding box specified is invalid.\n" +
                        " No predicates defined for filtered resource, for example, neither track nor follow parameter defined.\n" +
                        " Follow userid cannot be read."
                HttpResponseCode.ENHANCE_YOUR_CLAIM -> cause = "Returned by the Search and Trends API when you are being rate limited (https://dev.twitter.com/docs/rate-limiting).\n" +
                        "Returned by the Streaming API:\n Too many login attempts in a short period of time.\n" +
                        " Running too many copies of the same application authenticating with the same account name."
                HttpResponseCode.UNPROCESSABLE_ENTITY -> cause = "Returned when an image uploaded to POST account/update_profile_banner(https://dev.twitter.com/docs/api/1/post/account/update_profile_banner) is unable to be processed."
                HttpResponseCode.TOO_MANY_REQUESTS -> cause = "Returned in API v1.1 when a request cannot be served due to the application's rate limit having been exhausted for the resource. See Rate Limiting in API v1.1.(https://dev.twitter.com/docs/rate-limiting/1.1)"
                HttpResponseCode.INTERNAL_SERVER_ERROR -> cause = "Something is broken. Please post to the group (https://dev.twitter.com/docs/support) so the Twitter team can investigate."
                HttpResponseCode.BAD_GATEWAY -> cause = "Twitter is down or being upgraded."
                HttpResponseCode.SERVICE_UNAVAILABLE -> cause = "The Twitter servers are up, but overloaded with requests. Try again later."
                HttpResponseCode.GATEWAY_TIMEOUT -> cause = "The Twitter servers are up, but the request couldn't be serviced due to some failure within our stack. Try again later."
                else -> cause = ""
            }
            return "$statusCode:$cause"
        }
    }
}
