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

import java.io.Serializable
import java.util.Date

/**
 * A data interface representing Basic user information element
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
interface User : Comparable<User?>, TwitterResponse, Serializable {

    /**
     * Returns the id of the user
     *
     * @return the id of the user
     */
    val id: Long

    /**
     * Returns the name of the user
     *
     * @return the name of the user
     */
    val name: String

    /**
     * Returns the email of the user, if the app is whitelisted by Twitter
     *
     * @return the email of the user
     */
    val email: String?

    /**
     * Returns the screen name of the user
     *
     * @return the screen name of the user
     */
    val screenName: String?

    /**
     * Returns the location of the user
     *
     * @return the location of the user
     */
    val location: String?

    /**
     * Returns the description of the user
     *
     * @return the description of the user
     */
    val description: String

    /**
     * Tests if the user is enabling contributors
     *
     * @return if the user is enabling contributors
     * @since Twitter4J 2.1.2
     */
    val isContributorsEnabled: Boolean

    /**
     * Returns the profile image url of the user
     *
     * @return the profile image url of the user
     */
    val profileImageURLHttps: String?
    val biggerProfileImageURLHttps: String?
    val miniProfileImageURLHttps: String?
    val originalProfileImageURLHttps: String?

    /**
     * @since Twitter4J 4.0.7
     * @return profile image url
     */
    fun get400x400ProfileImageURLHttps(): String?

    /**
     * Tests if the user has not uploaded their own avatar
     *
     * @return if the user has not uploaded their own avatar
     */
    val isDefaultProfileImage: Boolean

    /**
     * Returns the url of the user
     *
     * @return the url of the user
     */
    val url: String?

    /**
     * Test if the user status is protected
     *
     * @return true if the user status is protected
     */
    val isProtected: Boolean

    /**
     * Returns the number of followers
     *
     * @return the number of followers
     * @since Twitter4J 1.0.4
     */
    val followersCount: Int

    /**
     * Returns the current status of the user<br></br>
     * This can be null if the instance if from Status.getUser().
     *
     * @return current status of the user
     * @since Twitter4J 2.1.1
     */
    val status: Status?

    /**
     * Tests if the user has not altered the theme or background
     *
     * @return if the user has not altered the theme or background
     */
    val isDefaultProfile: Boolean
    val isShowAllInlineMedia: Boolean

    /**
     * Returns the number of users the user follows (AKA "followings")
     *
     * @return the number of users the user follows
     */
    val friendsCount: Int

    val createdAt: Date?
    val favouritesCount: Int
    val utcOffset: Int
    val timeZone: String?

    /**
     * @since Twitter4J 3.0.0
     * @return profile banner URL
     */
    val profileBannerURL: String?

    /**
     * @since Twitter4J 3.0.0
     * @return profile banner retina URL
     */
    val profileBannerRetinaURL: String?

    /**
     * @since Twitter4J 3.0.0
     * @return profile banner iPad URL
     */
    val profileBannerIPadURL: String?

    /**
     * @since Twitter4J 3.0.0
     * @return profile banner iPad retina URL
     */
    val profileBannerIPadRetinaURL: String?

    /**
     * @since Twitter4J 3.0.0
     * @return profile banner mobile URL
     */
    val profileBannerMobileURL: String?

    /**
     * @since Twitter4J 3.0.0
     * @return profile banner mobile retina URL
     */
    val profileBannerMobileRetinaURL: String?

    /**
     * @since Twitter4J 4.0.7
     * @return profile banner 300x100 URL
     */
    val profileBanner300x100URL: String?

    /**
     * @since Twitter4J 4.0.7
     * @return profile banner 600x200 URL
     */
    val profileBanner600x200URL: String?

    /**
     * @since Twitter4J 4.0.7
     * @return profile banner 1500x500 URL
     */
    val profileBanner1500x500URL: String?

    /**
     * Returns the preferred language of the user
     *
     * @return the preferred language of the user
     * @since Twitter4J 2.1.2
     */
    val lang: String?

    val statusesCount: Int

    /**
     * @return the user is enabling geo location
     * @since Twitter4J 2.0.10
     */
    val isGeoEnabled: Boolean

    /**
     * @return returns true if the user is a verified celebrity
     * @since Twitter4J 2.0.10
     */
    val isVerified: Boolean

    /**
     * @return returns true if the user is a translator
     * @since Twitter4J 2.1.9
     */
    val isTranslator: Boolean

    /**
     * Returns the number of public lists the user is listed on, or -1
     * if the count is unavailable.
     *
     * @return the number of public lists the user is listed on.
     * @since Twitter4J 2.1.4
     */
    val listedCount: Int

    /**
     * Returns true if the authenticating user has requested to follow this user,
     * otherwise false.
     *
     * @return true if the authenticating user has requested to follow this user.
     * @since Twitter4J 2.1.4
     */
    val isFollowRequestSent: Boolean

    /**
     * Returns URL entities for user description.
     *
     * @return URL entities for user description
     * @since Twitter4J 3.0.3
     */
    val descriptionURLEntities: Array<URLEntity>

    /**
     * Returns URL entity for user's URL.
     *
     * @return URL entity for user's URL.
     * @since Twitter4J 3.0.3
     */
    val urlEntity: URLEntity

    /**
     * Returns the list of country codes where the user is withheld
     *
     * @return list of country codes where the tweet is withheld - null if not withheld
     * @since Twitter4j 4.0.3
     */
    val withheldInCountries: Array<String>?
}
