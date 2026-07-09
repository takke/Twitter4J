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
import java.io.Serializable

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.4
 */
// TwitPane(InternalResourcesEx.kt 等の別モジュール package twitter4j コード)が
// `twitter.factory` を ObjectFactory 型として参照するため public とする（旧Javaの package-private 相当）。
interface ObjectFactory : Serializable {
    @Throws(TwitterException::class)
    fun createStatus(json: JSONObject): Status

    @Throws(TwitterException::class)
    fun createUser(json: JSONObject): User

    @Throws(TwitterException::class)
    fun createAUserList(json: JSONObject): UserList

    @Throws(TwitterException::class)
    fun createRateLimitStatuses(res: HttpResponse): Map<String, RateLimitStatus>

    @Throws(TwitterException::class)
    fun createStatus(res: HttpResponse): Status

    @Throws(TwitterException::class)
    fun createStatusList(res: HttpResponse): ResponseList<Status>

    @Throws(TwitterException::class)
    fun createTrends(res: HttpResponse): Trends

    @Throws(TwitterException::class)
    fun createUser(res: HttpResponse): User

    @Throws(TwitterException::class)
    fun createUserList(res: HttpResponse): ResponseList<User>

    @Throws(TwitterException::class)
    fun createUserListFromJSONArray(res: HttpResponse): ResponseList<User>

    @Throws(TwitterException::class)
    fun createUserListFromJSONArray_Users(res: HttpResponse): ResponseList<User>

    @Throws(TwitterException::class)
    fun createQueryResult(res: HttpResponse, query: Query): QueryResult

    @Throws(TwitterException::class)
    fun createIDs(res: HttpResponse): IDs

    @Throws(TwitterException::class)
    fun createPagableUserList(res: HttpResponse): PagableResponseList<User>

    @Throws(TwitterException::class)
    fun createAUserList(res: HttpResponse): UserList

    @Throws(TwitterException::class)
    fun createPagableUserListList(res: HttpResponse): PagableResponseList<UserList>

    @Throws(TwitterException::class)
    fun createUserListList(res: HttpResponse): ResponseList<UserList>

    @Throws(TwitterException::class)
    fun createDirectMessage(res: HttpResponse): DirectMessage

    @Throws(TwitterException::class)
    fun createDirectMessageList(res: HttpResponse): DirectMessageList

    @Throws(TwitterException::class)
    fun createRelationship(res: HttpResponse): Relationship

    @Throws(TwitterException::class)
    fun createAccountSettings(res: HttpResponse): AccountSettings

    @Throws(TwitterException::class)
    fun createSavedSearch(res: HttpResponse): SavedSearch

    @Throws(TwitterException::class)
    fun createSavedSearchList(res: HttpResponse): ResponseList<SavedSearch>

    @Throws(TwitterException::class)
    fun createLocationList(res: HttpResponse): ResponseList<Location>

    @Throws(TwitterException::class)
    fun createPlace(res: HttpResponse): Place

    @Throws(TwitterException::class)
    fun createPlaceList(res: HttpResponse): ResponseList<Place>

    @Throws(TwitterException::class)
    fun createTwitterAPIConfiguration(res: HttpResponse): TwitterAPIConfiguration

    @Throws(TwitterException::class)
    fun createLanguageList(res: HttpResponse): ResponseList<HelpResources.Language>

    fun <T> createEmptyResponseList(): ResponseList<T>
}
