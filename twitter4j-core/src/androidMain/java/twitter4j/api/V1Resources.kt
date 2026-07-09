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

package twitter4j.api

interface V1Resources :
    TimelinesResources,
    TweetsResources,
    SearchResource,
    FriendsFollowersResources,
    UsersResources,
    FavoritesResources,
    ListsResources,
    SavedSearchesResources,
    TrendsResources,
    SpamReportingResource,
    HelpResources {

    /**
     * @return {@link twitter4j.api.TimelinesResources}
     * @since Twitter4J 3.0.4
     */
    fun timelines(): TimelinesResources

    /**
     * @return {@link twitter4j.api.TweetsResources}
     * @since Twitter4J 3.0.4
     */
    fun tweets(): TweetsResources

    /**
     * @return {@link twitter4j.api.SearchResource}
     * @since Twitter4J 3.0.4
     */
    fun search(): SearchResource

    /**
     * @return {@link twitter4j.api.FriendsFollowersResources}
     * @since Twitter4J 3.0.4
     */
    fun friendsFollowers(): FriendsFollowersResources

    /**
     * @return {@link twitter4j.api.UsersResources}
     * @since Twitter4J 3.0.4
     */
    fun users(): UsersResources

    /**
     * @return {@link twitter4j.api.FavoritesResources}
     * @since Twitter4J 3.0.4
     */
    fun favorites(): FavoritesResources

    /**
     * @return {@link twitter4j.api.ListsResources}
     * @since Twitter4J 3.0.4
     */
    fun list(): ListsResources

    /**
     * @return {@link twitter4j.api.SavedSearchesResources}
     * @since Twitter4J 3.0.4
     */
    fun savedSearches(): SavedSearchesResources

    /**
     * @return {@link twitter4j.api.TrendsResources}
     * @since Twitter4J 3.0.4
     */
    fun trends(): TrendsResources

    /**
     * @return {@link twitter4j.api.SpamReportingResource}
     * @since Twitter4J 3.0.4
     */
    fun spamReporting(): SpamReportingResource

    /**
     * @return {@link twitter4j.api.HelpResources}
     * @since Twitter4J 3.0.4
     */
    fun help(): HelpResources
}
