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
 * A data class representing Basic user information element
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
// TwitPane(MediaEntityGraphqlImpl 等の別モジュール package twitter4j コード)から `UserJSONImpl(json)` を
// 直接 new するため public クラス/public コンストラクタとする。
class UserJSONImpl : TwitterResponseImpl, User {

    override var id: Long = 0
        private set

    // 元実装では name/screenName が null になりうるが、User の各プロパティは非nullのため getter で "" にフォールバックする。
    private var nameRaw: String? = null
    override var email: String? = null
        private set
    private var screenNameRaw: String? = null
    override var location: String? = null
        private set
    private var descriptionRaw: String? = null
    override var descriptionURLEntities: Array<URLEntity> = emptyArray()
        private set
    private var urlEntityValue: URLEntity? = null
    override var isContributorsEnabled: Boolean = false
        private set
    override var profileImageURLHttps: String? = null
        private set
    override var isDefaultProfileImage: Boolean = false
        private set
    override var url: String? = null
        private set
    override var isProtected: Boolean = false
        private set
    override var followersCount: Int = 0
        private set

    override var status: Status? = null
        private set

    override var isDefaultProfile: Boolean = false
        private set
    override var isShowAllInlineMedia: Boolean = false
        private set
    override var friendsCount: Int = 0
        private set
    override var createdAt: Instant? = null
        private set
    override var favouritesCount: Int = 0
        private set
    override var utcOffset: Int = 0
        private set
    override var timeZone: String? = null
        private set
    private var profileBannerImageUrl: String? = null
    override var lang: String? = null
        private set
    override var statusesCount: Int = 0
        private set
    override var isGeoEnabled: Boolean = false
        private set
    override var isVerified: Boolean = false
        private set
    private var translator: Boolean = false
    override var listedCount: Int = 0
        private set
    override var isFollowRequestSent: Boolean = false
        private set
    override var withheldInCountries: Array<String>? = null
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : super(res) {
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
        }
        val json = res.asJSONObject()
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
        try {
            id = ParseUtil.getLong("id", json)
            nameRaw = ParseUtil.getRawString("name", json)
            email = ParseUtil.getRawString("email", json)
            screenNameRaw = ParseUtil.getRawString("screen_name", json)
            location = ParseUtil.getRawString("location", json)

            // descriptionUrlEntities <=> entities/descriptions/urls[]
            descriptionURLEntities = getURLEntitiesFromJSON(json, "description")

            // urlEntity <=> entities/url/urls[]
            val urlEntities = getURLEntitiesFromJSON(json, "url")
            if (urlEntities.isNotEmpty()) {
                urlEntityValue = urlEntities[0]
            }

            descriptionRaw = ParseUtil.getRawString("description", json)
            if (descriptionRaw != null) {
                descriptionRaw = HTMLEntity.unescapeAndSlideEntityIncdices(
                    descriptionRaw!!,
                    null, descriptionURLEntities, null, null
                )
            }

            isContributorsEnabled = ParseUtil.getBoolean("contributors_enabled", json)
            profileImageURLHttps = ParseUtil.getRawString("profile_image_url_https", json)
            isDefaultProfileImage = ParseUtil.getBoolean("default_profile_image", json)
            url = ParseUtil.getRawString("url", json)
            isProtected = ParseUtil.getBoolean("protected", json)
            isGeoEnabled = ParseUtil.getBoolean("geo_enabled", json)
            isVerified = ParseUtil.getBoolean("verified", json)
            translator = ParseUtil.getBoolean("is_translator", json)
            followersCount = ParseUtil.getInt("followers_count", json)

            isDefaultProfile = ParseUtil.getBoolean("default_profile", json)
            isShowAllInlineMedia = ParseUtil.getBoolean("show_all_inline_media", json)
            friendsCount = ParseUtil.getInt("friends_count", json)
            createdAt = ParseUtil.getInstant("created_at", json, "EEE MMM dd HH:mm:ss z yyyy")
            favouritesCount = ParseUtil.getInt("favourites_count", json)
            utcOffset = ParseUtil.getInt("utc_offset", json)
            timeZone = ParseUtil.getRawString("time_zone", json)
            profileBannerImageUrl = ParseUtil.getRawString("profile_banner_url", json)
            lang = ParseUtil.getRawString("lang", json)
            statusesCount = ParseUtil.getInt("statuses_count", json)
            listedCount = ParseUtil.getInt("listed_count", json)
            isFollowRequestSent = ParseUtil.getBoolean("follow_request_sent", json)
            if (!json.isNull("status")) {
                val statusJSON = json.getJSONObject("status")
                status = StatusJSONImpl(statusJSON)
            }
            if (!json.isNull("withheld_in_countries")) {
                val withheldInCountriesArray = json.getJSONArray("withheld_in_countries")
                val length = withheldInCountriesArray.length()
                withheldInCountries = Array(length) { i -> withheldInCountriesArray.getString(i) }
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone.message + ":" + json.toString(), jsone)
        }
    }

    /**
     * Get URL Entities from JSON Object.
     * returns URLEntity array by entities/[category]/urls/url[]
     *
     * @param json     user json object
     * @param category entities category. e.g. "description" or "url"
     * @return URLEntity array by entities/[category]/urls/url[]
     */
    @Throws(JSONException::class, TwitterException::class)
    private fun getURLEntitiesFromJSON(json: JSONObject, category: String): Array<URLEntity> {
        if (!json.isNull("entities")) {
            val entitiesJSON = json.getJSONObject("entities")
            if (!entitiesJSON.isNull(category)) {
                val descriptionEntitiesJSON = entitiesJSON.getJSONObject(category)
                if (!descriptionEntitiesJSON.isNull("urls")) {
                    val urlsArray = descriptionEntitiesJSON.getJSONArray("urls")
                    val len = urlsArray.length()
                    return Array(len) { i -> URLEntityJSONImpl(urlsArray.getJSONObject(i)) }
                }
            }
        }
        return emptyArray()
    }

    override fun compareTo(other: User?): Int {
        return (this.id - other!!.id).toInt()
    }

    override val name: String
        get() = nameRaw ?: ""

    override val screenName: String
        get() = screenNameRaw ?: ""

    // 元実装(getDescription)は description 欠損時に null を返す（テスト testGetDescriptionURLEntities2 が null を期待）。
    // インターフェース User.description は非null宣言だが、Java実装同様に実際は null を返しうるため、
    // 型消去された総称キャスト(uncheckedToNonNull)で非null型のまま null を透過させる。空文字("")はそのまま返す。
    override val description: String
        get() = uncheckedToNonNull(descriptionRaw)

    private fun toResizedURL(originalURL: String?, sizeSuffix: String): String? {
        if (null != originalURL && originalURL.length >= 1) {
            val index = originalURL.lastIndexOf("_")
            val suffixIndex = originalURL.lastIndexOf(".")
            val slashIndex = originalURL.lastIndexOf("/")
            var url = originalURL.substring(0, index) + sizeSuffix
            if (suffixIndex > slashIndex) {
                url += originalURL.substring(suffixIndex)
            }
            return url
        }
        return null
    }

    override val biggerProfileImageURLHttps: String?
        get() = toResizedURL(profileImageURLHttps, "_bigger")

    override val miniProfileImageURLHttps: String?
        get() = toResizedURL(profileImageURLHttps, "_mini")

    override val originalProfileImageURLHttps: String?
        get() = toResizedURL(profileImageURLHttps, "")

    override fun get400x400ProfileImageURLHttps(): String? {
        return toResizedURL(profileImageURLHttps, "_400x400")
    }

    override val profileBannerURL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/web" else null

    override val profileBannerRetinaURL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/web_retina" else null

    override val profileBannerIPadURL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/ipad" else null

    override val profileBannerIPadRetinaURL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/ipad_retina" else null

    override val profileBannerMobileURL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/mobile" else null

    override val profileBannerMobileRetinaURL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/mobile_retina" else null

    override val profileBanner300x100URL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/300x100" else null

    override val profileBanner600x200URL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/600x200" else null

    override val profileBanner1500x500URL: String?
        get() = if (profileBannerImageUrl != null) "$profileBannerImageUrl/1500x500" else null

    override val isTranslator: Boolean
        get() = translator

    override val urlEntity: URLEntity
        get() {
            if (urlEntityValue == null) {
                val plainURL = url ?: ""
                urlEntityValue = URLEntityJSONImpl(0, plainURL.length, plainURL, plainURL, plainURL)
            }
            return urlEntityValue!!
        }

    override val following: Boolean?
        get() = null

    override val followedBy: Boolean?
        get() = null

    override val canDm: Boolean?
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
        return obj is User && obj.id == this.id
    }

    override fun toString(): String {
        return "UserJSONImpl{" +
                "id=" + id +
                ", name='" + nameRaw + '\'' +
                ", email='" + email + '\'' +
                ", screenName='" + screenNameRaw + '\'' +
                ", location='" + location + '\'' +
                ", description='" + descriptionRaw + '\'' +
                ", isContributorsEnabled=" + isContributorsEnabled +
                ", profileImageUrlHttps='" + profileImageURLHttps + '\'' +
                ", isDefaultProfileImage=" + isDefaultProfileImage +
                ", url='" + url + '\'' +
                ", isProtected=" + isProtected +
                ", followersCount=" + followersCount +
                ", status=" + status +
                ", isDefaultProfile=" + isDefaultProfile +
                ", showAllInlineMedia=" + isShowAllInlineMedia +
                ", friendsCount=" + friendsCount +
                ", createdAt=" + createdAt +
                ", favouritesCount=" + favouritesCount +
                ", utcOffset=" + utcOffset +
                ", timeZone='" + timeZone + '\'' +
                ", lang='" + lang + '\'' +
                ", statusesCount=" + statusesCount +
                ", isGeoEnabled=" + isGeoEnabled +
                ", isVerified=" + isVerified +
                ", translator=" + translator +
                ", listedCount=" + listedCount +
                ", isFollowRequestSent=" + isFollowRequestSent +
                ", withheldInCountries=" + withheldInCountries?.contentToString() +
                '}'
    }

    companion object {
        private const val serialVersionUID = -5448266606847617015L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createPagableUserList(res: HttpResponse, conf: JsonStoreConfiguration): PagableResponseList<User> {
            try {
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.clearThreadLocalMap()
                }
                val json = res.asJSONObject()
                val list = json.getJSONArray("users")
                val size = list.length()
                val users: PagableResponseList<User> = PagableResponseListImpl(size, json, res)
                for (i in 0 until size) {
                    val userJson = list.getJSONObject(i)
                    val user: User = UserJSONImpl(userJson)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(user, userJson)
                    }
                    users.add(user)
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(users, json)
                }
                return users
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }

        @JvmStatic
        @Throws(TwitterException::class)
        fun createUserList(res: HttpResponse, conf: JsonStoreConfiguration): ResponseList<User> {
            return createUserList(res.asJSONArray(), res, conf)
        }

        @JvmStatic
        @Throws(TwitterException::class)
        fun createUserList(list: JSONArray, res: HttpResponse, conf: JsonStoreConfiguration): ResponseList<User> {
            try {
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.clearThreadLocalMap()
                }
                val size = list.length()
                val users: ResponseList<User> = ResponseListImpl(size, res)
                for (i in 0 until size) {
                    val json = list.getJSONObject(i)
                    val user: User = UserJSONImpl(json)
                    users.add(user)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(user, json)
                    }
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(users, list)
                }
                return users
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}

/**
 * 非null宣言のプロパティで、Java実装同様に実際は null を返しうる値をそのまま透過させるためのヘルパー。
 * 型パラメータ T は型消去されるため `value as T` は実行時にキャストチェックが行われず（null でも例外を投げない）、
 * 元の Java 実装が持っていた「非null契約だが実体は null」という挙動を保持する。
 */
@Suppress("UNCHECKED_CAST")
private fun <T> uncheckedToNonNull(value: T?): T = value as T
