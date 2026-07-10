package twitter4j

import kotlin.jvm.JvmStatic

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 4.0.0
 */
object TwitterObjectFactory {

    // KMP移行 Phase 5-3: java.lang.ThreadLocal 直接依存を expect/actual の [RawJsonThreadLocalMap] へ移設。
    // JVM(Android) では従来どおりスレッドローカルなマップ、iOS では共有マップで代替する。
    private val rawJsonMap = RawJsonThreadLocalMap()

    private var registeredAtleastOnce = false

    /**
     * Returns a raw JSON form of the provided object.<br>
     * Note that raw JSON forms can be retrieved only from the same thread invoked the last method call and will become inaccessible once another method call
     *
     * @param obj target object to retrieve JSON
     * @return raw JSON
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    fun getRawJSON(obj: Any): String? {
        if (!registeredAtleastOnce) {
            throw IllegalStateException("Apparently jsonStoreEnabled is not set to true.")
        }
        val json = rawJsonMap.get()[obj]
        return when (json) {
            is String -> json
            null -> null
            // object must be instance of JSONObject
            else -> json.toString()
        }
    }

    /**
     * Constructs a Status object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return Status
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createStatus(rawJSON: String): Status {
        try {
            return StatusJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a User object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return User
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createUser(rawJSON: String): User {
        try {
            return UserJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a Relationship object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return Relationship
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createRelationship(rawJSON: String): Relationship {
        try {
            return RelationshipJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a Place object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return Place
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createPlace(rawJSON: String): Place {
        try {
            return PlaceJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a SavedSearch object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return SavedSearch
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createSavedSearch(rawJSON: String): SavedSearch {
        try {
            return SavedSearchJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a Trend object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return Trend
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createTrend(rawJSON: String): Trend {
        try {
            return TrendJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a Trends object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return Trends
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createTrends(rawJSON: String): Trends {
        return TrendsJSONImpl(rawJSON)
    }

    /**
     * Constructs a IDs object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return IDs
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createIDs(rawJSON: String): IDs {
        return IDsJSONImpl(rawJSON)
    }

    /**
     * Constructs a RateLimitStatus object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return RateLimitStatus
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createRateLimitStatus(rawJSON: String): Map<String, RateLimitStatus> {
        try {
            return RateLimitStatusJSONImpl.createRateLimitStatuses(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a DirectMessage object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return DirectMessage
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createDirectMessage(rawJSON: String): DirectMessage {
        try {
            return DirectMessageJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a Location object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return Location
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createLocation(rawJSON: String): Location {
        try {
            return LocationJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * Constructs a UserList object from rawJSON string.
     *
     * @param rawJSON raw JSON form as String
     * @return UserList
     * @throws TwitterException when provided string is not a valid JSON string.
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    @Throws(TwitterException::class)
    fun createUserList(rawJSON: String): UserList {
        try {
            return UserListJSONImpl(JSONObject(rawJSON))
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    /**
     * clear raw JSON forms associated with the current thread.<br>
     *
     * @since Twitter4J 2.1.7
     */
    // KMP移行 Phase 6: 外部モジュール（twitter4j-v2-support の V2ResponseFactory）が JSONStore 有効時に
    // 呼び出すため、upstream 同様に public API とする（KMP化時に暫定 internal 化されていたのを復元）。
    @JvmStatic
    fun clearThreadLocalMap() {
        rawJsonMap.get().clear()
    }

    /**
     * associate a raw JSON form to the current thread<br>
     *
     * @since Twitter4J 2.1.7
     */
    @JvmStatic
    fun <T> registerJSONObject(key: T, json: Any): T {
        registeredAtleastOnce = true
        rawJsonMap.get()[key] = json
        return key
    }

    /**
     * remove a raw JSON form from the current thread<br>
     *
     * @since Twitter4J v4.0.8-20240903
     */
    @JvmStatic
    fun <T> removeJSONObject(key: T) {
        rawJsonMap.get().remove(key)
    }
}
