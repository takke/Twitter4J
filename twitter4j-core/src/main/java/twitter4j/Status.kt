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
 * A data interface representing one single status of a user.
 * (e.g. https://dev.twitter.com/rest/reference/get/statuses/show/%3Aid)
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
interface Status : Comparable<Status?>, TwitterResponse, EntitySupport, Serializable {

    /**
     * Return the created_at
     *
     * @return created_at
     * @since Twitter4J 1.1.0
     */
    val createdAt: Date

    /**
     * Returns the id of the status
     *
     * @return the id (e.g. 210462857140252672)
     */
    val id: Long

    /**
     * Returns the text of the status
     *
     * @return the text (e.g. Along with our new #Twitterbird, we've also updated our Display Guidelines: https://t.co/Ed4omjYs  ^JC)
     */
    val text: String

    val isPromoted: Boolean

    val card: Card?

    val displayTextRangeStart: Int
    val displayTextRangeEnd: Int

    /**
     * Returns the source
     *
     * @return the source (e.g. &lt;a href="http://twitter.com" rel="nofollow"&gt;Twitter Web Client&lt;/a&gt;)
     * @since Twitter4J 1.0.4
     */
    var source: String

    /**
     * Test if the status is truncated
     *
     * @return true if truncated
     * @since Twitter4J 1.0.4
     */
    val isTruncated: Boolean

    /**
     * Returns the in_reply_tostatus_id
     *
     * @return the in_reply_tostatus_id
     * @since Twitter4J 1.0.4
     */
    var inReplyToStatusId: Long

    /**
     * Returns the in_reply_user_id
     *
     * @return the in_reply_tostatus_id
     * @since Twitter4J 1.0.4
     */
    var inReplyToUserId: Long

    /**
     * Returns the in_reply_to_screen_name
     *
     * @return the in_in_reply_to_screen_name
     * @since Twitter4J 2.0.4
     */
    var inReplyToScreenName: String?

    /**
     * Returns The location that this tweet refers to if available.
     *
     * @return returns The location that this tweet refers to if available (can be null)
     * @since Twitter4J 2.1.0
     */
    val geoLocation: GeoLocation?

    /**
     * Returns the place attached to this status
     *
     * @return The place attached to this status
     * @since Twitter4J 2.1.1
     */
    val place: Place?

    /**
     * Test if the status is favorited
     *
     * @return true if favorited
     * @since Twitter4J 1.0.4
     */
    val isFavorited: Boolean

    /**
     * Test if the status is retweeted
     *
     * @return true if retweeted
     * @since Twitter4J 3.0.4
     */
    val isRetweeted: Boolean

    /**
     * Indicates approximately how many times this Tweet has been "favorited" by Twitter users.
     *
     * @return the favorite count
     * @since Twitter4J 3.0.4
     */
    val favoriteCount: Int

    /**
     * Return the user associated with the status.<br></br>
     * This can be null if the instance is from User.getStatus().
     *
     * @return the user
     */
    val user: User?

    /**
     * @since Twitter4J 2.0.10
     * @return if the status is retweet or not
     */
    val isRetweet: Boolean

    /**
     * @since Twitter4J 2.1.0
     * @return retweeted status
     */
    val retweetedStatus: Status?

    /**
     * Returns an array of contributors, or null if no contributor is associated with this status.
     *
     * @since Twitter4J 2.2.3
     * @return contributors
     */
    val contributors: LongArray?

    /**
     * Returns the number of times this tweet has been retweeted, or -1 when the tweet was
     * created before this feature was enabled.
     *
     * @return the retweet count.
     */
    val retweetCount: Int

    /**
     * Returns true if the authenticating user has retweeted this tweet, or false when the tweet was
     * created before this feature was enabled.
     *
     * @return whether the authenticating user has retweeted this tweet.
     * @since Twitter4J 2.1.4
     */
    val isRetweetedByMe: Boolean

    /**
     * Returns the authenticating user's retweet's id of this tweet, or -1L when the tweet was created
     * before this feature was enabled.
     *
     * @return the authenticating user's retweet's id of this tweet
     * @since Twitter4J 3.0.1
     */
    val currentUserRetweetId: Long

    /**
     * Returns true if the status contains a link that is identified as sensitive.
     *
     * @return whether the status contains sensitive links
     * @since Twitter4J 3.0.0
     */
    val isPossiblySensitive: Boolean

    /**
     * Returns the lang of the status text if available.
     *
     * @return two-letter iso language code (e.g. en)
     * @since Twitter4J 3.0.6
     */
    val lang: String?

    /**
     * Returns the targeting scopes applied to a status.
     *
     * @return the targeting scopes applied to a status.
     * @since Twitter4J 3.0.6
     */
    val scopes: Scopes?

    /**
     * Returns the list of country codes where the tweet is withheld
     *
     * @return list of country codes where the tweet is withheld - null if not withheld
     * @since Twitter4j 4.0.3
     */
    val withheldInCountries: Array<String>?

    /**
     * Returns the Tweet ID of the quoted Tweet
     *
     * @return the Tweet ID of the quoted Tweet
     * @since Twitter4J 4.0.4
     */
    val quotedStatusId: Long

    /**
     * Returns the Tweet object of the original Tweet that was quoted.
     *
     * @return the quoted Tweet object
     * @since Twitter4J 4.0.4
     */
    var quotedStatus: Status?

    /**
     * Returns the URLEntity object that represents the permalink of the quoted Tweet.
     *
     * Note that "text" and an indices of "start", "end" are not provided.
     *
     * @return the URLEntity object that represents the permalink of the quoted Tweet. - null if not presents
     * @since Twitter4J 4.0.7
     */
    val quotedStatusPermalink: URLEntity?

    val editControl: EditControl?
    val initialTweetId: Long
}
