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

import twitter4j.*

/**
 * @author Joern Huxhorn - jhuxhorn at googlemail.com
 */
interface ListsResources {
    /**
     * List the lists of the specified user. Private lists will be included if the authenticated users is the same as the user whose lists are being returned.
     * <br>This method calls https://api.twitter.com/1.1/lists.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     *                            as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/list">GET lists/list | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun getUserLists(listOwnerScreenName: String): ResponseList<UserList>

    /**
     * List the lists of the specified user. Private lists will be included if the authenticated users is the same as the user whose lists are being returned.
     * <br>This method calls https://api.twitter.com/1.1/lists.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     *                            as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param reverse             Set this to true if you would like owned lists to be returned first
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/list">GET lists/list | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserLists(listOwnerScreenName: String, reverse: Boolean): ResponseList<UserList>

    /**
     * List the lists of the specified user. Private lists will be included if the authenticated users is the same as the user whose lists are being returned.
     * <br>This method calls https://api.twitter.com/1.1/lists.json
     *
     * @param listOwnerUserId The id of the list owner
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/list">GET lists/list | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun getUserLists(listOwnerUserId: Long): ResponseList<UserList>

    /**
     * List the lists of the specified user. Private lists will be included if the authenticated users is the same as the user whose lists are being returned.
     * <br>This method calls https://api.twitter.com/1.1/lists.json
     *
     * @param listOwnerUserId The id of the list owner
     * @param reverse         Set this to true if you would like owned lists to be returned first
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/list">GET lists/list | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserLists(listOwnerUserId: Long, reverse: Boolean): ResponseList<UserList>

    /**
     * Show tweet timeline for members of the specified list.
     * <br>https://api.twitter.com/1/user/lists/list_id/statuses.json
     *
     * @param listId The id of the list
     * @param paging controls pagination. Supports since_id, max_id, count and page parameters.
     * @return list of statuses for members of the specified list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/statuses">GET lists/statuses | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun getUserListStatuses(listId: Long, paging: Paging): ResponseList<Status>

    /**
     * Show tweet timeline for members of the specified list.
     * <br>https://api.twitter.com/1/user/lists/list_id/statuses.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param paging  controls pagination. Supports since_id, max_id, count and page parameters.
     * @return list of statuses for members of the specified list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/statuses">GET lists/statuses | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun getUserListStatuses(ownerId: Long, slug: String, paging: Paging): ResponseList<Status>

    /**
     * Show tweet timeline for members of the specified list.
     * <br>https://api.twitter.com/1.1/lists/statuses.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param paging          controls pagination. Supports since_id, max_id, count and page parameters.
     * @return list of statuses for members of the specified list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/statuses">GET lists/statuses | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun getUserListStatuses(ownerScreenName: String, slug: String, paging: Paging): ResponseList<Status>

    /**
     * Removes the specified member from the list. The authenticated user must be the list's owner to remove members from the list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/destroy.json
     *
     * @param listId The id of the list.
     * @param userId The screen name of the member you wish to remove from the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/destroy">POST lists/members/destroy | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun destroyUserListMember(listId: Long, userId: Long): UserList

    /**
     * Removes the specified members from the list. The authenticated user must be the list's owner to remove members from the list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/destroy.json
     *
     * @param listId     The id of the list.
     * @param screenName The screen name of the member you wish to remove from the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/destroy">POST lists/members/destroy | Twitter Developers</a>
     * @since Twitter4J 3.0.6
     */
    @Throws(TwitterException::class)
    fun destroyUserListMember(listId: Long, screenName: String): UserList

    /**
     * Removes the specified members from the list. The authenticated user must be the list's owner to remove members from the list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/destroy_all.json
     *
     * @param listId      The id of the list.
     * @param screenNames The screen names of the members you wish to remove from the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/destroy_all">POST lists/members/destroy_all | Twitter Developers</a>
     * @since Twitter4J 3.0.6
     */
    @Throws(TwitterException::class)
    fun destroyUserListMembers(listId: Long, screenNames: Array<String>): UserList

    /**
     * Removes the specified members from the list. The authenticated user must be the list's owner to remove members from the list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/destroy_all.json
     *
     * @param listId  The id of the list.
     * @param userIds The array of ids of the user to add as member of the list. up to 100 are allowed in a single request.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/destroy_all">POST lists/members/destroy_all | Twitter Developers</a>
     * @since Twitter4J 3.0.6
     */
    @Throws(TwitterException::class)
    fun destroyUserListMembers(listId: Long, userIds: LongArray): UserList

    /**
     * Removes the specified members from the list. The authenticated user must be the list's owner to remove members from the list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/destroy_all.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param screenNames     The screen names of the members you wish to remove from the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/destroy_all">POST lists/members/destroy_all | Twitter Developers</a>
     * @since Twitter4J 3.0.6
     */
    @Throws(TwitterException::class)
    fun destroyUserListMembers(ownerScreenName: String, slug: String, screenNames: Array<String>): UserList

    /**
     * Removes the specified member from the list. The authenticated user must be the list's owner to remove members from the list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/destroy.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param userId  The screen name of the member you wish to remove from the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/destroy">POST lists/members/destroy | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun destroyUserListMember(ownerId: Long, slug: String, userId: Long): UserList

    /**
     * Removes the specified member from the list. The authenticated user must be the list's owner to remove members from the list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/destroy.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param userId          The screen name of the member you wish to remove from the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/destroy">POST lists/members/destroy | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun destroyUserListMember(ownerScreenName: String, slug: String, userId: Long): UserList

    /**
     * List the lists the authenticating user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param cursor Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists
     * @throws TwitterException      when Twitter service or network is unavailable
     * @throws IllegalStateException when authorization is not enabled
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 2.2.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the authenticating user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param count  The amount of results to return per page. No more than 1000 results will ever be returned in a single page.
     * @param cursor Breaks the results into pages. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists
     * @throws TwitterException      when Twitter service or network is unavailable
     * @throws IllegalStateException when authorization is not enabled
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(count: Int, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberId The id of the list member
     * @param cursor       Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 2.2.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberId: Long, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberId The id of the list member
     * @param count        The amount of results to return per page. No more than 1000 results will ever be returned in a single page.
     * @param cursor       Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberId: Long, count: Int, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberScreenName The screen name of the list member
     * @param cursor               Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberScreenName: String, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberScreenName The screen name of the list member
     * @param count                The amount of results to return per page. No more than 1000 results will ever be returned in a single page.
     * @param cursor               Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberScreenName: String, count: Int, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberScreenName The screen name of the list member
     * @param cursor               Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param filterToOwnedLists   Whether to return just lists the authenticating user owns, and the user represented by listMemberScreenName is a member of.
     * @return the list of lists
     * @throws TwitterException      when Twitter service or network is unavailable
     * @throws IllegalStateException when filerToOwnedLists is true but authorization is not enabled
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 2.2.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberScreenName: String, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberScreenName The screen name of the list member
     * @param count                The amount of results to return per page. No more than 1000 results will ever be returned in a single page.
     * @param cursor               Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param filterToOwnedLists   Whether to return just lists the authenticating user owns, and the user represented by listMemberScreenName is a member of.
     * @return the list of lists
     * @throws TwitterException      when Twitter service or network is unavailable
     * @throws IllegalStateException when filerToOwnedLists is true but authorization is not enabled
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberScreenName: String, count: Int, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberId       The id of the list member
     * @param cursor             Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param filterToOwnedLists Whether to return just lists the authenticating user owns, and the user represented by listMemberId is a member of.
     * @return the list of lists
     * @throws TwitterException      when Twitter service or network is unavailable
     * @throws IllegalStateException when filerToOwnedLists is true but authorization is not enabled
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 2.2.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberId: Long, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList>

    /**
     * List the lists the specified user has been added to.
     * <br>This method calls https://api.twitter.com/1.1/lists/memberships.json
     *
     * @param listMemberId       The id of the list member
     * @param count              The amount of results to return per page. No more than 1000 results will ever be returned in a single page.
     * @param cursor             Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param filterToOwnedLists Whether to return just lists the authenticating user owns, and the user represented by listMemberId is a member of.
     * @return the list of lists
     * @throws TwitterException      when Twitter service or network is unavailable
     * @throws IllegalStateException when filerToOwnedLists is true but authorization is not enabled
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/memberships">GET lists/memberships | Twitter Developers</a>
     * @since Twitter4J 2.2.4
     */
    @Throws(TwitterException::class)
    fun getUserListMemberships(listMemberId: Long, count: Int, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param listId The id of the list
     * @param cursor Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(listId: Long, cursor: Long): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param listId The id of the list
     * @param count  Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(listId: Long, count: Int, cursor: Long): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param listId     The id of the list
     * @param count      Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor     Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param skipStatus When set to either true, t or 1 statuses will not be included in the returned user objects.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(listId: Long, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param cursor  Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(ownerId: Long, slug: String, cursor: Long): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param count   Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor  Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(ownerId: Long, slug: String, count: Int, cursor: Long): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param ownerId    The user ID of the user who owns the list being requested by a slug.
     * @param slug       slug of the list
     * @param count      Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor     Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param skipStatus When set to either true, t or 1 statuses will not be included in the returned user objects.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(ownerId: Long, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param cursor          Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(ownerScreenName: String, slug: String, cursor: Long): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param count           Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor          Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(ownerScreenName: String, slug: String, count: Int, cursor: Long): PagableResponseList<User>

    /**
     * Returns the subscribers of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param count           Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor          Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param skipStatus      When set to either true, t or 1 statuses will not be included in the returned user objects.
     * @return the members of the specified list.
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers">GET lists/subscribers | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscribers(ownerScreenName: String, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User>

    /**
     * Make the authenticated user follow the specified list.
     * <br>This method calls https://api.twitter.com/1.1/list/subscribers/create.json
     *
     * @param listId The id of the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/subscribers/create">POST lists/subscribers/create | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun createUserListSubscription(listId: Long): UserList

    /**
     * Make the authenticated user follow the specified list.
     * <br>This method calls https://api.twitter.com/1.1/list/subscribers/create.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/subscribers/create">POST lists/subscribers/create | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun createUserListSubscription(ownerId: Long, slug: String): UserList

    /**
     * Make the authenticated user follow the specified list.
     * <br>This method calls https://api.twitter.com/1.1/list/subscribers/create.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/subscribers/create">POST lists/subscribers/create | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun createUserListSubscription(ownerScreenName: String, slug: String): UserList

    /**
     * Check if the specified user is a subscriber of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers/show.json
     *
     * @param listId The id of the list.
     * @param userId The id of the user who you want to know is a member or not of the specified list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     *                          , or the user is not a member of the specified list(TwitterException.getStatusCode() returns 404 in that case.)
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers/show">GET lists/subscribers/show | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun showUserListSubscription(listId: Long, userId: Long): User

    /**
     * Check if the specified user is a subscriber of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers/show.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param userId  The id of the user who you want to know is a member or not of the specified list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     *                          , or the user is not a member of the specified list(TwitterException.getStatusCode() returns 404 in that case.)
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers/show">GET lists/subscribers/show | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun showUserListSubscription(ownerId: Long, slug: String, userId: Long): User

    /**
     * Check if the specified user is a subscriber of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscribers/show.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param userId          The id of the user who you want to know is a member or not of the specified list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     *                          , or the user is not a member of the specified list(TwitterException.getStatusCode() returns 404 in that case.)
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscribers/show">GET lists/subscribers/show | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun showUserListSubscription(ownerScreenName: String, slug: String, userId: Long): User

    /**
     * Unsubscribes the authenticated user form the specified list.
     * <br>This method calls https://api.twitter.com/1.1/subscribers/destroy.json
     *
     * @param listId The id of the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/subscribers/destroy">POST lists/subscribers/destroy | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun destroyUserListSubscription(listId: Long): UserList

    /**
     * Unsubscribes the authenticated user form the specified list.
     * <br>This method calls https://api.twitter.com/1.1/subscribers/destroy.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/subscribers/destroy">POST lists/subscribers/destroy | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun destroyUserListSubscription(ownerId: Long, slug: String): UserList

    /**
     * Unsubscribes the authenticated user form the specified list.
     * <br>This method calls https://api.twitter.com/1.1/subscribers/destroy.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/subscribers/destroy">POST lists/subscribers/destroy | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun destroyUserListSubscription(ownerScreenName: String, slug: String): UserList

    /**
     * Adds multiple members to a list, by specifying a comma-separated list of member ids or screen names. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members, and you are limited to adding up to 100 members to a list at a time with this method.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create_all.json
     *
     * @param listId  The id of the list.
     * @param userIds The array of ids of the user to add as member of the list. up to 100 are allowed in a single request.
     * @return the list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create_all">POST lists/members/create_all | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun createUserListMembers(listId: Long, vararg userIds: Long): UserList

    /**
     * Adds multiple members to a list, by specifying a comma-separated list of member ids or screen names. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members, and you are limited to adding up to 100 members to a list at a time with this method.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create_all.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param userIds The array of ids of the user to add as member of the list. up to 100 are allowed in a single request.
     * @return the list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create_all">POST lists/members/create_all | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun createUserListMembers(ownerId: Long, slug: String, vararg userIds: Long): UserList

    /**
     * Adds multiple members to a list, by specifying a comma-separated list of member ids or screen names. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members, and you are limited to adding up to 100 members to a list at a time with this method.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create_all.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param userIds         The array of ids of the user to add as member of the list. up to 100 are allowed in a single request.
     * @return the list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create_all">POST lists/members/create_all | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun createUserListMembers(ownerScreenName: String, slug: String, vararg userIds: Long): UserList

    /**
     * Adds multiple members to a list, by specifying a comma-separated list of member ids or screen names. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members, and you are limited to adding up to 100 members to a list at a time with this method.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create_all.json
     *
     * @param listId      The id of the list.
     * @param screenNames The array of screen names of the user to add as member of the list. up to 100 are allowed in a single request.
     * @return the list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create_all">POST lists/members/create_all | Twitter Developers</a>
     * @since Twitter4J 2.1.7
     */
    @Throws(TwitterException::class)
    fun createUserListMembers(listId: Long, vararg screenNames: String): UserList

    /**
     * Adds multiple members to a list, by specifying a comma-separated list of member ids or screen names. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members, and you are limited to adding up to 100 members to a list at a time with this method.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create_all.json
     *
     * @param ownerId     The user ID of the user who owns the list being requested by a slug.
     * @param slug        slug of the list
     * @param screenNames The array of screen names of the user to add as member of the list. up to 100 are allowed in a single request.
     * @return the list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create_all">POST lists/members/create_all | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun createUserListMembers(ownerId: Long, slug: String, vararg screenNames: String): UserList

    /**
     * Adds multiple members to a list, by specifying a comma-separated list of member ids or screen names. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members, and you are limited to adding up to 100 members to a list at a time with this method.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create_all.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param screenNames     The array of screen names of the user to add as member of the list. up to 100 are allowed in a single request.
     * @return the list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create_all">POST lists/members/create_all | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun createUserListMembers(ownerScreenName: String, slug: String, vararg screenNames: String): UserList

    /**
     * Check if a user is a member of the specified list.<br>
     * <br>This method calls https://api.twitter.com/1.1/lists/members/show.json
     *
     * @param listId The id of the list.
     * @param userId The id of the user who you want to know is a member or not of the specified list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     *                          , or the user is not a member of the specified list(TwitterException.getStatusCode() returns 404 in that case.)
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members/show">GET lists/members/show | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun showUserListMembership(listId: Long, userId: Long): User

    /**
     * Check if a user is a member of the specified list.<br>
     * <br>This method calls https://api.twitter.com/1.1/lists/members/show.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param userId  The id of the user who you want to know is a member or not of the specified list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     *                          , or the user is not a member of the specified list(TwitterException.getStatusCode() returns 404 in that case.)
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members/show">GET lists/members/show | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun showUserListMembership(ownerId: Long, slug: String, userId: Long): User

    /**
     * Check if a user is a member of the specified list.<br>
     * <br>This method calls https://api.twitter.com/1.1/lists/members/show.json
     *
     * @param ownerScreenName Id The user ID of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param userId          The id of the user who you want to know is a member or not of the specified list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     *                          , or the user is not a member of the specified list(TwitterException.getStatusCode() returns 404 in that case.)
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members/show">GET lists/members/show | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun showUserListMembership(ownerScreenName: String, slug: String, userId: Long): User

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param listId The id of the list
     * @param cursor Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(listId: Long, cursor: Long): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param listId The id of the list
     * @param count  Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(listId: Long, count: Int, cursor: Long): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param listId     The id of the list
     * @param count      Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor     Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param skipStatus When set to either true, t or 1 statuses will not be included in the returned user objects.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(listId: Long, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param cursor  Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(ownerId: Long, slug: String, cursor: Long): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param count   Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor  Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(ownerId: Long, slug: String, count: Int, cursor: Long): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param ownerId    The user ID of the user who owns the list being requested by a slug.
     * @param slug       slug of the list
     * @param count      Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor     Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param skipStatus When set to either true, t or 1 statuses will not be included in the returned user objects.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(ownerId: Long, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param cursor          Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(ownerScreenName: String, slug: String, cursor: Long): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param count           Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor          Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(ownerScreenName: String, slug: String, count: Int, cursor: Long): PagableResponseList<User>

    /**
     * Returns the members of the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/members.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param count           Specifies the number of results to return per page. The default is 20, with a maximum of 5,000.
     * @param cursor          Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @param skipStatus      When set to either true, t or 1 statuses will not be included in the returned user objects.
     * @return the members of the specified list.
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/members">GET lists/members | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListMembers(ownerScreenName: String, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User>

    /**
     * Adds a member to a list. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create.json
     *
     * @param listId The id of the list.
     * @param userId The id of the user to add as a member of the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create">POST lists/members/create | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun createUserListMember(listId: Long, userId: Long): UserList

    /**
     * Adds a member to a list. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @param userId  The id of the user to add as a member of the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create">POST lists/members/create | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun createUserListMember(ownerId: Long, slug: String, userId: Long): UserList

    /**
     * Adds a member to a list. The authenticated user must own the list to be able to add members to it. Lists are limited to having 5000 members.
     * <br>This method calls https://api.twitter.com/1.1/lists/members/create.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param userId          The id of the user to add as a member of the list.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/members/create">POST lists/members/create | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun createUserListMember(ownerScreenName: String, slug: String, userId: Long): UserList

    /**
     * Deletes the specified list. Must be owned by the authenticated user.
     * <br>This method calls https://api.twitter.com/1.1/lists/destroy.json
     *
     * @param listId The id of the list to delete
     * @return the deleted list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/destroy">POST lists/destroy | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun destroyUserList(listId: Long): UserList

    /**
     * Deletes the specified list. Must be owned by the authenticated user.
     * <br>This method calls https://api.twitter.com/1.1/lists/destroy.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @return the deleted list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/destroy">POST lists/destroy | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun destroyUserList(ownerId: Long, slug: String): UserList

    /**
     * Deletes the specified list. Must be owned by the authenticated user.
     * <br>This method calls https://api.twitter.com/1.1/lists/destroy.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @return the deleted list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/destroy">POST lists/destroy | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun destroyUserList(ownerScreenName: String, slug: String): UserList

    /**
     * Updates the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/update.json
     *
     * @param listId         The id of the list to update.
     * @param newListName    What you'd like to change the list's name to.
     * @param isPublicList   Whether your list is public or private. Optional. Values can be public or private. Lists are public by default if no mode is specified.
     * @param newDescription What you'd like to change the list description to.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/update">POST lists/update | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun updateUserList(listId: Long, newListName: String?, isPublicList: Boolean, newDescription: String?): UserList

    /**
     * Updates the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/update.json
     *
     * @param ownerId        The user ID of the user who owns the list being requested by a slug.
     * @param slug           slug of the list
     * @param newListName    What you'd like to change the list's name to.
     * @param isPublicList   Whether your list is public or private. Optional. Values can be public or private. Lists are public by default if no mode is specified.
     * @param newDescription What you'd like to change the list description to.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/update">POST lists/update | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun updateUserList(ownerId: Long, slug: String, newListName: String?, isPublicList: Boolean, newDescription: String?): UserList

    /**
     * Updates the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/update.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @param newListName     What you'd like to change the list's name to.
     * @param isPublicList    Whether your list is public or private. Optional. Values can be public or private. Lists are public by default if no mode is specified.
     * @param newDescription  What you'd like to change the list description to.
     * @return the updated list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/update">POST lists/update | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun updateUserList(ownerScreenName: String, slug: String, newListName: String?, isPublicList: Boolean, newDescription: String?): UserList

    /**
     * Creates a new list for the authenticated user. Accounts are limited to 20 lists.
     * <br>This method calls https://api.twitter.com/1.1/lists/create.json
     *
     * @param listName     The name of the list you are creating. Required.
     * @param isPublicList set true if you wish to make a public list
     * @param description  The description of the list you are creating. Optional.
     * @return the list that was created
     * @throws twitter4j.TwitterException when Twitter service or network is unavailable, or the authenticated user already has 20 lists(TwitterException.getStatusCode() == 403).
     * @see <a href="https://dev.twitter.com/docs/api/1.1/post/lists/create">POST lists/create | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun createUserList(listName: String, isPublicList: Boolean, description: String?): UserList

    /**
     * Show the specified list. Private lists will only be shown if the authenticated user owns the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/show.json
     *
     * @param listId The id of the list to show
     * @return the specified list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/show">https://dev.twitter.com/docs/api/1.1/get/lists/show | Twitter Developers</a>
     * @since Twitter4J 2.2.3
     */
    @Throws(TwitterException::class)
    fun showUserList(listId: Long): UserList

    /**
     * Show the specified list. Private lists will only be shown if the authenticated user owns the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/show.json
     *
     * @param ownerId The user ID of the user who owns the list being requested by a slug.
     * @param slug    slug of the list
     * @return the specified list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/show">https://dev.twitter.com/docs/api/1.1/get/lists/show | Twitter Developers</a>
     * @since Twitter4J 3.0.0
     */
    @Throws(TwitterException::class)
    fun showUserList(ownerId: Long, slug: String): UserList

    /**
     * Show the specified list. Private lists will only be shown if the authenticated user owns the specified list.
     * <br>This method calls https://api.twitter.com/1.1/lists/show.json
     *
     * @param ownerScreenName The screen name of the user who owns the list being requested by a slug.
     * @param slug            slug of the list
     * @return the specified list
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/show">https://dev.twitter.com/docs/api/1.1/get/lists/show | Twitter Developers</a>
     * @since Twitter4J 3.0.2
     */
    @Throws(TwitterException::class)
    fun showUserList(ownerScreenName: String, slug: String): UserList

    /**
     * List the lists the specified user follows.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscriptions.json
     *
     * @param listSubscriberScreenName The screen name of the list subscriber
     * @param cursor                   Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists the specified user is subscribed to
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscriptions">GET lists/subscriptions | Twitter Developers</a>
     * @since Twitter4J 2.1.0
     */
    @Throws(TwitterException::class)
    fun getUserListSubscriptions(listSubscriberScreenName: String, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user follows.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscriptions.json
     *
     * @param listSubscriberScreenName The screen name of the list subscriber
     * @param count                    The amount of results to return per page. Defaults to 20. No more than 1000 results will ever be returned in a single page.
     * @param cursor                   Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists the specified user is subscribed to
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscriptions">GET lists/subscriptions | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscriptions(listSubscriberScreenName: String, count: Int, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user follows.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscriptions.json
     *
     * @param listSubscriberId The ID of the list subscriber
     * @param cursor           Breaks the results into pages. A single page contains 20 lists. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists the specified user is subscribed to
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscriptions">GET lists/subscriptions | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscriptions(listSubscriberId: Long, cursor: Long): PagableResponseList<UserList>

    /**
     * List the lists the specified user follows.
     * <br>This method calls https://api.twitter.com/1.1/lists/subscriptions.json
     *
     * @param listSubscriberId The ID of the list subscriber
     * @param count            The amount of results to return per page. Defaults to 20. No more than 1000 results will ever be returned in a single page.
     * @param cursor           Breaks the results into pages. Provide a value of -1 to begin paging. Provide values as returned to in the response body's next_cursor and previous_cursor attributes to page back and forth in the list.
     * @return the list of lists the specified user is subscribed to
     * @throws TwitterException when Twitter service or network is unavailable
     * @see <a href="https://dev.twitter.com/docs/api/1.1/get/lists/subscriptions">GET lists/subscriptions | Twitter Developers</a>
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListSubscriptions(listSubscriberId: Long, count: Int, cursor: Long): PagableResponseList<UserList>

    /**
     * Returns the lists owned by the specified Twitter user. Private lists will only be shown if the authenticated user is also the owner of the lists.
     * <br>This method calls https://api.twitter.com/1.1/lists/ownerships.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     * @param cursor              Breaks the results into pages. Provide a value of -1 to begin paging. Provide values as returned in the response body's next_cursor and previous_cursor attributes to page back and forth in the list. It is recommended to always use cursors when the method supports them. See <a href="https://dev.twitter.com/docs/misc/cursoring">Using cursors</a> to navigate collections for more information.
     * @return lists owned by the specified Twitter user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListsOwnerships(listOwnerScreenName: String, cursor: Long): PagableResponseList<UserList>

    /**
     * Returns the lists owned by the specified Twitter user. Private lists will only be shown if the authenticated user is also the owner of the lists.
     * <br>This method calls https://api.twitter.com/1.1/lists/ownerships.json
     *
     * @param listOwnerScreenName The screen name of the list owner
     * @param count               The amount of results to return per page. Defaults to 20. No more than 1000 results will ever be returned in a single page.
     * @param cursor              Breaks the results into pages. Provide a value of -1 to begin paging. Provide values as returned in the response body's next_cursor and previous_cursor attributes to page back and forth in the list. It is recommended to always use cursors when the method supports them. See <a href="https://dev.twitter.com/docs/misc/cursoring">Using cursors</a> to navigate collections for more information.
     * @return lists owned by the specified Twitter user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 4.0.1
     */
    @Throws(TwitterException::class)
    fun getUserListsOwnerships(listOwnerScreenName: String, count: Int, cursor: Long): PagableResponseList<UserList>

    /**
     * Returns the lists owned by the specified Twitter user. Private lists will only be shown if the authenticated user is also the owner of the lists.
     * <br>This method calls https://api.twitter.com/1.1/lists/ownerships.json
     *
     * @param listOwnerId The id of the list owner
     * @param cursor      Breaks the results into pages. Provide a value of -1 to begin paging. Provide values as returned in the response body's next_cursor and previous_cursor attributes to page back and forth in the list. It is recommended to always use cursors when the method supports them. See <a href="https://dev.twitter.com/docs/misc/cursoring">Using cursors</a> to navigate collections for more information.
     * @return lists owned by the specified Twitter user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 4.0.4
     */
    @Throws(TwitterException::class)
    fun getUserListsOwnerships(listOwnerId: Long, cursor: Long): PagableResponseList<UserList>

    /**
     * Returns the lists owned by the specified Twitter user. Private lists will only be shown if the authenticated user is also the owner of the lists.
     * <br>This method calls https://api.twitter.com/1.1/lists/ownerships.json
     *
     * @param listOwnerId The id of the list owner
     * @param count       The amount of results to return per page. Defaults to 20. No more than 1000 results will ever be returned in a single page.
     * @param cursor      Breaks the results into pages. Provide a value of -1 to begin paging. Provide values as returned in the response body's next_cursor and previous_cursor attributes to page back and forth in the list. It is recommended to always use cursors when the method supports them. See <a href="https://dev.twitter.com/docs/misc/cursoring">Using cursors</a> to navigate collections for more information.
     * @return lists owned by the specified Twitter user
     * @throws TwitterException when Twitter service or network is unavailable
     * @since Twitter4J 4.0.1
     */
    @Throws(TwitterException::class)
    fun getUserListsOwnerships(listOwnerId: Long, count: Int, cursor: Long): PagableResponseList<UserList>
}
