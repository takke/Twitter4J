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

/**
 * A utility class to handle HTTP request/response.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
interface HttpClient {

    fun addDefaultRequestHeader(name: String, value: String)

    fun getRequestHeaders(): Map<String, String>

    @Throws(TwitterException::class)
    fun request(req: HttpRequest): HttpResponse

    @Throws(TwitterException::class)
    fun request(req: HttpRequest, listener: HttpResponseListener?): HttpResponse

    @Throws(TwitterException::class)
    fun get(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse

    @Throws(TwitterException::class)
    fun get(url: String): HttpResponse

    @Throws(TwitterException::class)
    fun post(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse

    @Throws(TwitterException::class)
    fun post(url: String): HttpResponse

    @Throws(TwitterException::class)
    fun delete(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse

    @Throws(TwitterException::class)
    fun delete(url: String): HttpResponse

    @Throws(TwitterException::class)
    fun head(url: String): HttpResponse

    @Throws(TwitterException::class)
    fun put(
        url: String, parameters: Array<HttpParameter>?,
        authorization: Authorization?, listener: HttpResponseListener?
    ): HttpResponse

    @Throws(TwitterException::class)
    fun put(url: String): HttpResponse
}
