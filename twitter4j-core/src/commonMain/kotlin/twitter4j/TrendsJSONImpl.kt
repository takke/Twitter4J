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

import kotlin.jvm.JvmStatic
import kotlin.time.Instant
import twitter4j.conf.JsonStoreConfiguration

/**
 * A data class representing Trends.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.0.2
 */
internal class TrendsJSONImpl : TwitterResponseImpl, Trends {
    override var asOf: Instant = Instant.fromEpochMilliseconds(0)
        private set
    override var trendAt: Instant = Instant.fromEpochMilliseconds(0)
        private set
    override var trends: Array<Trend> = emptyArray()
        private set
    // 元実装では location が null になりうる(Search API系のトレンドでは null)が、Trends.location は
    // 非nullのため内部で nullable 保持し、公開時に非nullとして返す。
    private var _location: Location? = null

    override val location: Location
        get() = _location!!

    override fun compareTo(other: Trends): Int {
        return this.trendAt.compareTo(other.trendAt)
    }

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : super(res) {
        init(res.asString(), conf.isJSONStoreEnabled)
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
            TwitterObjectFactory.registerJSONObject(this, res.asString())
        }
    }

    @Throws(TwitterException::class)
    constructor(jsonStr: String) : this(jsonStr, false)

    @Throws(TwitterException::class)
    constructor(jsonStr: String, storeJSON: Boolean) : super() {
        init(jsonStr, storeJSON)
    }

    private constructor(asOf: Instant, location: Location?, trendAt: Instant, trends: Array<Trend>) : super() {
        this.asOf = asOf
        this._location = location
        this.trendAt = trendAt
        this.trends = trends
    }

    @Throws(TwitterException::class)
    private fun init(jsonStr: String, storeJSON: Boolean) {
        try {
            val json: JSONObject
            if (jsonStr.startsWith("[")) {
                val array = JSONArray(jsonStr)
                json = if (array.length() > 0) {
                    array.getJSONObject(0)
                } else {
                    throw TwitterException("No trends found on the specified woeid")
                }
            } else {
                json = JSONObject(jsonStr)
            }
            this.asOf = ParseUtil.parseTrendsDate(json.getString("as_of"))
            this._location = extractLocation(json, storeJSON)
            val array = json.getJSONArray("trends")
            this.trendAt = asOf
            this.trends = jsonArrayToTrendArray(array, storeJSON)
        } catch (jsone: JSONException) {
            throw TwitterException(jsone.message, jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Trends) return false

        val trends1 = o

        if (asOf != trends1.asOf) return false
        if (trendAt != trends1.trendAt) return false
        if (!trends.contentEquals(trends1.trends)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = asOf.hashCode()
        result = 31 * result + trendAt.hashCode()
        result = 31 * result + trends.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "TrendsJSONImpl{" +
                "asOf=" + asOf +
                ", trendAt=" + trendAt +
                ", trends=" + trends.asList() +
                '}'
    }

    companion object {
        private const val serialVersionUID = 2054973282133379835L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createTrendsList(res: HttpResponse, storeJSON: Boolean): ResponseList<Trends> {
            val json = res.asJSONObject()
            val trends: ResponseList<Trends>
            try {
                val asOf = ParseUtil.parseTrendsDate(json.getString("as_of"))
                val trendsJson = json.getJSONObject("trends")
                val location = extractLocation(json, storeJSON)
                trends = ResponseListImpl(trendsJson.length(), res)
                val ite = trendsJson.keys()
                while (ite.hasNext()) {
                    val key = ite.next() as String
                    val array = trendsJson.getJSONArray(key)
                    val trendsArray = jsonArrayToTrendArray(array, storeJSON)
                    if (key.length == 19) {
                        // current trends
                        trends.add(TrendsJSONImpl(asOf, location, ParseUtil.getInstant(key, "yyyy-MM-dd HH:mm:ss"), trendsArray))
                    } else if (key.length == 16) {
                        // daily trends
                        trends.add(TrendsJSONImpl(asOf, location, ParseUtil.getInstant(key, "yyyy-MM-dd HH:mm"), trendsArray))
                    } else if (key.length == 10) {
                        // weekly trends
                        trends.add(TrendsJSONImpl(asOf, location, ParseUtil.getInstant(key, "yyyy-MM-dd"), trendsArray))
                    }
                }
                // KMP移行 Phase 5-3: java.util.Collections.sort → MutableList.sort()（Trends は Comparable<Trends>）。
                // 自然順序による安定ソートで、旧 Collections.sort と同一挙動。
                trends.sort()
                return trends
            } catch (jsone: JSONException) {
                throw TwitterException(jsone.message + ":" + res.asString(), jsone)
            }
        }

        @JvmStatic
        @Throws(TwitterException::class)
        private fun extractLocation(json: JSONObject, storeJSON: Boolean): Location? {
            if (json.isNull("locations")) {
                return null
            }
            val locations: ResponseList<Location>
            try {
                locations = LocationJSONImpl.createLocationList(json.getJSONArray("locations"), storeJSON)
            } catch (e: JSONException) {
                throw AssertionError("locations can't be null")
            }
            return if (0 != locations.size) {
                locations[0]
            } else {
                null
            }
        }

        @JvmStatic
        @Throws(JSONException::class)
        private fun jsonArrayToTrendArray(array: JSONArray, storeJSON: Boolean): Array<Trend> {
            return Array(array.length()) { i ->
                TrendJSONImpl(array.getJSONObject(i), storeJSON)
            }
        }
    }
}
