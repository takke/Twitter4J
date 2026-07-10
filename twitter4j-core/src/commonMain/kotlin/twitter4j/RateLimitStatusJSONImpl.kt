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

@file:OptIn(ExperimentalTime::class)

package twitter4j

import kotlin.jvm.JvmStatic
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import twitter4j.conf.JsonStoreConfiguration

/**
 * A data class representing Twitter REST API's rate limit status
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see <a href="https://dev.twitter.com/docs/rate-limiting">Rate Limiting | Twitter Developers</a>
 */
class RateLimitStatusJSONImpl : RateLimitStatus, Serializable {

    override var remaining: Int = 0
        private set
    override var limit: Int = 0
        private set
    override var resetTimeInSeconds: Int = 0
        private set
    override var secondsUntilReset: Int = 0
        private set

    private constructor(limit: Int, remaining: Int, resetTimeInSeconds: Int) {
        this.limit = limit
        this.remaining = remaining
        this.resetTimeInSeconds = resetTimeInSeconds
        this.secondsUntilReset = ((resetTimeInSeconds * 1000L - Clock.System.now().toEpochMilliseconds()) / 1000).toInt()
    }

    @Throws(TwitterException::class)
    internal constructor(json: JSONObject) {
        init(json)
    }

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        this.limit = ParseUtil.getInt("limit", json)
        this.remaining = ParseUtil.getInt("remaining", json)
        this.resetTimeInSeconds = ParseUtil.getInt("reset", json)
        this.secondsUntilReset = ((resetTimeInSeconds * 1000L - Clock.System.now().toEpochMilliseconds()) / 1000).toInt()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || this::class != o::class) return false

        val that = o as RateLimitStatusJSONImpl

        if (limit != that.limit) return false
        if (remaining != that.remaining) return false
        if (resetTimeInSeconds != that.resetTimeInSeconds) return false
        if (secondsUntilReset != that.secondsUntilReset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = remaining
        result = 31 * result + limit
        result = 31 * result + resetTimeInSeconds
        result = 31 * result + secondsUntilReset
        return result
    }

    override fun toString(): String {
        return "RateLimitStatusJSONImpl{" +
                "remaining=" + remaining +
                ", limit=" + limit +
                ", resetTimeInSeconds=" + resetTimeInSeconds +
                ", secondsUntilReset=" + secondsUntilReset +
                '}'
    }

    companion object {
        private const val serialVersionUID = 7790337632915862445L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createRateLimitStatuses(res: HttpResponse, conf: JsonStoreConfiguration): Map<String, RateLimitStatus> {
            val json = res.asJSONObject()
            val map = createRateLimitStatuses(json)
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
                TwitterObjectFactory.registerJSONObject(map, json)
            }
            return map
        }

        @JvmStatic
        @Throws(TwitterException::class)
        fun createRateLimitStatuses(json: JSONObject): Map<String, RateLimitStatus> {
            val map: MutableMap<String, RateLimitStatus> = HashMap()
            try {
                val resources = json.getJSONObject("resources")
                val resourceKeys = resources.keys()
                while (resourceKeys.hasNext()) {
                    val resource = resources.getJSONObject(resourceKeys.next())
                    val endpointKeys = resource.keys()
                    while (endpointKeys.hasNext()) {
                        val endpoint = endpointKeys.next()
                        val rateLimitStatusJSON = resource.getJSONObject(endpoint)
                        val rateLimitStatus: RateLimitStatus = RateLimitStatusJSONImpl(rateLimitStatusJSON)
                        map[endpoint] = rateLimitStatus
                    }
                }
                return map.toMap()
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }

        @JvmStatic
        fun createFromResponseHeader(res: HttpResponse?): RateLimitStatus? {
            if (null == res) {
                return null
            }
            val remainingHits: Int //"X-Rate-Limit-Remaining"
            val limit: Int //"X-Rate-Limit-Limit"
            val resetTimeInSeconds: Int //not included in the response header. Need to be calculated.

            val strLimit = res.getResponseHeader("X-Rate-Limit-Limit")
            limit = if (strLimit != null) {
                strLimit.toInt()
            } else {
                return null
            }
            val remaining = res.getResponseHeader("X-Rate-Limit-Remaining")
            remainingHits = if (remaining != null) {
                remaining.toInt()
            } else {
                return null
            }
            val reset = res.getResponseHeader("X-Rate-Limit-Reset")
            if (reset != null) {
                val longReset = reset.toLong()
                resetTimeInSeconds = longReset.toInt()
            } else {
                return null
            }
            return RateLimitStatusJSONImpl(limit, remainingHits, resetTimeInSeconds)
        }
    }
}
