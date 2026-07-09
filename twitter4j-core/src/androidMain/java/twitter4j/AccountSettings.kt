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
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.9
 */
interface AccountSettings : TwitterResponse, java.io.Serializable {
    /**
     * Returns true if the user enabled sleep time.
     *
     * @return true if the user enabled sleep time
     */
    val isSleepTimeEnabled: Boolean

    /**
     * Returns sleep start time.
     *
     * @return sleep start time
     */
    val sleepStartTime: String

    /**
     * Returns sleep end time.
     *
     * @return sleep end time
     */
    val sleepEndTime: String

    /**
     * Return the user's trend locations
     *
     * @return the user's trend locations
     */
    val trendLocations: Array<Location>

    /**
     * Return true if the user is enabling geo location
     *
     * @return true if the user is enabling geo location
     */
    val isGeoEnabled: Boolean

    /**
     * Returns the timezone configured for this user.
     *
     * @return the timezone (formated as a Rails TimeZone name)
     */
    val timeZone: TimeZone

    /**
     * Returns the language used to render Twitter's UII for this user.
     *
     * @return the language ISO 639-1 representation
     */
    val language: String

    /**
     * Returns true if the user is discoverable by email.
     *
     * @return true if the user is discoverable by email
     */
    val isDiscoverableByEmail: Boolean

    /**
     * Returns true if the wants to always access twitter using HTTPS.
     *
     * @return true if the wants to always access twitter using HTTPS
     */
    val isAlwaysUseHttps: Boolean

    /**
     * Returns the user's screen name
     *
     * @return the user's screen name
     * @since Twitter4J 3.0.5
     */
    val screenName: String

    /**
     * Returns if the user can accept DMs from anyone
     *
     * @return all if allowed by anyone, followers if friends only
     * @since Twitter4J 4.0.7
     */
    val allowDmsFrom: String
}
