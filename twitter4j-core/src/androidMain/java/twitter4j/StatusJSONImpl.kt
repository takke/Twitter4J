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

import twitter4j.conf.Configuration
import java.util.Date

/**
 * A data class representing one single status of a user.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
internal class StatusJSONImpl : TwitterResponseImpl, Status {

    // 元実装では createdAt/text/source が null になりうるが、Status の各プロパティは非nullのため既定値を用いる。
    override var createdAt: Date = Date(0)
        private set
    override var id: Long = 0
        private set
    override var text: String = ""
        private set
    override var displayTextRangeStart: Int = -1
        private set
    override var displayTextRangeEnd: Int = -1
        private set
    override var source: String = ""
    override var isTruncated: Boolean = false
        private set
    override var inReplyToStatusId: Long = 0
    override var inReplyToUserId: Long = 0
    override var isFavorited: Boolean = false
        private set
    override var isRetweeted: Boolean = false
        private set
    override var favoriteCount: Int = 0
        private set
    override var inReplyToScreenName: String? = null
    override var geoLocation: GeoLocation? = null
        private set
    override var place: Place? = null
        private set

    // this field should be int in theory, but left as long for the serialized form compatibility - TFJ-790
    private var retweetCountLong: Long = 0
    override var isPossiblySensitive: Boolean = false
        private set
    override var lang: String? = null
        private set

    private var contributorsIDs: LongArray = LongArray(0)

    override var retweetedStatus: Status? = null
        private set
    private var _userMentionEntities: Array<UserMentionEntity> = emptyArray()
    private var _urlEntities: Array<URLEntity> = emptyArray()
    private var _hashtagEntities: Array<HashtagEntity> = emptyArray()
    private var _mediaEntities: Array<MediaEntity> = emptyArray()
    private var _symbolEntities: Array<SymbolEntity> = emptyArray()
    override var currentUserRetweetId: Long = -1L
        private set
    override var scopes: Scopes? = null
        private set
    override var user: User? = null
        private set
    override var withheldInCountries: Array<String>? = null
        private set
    override var quotedStatus: Status? = null
    override var quotedStatusId: Long = -1L
        private set
    override var quotedStatusPermalink: URLEntity? = null
        private set
    override var editControl: EditControl? = null
        private set
    override var initialTweetId: Long = -1L
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: Configuration) : super(res) {
        val json = res.asJSONObject()
        init(json)
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
            TwitterObjectFactory.registerJSONObject(this, json)
        }
    }

    @Throws(TwitterException::class)
    constructor(json: JSONObject, conf: Configuration) : super() {
        init(json)
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.registerJSONObject(this, json)
        }
    }

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        init(json)
    }

    /* Only for serialization purposes. */
    constructor() : super()

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        id = ParseUtil.getLong("id", json)
        source = ParseUtil.getUnescapedString("source", json) ?: ""
        createdAt = ParseUtil.getDate("created_at", json) ?: Date(0)
        isTruncated = ParseUtil.getBoolean("truncated", json)
        inReplyToStatusId = ParseUtil.getLong("in_reply_to_status_id", json)
        inReplyToUserId = ParseUtil.getLong("in_reply_to_user_id", json)
        isFavorited = ParseUtil.getBoolean("favorited", json)
        isRetweeted = ParseUtil.getBoolean("retweeted", json)
        inReplyToScreenName = ParseUtil.getUnescapedString("in_reply_to_screen_name", json)
        retweetCountLong = ParseUtil.getLong("retweet_count", json)
        favoriteCount = ParseUtil.getInt("favorite_count", json)
        isPossiblySensitive = ParseUtil.getBoolean("possibly_sensitive", json)
        try {
            if (!json.isNull("user")) {
                user = UserJSONImpl(json.getJSONObject("user"))
            }
            geoLocation = JSONImplFactory.createGeoLocation(json)
            if (!json.isNull("place")) {
                place = PlaceJSONImpl(json.getJSONObject("place"))
            }

            if (!json.isNull("retweeted_status")) {
                retweetedStatus = StatusJSONImpl(json.getJSONObject("retweeted_status"))
            }
            if (!json.isNull("contributors")) {
                val contributorsArray = json.getJSONArray("contributors")
                contributorsIDs = LongArray(contributorsArray.length())
                for (i in 0 until contributorsArray.length()) {
                    contributorsIDs[i] = contributorsArray.getString(i).toLong()
                }
            } else {
                contributorsIDs = LongArray(0)
            }

            collectEntities(json)
            mergeExtendedEntities(json)
            if (!json.isNull("quoted_status")) {
                quotedStatus = StatusJSONImpl(json.getJSONObject("quoted_status"))
            }
            if (!json.isNull("quoted_status_id")) {
                quotedStatusId = ParseUtil.getLong("quoted_status_id", json)
            }

            // edit controls
            val extEditControlJson = json.optJSONObject("ext_edit_control")
            if (extEditControlJson != null) {
                if (!extEditControlJson.isNull("initial")) {
                    editControl = EditControlImpl(extEditControlJson.getJSONObject("initial"))
                } else if (!extEditControlJson.isNull("edit")) {
                    val edit = extEditControlJson.getJSONObject("edit")
                    if (!edit.isNull("edit_control_initial")) {
                        editControl = EditControlImpl(edit.getJSONObject("edit_control_initial"))
                    }
                    initialTweetId = edit.getLong("initial_tweet_id")
                }
            }

            if (!json.isNull("quoted_status")) {
                quotedStatus = StatusJSONImpl(json.getJSONObject("quoted_status"))
            }

            if (!json.isNull("display_text_range")) {
                val indicesArray = json.getJSONArray("display_text_range")
                displayTextRangeStart = indicesArray.getInt(0)
                displayTextRangeEnd = indicesArray.getInt(1)
            }

            if (!json.isNull("text")) {
                text = HTMLEntity.unescapeAndSlideEntityIncdices(
                    json.getString("text"), _userMentionEntities,
                    _urlEntities, _hashtagEntities, _mediaEntities
                )
            }
            if (!json.isNull("full_text")) {
                text = HTMLEntity.unescapeAndSlideEntityIncdices(
                    json.getString("full_text"), _userMentionEntities,
                    _urlEntities, _hashtagEntities, _mediaEntities
                )
            }

            if (!json.isNull("extended_tweet")) {
                mergeExtendedTweet(json.getJSONObject("extended_tweet"))
            }

            if (!json.isNull("current_user_retweet")) {
                currentUserRetweetId = json.getJSONObject("current_user_retweet").getLong("id")
            }
            if (!json.isNull("lang")) {
                lang = ParseUtil.getUnescapedString("lang", json)
            }

            if (!json.isNull("scopes")) {
                val scopesJson = json.getJSONObject("scopes")
                if (!scopesJson.isNull("place_ids")) {
                    val placeIdsArray = scopesJson.getJSONArray("place_ids")
                    val len = placeIdsArray.length()
                    val placeIds = Array(len) { i -> placeIdsArray.getString(i) }
                    scopes = ScopesImpl(placeIds)
                }
            }
            if (!json.isNull("withheld_in_countries")) {
                val withheldInCountriesArray = json.getJSONArray("withheld_in_countries")
                val length = withheldInCountriesArray.length()
                withheldInCountries = Array(length) { i -> withheldInCountriesArray.getString(i) }
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    @Throws(JSONException::class, TwitterException::class)
    private fun collectEntities(json: JSONObject) {
        if (!json.isNull("entities")) {
            val entities = json.getJSONObject("entities")
            _userMentionEntities = EntitiesParseUtil.getUserMentions(entities) ?: emptyArray()
            _urlEntities = EntitiesParseUtil.getUrls(entities) ?: emptyArray()
            _hashtagEntities = EntitiesParseUtil.getHashtags(entities) ?: emptyArray()
            _symbolEntities = EntitiesParseUtil.getSymbols(entities) ?: emptyArray()
            _mediaEntities = EntitiesParseUtil.getMedia(entities) ?: emptyArray()
        }
        if (!json.isNull("quoted_status")) {
            quotedStatus = StatusJSONImpl(json.getJSONObject("quoted_status"))
        }
        if (!json.isNull("quoted_status_id")) {
            quotedStatusId = ParseUtil.getLong("quoted_status_id", json)
        }
        if (!json.isNull("quoted_status_permalink")) {
            quotedStatusPermalink = QuotedStatusPermalinkJSONImpl(json.getJSONObject("quoted_status_permalink"))
        }
    }

    @Throws(JSONException::class, TwitterException::class)
    private fun mergeExtendedEntities(json: JSONObject) {
        if (!json.isNull("extended_entities")) {
            val extendedEntities = json.getJSONObject("extended_entities")
            if (!extendedEntities.isNull("media")) {
                val mediaArray = extendedEntities.getJSONArray("media")
                val len = mediaArray.length()
                _mediaEntities = Array(len) { i -> MediaEntityJSONImpl(mediaArray.getJSONObject(i)) }
            }
        }
    }

    @Throws(TwitterException::class)
    private fun mergeExtendedTweet(extendedTweet: JSONObject) {
        try {
            val indicesArray = extendedTweet.getJSONArray("display_text_range")
            displayTextRangeStart = indicesArray.getInt(0)
            displayTextRangeEnd = indicesArray.getInt(1)

            collectEntities(extendedTweet)

            text = HTMLEntity.unescapeAndSlideEntityIncdices(
                extendedTweet.getString("full_text"), _userMentionEntities,
                _urlEntities, _hashtagEntities, _mediaEntities
            )
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun compareTo(other: Status?): Int {
        val delta = this.id - other!!.id
        return when {
            delta < Int.MIN_VALUE -> Int.MIN_VALUE
            delta > Int.MAX_VALUE -> Int.MAX_VALUE
            else -> delta.toInt()
        }
    }

    override val contributors: LongArray?
        get() = contributorsIDs

    override val isRetweet: Boolean
        get() = retweetedStatus != null

    override val retweetCount: Int
        get() = retweetCountLong.toInt()

    override val quoteCount: Int
        get() = 0

    override val replyCount: Int
        get() = 0

    override val bookmarkCount: Int
        get() = 0

    override val isRetweetedByMe: Boolean
        get() = currentUserRetweetId != -1L

    override val userMentionEntities: Array<UserMentionEntity>
        get() = _userMentionEntities

    override val uRLEntities: Array<URLEntity>
        get() = _urlEntities

    override val hashtagEntities: Array<HashtagEntity>
        get() = _hashtagEntities

    override val mediaEntities: Array<MediaEntity>
        get() = _mediaEntities

    override val symbolEntities: Array<SymbolEntity>
        get() = _symbolEntities

    override val isPromoted: Boolean
        get() = false

    override val card: Card?
        get() = null

    override fun hashCode(): Int {
        return id.toInt()
    }

    override fun equals(obj: Any?): Boolean {
        if (null == obj) {
            return false
        }
        if (this === obj) {
            return true
        }
        return obj is Status && obj.id == this.id
    }

    override fun toString(): String {
        return "StatusJSONImpl{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                ", isTruncated=" + isTruncated +
                ", inReplyToStatusId=" + inReplyToStatusId +
                ", inReplyToUserId=" + inReplyToUserId +
                ", isFavorited=" + isFavorited +
                ", isRetweeted=" + isRetweeted +
                ", favoriteCount=" + favoriteCount +
                ", inReplyToScreenName='" + inReplyToScreenName + '\'' +
                ", geoLocation=" + geoLocation +
                ", place=" + place +
                ", retweetCount=" + retweetCountLong +
                ", isPossiblySensitive=" + isPossiblySensitive +
                ", lang='" + lang + '\'' +
                ", contributorsIDs=" + contributorsIDs.contentToString() +
                ", retweetedStatus=" + retweetedStatus +
                ", userMentionEntities=" + _userMentionEntities.contentToString() +
                ", urlEntities=" + _urlEntities.contentToString() +
                ", hashtagEntities=" + _hashtagEntities.contentToString() +
                ", mediaEntities=" + _mediaEntities.contentToString() +
                ", symbolEntities=" + _symbolEntities.contentToString() +
                ", currentUserRetweetId=" + currentUserRetweetId +
                ", user=" + user +
                ", withHeldInCountries=" + withheldInCountries?.contentToString() +
                ", quotedStatusId=" + quotedStatusId +
                ", quotedStatus=" + quotedStatus +
                '}'
    }

    companion object {
        private const val serialVersionUID = -6461195536943679985L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createStatusList(res: HttpResponse, conf: Configuration): ResponseList<Status> {
            try {
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.clearThreadLocalMap()
                }
                val list = res.asJSONArray()
                val size = list.length()
                val statuses: ResponseList<Status> = ResponseListImpl(size, res)
                for (i in 0 until size) {
                    val json = list.getJSONObject(i)
                    val status: Status = StatusJSONImpl(json)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(status, json)
                    }
                    statuses.add(status)
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(statuses, list)
                }
                return statuses
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}
