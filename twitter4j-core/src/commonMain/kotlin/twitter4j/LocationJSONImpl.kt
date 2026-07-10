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
import twitter4j.conf.JsonStoreConfiguration

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
@Suppress("PropertyName")
internal class LocationJSONImpl
@Throws(TwitterException::class)
constructor(location: JSONObject) : Location {

    override val woeid: Int
    override val countryName: String
    override val countryCode: String
    override val placeName: String
    override val placeCode: Int
    override val name: String
    override val URL: String

    init {
        try {
            woeid = ParseUtil.getInt("woeid", location)
            // 元実装では各文字列は不在時 null になりうるが、Location の各プロパティは非nullのため "" とする。
            countryName = ParseUtil.getUnescapedString("country", location) ?: ""
            countryCode = ParseUtil.getRawString("countryCode", location) ?: ""
            if (!location.isNull("placeType")) {
                val placeJSON = location.getJSONObject("placeType")
                placeName = ParseUtil.getUnescapedString("name", placeJSON) ?: ""
                placeCode = ParseUtil.getInt("code", placeJSON)
            } else {
                placeName = ""
                placeCode = -1
            }
            name = ParseUtil.getUnescapedString("name", location) ?: ""
            URL = ParseUtil.getUnescapedString("url", location) ?: ""
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is LocationJSONImpl) return false

        val that = o

        if (woeid != that.woeid) return false

        return true
    }

    override fun hashCode(): Int {
        return woeid
    }

    override fun toString(): String {
        return "LocationJSONImpl{" +
                "woeid=" + woeid +
                ", countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeCode='" + placeCode + '\'' +
                ", name='" + name + '\'' +
                ", url='" + URL + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = -1312752311160422264L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createLocationList(res: HttpResponse, conf: JsonStoreConfiguration): ResponseList<Location> {
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
            }
            return createLocationList(res.asJSONArray(), conf.isJSONStoreEnabled)
        }

        @JvmStatic
        @Throws(TwitterException::class)
        fun createLocationList(list: JSONArray, storeJSON: Boolean): ResponseList<Location> {
            try {
                val size = list.length()
                val locations: ResponseList<Location> =
                    ResponseListImpl(size, null)
                for (i in 0 until size) {
                    val json = list.getJSONObject(i)
                    val location: Location = LocationJSONImpl(json)
                    locations.add(location)
                    if (storeJSON) {
                        TwitterObjectFactory.registerJSONObject(location, json)
                    }
                }
                if (storeJSON) {
                    TwitterObjectFactory.registerJSONObject(locations, list)
                }
                return locations
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}
