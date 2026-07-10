/*
 * Copyright (C) 2007 Yusuke Yamamoto
 * Copyright (C) 2011 Twitter, Inc.
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

import twitter4j.api.HelpResources
import twitter4j.conf.Configuration

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.4
 */
internal class JSONImplFactory(private val conf: Configuration) : ObjectFactory {

    @Throws(TwitterException::class)
    override fun createStatus(json: JSONObject): Status {
        return StatusJSONImpl(json)
    }

    @Throws(TwitterException::class)
    override fun createUser(json: JSONObject): User {
        return UserJSONImpl(json)
    }

    @Throws(TwitterException::class)
    override fun createAUserList(json: JSONObject): UserList {
        return UserListJSONImpl(json)
    }

    @Throws(TwitterException::class)
    override fun createRateLimitStatuses(res: HttpResponse): Map<String, RateLimitStatus> {
        return RateLimitStatusJSONImpl.createRateLimitStatuses(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createStatus(res: HttpResponse): Status {
        return StatusJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createStatusList(res: HttpResponse): ResponseList<Status> {
        return StatusJSONImpl.createStatusList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createTrends(res: HttpResponse): Trends {
        return TrendsJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createUser(res: HttpResponse): User {
        return UserJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createUserList(res: HttpResponse): ResponseList<User> {
        return UserJSONImpl.createUserList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createUserListFromJSONArray(res: HttpResponse): ResponseList<User> {
        return UserJSONImpl.createUserList(res.asJSONArray(), res, conf)
    }

    @Throws(TwitterException::class)
    override fun createUserListFromJSONArray_Users(res: HttpResponse): ResponseList<User> {
        try {
            return UserJSONImpl.createUserList(res.asJSONObject().getJSONArray("users"), res, conf)
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    @Throws(TwitterException::class)
    override fun createQueryResult(res: HttpResponse, query: Query): QueryResult {
        return try {
            QueryResultJSONImpl(res, conf)
        } catch (te: TwitterException) {
            if (404 == te.statusCode) {
                QueryResultJSONImpl(query)
            } else {
                throw te
            }
        }
    }

    @Throws(TwitterException::class)
    override fun createIDs(res: HttpResponse): IDs {
        return IDsJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createPagableUserList(res: HttpResponse): PagableResponseList<User> {
        return UserJSONImpl.createPagableUserList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createAUserList(res: HttpResponse): UserList {
        return UserListJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createPagableUserListList(res: HttpResponse): PagableResponseList<UserList> {
        return UserListJSONImpl.createPagableUserListList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createUserListList(res: HttpResponse): ResponseList<UserList> {
        return UserListJSONImpl.createUserListList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createDirectMessage(res: HttpResponse): DirectMessage {
        return DirectMessageJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createDirectMessageList(res: HttpResponse): DirectMessageList {
        return DirectMessageJSONImpl.createDirectMessageList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createRelationship(res: HttpResponse): Relationship {
        return RelationshipJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createAccountSettings(res: HttpResponse): AccountSettings {
        return AccountSettingsJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createSavedSearch(res: HttpResponse): SavedSearch {
        return SavedSearchJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createSavedSearchList(res: HttpResponse): ResponseList<SavedSearch> {
        return SavedSearchJSONImpl.createSavedSearchList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createLocationList(res: HttpResponse): ResponseList<Location> {
        return LocationJSONImpl.createLocationList(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createPlace(res: HttpResponse): Place {
        return PlaceJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createPlaceList(res: HttpResponse): ResponseList<Place> {
        return try {
            PlaceJSONImpl.createPlaceList(res, conf)
        } catch (te: TwitterException) {
            if (te.statusCode == 404) {
                ResponseListImpl<Place>(0, null)
            } else {
                throw te
            }
        }
    }

    @Throws(TwitterException::class)
    override fun createTwitterAPIConfiguration(res: HttpResponse): TwitterAPIConfiguration {
        return TwitterAPIConfigurationJSONImpl(res, conf)
    }

    @Throws(TwitterException::class)
    override fun createLanguageList(res: HttpResponse): ResponseList<HelpResources.Language> {
        return LanguageJSONImpl.createLanguageList(res, conf)
    }

    override fun <T> createEmptyResponseList(): ResponseList<T> {
        return ResponseListImpl<T>(0, null)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is JSONImplFactory) return false

        val that = o

        if (conf != that.conf) return false

        return true
    }

    override fun hashCode(): Int {
        return conf.hashCode()
    }

    override fun toString(): String {
        return "JSONImplFactory{" +
                "conf=" + conf +
                '}'
    }

    companion object {
        private const val serialVersionUID = -1853541456182663343L

        // ※ createGeoLocation / coordinatesAsGeoLocationArray は KMP移行 Phase 5-3 で
        //    commonMain の [GeoLocationJSONImplFactory] へ移設した（StatusJSONImpl / PlaceJSONImpl が
        //    commonMain から参照するため）。

        @JvmStatic
        fun createRateLimitStatusFromResponseHeader(res: HttpResponse): RateLimitStatus? {
            return RateLimitStatusJSONImpl.createFromResponseHeader(res)
        }

        /**
         * static factory method for twitter-text-java
         *
         * @return hashtag entity
         * @since Twitter4J 2.2.6
         */
        @JvmStatic
        fun createHashtagEntity(start: Int, end: Int, text: String): HashtagEntity {
            return HashtagEntityJSONImpl(start, end, text)
        }

        /**
         * static factory method for twitter-text-java
         *
         * @return user mention entity
         * @since Twitter4J 2.2.6
         */
        @JvmStatic
        fun createUserMentionEntity(start: Int, end: Int, name: String, screenName: String,
                                    id: Long): UserMentionEntity {
            return UserMentionEntityJSONImpl(start, end, name, screenName, id)
        }

        /**
         * static factory method for twitter-text-java
         *
         * @return url entity
         * @since Twitter4J 2.2.6
         */
        @JvmStatic
        fun createUrlEntity(start: Int, end: Int, url: String, expandedURL: String, displayURL: String): URLEntity {
            return URLEntityJSONImpl(start, end, url, expandedURL, displayURL)
        }
    }
}
