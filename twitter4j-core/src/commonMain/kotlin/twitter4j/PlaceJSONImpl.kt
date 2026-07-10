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
 * @since Twitter4J 2.1.1
 */
internal class PlaceJSONImpl : TwitterResponseImpl, Place {
    // 元実装では各文字列/配列が null になりうるが、Place の各プロパティは非nullのため
    // 文字列は "" 、配列は空配列を既定値とする。
    override var name: String = ""
        private set
    override var streetAddress: String = ""
        private set
    override var countryCode: String = ""
        private set
    override var id: String = ""
        private set
    override var country: String = ""
        private set
    override var placeType: String = ""
        private set
    override var URL: String = ""
        private set
    override var fullName: String = ""
        private set
    override var boundingBoxType: String = ""
        private set
    override var boundingBoxCoordinates: Array<Array<GeoLocation>> = emptyArray()
        private set
    override var geometryType: String = ""
        private set
    override var geometryCoordinates: Array<Array<GeoLocation>> = emptyArray()
        private set
    override var containedWithIn: Array<Place> = emptyArray()
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : super(res) {
        val json = res.asJSONObject()
        init(json)
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
            TwitterObjectFactory.registerJSONObject(this, json)
        }
    }

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        init(json)
    }

    /* For serialization purposes only. */
    constructor() : super()

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        try {
            name = ParseUtil.getUnescapedString("name", json) ?: ""
            streetAddress = ParseUtil.getUnescapedString("street_address", json) ?: ""
            countryCode = ParseUtil.getRawString("country_code", json) ?: ""
            id = ParseUtil.getRawString("id", json) ?: ""
            country = ParseUtil.getRawString("country", json) ?: ""
            placeType = if (!json.isNull("place_type")) {
                ParseUtil.getRawString("place_type", json) ?: ""
            } else {
                ParseUtil.getRawString("type", json) ?: ""
            }
            URL = ParseUtil.getRawString("url", json) ?: ""
            fullName = ParseUtil.getRawString("full_name", json) ?: ""
            if (!json.isNull("bounding_box")) {
                val boundingBoxJSON = json.getJSONObject("bounding_box")
                boundingBoxType = ParseUtil.getRawString("type", boundingBoxJSON) ?: ""
                val array = boundingBoxJSON.getJSONArray("coordinates")
                boundingBoxCoordinates = GeoLocationJSONImplFactory.coordinatesAsGeoLocationArray(array)
            } else {
                boundingBoxType = ""
                boundingBoxCoordinates = emptyArray()
            }

            if (!json.isNull("geometry")) {
                val geometryJSON = json.getJSONObject("geometry")
                geometryType = ParseUtil.getRawString("type", geometryJSON) ?: ""
                val array = geometryJSON.getJSONArray("coordinates")
                when (geometryType) {
                    "Point" -> {
                        geometryCoordinates = arrayOf(arrayOf(GeoLocation(array.getDouble(1), array.getDouble(0))))
                    }
                    "Polygon" -> {
                        geometryCoordinates = GeoLocationJSONImplFactory.coordinatesAsGeoLocationArray(array)
                    }
                    else -> {
                        // MultiPolygon currently unsupported.
                        geometryType = ""
                        geometryCoordinates = emptyArray()
                    }
                }
            } else {
                geometryType = ""
                geometryCoordinates = emptyArray()
            }

            if (!json.isNull("contained_within")) {
                val containedWithInJSON = json.getJSONArray("contained_within")
                containedWithIn = Array<Place>(containedWithInJSON.length()) { i ->
                    PlaceJSONImpl(containedWithInJSON.getJSONObject(i))
                }
            } else {
                containedWithIn = emptyArray()
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone.message + ":" + json.toString(), jsone)
        }
    }

    override fun compareTo(other: Place): Int {
        return this.id.compareTo(other.id)
    }

    override fun equals(obj: Any?): Boolean {
        if (null == obj) {
            return false
        }
        if (this === obj) {
            return true
        }
        return obj is Place && obj.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "PlaceJSONImpl{" +
                "name='" + name + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", id='" + id + '\'' +
                ", country='" + country + '\'' +
                ", placeType='" + placeType + '\'' +
                ", url='" + URL + '\'' +
                ", fullName='" + fullName + '\'' +
                ", boundingBoxType='" + boundingBoxType + '\'' +
                ", boundingBoxCoordinates=" + boundingBoxCoordinates.asList() +
                ", geometryType='" + geometryType + '\'' +
                ", geometryCoordinates=" + geometryCoordinates.asList() +
                ", containedWithIn=" + containedWithIn.asList() +
                '}'
    }

    companion object {
        private const val serialVersionUID = -6368276880878829754L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createPlaceList(res: HttpResponse, conf: JsonStoreConfiguration): ResponseList<Place> {
            var json: JSONObject? = null
            try {
                json = res.asJSONObject()
                return createPlaceList(json.getJSONObject("result").getJSONArray("places"), res, conf)
            } catch (jsone: JSONException) {
                throw TwitterException(jsone.message + ":" + json.toString(), jsone)
            }
        }

        @JvmStatic
        @Throws(TwitterException::class)
        fun createPlaceList(list: JSONArray, res: HttpResponse, conf: JsonStoreConfiguration): ResponseList<Place> {
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
            }
            try {
                val size = list.length()
                val places: ResponseList<Place> = ResponseListImpl(size, res)
                for (i in 0 until size) {
                    val json = list.getJSONObject(i)
                    val place: Place = PlaceJSONImpl(json)
                    places.add(place)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(place, json)
                    }
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(places, list)
                }
                return places
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}
