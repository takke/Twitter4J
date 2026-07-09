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
 * @author Andrew Hedges - andrew.hedges at gmail.com
 */
class HttpResponseEvent internal constructor(
    /**
     * returns the request associated with the event
     *
     * @return the request associated with the event
     */
    val request: HttpRequest,
    /**
     * returns the response associated with the event
     *
     * @return the response associated with the event
     */
    val response: HttpResponse?,
    /**
     * returns the TwitterException associated with the event
     *
     * @return the TwitterException associated with the event
     */
    val twitterException: TwitterException?
) {

    fun isAuthenticated(): Boolean {
        return request.authorization!!.isEnabled()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as HttpResponseEvent

        if (request != that.request)
            return false
        if (if (response != null) response != that.response else that.response != null)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = request.hashCode()
        result = 31 * result + (response?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("HttpResponseEvent{" +
                "request=" + request +
                ", response=" + response +
                '}')
    }
}
