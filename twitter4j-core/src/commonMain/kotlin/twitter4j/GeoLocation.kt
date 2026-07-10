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
 * A data class representing geo location.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class GeoLocation
/**
 * Creates a GeoLocation instance
 *
 * @param latitude  the latitude
 * @param longitude the longitude
 */
    (
    /**
     * returns the latitude of the geo location
     *
     * @return the latitude
     */
    val latitude: Double,
    /**
     * returns the longitude of the geo location
     *
     * @return the longitude
     */
    val longitude: Double
) : Serializable {

    /* For serialization purposes only. */
    internal constructor() : this(0.0, 0.0)

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is GeoLocation) return false

        val that = o

        // java.lang.Double.compare 相当を Kotlin common の Double.compareTo で置換（NaN/±0 の扱いも同等）
        if (that.latitude.compareTo(latitude) != 0) return false
        if (that.longitude.compareTo(longitude) != 0) return false

        return true
    }

    override fun hashCode(): Int {
        val result: Int
        var temp: Long
        // java.lang.Double.doubleToLongBits 相当を Kotlin common の Double.toBits で置換（NaN正規化も同等）
        temp = latitude.toBits()
        result = (temp xor (temp ushr 32)).toInt()
        temp = longitude.toBits()
        return 31 * result + (temp xor (temp ushr 32)).toInt()
    }

    override fun toString(): String {
        return ("GeoLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}')
    }

    companion object {
        private const val serialVersionUID = 6353721071298376949L
    }
}
