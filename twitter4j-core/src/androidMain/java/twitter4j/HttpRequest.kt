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

import twitter4j.auth.Authorization
import java.util.Arrays

/**
 * HTTP Request parameter object
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 *
 * @param method         Specifies the HTTP method
 * @param url            the request to request
 * @param parameters     parameters
 * @param authorization  Authentication implementation. Currently BasicAuthentication, OAuthAuthentication and NullAuthentication are supported.
 * @param requestHeaders request headers
 */
class HttpRequest(
    val method: RequestMethod,
    url: String,
    parameters: Array<HttpParameter>?,
    val authorization: Authorization?,
    val requestHeaders: Map<String, String>?
) : java.io.Serializable {

    @get:JvmName("getURL")
    val url: String

    val parameters: Array<HttpParameter>?

    init {
        if (method != RequestMethod.POST && parameters != null && parameters.isNotEmpty()) {
            this.url = url + "?" + HttpParameter.encodeParameters(parameters)
            this.parameters = NULL_PARAMETERS
        } else {
            this.url = url
            this.parameters = parameters
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as HttpRequest

        if (if (authorization != null) authorization != that.authorization else that.authorization != null)
            return false
        if (!Arrays.equals(parameters, that.parameters)) return false
        if (if (requestHeaders != null) requestHeaders != that.requestHeaders else that.requestHeaders != null)
            return false
        if (method != that.method)
            return false
        if (url != that.url)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (if (parameters != null) Arrays.hashCode(parameters) else 0)
        result = 31 * result + (authorization?.hashCode() ?: 0)
        result = 31 * result + (requestHeaders?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("HttpRequest{" +
                "requestMethod=" + method +
                ", url='" + url + '\'' +
                ", postParams=" + (if (parameters == null) null else Arrays.asList(*parameters)) +
                ", authentication=" + authorization +
                ", requestHeaders=" + requestHeaders +
                '}')
    }

    companion object {
        private const val serialVersionUID = 3365496352032493020L

        private val NULL_PARAMETERS = arrayOf<HttpParameter>()
    }
}
