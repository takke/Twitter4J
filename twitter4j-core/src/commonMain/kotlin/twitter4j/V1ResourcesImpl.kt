package twitter4j

import twitter4j.HttpParameter.Companion.getParameterArray
import twitter4j.api.FavoritesResources
import twitter4j.api.FriendsFollowersResources
import twitter4j.api.HelpResources
import twitter4j.api.ListsResources
import twitter4j.api.SavedSearchesResources
import twitter4j.api.SearchResource
import twitter4j.api.SpamReportingResource
import twitter4j.api.TimelinesResources
import twitter4j.api.TrendsResources
import twitter4j.api.TweetsResources
import twitter4j.api.UsersResources
import twitter4j.api.V1Resources
import twitter4j.conf.ChunkedUploadConfiguration
import twitter4j.File
import twitter4j.InputStream

internal class V1ResourcesImpl(private val twitter: TwitterImpl) : V1Resources {

    override fun timelines(): TimelinesResources {
        return this
    }

    override fun tweets(): TweetsResources {
        return this
    }

    override fun search(): SearchResource {
        return this
    }

    override fun friendsFollowers(): FriendsFollowersResources {
        return this
    }

    override fun users(): UsersResources {
        return this
    }

    override fun favorites(): FavoritesResources {
        return this
    }

    override fun list(): ListsResources {
        return this
    }

    override fun savedSearches(): SavedSearchesResources {
        return this
    }

    override fun trends(): TrendsResources {
        return this
    }

    override fun spamReporting(): SpamReportingResource {
        return this
    }

    override fun help(): HelpResources {
        return this
    }


    /* Timelines Resources */

    @Throws(TwitterException::class)
    override fun getMentionsTimeline(): ResponseList<Status> {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "statuses/mentions_timeline.json"))
    }

    @Throws(TwitterException::class)
    override fun getMentionsTimeline(paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(twitter.conf.getRestBaseURL() + "statuses/mentions_timeline.json", *paging.asPostParameterArray())
        )
    }

    @Throws(TwitterException::class)
    override fun getHomeTimeline(): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(twitter.conf.getRestBaseURL() + "statuses/home_timeline.json", twitter.INCLUDE_MY_RETWEET)
        )
    }

    @Throws(TwitterException::class)
    override fun getHomeTimeline(paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "statuses/home_timeline.json",
                *twitter.mergeParameters(paging.asPostParameterArray(), arrayOf(twitter.INCLUDE_MY_RETWEET))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getRetweetsOfMe(): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(twitter.conf.getRestBaseURL() + "statuses/retweets_of_me.json")
        )
    }

    @Throws(TwitterException::class)
    override fun getRetweetsOfMe(paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(twitter.conf.getRestBaseURL() + "statuses/retweets_of_me.json", *paging.asPostParameterArray())
        )
    }

    @Throws(TwitterException::class)
    override fun getUserTimeline(screenName: String, paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "statuses/user_timeline.json",
                *twitter.mergeParameters(
                    arrayOf(HttpParameter("screen_name", screenName), twitter.INCLUDE_MY_RETWEET),
                    paging.asPostParameterArray()
                )
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserTimeline(userId: Long, paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "statuses/user_timeline.json",
                *twitter.mergeParameters(
                    arrayOf(HttpParameter("user_id", userId), twitter.INCLUDE_MY_RETWEET),
                    paging.asPostParameterArray()
                )
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserTimeline(screenName: String): ResponseList<Status> {
        return getUserTimeline(screenName, Paging())
    }

    @Throws(TwitterException::class)
    override fun getUserTimeline(userId: Long): ResponseList<Status> {
        return getUserTimeline(userId, Paging())
    }

    @Throws(TwitterException::class)
    override fun getUserTimeline(): ResponseList<Status> {
        return getUserTimeline(Paging())
    }

    @Throws(TwitterException::class)
    override fun getUserTimeline(paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "statuses/user_timeline.json",
                *twitter.mergeParameters(arrayOf(twitter.INCLUDE_MY_RETWEET), paging.asPostParameterArray())
            )
        )
    }

    /* Tweets Resources */

    @Throws(TwitterException::class)
    override fun getRetweets(statusId: Long): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(twitter.conf.getRestBaseURL() + "statuses/retweets/" + statusId + ".json?count=100")
        )
    }

    @Throws(TwitterException::class)
    override fun getRetweeterIds(statusId: Long, cursor: Long): IDs {
        return getRetweeterIds(statusId, 100, cursor)
    }

    @Throws(TwitterException::class)
    override fun getRetweeterIds(statusId: Long, count: Int, cursor: Long): IDs {
        return twitter.factory.createIDs(
            twitter.get(
                twitter.conf.getRestBaseURL() + "statuses/retweeters/ids.json?id=" + statusId +
                    "&cursor=" + cursor + "&count=" + count
            )
        )
    }

    @Throws(TwitterException::class)
    override fun showStatus(id: Long): Status {
        return twitter.factory.createStatus(
            twitter.get(twitter.conf.getRestBaseURL() + "statuses/show/" + id + ".json", twitter.INCLUDE_MY_RETWEET)
        )
    }

    @Throws(TwitterException::class)
    override fun destroyStatus(statusId: Long): Status {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "statuses/destroy/" + statusId + ".json"))
    }

    @Throws(TwitterException::class)
    override fun updateStatus(status: String): Status {
        return twitter.factory.createStatus(
            twitter.post(twitter.conf.getRestBaseURL() + "statuses/update.json", HttpParameter("status", status))
        )
    }

    @Throws(TwitterException::class)
    override fun updateStatus(latestStatus: StatusUpdate): Status {
        val url = twitter.conf.getRestBaseURL() + (if (latestStatus.isForUpdateWithMedia())
            "statuses/update_with_media.json" else "statuses/update.json")
        return twitter.factory.createStatus(twitter.post(url, *latestStatus.asHttpParameterArray()))
    }

    @Throws(TwitterException::class)
    override fun retweetStatus(statusId: Long): Status {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "statuses/retweet/" + statusId + ".json"))
    }

    @Throws(TwitterException::class)
    override fun unRetweetStatus(statusId: Long): Status {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "statuses/unretweet/" + statusId + ".json"))
    }

    @Throws(TwitterException::class)
    override fun lookup(vararg ids: Long): ResponseList<Status> {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "statuses/lookup.json?id=" + StringUtil.join(ids)))
    }

    @Throws(TwitterException::class)
    override fun uploadMedia(mediaFile: File): UploadedMedia {
        MediaUploadSupport.checkFileValidity(mediaFile)
        return UploadedMedia(
            twitter.post(twitter.conf.getUploadBaseURL() + "media/upload.json", MediaUploadSupport.mediaHttpParameter("media", mediaFile)).asJSONObject()
        )
    }

    @Throws(TwitterException::class)
    override fun uploadMedia(fileName: String, media: InputStream): UploadedMedia {
        return UploadedMedia(
            twitter.post(twitter.conf.getUploadBaseURL() + "media/upload.json", MediaUploadSupport.mediaHttpParameter("media", fileName, media)).asJSONObject()
        )
    }

    @Throws(TwitterException::class)
    override fun createMediaMetadata(mediaId: Long, altText: String): Int {
        try {
            val json = JSONObject()
                .put("media_id", "" + mediaId)
                .put("alt_text", JSONObject().put("text", altText))
            return twitter.post(twitter.conf.getUploadBaseURL() + "media/metadata/create.json", json).getStatusCode()
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    @Throws(TwitterException::class)
    override fun uploadMediaChunked(chunkedUploadConfiguration: ChunkedUploadConfiguration): UploadedMedia {
        return MediaUploadSupport.uploadMediaChunked(twitter, chunkedUploadConfiguration)
    }

    /* Search Resources */

    @Throws(TwitterException::class)
    override fun search(query: Query): QueryResult {
        return if (query.nextPage() != null) {
            twitter.factory.createQueryResult(
                twitter.get(twitter.conf.getRestBaseURL() + "search/tweets.json" + query.nextPage()), query
            )
        } else {
            twitter.factory.createQueryResult(
                twitter.get(twitter.conf.getRestBaseURL() + "search/tweets.json", *query.asHttpParameterArray()), query
            )
        }
    }

    /* Friends & Followers Resources */

    @Throws(TwitterException::class)
    override fun getNoRetweetsFriendships(): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friendships/no_retweets/ids.json"))
    }

    @Throws(TwitterException::class)
    override fun getFriendsIDs(cursor: Long): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun getFriendsIDs(userId: Long, cursor: Long): IDs {
        return twitter.factory.createIDs(
            twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json?user_id=" + userId + "&cursor=" + cursor)
        )
    }

    @Throws(TwitterException::class)
    override fun getFriendsIDs(userId: Long, cursor: Long, count: Int): IDs {
        return twitter.factory.createIDs(
            twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json?user_id=" + userId + "&cursor=" + cursor + "&count=" + count)
        )
    }

    @Throws(TwitterException::class)
    override fun getFriendsIDs(screenName: String, cursor: Long): IDs {
        return twitter.factory.createIDs(
            twitter.get(
                twitter.conf.getRestBaseURL() + "friends/ids.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFriendsIDs(screenName: String, cursor: Long, count: Int): IDs {
        return twitter.factory.createIDs(
            twitter.get(
                twitter.conf.getRestBaseURL() + "friends/ids.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor),
                HttpParameter("count", count)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersIDs(cursor: Long): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun getFollowersIDs(userId: Long, cursor: Long): IDs {
        return twitter.factory.createIDs(
            twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json?user_id=" + userId + "&cursor=" + cursor)
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersIDs(userId: Long, cursor: Long, count: Int): IDs {
        return twitter.factory.createIDs(
            twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json?user_id=" + userId + "&cursor=" + cursor + "&count=" + count)
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersIDs(screenName: String, cursor: Long): IDs {
        return twitter.factory.createIDs(
            twitter.get(
                twitter.conf.getRestBaseURL() + "followers/ids.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersIDs(screenName: String, cursor: Long, count: Int): IDs {
        return twitter.factory.createIDs(
            twitter.get(
                twitter.conf.getRestBaseURL() + "followers/ids.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor),
                HttpParameter("count", count)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getIncomingFriendships(cursor: Long): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friendships/incoming.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun getOutgoingFriendships(cursor: Long): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friendships/outgoing.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun createFriendship(userId: Long): User {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/create.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun createFriendship(screenName: String): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "friendships/create.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun createFriendship(userId: Long, follow: Boolean): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "friendships/create.json?user_id=" + userId + "&follow=" + follow)
        )
    }

    @Throws(TwitterException::class)
    override fun createFriendship(screenName: String, follow: Boolean): User {
        return twitter.factory.createUser(
            twitter.post(
                twitter.conf.getRestBaseURL() + "friendships/create.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("follow", follow)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyFriendship(userId: Long): User {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/destroy.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun destroyFriendship(screenName: String): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "friendships/destroy.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun updateFriendship(userId: Long, enableDeviceNotification: Boolean, retweets: Boolean): Relationship {
        return twitter.factory.createRelationship(
            twitter.post(
                twitter.conf.getRestBaseURL() + "friendships/update.json",
                HttpParameter("user_id", userId),
                HttpParameter("device", enableDeviceNotification),
                HttpParameter("retweets", retweets)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun updateFriendship(screenName: String, enableDeviceNotification: Boolean, retweets: Boolean): Relationship {
        return twitter.factory.createRelationship(
            twitter.post(
                twitter.conf.getRestBaseURL() + "friendships/update.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("device", enableDeviceNotification),
                HttpParameter("retweets", retweets)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun showFriendship(sourceId: Long, targetId: Long): Relationship {
        return twitter.factory.createRelationship(
            twitter.get(
                twitter.conf.getRestBaseURL() + "friendships/show.json",
                HttpParameter("source_id", sourceId), HttpParameter("target_id", targetId)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun showFriendship(sourceScreenName: String, targetScreenName: String): Relationship {
        return twitter.factory.createRelationship(
            twitter.get(
                twitter.conf.getRestBaseURL() + "friendships/show.json",
                *getParameterArray("source_screen_name", sourceScreenName, "target_screen_name", targetScreenName)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFriendsList(userId: Long, cursor: Long): PagableResponseList<User> {
        return getFriendsList(userId, cursor, 20)
    }

    @Throws(TwitterException::class)
    override fun getFriendsList(userId: Long, cursor: Long, count: Int): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(twitter.conf.getRestBaseURL() + "friends/list.json?user_id=" + userId + "&cursor=" + cursor + "&count=" + count)
        )
    }

    @Throws(TwitterException::class)
    override fun getFriendsList(screenName: String, cursor: Long): PagableResponseList<User> {
        return getFriendsList(screenName, cursor, 20)
    }

    @Throws(TwitterException::class)
    override fun getFriendsList(screenName: String, cursor: Long, count: Int): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "friends/list.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor),
                HttpParameter("count", count)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFriendsList(userId: Long, cursor: Long, count: Int, skipStatus: Boolean, includeUserEntities: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "friends/list.json?user_id=" + userId + "&cursor=" + cursor + "&count=" + count +
                    "&skip_status=" + skipStatus + "&include_user_entities=" + includeUserEntities
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFriendsList(screenName: String, cursor: Long, count: Int, skipStatus: Boolean, includeUserEntities: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "friends/list.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor),
                HttpParameter("count", count),
                HttpParameter("skip_status", skipStatus),
                HttpParameter("include_user_entities", includeUserEntities)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersList(userId: Long, cursor: Long): PagableResponseList<User> {
        return getFollowersList(userId, cursor, 20)
    }

    @Throws(TwitterException::class)
    override fun getFollowersList(screenName: String, cursor: Long): PagableResponseList<User> {
        return getFollowersList(screenName, cursor, 20)
    }

    @Throws(TwitterException::class)
    override fun getFollowersList(userId: Long, cursor: Long, count: Int): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(twitter.conf.getRestBaseURL() + "followers/list.json?user_id=" + userId + "&cursor=" + cursor + "&count=" + count)
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersList(screenName: String, cursor: Long, count: Int): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "followers/list.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor),
                HttpParameter("count", count)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersList(userId: Long, cursor: Long, count: Int, skipStatus: Boolean, includeUserEntities: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "followers/list.json?user_id=" + userId + "&cursor=" + cursor + "&count=" + count +
                    "&skip_status=" + skipStatus + "&include_user_entities=" + includeUserEntities
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFollowersList(screenName: String, cursor: Long, count: Int, skipStatus: Boolean, includeUserEntities: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "followers/list.json",
                HttpParameter("screen_name", screenName),
                HttpParameter("cursor", cursor),
                HttpParameter("count", count),
                HttpParameter("skip_status", skipStatus),
                HttpParameter("include_user_entities", includeUserEntities)
            )
        )
    }

    /* Users Resources */

    @Throws(TwitterException::class)
    override fun getAccountSettings(): AccountSettings {
        return twitter.factory.createAccountSettings(twitter.get(twitter.conf.getRestBaseURL() + "account/settings.json"))
    }

    @Throws(TwitterException::class)
    override fun verifyCredentials(): User {
        return twitter.fillInIDAndScreenName(
            arrayOf(HttpParameter("include_email", twitter.conf.isIncludeEmailEnabled()))
        )
    }

    @Throws(TwitterException::class)
    override fun updateAccountSettings(
        trendLocationWoeid: Int?, sleepTimeEnabled: Boolean?, startSleepTime: String?,
        endSleepTime: String?, timeZone: String?, lang: String?
    ): AccountSettings {
        val profile: MutableList<HttpParameter> = ArrayList(6)
        if (trendLocationWoeid != null) {
            profile.add(HttpParameter("trend_location_woeid", trendLocationWoeid))
        }
        if (sleepTimeEnabled != null) {
            profile.add(HttpParameter("sleep_time_enabled", sleepTimeEnabled.toString()))
        }
        if (startSleepTime != null) {
            profile.add(HttpParameter("start_sleep_time", startSleepTime))
        }
        if (endSleepTime != null) {
            profile.add(HttpParameter("end_sleep_time", endSleepTime))
        }
        if (timeZone != null) {
            profile.add(HttpParameter("time_zone", timeZone))
        }
        if (lang != null) {
            profile.add(HttpParameter("lang", lang))
        }
        return twitter.factory.createAccountSettings(
            twitter.post(twitter.conf.getRestBaseURL() + "account/settings.json", *profile.toTypedArray())
        )
    }

    @Throws(TwitterException::class)
    override fun updateAllowDmsFrom(allowDmsFrom: String): AccountSettings {
        return twitter.factory.createAccountSettings(
            twitter.post(twitter.conf.getRestBaseURL() + "account/settings.json?allow_dms_from=" + allowDmsFrom)
        )
    }

    @Throws(TwitterException::class)
    override fun updateProfile(name: String?, url: String?, location: String?, description: String?): User {
        val profile: MutableList<HttpParameter> = ArrayList(4)
        addParameterToList(profile, "name", name)
        addParameterToList(profile, "url", url)
        addParameterToList(profile, "location", location)
        addParameterToList(profile, "description", description)
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "account/update_profile.json", *profile.toTypedArray())
        )
    }


    private fun addParameterToList(colors: MutableList<HttpParameter>, paramName: String, color: String?) {
        if (color != null) {
            colors.add(HttpParameter(paramName, color))
        }
    }

    @Throws(TwitterException::class)
    override fun updateProfileImage(image: File): User {
        MediaUploadSupport.checkFileValidity(image)
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "account/update_profile_image.json", MediaUploadSupport.mediaHttpParameter("image", image))
        )
    }

    @Throws(TwitterException::class)
    override fun updateProfileImage(image: InputStream): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "account/update_profile_image.json", MediaUploadSupport.mediaHttpParameter("image", "image", image))
        )
    }

    // KMP移行 Phase 4-4: checkFileValidity（java.io.File の存在・種別チェック）は JVM 固有処理のため
    // MediaUploadSupport.checkFileValidity へ移設した。

    @Throws(TwitterException::class)
    override fun getBlocksList(): PagableResponseList<User> {
        return getBlocksList(-1L)
    }

    @Throws(TwitterException::class)
    override fun getBlocksList(cursor: Long): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "blocks/list.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun getBlocksIDs(): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "blocks/ids.json"))
    }

    @Throws(TwitterException::class)
    override fun getBlocksIDs(cursor: Long): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "blocks/ids.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun createBlock(userId: Long): User {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "blocks/create.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun createBlock(screenName: String): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "blocks/create.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun destroyBlock(userId: Long): User {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "blocks/destroy.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun destroyBlock(screen_name: String): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "blocks/destroy.json", HttpParameter("screen_name", screen_name))
        )
    }


    @Throws(TwitterException::class)
    override fun getMutesList(cursor: Long): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "mutes/users/list.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun getMutesIDs(cursor: Long): IDs {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "mutes/users/ids.json?cursor=" + cursor))
    }

    @Throws(TwitterException::class)
    override fun createMute(userId: Long): User {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/create.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun createMute(screenName: String): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/create.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun destroyMute(userId: Long): User {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/destroy.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun destroyMute(screenName: String): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/destroy.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun lookupUsers(vararg ids: Long): ResponseList<User> {
        return twitter.factory.createUserList(
            twitter.get(twitter.conf.getRestBaseURL() + "users/lookup.json", HttpParameter("user_id", StringUtil.join(ids)))
        )
    }

    @Throws(TwitterException::class)
    override fun lookupUsers(vararg screenNames: String): ResponseList<User> {
        return twitter.factory.createUserList(
            twitter.get(twitter.conf.getRestBaseURL() + "users/lookup.json", HttpParameter("screen_name", StringUtil.join(arrayOf(*screenNames))))
        )
    }

    @Throws(TwitterException::class)
    override fun showUser(userId: Long): User {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() + "users/show.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun showUser(screenName: String): User {
        return twitter.factory.createUser(
            twitter.get(twitter.conf.getRestBaseURL() + "users/show.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun searchUsers(query: String, page: Int): ResponseList<User> {
        return twitter.factory.createUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "users/search.json",
                HttpParameter("q", query), HttpParameter("per_page", 20),
                HttpParameter("page", page)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getContributees(userId: Long): ResponseList<User> {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/contributees.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun getContributees(screenName: String): ResponseList<User> {
        return twitter.factory.createUserList(
            twitter.get(twitter.conf.getRestBaseURL() + "users/contributees.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun getContributors(userId: Long): ResponseList<User> {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/contributors.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun getContributors(screenName: String): ResponseList<User> {
        return twitter.factory.createUserList(
            twitter.get(twitter.conf.getRestBaseURL() + "users/contributors.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun removeProfileBanner() {
        twitter.post(twitter.conf.getRestBaseURL() + "account/remove_profile_banner.json")
    }

    @Throws(TwitterException::class)
    override fun updateProfileBanner(image: File) {
        MediaUploadSupport.checkFileValidity(image)
        twitter.post(twitter.conf.getRestBaseURL() + "account/update_profile_banner.json", MediaUploadSupport.mediaHttpParameter("banner", image))
    }

    @Throws(TwitterException::class)
    override fun updateProfileBanner(image: InputStream) {
        twitter.post(twitter.conf.getRestBaseURL() + "account/update_profile_banner.json", MediaUploadSupport.mediaHttpParameter("banner", "banner", image))
    }

    /* Favorites Resources */

    @Throws(TwitterException::class)
    override fun getFavorites(): ResponseList<Status> {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json"))
    }

    @Throws(TwitterException::class)
    override fun getFavorites(userId: Long): ResponseList<Status> {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun getFavorites(screenName: String): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json", HttpParameter("screen_name", screenName))
        )
    }

    @Throws(TwitterException::class)
    override fun getFavorites(paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json", *paging.asPostParameterArray())
        )
    }

    @Throws(TwitterException::class)
    override fun getFavorites(userId: Long, paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "favorites/list.json",
                *twitter.mergeParameters(arrayOf(HttpParameter("user_id", userId)), paging.asPostParameterArray())
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getFavorites(screenName: String, paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "favorites/list.json",
                *twitter.mergeParameters(arrayOf(HttpParameter("screen_name", screenName)), paging.asPostParameterArray())
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyFavorite(id: Long): Status {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "favorites/destroy.json?id=" + id))
    }

    @Throws(TwitterException::class)
    override fun createFavorite(id: Long): Status {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "favorites/create.json?id=" + id))
    }

    /* Lists Resources */

    @Throws(TwitterException::class)
    override fun getUserLists(listOwnerScreenName: String): ResponseList<UserList> {
        return getUserLists(listOwnerScreenName, false)
    }

    @Throws(TwitterException::class)
    override fun getUserLists(listOwnerScreenName: String, reverse: Boolean): ResponseList<UserList> {
        return twitter.factory.createUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/list.json",
                HttpParameter("screen_name", listOwnerScreenName),
                HttpParameter("reverse", reverse)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserLists(listOwnerUserId: Long): ResponseList<UserList> {
        return getUserLists(listOwnerUserId, false)
    }

    @Throws(TwitterException::class)
    override fun getUserLists(listOwnerUserId: Long, reverse: Boolean): ResponseList<UserList> {
        return twitter.factory.createUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/list.json",
                HttpParameter("user_id", listOwnerUserId),
                HttpParameter("reverse", reverse)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListStatuses(listId: Long, paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/statuses.json",
                *twitter.mergeParameters(paging.asPostParameterArray(Paging.SMCP, Paging.COUNT), HttpParameter("list_id", listId))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListStatuses(ownerId: Long, slug: String, paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/statuses.json",
                *twitter.mergeParameters(
                    paging.asPostParameterArray(Paging.SMCP, Paging.COUNT),
                    arrayOf(HttpParameter("owner_id", ownerId), HttpParameter("slug", slug))
                )
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListStatuses(ownerScreenName: String, slug: String, paging: Paging): ResponseList<Status> {
        return twitter.factory.createStatusList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/statuses.json",
                *twitter.mergeParameters(
                    paging.asPostParameterArray(Paging.SMCP, Paging.COUNT),
                    arrayOf(HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug))
                )
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListMember(listId: Long, userId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/destroy.json",
                HttpParameter("list_id", listId), HttpParameter("user_id", userId)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListMember(ownerId: Long, slug: String, userId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/destroy.json",
                HttpParameter("owner_id", ownerId), HttpParameter("slug", slug), HttpParameter("user_id", userId)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListMember(listId: Long, screenName: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/destroy.json",
                HttpParameter("list_id", listId), HttpParameter("screen_name", screenName)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListMember(ownerScreenName: String, slug: String, userId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/destroy.json",
                HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug), HttpParameter("user_id", userId)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListMembers(listId: Long, screenNames: Array<String>): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/destroy_all.json",
                HttpParameter("list_id", listId), HttpParameter("screen_name", StringUtil.join(screenNames))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListMembers(listId: Long, userIds: LongArray): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/destroy_all.json",
                HttpParameter("list_id", listId), HttpParameter("user_id", StringUtil.join(userIds))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListMembers(ownerScreenName: String, slug: String, screenNames: Array<String>): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/destroy_all.json",
                HttpParameter("owner_screen_name", ownerScreenName),
                HttpParameter("slug", slug),
                HttpParameter("screen_name", StringUtil.join(screenNames))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(cursor: Long): PagableResponseList<UserList> {
        return getUserListMemberships(20, cursor)
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(count: Int, cursor: Long): PagableResponseList<UserList> {
        return twitter.factory.createPagableUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/memberships.json",
                HttpParameter("cursor", cursor),
                HttpParameter("count", count)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberScreenName: String, cursor: Long): PagableResponseList<UserList> {
        return getUserListMemberships(listMemberScreenName, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberScreenName: String, count: Int, cursor: Long): PagableResponseList<UserList> {
        return getUserListMemberships(listMemberScreenName, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberScreenName: String, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList> {
        return getUserListMemberships(listMemberScreenName, 20, cursor, filterToOwnedLists)
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberScreenName: String, count: Int, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList> {
        return twitter.factory.createPagableUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/memberships.json",
                HttpParameter("screen_name", listMemberScreenName),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("filter_to_owned_lists", filterToOwnedLists)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberId: Long, cursor: Long): PagableResponseList<UserList> {
        return getUserListMemberships(listMemberId, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberId: Long, count: Int, cursor: Long): PagableResponseList<UserList> {
        return getUserListMemberships(listMemberId, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberId: Long, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList> {
        return getUserListMemberships(listMemberId, 20, cursor, filterToOwnedLists)
    }

    @Throws(TwitterException::class)
    override fun getUserListMemberships(listMemberId: Long, count: Int, cursor: Long, filterToOwnedLists: Boolean): PagableResponseList<UserList> {
        return twitter.factory.createPagableUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/memberships.json",
                HttpParameter("user_id", listMemberId),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("filter_to_owned_lists", filterToOwnedLists)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(listId: Long, cursor: Long): PagableResponseList<User> {
        return getUserListSubscribers(listId, 20, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(listId: Long, count: Int, cursor: Long): PagableResponseList<User> {
        return getUserListSubscribers(listId, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(listId: Long, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/subscribers.json",
                HttpParameter("list_id", listId),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("skip_status", skipStatus)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(ownerId: Long, slug: String, cursor: Long): PagableResponseList<User> {
        return getUserListSubscribers(ownerId, slug, 20, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(ownerId: Long, slug: String, count: Int, cursor: Long): PagableResponseList<User> {
        return getUserListSubscribers(ownerId, slug, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(ownerId: Long, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/subscribers.json",
                HttpParameter("owner_id", ownerId),
                HttpParameter("slug", slug),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("skip_status", skipStatus)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(ownerScreenName: String, slug: String, cursor: Long): PagableResponseList<User> {
        return getUserListSubscribers(ownerScreenName, slug, 20, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(ownerScreenName: String, slug: String, count: Int, cursor: Long): PagableResponseList<User> {
        return getUserListSubscribers(ownerScreenName, slug, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscribers(ownerScreenName: String, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/subscribers.json",
                HttpParameter("owner_screen_name", ownerScreenName),
                HttpParameter("slug", slug),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("skip_status", skipStatus)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListSubscription(listId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(twitter.conf.getRestBaseURL() + "lists/subscribers/create.json", HttpParameter("list_id", listId))
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListSubscription(ownerId: Long, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/subscribers/create.json",
                HttpParameter("owner_id", ownerId), HttpParameter("slug", slug)
            )
        )
    }


    @Throws(TwitterException::class)
    override fun createUserListSubscription(ownerScreenName: String, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/subscribers/create.json",
                HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun showUserListSubscription(listId: Long, userId: Long): User {
        return twitter.factory.createUser(
            twitter.get(twitter.conf.getRestBaseURL() + "lists/subscribers/show.json?list_id=" + listId + "&user_id=" + userId)
        )
    }

    @Throws(TwitterException::class)
    override fun showUserListSubscription(ownerId: Long, slug: String, userId: Long): User {
        return twitter.factory.createUser(
            twitter.get(twitter.conf.getRestBaseURL() + "lists/subscribers/show.json?owner_id=" + ownerId + "&slug=" + slug + "&user_id=" + userId)
        )
    }

    @Throws(TwitterException::class)
    override fun showUserListSubscription(ownerScreenName: String, slug: String, userId: Long): User {
        return twitter.factory.createUser(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/subscribers/show.json",
                HttpParameter("owner_screen_name", ownerScreenName),
                HttpParameter("slug", slug),
                HttpParameter("user_id", userId)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListSubscription(listId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(twitter.conf.getRestBaseURL() + "lists/subscribers/destroy.json", HttpParameter("list_id", listId))
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListSubscription(ownerId: Long, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/subscribers/destroy.json",
                HttpParameter("owner_id", ownerId), HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserListSubscription(ownerScreenName: String, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/subscribers/destroy.json",
                HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMembers(listId: Long, vararg userIds: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                HttpParameter("list_id", listId), HttpParameter("user_id", StringUtil.join(userIds))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMembers(ownerId: Long, slug: String, vararg userIds: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                HttpParameter("owner_id", ownerId), HttpParameter("slug", slug),
                HttpParameter("user_id", StringUtil.join(userIds))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMembers(ownerScreenName: String, slug: String, vararg userIds: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug),
                HttpParameter("user_id", StringUtil.join(userIds))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMembers(listId: Long, vararg screenNames: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                HttpParameter("list_id", listId),
                HttpParameter("screen_name", StringUtil.join(arrayOf(*screenNames)))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMembers(ownerId: Long, slug: String, vararg screenNames: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                HttpParameter("owner_id", ownerId), HttpParameter("slug", slug),
                HttpParameter("screen_name", StringUtil.join(arrayOf(*screenNames)))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMembers(ownerScreenName: String, slug: String, vararg screenNames: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug),
                HttpParameter("screen_name", StringUtil.join(arrayOf(*screenNames)))
            )
        )
    }

    @Throws(TwitterException::class)
    override fun showUserListMembership(listId: Long, userId: Long): User {
        return twitter.factory.createUser(
            twitter.get(twitter.conf.getRestBaseURL() + "lists/members/show.json?list_id=" + listId + "&user_id=" + userId)
        )
    }

    @Throws(TwitterException::class)
    override fun showUserListMembership(ownerId: Long, slug: String, userId: Long): User {
        return twitter.factory.createUser(
            twitter.get(twitter.conf.getRestBaseURL() + "lists/members/show.json?owner_id=" + ownerId + "&slug=" + slug + "&user_id=" + userId)
        )
    }

    @Throws(TwitterException::class)
    override fun showUserListMembership(ownerScreenName: String, slug: String, userId: Long): User {
        return twitter.factory.createUser(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/members/show.json",
                HttpParameter("owner_screen_name", ownerScreenName),
                HttpParameter("slug", slug),
                HttpParameter("user_id", userId)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(listId: Long, cursor: Long): PagableResponseList<User> {
        return getUserListMembers(listId, 20, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(listId: Long, count: Int, cursor: Long): PagableResponseList<User> {
        return getUserListMembers(listId, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(listId: Long, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/members.json",
                HttpParameter("list_id", listId),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("skip_status", skipStatus)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(ownerId: Long, slug: String, cursor: Long): PagableResponseList<User> {
        return getUserListMembers(ownerId, slug, 20, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(ownerId: Long, slug: String, count: Int, cursor: Long): PagableResponseList<User> {
        return getUserListMembers(ownerId, slug, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(ownerId: Long, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/members.json",
                HttpParameter("owner_id", ownerId),
                HttpParameter("slug", slug),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("skip_status", skipStatus)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(ownerScreenName: String, slug: String, cursor: Long): PagableResponseList<User> {
        return getUserListMembers(ownerScreenName, slug, 20, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(ownerScreenName: String, slug: String, count: Int, cursor: Long): PagableResponseList<User> {
        return getUserListMembers(ownerScreenName, slug, count, cursor, false)
    }

    @Throws(TwitterException::class)
    override fun getUserListMembers(ownerScreenName: String, slug: String, count: Int, cursor: Long, skipStatus: Boolean): PagableResponseList<User> {
        return twitter.factory.createPagableUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/members.json",
                HttpParameter("owner_screen_name", ownerScreenName),
                HttpParameter("slug", slug),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor),
                HttpParameter("skip_status", skipStatus)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMember(listId: Long, userId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create.json",
                HttpParameter("user_id", userId), HttpParameter("list_id", listId)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMember(ownerId: Long, slug: String, userId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create.json",
                HttpParameter("user_id", userId), HttpParameter("owner_id", ownerId), HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun createUserListMember(ownerScreenName: String, slug: String, userId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/members/create.json",
                HttpParameter("user_id", userId), HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserList(listId: Long): UserList {
        return twitter.factory.createAUserList(
            twitter.post(twitter.conf.getRestBaseURL() + "lists/destroy.json", HttpParameter("list_id", listId))
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserList(ownerId: Long, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/destroy.json",
                HttpParameter("owner_id", ownerId), HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun destroyUserList(ownerScreenName: String, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.post(
                twitter.conf.getRestBaseURL() + "lists/destroy.json",
                HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun updateUserList(listId: Long, newListName: String?, isPublicList: Boolean, newDescription: String?): UserList {
        return updateUserList(newListName, isPublicList, newDescription, HttpParameter("list_id", listId))
    }

    @Throws(TwitterException::class)
    override fun updateUserList(ownerId: Long, slug: String, newListName: String?, isPublicList: Boolean, newDescription: String?): UserList {
        return updateUserList(
            newListName, isPublicList, newDescription,
            HttpParameter("owner_id", ownerId), HttpParameter("slug", slug)
        )
    }

    @Throws(TwitterException::class)
    override fun updateUserList(ownerScreenName: String, slug: String, newListName: String?, isPublicList: Boolean, newDescription: String?): UserList {
        return updateUserList(
            newListName, isPublicList, newDescription,
            HttpParameter("owner_screen_name", ownerScreenName), HttpParameter("slug", slug)
        )
    }

    @Throws(TwitterException::class)
    private fun updateUserList(newListName: String?, isPublicList: Boolean, newDescription: String?, vararg params: HttpParameter): UserList {
        val httpParams: MutableList<HttpParameter> = ArrayList()
        httpParams.addAll(params)
        if (newListName != null) {
            httpParams.add(HttpParameter("name", newListName))
        }
        httpParams.add(HttpParameter("mode", if (isPublicList) "public" else "private"))
        if (newDescription != null) {
            httpParams.add(HttpParameter("description", newDescription))
        }
        return twitter.factory.createAUserList(
            twitter.post(twitter.conf.getRestBaseURL() + "lists/update.json", *httpParams.toTypedArray())
        )
    }

    @Throws(TwitterException::class)
    override fun createUserList(listName: String, isPublicList: Boolean, description: String?): UserList {
        val httpParams: MutableList<HttpParameter> = ArrayList()
        httpParams.add(HttpParameter("name", listName))
        httpParams.add(HttpParameter("mode", if (isPublicList) "public" else "private"))
        if (description != null) {
            httpParams.add(HttpParameter("description", description))
        }
        return twitter.factory.createAUserList(
            twitter.post(twitter.conf.getRestBaseURL() + "lists/create.json", *httpParams.toTypedArray())
        )
    }

    @Throws(TwitterException::class)
    override fun showUserList(listId: Long): UserList {
        return twitter.factory.createAUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/show.json?list_id=" + listId))
    }

    @Throws(TwitterException::class)
    override fun showUserList(ownerId: Long, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.get(twitter.conf.getRestBaseURL() + "lists/show.json?owner_id=" + ownerId + "&slug=" + slug)
        )
    }

    @Throws(TwitterException::class)
    override fun showUserList(ownerScreenName: String, slug: String): UserList {
        return twitter.factory.createAUserList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/show.json",
                HttpParameter("owner_screen_name", ownerScreenName),
                HttpParameter("slug", slug)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscriptions(listSubscriberScreenName: String, cursor: Long): PagableResponseList<UserList> {
        return getUserListSubscriptions(listSubscriberScreenName, 20, cursor)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscriptions(listSubscriberScreenName: String, count: Int, cursor: Long): PagableResponseList<UserList> {
        return twitter.factory.createPagableUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/subscriptions.json",
                HttpParameter("screen_name", listSubscriberScreenName),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscriptions(listSubscriberId: Long, cursor: Long): PagableResponseList<UserList> {
        return getUserListSubscriptions(listSubscriberId, 20, cursor)
    }

    @Throws(TwitterException::class)
    override fun getUserListSubscriptions(listSubscriberId: Long, count: Int, cursor: Long): PagableResponseList<UserList> {
        return twitter.factory.createPagableUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/subscriptions.json",
                HttpParameter("user_id", listSubscriberId),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListsOwnerships(listOwnerScreenName: String, cursor: Long): PagableResponseList<UserList> {
        return getUserListsOwnerships(listOwnerScreenName, 20, cursor)
    }

    @Throws(TwitterException::class)
    override fun getUserListsOwnerships(listOwnerScreenName: String, count: Int, cursor: Long): PagableResponseList<UserList> {
        return twitter.factory.createPagableUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/ownerships.json",
                HttpParameter("screen_name", listOwnerScreenName),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor)
            )
        )
    }

    @Throws(TwitterException::class)
    override fun getUserListsOwnerships(listOwnerId: Long, cursor: Long): PagableResponseList<UserList> {
        return getUserListsOwnerships(listOwnerId, 20, cursor)
    }

    @Throws(TwitterException::class)
    override fun getUserListsOwnerships(listOwnerId: Long, count: Int, cursor: Long): PagableResponseList<UserList> {
        return twitter.factory.createPagableUserListList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "lists/ownerships.json",
                HttpParameter("user_id", listOwnerId),
                HttpParameter("count", count),
                HttpParameter("cursor", cursor)
            )
        )
    }

    /* Saved Searches Resources */

    @Throws(TwitterException::class)
    override fun getSavedSearches(): ResponseList<SavedSearch> {
        return twitter.factory.createSavedSearchList(twitter.get(twitter.conf.getRestBaseURL() + "saved_searches/list.json"))
    }

    @Throws(TwitterException::class)
    override fun showSavedSearch(id: Long): SavedSearch {
        return twitter.factory.createSavedSearch(twitter.get(twitter.conf.getRestBaseURL() + "saved_searches/show/" + id + ".json"))
    }

    @Throws(TwitterException::class)
    override fun createSavedSearch(query: String): SavedSearch {
        return twitter.factory.createSavedSearch(
            twitter.post(twitter.conf.getRestBaseURL() + "saved_searches/create.json", HttpParameter("query", query))
        )
    }

    @Throws(TwitterException::class)
    override fun destroySavedSearch(id: Long): SavedSearch {
        return twitter.factory.createSavedSearch(
            twitter.post(twitter.conf.getRestBaseURL() + "saved_searches/destroy/" + id + ".json")
        )
    }

    /* Trends Resources */

    @Throws(TwitterException::class)
    override fun getPlaceTrends(woeid: Int): Trends {
        return twitter.factory.createTrends(twitter.get(twitter.conf.getRestBaseURL() + "trends/place.json?id=" + woeid))
    }

    @Throws(TwitterException::class)
    override fun getAvailableTrends(): ResponseList<Location> {
        return twitter.factory.createLocationList(twitter.get(twitter.conf.getRestBaseURL() + "trends/available.json"))
    }

    @Throws(TwitterException::class)
    override fun getClosestTrends(location: GeoLocation): ResponseList<Location> {
        return twitter.factory.createLocationList(
            twitter.get(
                twitter.conf.getRestBaseURL() + "trends/closest.json",
                HttpParameter("lat", location.latitude), HttpParameter("long", location.longitude)
            )
        )
    }

    /* Spam Reporting Resources */

    @Throws(TwitterException::class)
    override fun reportSpam(userId: Long): User {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "users/report_spam.json?user_id=" + userId))
    }

    @Throws(TwitterException::class)
    override fun reportSpam(screenName: String): User {
        return twitter.factory.createUser(
            twitter.post(twitter.conf.getRestBaseURL() + "users/report_spam.json", HttpParameter("screen_name", screenName))
        )
    }

    /* Help Resources */

    @Throws(TwitterException::class)
    override fun getAPIConfiguration(): TwitterAPIConfiguration {
        return twitter.factory.createTwitterAPIConfiguration(twitter.get(twitter.conf.getRestBaseURL() + "help/configuration.json"))
    }

    @Throws(TwitterException::class)
    override fun getLanguages(): ResponseList<HelpResources.Language> {
        return twitter.factory.createLanguageList(twitter.get(twitter.conf.getRestBaseURL() + "help/languages.json"))
    }

    @Throws(TwitterException::class)
    override fun getPrivacyPolicy(): String {
        try {
            return twitter.get(twitter.conf.getRestBaseURL() + "help/privacy.json").asJSONObject().getString("privacy")
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    @Throws(TwitterException::class)
    override fun getTermsOfService(): String {
        try {
            return twitter.get(twitter.conf.getRestBaseURL() + "help/tos.json").asJSONObject().getString("tos")
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    @Throws(TwitterException::class)
    override fun getRateLimitStatus(): Map<String, RateLimitStatus> {
        return twitter.factory.createRateLimitStatuses(twitter.get(twitter.conf.getRestBaseURL() + "application/rate_limit_status.json"))
    }

    @Throws(TwitterException::class)
    override fun getRateLimitStatus(vararg resources: String): Map<String, RateLimitStatus> {
        return twitter.factory.createRateLimitStatuses(
            twitter.get(twitter.conf.getRestBaseURL() + "application/rate_limit_status.json?resources=" + StringUtil.join(arrayOf(*resources)))
        )
    }

}
