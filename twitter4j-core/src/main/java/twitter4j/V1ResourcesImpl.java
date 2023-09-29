package twitter4j;

import static twitter4j.HttpParameter.getParameterArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import twitter4j.api.DirectMessagesResources;
import twitter4j.api.FavoritesResources;
import twitter4j.api.FriendsFollowersResources;
import twitter4j.api.HelpResources;
import twitter4j.api.ListsResources;
import twitter4j.api.PlacesGeoResources;
import twitter4j.api.SavedSearchesResources;
import twitter4j.api.SearchResource;
import twitter4j.api.SpamReportingResource;
import twitter4j.api.SuggestedUsersResources;
import twitter4j.api.TimelinesResources;
import twitter4j.api.TrendsResources;
import twitter4j.api.TweetsResources;
import twitter4j.api.UsersResources;
import twitter4j.api.V1Resources;
import twitter4j.conf.ChunkedUploadConfiguration;

class V1ResourcesImpl implements V1Resources {
    
    private final TwitterImpl twitter;
    
    public V1ResourcesImpl(TwitterImpl twitter) {
        this.twitter = twitter;
    }

    @Override
    public TimelinesResources timelines() {
        return this;
    }

    @Override
    public TweetsResources tweets() {
        return this;
    }

    @Override
    public SearchResource search() {
        return this;
    }

    @Override
    public DirectMessagesResources directMessages() {
        return this;
    }

    @Override
    public FriendsFollowersResources friendsFollowers() {
        return this;
    }

    @Override
    public UsersResources users() {
        return this;
    }

    @Override
    public SuggestedUsersResources suggestedUsers() {
        return this;
    }

    @Override
    public FavoritesResources favorites() {
        return this;
    }

    @Override
    public ListsResources list() {
        return this;
    }

    @Override
    public SavedSearchesResources savedSearches() {
        return this;
    }

    @Override
    public PlacesGeoResources placesGeo() {
        return this;
    }

    @Override
    public TrendsResources trends() {
        return this;
    }

    @Override
    public SpamReportingResource spamReporting() {
        return this;
    }

    @Override
    public HelpResources help() {
        return this;
    }


    /* Timelines Resources */

    @Override
    public ResponseList<Status> getMentionsTimeline() throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "statuses/mentions_timeline.json"));
    }

    @Override
    public ResponseList<Status> getMentionsTimeline(Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL()
                + "statuses/mentions_timeline.json", paging.asPostParameterArray()));
    }

    @Override
    public ResponseList<Status> getHomeTimeline() throws
            TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL()
                + "statuses/home_timeline.json", twitter.INCLUDE_MY_RETWEET));
    }

    @Override
    public ResponseList<Status> getHomeTimeline(Paging paging) throws
            TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL()
                + "statuses/home_timeline.json", twitter.mergeParameters(paging.asPostParameterArray(), new HttpParameter[]{twitter.INCLUDE_MY_RETWEET})));
    }

    @Override
    public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL()
                + "statuses/retweets_of_me.json"));
    }

    @Override
    public ResponseList<Status> getRetweetsOfMe(Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL()
                + "statuses/retweets_of_me.json", paging.asPostParameterArray()));
    }

    @Override
    public ResponseList<Status> getUserTimeline(String screenName, Paging paging)
            throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL()
                        + "statuses/user_timeline.json",
                twitter.mergeParameters(new HttpParameter[]{new HttpParameter("screen_name", screenName)
                                , twitter.INCLUDE_MY_RETWEET}
                        , paging.asPostParameterArray())
        ));
    }

    @Override
    public ResponseList<Status> getUserTimeline(long userId, Paging paging)
            throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL()
                        + "statuses/user_timeline.json",
                twitter.mergeParameters(new HttpParameter[]{new HttpParameter("user_id", userId)
                                , twitter.INCLUDE_MY_RETWEET}
                        , paging.asPostParameterArray())
        ));
    }

    @Override
    public ResponseList<Status> getUserTimeline(String screenName) throws TwitterException {
        return getUserTimeline(screenName, new Paging());
    }

    @Override
    public ResponseList<Status> getUserTimeline(long userId) throws TwitterException {
        return getUserTimeline(userId, new Paging());
    }

    @Override
    public ResponseList<Status> getUserTimeline() throws
            TwitterException {
        return getUserTimeline(new Paging());
    }

    @Override
    public ResponseList<Status> getUserTimeline(Paging paging) throws
            TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() +
                        "statuses/user_timeline.json",
                twitter.mergeParameters(new HttpParameter[]{twitter.INCLUDE_MY_RETWEET}
                        , paging.asPostParameterArray())
        ));
    }

    /* Tweets Resources */

    @Override
    public ResponseList<Status> getRetweets(long statusId) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "statuses/retweets/" + statusId
                + ".json?count=100"));
    }

    @Override
    public IDs getRetweeterIds(long statusId, long cursor) throws TwitterException {
        return getRetweeterIds(statusId, 100, cursor);
    }

    @Override
    public IDs getRetweeterIds(long statusId, int count, long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "statuses/retweeters/ids.json?id=" + statusId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public Status showStatus(long id) throws TwitterException {
        return twitter.factory.createStatus(twitter.get(twitter.conf.getRestBaseURL() + "statuses/show/" + id + ".json", new HttpParameter[]{twitter.INCLUDE_MY_RETWEET}));
    }

    @Override
    public Status destroyStatus(long statusId) throws TwitterException {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "statuses/destroy/" + statusId + ".json"));
    }

    @Override
    public Status updateStatus(String status) throws TwitterException {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "statuses/update.json",
                new HttpParameter[]{new HttpParameter("status", status)}));
    }

    @Override
    public Status updateStatus(StatusUpdate status) throws TwitterException {
        String url = twitter.conf.getRestBaseURL() + (status.isForUpdateWithMedia() ?
                "statuses/update_with_media.json" : "statuses/update.json");
        return twitter.factory.createStatus(twitter.post(url, status.asHttpParameterArray()));
    }

    @Override
    public Status retweetStatus(long statusId) throws TwitterException {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "statuses/retweet/" + statusId + ".json"));
    }

    @Override
    public Status unRetweetStatus(long statusId) throws TwitterException {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "statuses/unretweet/" + statusId + ".json"));
    }

    @Override
    public OEmbed getOEmbed(OEmbedRequest req) throws TwitterException {
        return twitter.factory.createOEmbed(twitter.get(twitter.conf.getRestBaseURL()
                + "statuses/oembed.json", req.asHttpParameterArray()));
    }

    @Override
    public ResponseList<Status> lookup(long... ids) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "statuses/lookup.json?id=" + StringUtil.join(ids)));
    }

    @Override
    public UploadedMedia uploadMedia(File image) throws TwitterException {
        checkFileValidity(image);
        return new UploadedMedia(twitter.post(twitter.conf.getUploadBaseURL() + "media/upload.json"
                , new HttpParameter("media", image)).asJSONObject());
    }

    @Override
    public UploadedMedia uploadMedia(String fileName, InputStream image) throws TwitterException {
        return new UploadedMedia(twitter.post(twitter.conf.getUploadBaseURL() + "media/upload.json"
                , new HttpParameter("media", fileName, image)).asJSONObject());
    }

    @Override
    public int createMediaMetadata(long mediaId, String altText) throws TwitterException {
        try {
            JSONObject json = new JSONObject()
                    .put("media_id", ""+mediaId)
                    .put("alt_text", new JSONObject().put("text", altText));
            return twitter.post(twitter.conf.getUploadBaseURL() + "media/metadata/create.json", json).getStatusCode();
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public UploadedMedia uploadMediaChunked(ChunkedUploadConfiguration uploadConfiguration) throws TwitterException {

        return new ChunkedUploadDelegate(twitter).uploadMediaChunked(uploadConfiguration);
    }

    /* Search Resources */

    @Override
    public QueryResult search(Query query) throws TwitterException {
        if (query.nextPage() != null) {
            return twitter.factory.createQueryResult(twitter.get(twitter.conf.getRestBaseURL()
                    + "search/tweets.json" + query.nextPage()), query);
        } else {
            return twitter.factory.createQueryResult(twitter.get(twitter.conf.getRestBaseURL()
                    + "search/tweets.json", query.asHttpParameterArray()), query);
        }
    }

    /* Direct Messages Resources */


    @Override
    public DirectMessageList getDirectMessages(int count) throws TwitterException {
        return twitter.factory.createDirectMessageList(twitter.get(twitter.conf.getRestBaseURL() + "direct_messages/events/list.json"
                , new HttpParameter("count", count) ));
    }

    @Override
    public DirectMessageList getDirectMessages(int count, String cursor) throws TwitterException {
        return twitter.factory.createDirectMessageList(twitter.get(twitter.conf.getRestBaseURL() + "direct_messages/events/list.json"
                , new HttpParameter("count", count)
                , new HttpParameter("cursor", cursor)));
    }


    @Override
    public DirectMessage showDirectMessage(long id) throws TwitterException {
        return twitter.factory.createDirectMessage(twitter.get(twitter.conf.getRestBaseURL() + "direct_messages/events/show.json?id=" + id));
    }

    @Override
    public void destroyDirectMessage(long id) throws TwitterException {
        twitter.ensureAuthorizationEnabled();
        twitter.http.delete(twitter.conf.getRestBaseURL() + "direct_messages/events/destroy.json?id=" + id, null, twitter.auth, null);
    }

    @Override
    public DirectMessage sendDirectMessage(long recipientId, String text, QuickReply... quickReplies)
            throws TwitterException {
        try {
            return twitter.factory.createDirectMessage(twitter.post(twitter.conf.getRestBaseURL() + "direct_messages/events/new.json",
                    createMessageCreateJsonObject(recipientId, text, -1L,  null, quickReplies)));
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }
    @Override
    public DirectMessage sendDirectMessage(long recipientId, String text, String quickReplyResponse)
            throws TwitterException {
        try {
            return twitter.factory.createDirectMessage(twitter.post(twitter.conf.getRestBaseURL() + "direct_messages/events/new.json",
                    createMessageCreateJsonObject(recipientId, text, -1L,  quickReplyResponse)));
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    private static JSONObject createMessageCreateJsonObject(long recipientId, String text, long mediaId, String quickReplyResponse, QuickReply... quickReplies) throws JSONException {
        String type = mediaId == -1 ? null : "media";

        final JSONObject messageDataJSON = new JSONObject();

        final JSONObject target = new JSONObject();
        target.put("recipient_id", recipientId);
        messageDataJSON.put("target", target);

        final JSONObject messageData = new JSONObject();
        messageData.put("text", text);
        if (type != null && mediaId != -1) {
            final JSONObject attachment = new JSONObject();
            attachment.put("type", type);
            if (type.equals("media")) {
                final JSONObject media = new JSONObject();
                media.put("id", mediaId);
                attachment.put("media", media);
            }
            messageData.put("attachment", attachment);
        }
        // https://developer.twitter.com/en/docs/direct-messages/quick-replies/api-reference/options
        if (quickReplies.length > 0) {
            JSONObject quickReplyJSON = new JSONObject();
            quickReplyJSON.put("type", "options");
            JSONArray jsonArray = new JSONArray();
            for (QuickReply quickReply : quickReplies) {
                JSONObject option = new JSONObject();
                option.put("label", quickReply.getLabel());
                if (quickReply.getDescription() != null) {
                    option.put("description", quickReply.getDescription());
                }
                if (quickReply.getMetadata() != null) {
                    option.put("metadata", quickReply.getMetadata());
                }
                jsonArray.put(option);
            }
            quickReplyJSON.put("options",jsonArray);
            messageData.put("quick_reply", quickReplyJSON);
        }
        if (quickReplyResponse != null) {
            JSONObject quickReplyResponseJSON = new JSONObject();
            quickReplyResponseJSON.put("type","options");
            quickReplyResponseJSON.put("metadata", quickReplyResponse);
            messageData.put("quick_reply_response", quickReplyResponseJSON);
        }
        messageDataJSON.put("message_data", messageData);

        final JSONObject json = new JSONObject();
        final JSONObject event = new JSONObject();
        event.put("type", "message_create");
        event.put("message_create", messageDataJSON);
        json.put("event", event);

        return json;
    }

    @Override
    public DirectMessage sendDirectMessage(long recipientId, String text, long mediaId)
            throws TwitterException {
        try {
            return twitter.factory.createDirectMessage(twitter.post(twitter.conf.getRestBaseURL() + "direct_messages/events/new.json",
                    createMessageCreateJsonObject(recipientId, text, mediaId, null)));
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public DirectMessage sendDirectMessage(long recipientId, String text)
            throws TwitterException {
        return this.sendDirectMessage(recipientId, text, -1L);
    }

    @Override
    public DirectMessage sendDirectMessage(String screenName, String text) throws TwitterException {
        return this.sendDirectMessage(showUser(screenName).getId(), text);
    }

    @Override
    public InputStream getDMImageAsStream(String url) throws TwitterException {
        return twitter.get(url).asStream();
    }

    /* Friends & Followers Resources */

    @Override
    public IDs getNoRetweetsFriendships() throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friendships/no_retweets/ids.json"));
    }

    @Override
    public IDs getFriendsIDs(long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json?cursor=" + cursor));
    }

    @Override
    public IDs getFriendsIDs(long userId, long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json?user_id=" + userId +
                "&cursor=" + cursor));
    }

    @Override
    public IDs getFriendsIDs(long userId, long cursor, int count) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json?user_id=" + userId +
                "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public IDs getFriendsIDs(String screenName, long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor)));
    }

    @Override
    public IDs getFriendsIDs(String screenName, long cursor, int count) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friends/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public IDs getFollowersIDs(long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json?cursor=" + cursor));
    }

    @Override
    public IDs getFollowersIDs(long userId, long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json?user_id=" + userId
                + "&cursor=" + cursor));
    }

    @Override
    public IDs getFollowersIDs(long userId, long cursor, int count) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public IDs getFollowersIDs(String screenName, long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor)));
    }

    @Override
    public IDs getFollowersIDs(String screenName, long cursor, int count) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "followers/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public ResponseList<Friendship> lookupFriendships(long... ids) throws TwitterException {
        return twitter.factory.createFriendshipList(twitter.get(twitter.conf.getRestBaseURL() + "friendships/lookup.json?user_id=" + StringUtil.join(ids)));
    }

    @Override
    public ResponseList<Friendship> lookupFriendships(String... screenNames) throws TwitterException {
        return twitter.factory.createFriendshipList(twitter.get(twitter.conf.getRestBaseURL()
                + "friendships/lookup.json?screen_name=" + StringUtil.join(screenNames)));
    }

    @Override
    public IDs getIncomingFriendships(long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friendships/incoming.json?cursor=" + cursor));
    }

    @Override
    public IDs getOutgoingFriendships(long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "friendships/outgoing.json?cursor=" + cursor));
    }

    @Override
    public User createFriendship(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/create.json?user_id=" + userId));
    }

    @Override
    public User createFriendship(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public User createFriendship(long userId, boolean follow) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/create.json?user_id=" + userId + "&follow="
                + follow));
    }

    @Override
    public User createFriendship(String screenName, boolean follow) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName),
                        new HttpParameter("follow", follow)
                }
        ));
    }

    @Override
    public User destroyFriendship(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/destroy.json?user_id=" + userId));
    }

    @Override
    public User destroyFriendship(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "friendships/destroy.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public Relationship updateFriendship(long userId, boolean enableDeviceNotification
            , boolean retweets) throws TwitterException {
        return twitter.factory.createRelationship((twitter.post(twitter.conf.getRestBaseURL() + "friendships/update.json",
                new HttpParameter("user_id", userId),
                new HttpParameter("device", enableDeviceNotification),
                new HttpParameter("retweets", retweets))));
    }

    @Override
    public Relationship updateFriendship(String screenName, boolean enableDeviceNotification
            , boolean retweets) throws TwitterException {
        return twitter.factory.createRelationship(twitter.post(twitter.conf.getRestBaseURL() + "friendships/update.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("device", enableDeviceNotification),
                new HttpParameter("retweets", retweets)));
    }

    @Override
    public Relationship showFriendship(long sourceId, long targetId) throws TwitterException {
        return twitter.factory.createRelationship(twitter.get(twitter.conf.getRestBaseURL() + "friendships/show.json"
                , new HttpParameter("source_id", sourceId), new HttpParameter("target_id", targetId)));
    }

    @Override
    public Relationship showFriendship(String sourceScreenName, String targetScreenName) throws TwitterException {
        return twitter.factory.createRelationship(twitter.get(twitter.conf.getRestBaseURL() + "friendships/show.json",
                getParameterArray("source_screen_name", sourceScreenName, "target_screen_name", targetScreenName)));
    }

    @Override
    public PagableResponseList<User> getFriendsList(long userId, long cursor) throws TwitterException {
        return getFriendsList(userId, cursor, 20);
    }

    @Override
    public PagableResponseList<User> getFriendsList(long userId, long cursor, int count) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "friends/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public PagableResponseList<User> getFriendsList(String screenName, long cursor) throws TwitterException {
        return getFriendsList(screenName, cursor, 20);
    }

    @Override
    public PagableResponseList<User> getFriendsList(String screenName, long cursor, int count) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "friends/list.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public PagableResponseList<User> getFriendsList(long userId, long cursor, int count, boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "friends/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count
                + "&skip_status=" + skipStatus + "&include_user_entities=" + includeUserEntities));
    }

    @Override
    public PagableResponseList<User> getFriendsList(String screenName, long cursor, int count,
                                                    boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "friends/list.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count),
                new HttpParameter("skip_status", skipStatus),
                new HttpParameter("include_user_entities", includeUserEntities)));
    }

    @Override
    public PagableResponseList<User> getFollowersList(long userId, long cursor) throws TwitterException {
        return getFollowersList(userId, cursor, 20);
    }

    @Override
    public PagableResponseList<User> getFollowersList(String screenName, long cursor) throws TwitterException {
        return getFollowersList(screenName, cursor, 20);
    }

    @Override
    public PagableResponseList<User> getFollowersList(long userId, long cursor, int count) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "followers/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public PagableResponseList<User> getFollowersList(String screenName, long cursor, int count) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "followers/list.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public PagableResponseList<User> getFollowersList(long userId, long cursor, int count,
                                                      boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "followers/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count
                + "&skip_status=" + skipStatus + "&include_user_entities=" + includeUserEntities));
    }

    @Override
    public PagableResponseList<User> getFollowersList(String screenName, long cursor, int count,
                                                      boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "followers/list.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count),
                new HttpParameter("skip_status", skipStatus),
                new HttpParameter("include_user_entities", includeUserEntities)));
    }

    /* Users Resources */

    @Override
    public AccountSettings getAccountSettings() throws TwitterException {
        return twitter.factory.createAccountSettings(twitter.get(twitter.conf.getRestBaseURL() + "account/settings.json"));
    }

    @Override
    public User verifyCredentials() throws TwitterException {
        return twitter.fillInIDAndScreenName(
                new HttpParameter[]{new HttpParameter("include_email", twitter.conf.isIncludeEmailEnabled())});
    }

    @Override
    public AccountSettings updateAccountSettings(Integer trend_locationWoeid,
                                                 Boolean sleep_timeEnabled, String start_sleepTime,
                                                 String end_sleepTime, String time_zone, String lang)
            throws TwitterException {
        List<HttpParameter> profile = new ArrayList<HttpParameter>(6);
        if (trend_locationWoeid != null) {
            profile.add(new HttpParameter("trend_location_woeid", trend_locationWoeid));
        }
        if (sleep_timeEnabled != null) {
            profile.add(new HttpParameter("sleep_time_enabled", sleep_timeEnabled.toString()));
        }
        if (start_sleepTime != null) {
            profile.add(new HttpParameter("start_sleep_time", start_sleepTime));
        }
        if (end_sleepTime != null) {
            profile.add(new HttpParameter("end_sleep_time", end_sleepTime));
        }
        if (time_zone != null) {
            profile.add(new HttpParameter("time_zone", time_zone));
        }
        if (lang != null) {
            profile.add(new HttpParameter("lang", lang));
        }
        return twitter.factory.createAccountSettings(twitter.post(twitter.conf.getRestBaseURL() + "account/settings.json"
                , profile.toArray(new HttpParameter[profile.size()])));

    }

    @Override
    public AccountSettings updateAllowDmsFrom(String allowDmsFrom) throws TwitterException {
        return twitter.factory.createAccountSettings(twitter.post(twitter.conf.getRestBaseURL() + "account/settings.json?allow_dms_from=" + allowDmsFrom));
    }

    @Override
    public User updateProfile(String name, String url
            , String location, String description) throws TwitterException {
        List<HttpParameter> profile = new ArrayList<HttpParameter>(4);
        addParameterToList(profile, "name", name);
        addParameterToList(profile, "url", url);
        addParameterToList(profile, "location", location);
        addParameterToList(profile, "description", description);
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "account/update_profile.json"
                , profile.toArray(new HttpParameter[profile.size()])));
    }


    private void addParameterToList(List<HttpParameter> colors,
                                    String paramName, String color) {
        if (color != null) {
            colors.add(new HttpParameter(paramName, color));
        }
    }

    @Override
    public User updateProfileImage(File image) throws TwitterException {
        checkFileValidity(image);
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL()
                        + "account/update_profile_image.json"
                , new HttpParameter[]{new HttpParameter("image", image)}));
    }

    @Override
    public User updateProfileImage(InputStream image) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL()
                        + "account/update_profile_image.json"
                , new HttpParameter[]{new HttpParameter("image", "image", image)}));
    }

    /**
     * Check the existence, and the type of the specified file.
     *
     * @param image image to be uploaded
     * @throws TwitterException when the specified file is not found (FileNotFoundException will be nested)
     *                          , or when the specified file object is not representing a file(IOException will be nested).
     */
    /*package*/ void checkFileValidity(File image) throws TwitterException {
        if (!image.exists()) {
            //noinspection ThrowableInstanceNeverThrown
            throw new TwitterException(new FileNotFoundException(image + " is not found."));
        }
        if (!image.isFile()) {
            //noinspection ThrowableInstanceNeverThrown
            throw new TwitterException(new IOException(image + " is not a file."));
        }
    }

    @Override
    public PagableResponseList<User> getBlocksList() throws
            TwitterException {
        return getBlocksList(-1L);
    }

    @Override
    public PagableResponseList<User> getBlocksList(long cursor) throws
            TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "blocks/list.json?cursor=" + cursor));
    }

    @Override
    public IDs getBlocksIDs() throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "blocks/ids.json"));
    }

    @Override
    public IDs getBlocksIDs(long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "blocks/ids.json?cursor=" + cursor));
    }

    @Override
    public User createBlock(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "blocks/create.json?user_id=" + userId));
    }

    @Override
    public User createBlock(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "blocks/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public User destroyBlock(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "blocks/destroy.json?user_id=" + userId));
    }

    @Override
    public User destroyBlock(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "blocks/destroy.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName),
                }
        ));
    }


    @Override
    public PagableResponseList<User> getMutesList(long cursor) throws
            TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "mutes/users/list.json?cursor=" + cursor));
    }

    @Override
    public IDs getMutesIDs(long cursor) throws TwitterException {
        return twitter.factory.createIDs(twitter.get(twitter.conf.getRestBaseURL() + "mutes/users/ids.json?cursor=" + cursor));
    }

    @Override
    public User createMute(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/create.json?user_id=" + userId));
    }

    @Override
    public User createMute(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public User destroyMute(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/destroy.json?user_id=" + userId));
    }

    @Override
    public User destroyMute(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "mutes/users/destroy.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public ResponseList<User> lookupUsers(long... ids) throws TwitterException {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/lookup.json"
                , new HttpParameter("user_id", StringUtil.join(ids))));
    }

    @Override
    public ResponseList<User> lookupUsers(String... screenNames) throws TwitterException {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/lookup.json"
                , new HttpParameter("screen_name", StringUtil.join(screenNames))));
    }

    @Override
    public User showUser(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() + "users/show.json?user_id=" + userId));
    }

    @Override
    public User showUser(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() + "users/show.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public ResponseList<User> searchUsers(String query, int page) throws TwitterException {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/search.json"
                , new HttpParameter("q", query), new HttpParameter("per_page", 20)
                , new HttpParameter("page", page)));
    }

    @Override
    public ResponseList<User> getContributees(long userId) throws TwitterException {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/contributees.json?user_id=" + userId));
    }

    @Override
    public ResponseList<User> getContributees(String screenName) throws TwitterException {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/contributees.json",
                new HttpParameter("screen_name", screenName)));
    }

    @Override
    public ResponseList<User> getContributors(long userId) throws TwitterException {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/contributors.json?user_id=" + userId));
    }

    @Override
    public ResponseList<User> getContributors(String screenName) throws TwitterException {
        return twitter.factory.createUserList(twitter.get(twitter.conf.getRestBaseURL() + "users/contributors.json",
                new HttpParameter("screen_name", screenName)));
    }

    @Override
    public void removeProfileBanner() throws TwitterException {
        twitter.post(twitter.conf.getRestBaseURL()
                + "account/remove_profile_banner.json");
    }

    @Override
    public void updateProfileBanner(File image) throws TwitterException {
        checkFileValidity(image);
        twitter.post(twitter.conf.getRestBaseURL()
                        + "account/update_profile_banner.json"
                , new HttpParameter("banner", image));
    }

    @Override
    public void updateProfileBanner(InputStream image) throws TwitterException {
        twitter.post(twitter.conf.getRestBaseURL()
                        + "account/update_profile_banner.json"
                , new HttpParameter("banner", "banner", image));
    }

    /* Suggested Users Resources */

    @Override
    public ResponseList<User> getUserSuggestions(String categorySlug) throws TwitterException {
        HttpResponse res;
        try {
            res = twitter.get(twitter.conf.getRestBaseURL() + "users/suggestions/" + URLEncoder.encode(categorySlug, "UTF-8") + ".json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return twitter.factory.createUserListFromJSONArray_Users(res);
    }

    @Override
    public ResponseList<Category> getSuggestedUserCategories() throws TwitterException {
        return twitter.factory.createCategoryList(twitter.get(twitter.conf.getRestBaseURL() + "users/suggestions.json"));
    }

    @Override
    public ResponseList<User> getMemberSuggestions(String categorySlug) throws TwitterException {
        HttpResponse res;
        try {
            res = twitter.get(twitter.conf.getRestBaseURL() + "users/suggestions/" + URLEncoder.encode(categorySlug, "UTF-8") + "/members.json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return twitter.factory.createUserListFromJSONArray(res);
    }

    /* Favorites Resources */

    @Override
    public ResponseList<Status> getFavorites() throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json"));
    }

    @Override
    public ResponseList<Status> getFavorites(long userId) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json?user_id=" + userId));
    }

    @Override
    public ResponseList<Status> getFavorites(String screenName) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json",
                new HttpParameter("screen_name", screenName)));
    }

    @Override
    public ResponseList<Status> getFavorites(Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json", paging.asPostParameterArray()));
    }

    @Override
    public ResponseList<Status> getFavorites(long userId, Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json",
                twitter.mergeParameters(new HttpParameter[]{new HttpParameter("user_id", userId)}
                        , paging.asPostParameterArray())
        ));
    }

    @Override
    public ResponseList<Status> getFavorites(String screenName, Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "favorites/list.json",
                twitter.mergeParameters(new HttpParameter[]{new HttpParameter("screen_name", screenName)}
                        , paging.asPostParameterArray())
        ));
    }

    @Override
    public Status destroyFavorite(long id) throws TwitterException {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "favorites/destroy.json?id=" + id));
    }

    @Override
    public Status createFavorite(long id) throws TwitterException {
        return twitter.factory.createStatus(twitter.post(twitter.conf.getRestBaseURL() + "favorites/create.json?id=" + id));
    }

    /* Lists Resources */

    @Override
    public ResponseList<UserList> getUserLists(String listOwnerScreenName) throws TwitterException {
        return getUserLists(listOwnerScreenName, false);
    }

    @Override
    public ResponseList<UserList> getUserLists(String listOwnerScreenName, boolean reverse) throws TwitterException {
        return twitter.factory.createUserListList(twitter.get(twitter.conf.getRestBaseURL() + "lists/list.json",
                new HttpParameter("screen_name", listOwnerScreenName),
                new HttpParameter("reverse", reverse)));
    }

    @Override
    public ResponseList<UserList> getUserLists(long listOwnerUserId) throws TwitterException {
        return getUserLists(listOwnerUserId, false);
    }

    @Override
    public ResponseList<UserList> getUserLists(long listOwnerUserId, boolean reverse) throws TwitterException {
        return twitter.factory.createUserListList(twitter.get(twitter.conf.getRestBaseURL() + "lists/list.json",
                new HttpParameter("user_id", listOwnerUserId),
                new HttpParameter("reverse", reverse)));
    }

    @Override
    public ResponseList<Status> getUserListStatuses(long listId, Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "lists/statuses.json"
                , twitter.mergeParameters(paging.asPostParameterArray(Paging.SMCP, Paging.COUNT)
                        , new HttpParameter("list_id", listId))));
    }

    @Override
    public ResponseList<Status> getUserListStatuses(long ownerId, String slug, Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "lists/statuses.json"
                , twitter.mergeParameters(paging.asPostParameterArray(Paging.SMCP, Paging.COUNT)
                        , new HttpParameter[]{new HttpParameter("owner_id", ownerId)
                                , new HttpParameter("slug", slug)})));
    }

    @Override
    public ResponseList<Status> getUserListStatuses(String ownerScreenName,
                                                    String slug, Paging paging) throws TwitterException {
        return twitter.factory.createStatusList(twitter.get(twitter.conf.getRestBaseURL() + "lists/statuses.json"
                , twitter.mergeParameters(paging.asPostParameterArray(Paging.SMCP, Paging.COUNT)
                        , new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName)
                                , new HttpParameter("slug", slug)})));
    }

    @Override
    public UserList destroyUserListMember(long listId, long userId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                        "lists/members/destroy.json",
                new HttpParameter[]{new HttpParameter("list_id", listId), new HttpParameter("user_id", userId)}
        ));
    }

    @Override
    public UserList destroyUserListMember(long ownerId, String slug, long userId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/destroy.json", new HttpParameter[]{new HttpParameter("owner_id", ownerId)
                , new HttpParameter("slug", slug), new HttpParameter("user_id", userId)}));
    }

    @Override
    public UserList destroyUserListMember(long listId, String screenName) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/destroy.json", new HttpParameter[]{new HttpParameter("list_id", listId),
                new HttpParameter("screen_name", screenName)}));
    }

    @Override
    public UserList destroyUserListMember(String ownerScreenName, String slug,
                                          long userId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/destroy.json", new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName)
                , new HttpParameter("slug", slug), new HttpParameter("user_id", userId)}));
    }

    @Override
    public UserList destroyUserListMembers(long listId, String[] screenNames) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/destroy_all.json", new HttpParameter[]{new HttpParameter("list_id", listId),
                new HttpParameter("screen_name", StringUtil.join(screenNames))}));
    }

    @Override
    public UserList destroyUserListMembers(long listId, long[] userIds) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/destroy_all.json", new HttpParameter[]{new HttpParameter("list_id", listId),
                new HttpParameter("user_id", StringUtil.join(userIds))}));
    }

    @Override
    public UserList destroyUserListMembers(String ownerScreenName, String slug, String[] screenNames) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/destroy_all.json", new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName),
                new HttpParameter("slug", slug),
                new HttpParameter("screen_name", StringUtil.join(screenNames))}));
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(long cursor) throws TwitterException {
        return getUserListMemberships(20, cursor);
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(int count, long cursor) throws TwitterException {
        return twitter.factory.createPagableUserListList(twitter.get(twitter.conf.getRestBaseURL() + "lists/memberships.json",
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, long cursor) throws TwitterException {
        return getUserListMemberships(listMemberScreenName, cursor, false);
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, int count, long cursor) throws TwitterException {
        return getUserListMemberships(listMemberScreenName, count, cursor, false);
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, long cursor, boolean filterToOwnedLists) throws TwitterException {
        return getUserListMemberships(listMemberScreenName, 20, cursor, filterToOwnedLists);
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(String listMemberScreenName, int count, long cursor, boolean filterToOwnedLists) throws TwitterException {
        return twitter.factory.createPagableUserListList(twitter.get(twitter.conf.getRestBaseURL()
                        + "lists/memberships.json",
                new HttpParameter("screen_name", listMemberScreenName),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("filter_to_owned_lists", filterToOwnedLists)));
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(long listMemberId, long cursor) throws TwitterException {
        return getUserListMemberships(listMemberId, cursor, false);
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(long listMemberId, int count, long cursor) throws TwitterException {
        return getUserListMemberships(listMemberId, count, cursor, false);
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(long listMemberId, long cursor, boolean filterToOwnedLists) throws TwitterException {
        return getUserListMemberships(listMemberId, 20, cursor, filterToOwnedLists);
    }

    @Override
    public PagableResponseList<UserList> getUserListMemberships(long listMemberId, int count, long cursor, boolean filterToOwnedLists) throws TwitterException {
        return twitter.factory.createPagableUserListList(twitter.get(twitter.conf.getRestBaseURL()
                        + "lists/memberships.json",
                new HttpParameter("user_id", listMemberId),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("filter_to_owned_lists", filterToOwnedLists)));
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(long listId, long cursor) throws TwitterException {
        return getUserListSubscribers(listId, 20, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(long listId, int count, long cursor) throws TwitterException {
        return getUserListSubscribers(listId, count, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(long listId, int count, long cursor, boolean skipStatus) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/subscribers.json",
                new HttpParameter("list_id", listId),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("skip_status", skipStatus)));
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, long cursor) throws TwitterException {
        return getUserListSubscribers(ownerId, slug, 20, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, int count, long cursor) throws TwitterException {
        return getUserListSubscribers(ownerId, slug, count, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(long ownerId, String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/subscribers.json",
                new HttpParameter("owner_id", ownerId),
                new HttpParameter("slug", slug),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("skip_status", skipStatus)));
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(String ownerScreenName, String slug, long cursor) throws TwitterException {
        return getUserListSubscribers(ownerScreenName, slug, 20, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(String ownerScreenName, String slug, int count, long cursor) throws TwitterException {
        return getUserListSubscribers(ownerScreenName, slug, count, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListSubscribers(
            String ownerScreenName, String slug, int count, long cursor, boolean skipStatus)
            throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/subscribers.json",
                new HttpParameter("owner_screen_name", ownerScreenName),
                new HttpParameter("slug", slug),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("skip_status", skipStatus)));
    }

    @Override
    public UserList createUserListSubscription(long listId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/subscribers/create.json", new HttpParameter[]{new HttpParameter("list_id", listId)}));
    }

    @Override
    public UserList createUserListSubscription(long ownerId, String slug) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/subscribers/create.json", new HttpParameter[]{new HttpParameter("owner_id", ownerId)
                , new HttpParameter("slug", slug)}));
    }


    @Override
    public UserList createUserListSubscription(String ownerScreenName,
                                               String slug) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/subscribers/create.json", new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName)
                , new HttpParameter("slug", slug)}));
    }

    @Override
    public User showUserListSubscription(long listId, long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() +
                "lists/subscribers/show.json?list_id=" + listId + "&user_id=" + userId));
    }

    @Override
    public User showUserListSubscription(long ownerId, String slug, long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() +
                "lists/subscribers/show.json?owner_id=" + ownerId + "&slug=" + slug + "&user_id=" + userId));
    }

    @Override
    public User showUserListSubscription(String ownerScreenName, String slug,
                                         long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() +
                        "lists/subscribers/show.json",
                new HttpParameter[]{
                        new HttpParameter("owner_screen_name", ownerScreenName),
                        new HttpParameter("slug", slug),
                        new HttpParameter("user_id", userId)
                }
        ));
    }

    @Override
    public UserList destroyUserListSubscription(long listId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/subscribers/destroy.json", new HttpParameter[]{new HttpParameter("list_id", listId)}));
    }

    @Override
    public UserList destroyUserListSubscription(long ownerId, String slug) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/subscribers/destroy.json"
                , new HttpParameter[]{new HttpParameter("owner_id", ownerId), new HttpParameter("slug", slug)}));
    }

    @Override
    public UserList destroyUserListSubscription(String ownerScreenName,
                                                String slug) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/subscribers/destroy.json"
                , new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName), new HttpParameter("slug", slug)}));
    }

    @Override
    public UserList createUserListMembers(long listId, long... userIds) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                new HttpParameter[]{new HttpParameter("list_id", listId), new HttpParameter("user_id"
                        , StringUtil.join(userIds))}
        ));
    }

    @Override
    public UserList createUserListMembers(long ownerId, String slug, long... userIds) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                new HttpParameter[]{new HttpParameter("owner_id", ownerId), new HttpParameter("slug", slug)
                        , new HttpParameter("user_id", StringUtil.join(userIds))}
        ));
    }

    @Override
    public UserList createUserListMembers(String ownerScreenName, String slug,
                                          long... userIds) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/members/create_all.json",
                new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName), new HttpParameter("slug", slug)
                        , new HttpParameter("user_id", StringUtil.join(userIds))}
        ));
    }

    @Override
    public UserList createUserListMembers(long listId, String... screenNames) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                        "lists/members/create_all.json",
                new HttpParameter[]{
                        new HttpParameter("list_id", listId),
                        new HttpParameter("screen_name", StringUtil.join(screenNames))}
        ));
    }

    @Override
    public UserList createUserListMembers(long ownerId, String slug, String... screenNames) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                        "lists/members/create_all.json",
                new HttpParameter[]{new HttpParameter("owner_id", ownerId), new HttpParameter("slug", slug)
                        , new HttpParameter("screen_name", StringUtil.join(screenNames))}
        ));
    }

    @Override
    public UserList createUserListMembers(String ownerScreenName, String slug,
                                          String... screenNames) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                        "lists/members/create_all.json",
                new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName), new HttpParameter("slug", slug)
                        , new HttpParameter("screen_name", StringUtil.join(screenNames))}
        ));
    }

    @Override
    public User showUserListMembership(long listId, long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() +
                "lists/members/show.json?list_id=" + listId + "&user_id=" + userId));
    }

    @Override
    public User showUserListMembership(long ownerId, String slug, long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() +
                "lists/members/show.json?owner_id=" + ownerId + "&slug=" + slug + "&user_id=" + userId));
    }

    @Override
    public User showUserListMembership(String ownerScreenName, String slug,
                                       long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.get(twitter.conf.getRestBaseURL() +
                        "lists/members/show.json",
                new HttpParameter[]{
                        new HttpParameter("owner_screen_name", ownerScreenName),
                        new HttpParameter("slug", slug),
                        new HttpParameter("user_id", userId)
                }
        ));
    }

    @Override
    public PagableResponseList<User> getUserListMembers(long listId
            , long cursor) throws TwitterException {
        return getUserListMembers(listId, 20, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListMembers(long listId, int count, long cursor) throws TwitterException {
        return getUserListMembers(listId, count, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListMembers(long listId, int count, long cursor, boolean skipStatus) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/members.json",
                new HttpParameter("list_id", listId),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("skip_status", skipStatus)));
    }

    @Override
    public PagableResponseList<User> getUserListMembers(long ownerId, String slug, long cursor) throws TwitterException {
        return getUserListMembers(ownerId, slug, 20, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListMembers(long ownerId, String slug, int count, long cursor) throws TwitterException {
        return getUserListMembers(ownerId, slug, count, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListMembers(long ownerId, String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() +
                        "lists/members.json",
                new HttpParameter("owner_id", ownerId),
                new HttpParameter("slug", slug),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("skip_status", skipStatus)));
    }

    @Override
    public PagableResponseList<User> getUserListMembers(String ownerScreenName, String slug, long cursor) throws TwitterException {
        return getUserListMembers(ownerScreenName, slug, 20, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListMembers(String ownerScreenName, String slug, int count, long cursor) throws TwitterException {
        return getUserListMembers(ownerScreenName, slug, count, cursor, false);
    }

    @Override
    public PagableResponseList<User> getUserListMembers(String ownerScreenName,
                                                        String slug, int count, long cursor, boolean skipStatus) throws TwitterException {
        return twitter.factory.createPagableUserList(twitter.get(twitter.conf.getRestBaseURL() +
                        "lists/members.json",
                new HttpParameter("owner_screen_name", ownerScreenName),
                new HttpParameter("slug", slug),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor),
                new HttpParameter("skip_status", skipStatus)));
    }

    @Override
    public UserList createUserListMember(long listId, long userId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/create.json", new HttpParameter[]{new HttpParameter("user_id", userId)
                , new HttpParameter("list_id", listId)}));
    }

    @Override
    public UserList createUserListMember(long ownerId, String slug, long userId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/create.json", new HttpParameter[]{new HttpParameter("user_id", userId)
                , new HttpParameter("owner_id", ownerId), new HttpParameter("slug", slug)}));
    }

    @Override
    public UserList createUserListMember(String ownerScreenName, String slug,
                                         long userId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() +
                "lists/members/create.json", new HttpParameter[]{new HttpParameter("user_id", userId)
                , new HttpParameter("owner_screen_name", ownerScreenName), new HttpParameter("slug", slug)}));
    }

    @Override
    public UserList destroyUserList(long listId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/destroy.json",
                new HttpParameter[]{new HttpParameter("list_id", listId)}));
    }

    @Override
    public UserList destroyUserList(long ownerId, String slug) throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/destroy.json",
                new HttpParameter[]{new HttpParameter("owner_id", ownerId)
                        , new HttpParameter("slug", slug)}
        ));
    }

    @Override
    public UserList destroyUserList(String ownerScreenName, String slug)
            throws TwitterException {
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/destroy.json",
                new HttpParameter[]{new HttpParameter("owner_screen_name", ownerScreenName)
                        , new HttpParameter("slug", slug)}
        ));
    }

    @Override
    public UserList updateUserList(long listId, String newListName, boolean isPublicList, String newDescription) throws TwitterException {
        return updateUserList(newListName, isPublicList, newDescription, new HttpParameter("list_id", listId));
    }

    @Override
    public UserList updateUserList(long ownerId, String slug, String newListName, boolean isPublicList, String newDescription) throws TwitterException {
        return updateUserList(newListName, isPublicList, newDescription, new HttpParameter("owner_id", ownerId)
                , new HttpParameter("slug", slug));
    }

    @Override
    public UserList updateUserList(String ownerScreenName, String slug,
                                   String newListName, boolean isPublicList, String newDescription)
            throws TwitterException {
        return updateUserList(newListName, isPublicList, newDescription, new HttpParameter("owner_screen_name", ownerScreenName)
                , new HttpParameter("slug", slug));
    }

    private UserList updateUserList(String newListName, boolean isPublicList, String newDescription, HttpParameter... params) throws TwitterException {
        List<HttpParameter> httpParams = new ArrayList<HttpParameter>();
        Collections.addAll(httpParams, params);
        if (newListName != null) {
            httpParams.add(new HttpParameter("name", newListName));
        }
        httpParams.add(new HttpParameter("mode", isPublicList ? "public" : "private"));
        if (newDescription != null) {
            httpParams.add(new HttpParameter("description", newDescription));
        }
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/update.json", httpParams.toArray(new HttpParameter[httpParams.size()])));
    }

    @Override
    public UserList createUserList(String listName, boolean isPublicList, String description) throws TwitterException {
        List<HttpParameter> httpParams = new ArrayList<HttpParameter>();
        httpParams.add(new HttpParameter("name", listName));
        httpParams.add(new HttpParameter("mode", isPublicList ? "public" : "private"));
        if (description != null) {
            httpParams.add(new HttpParameter("description", description));
        }
        return twitter.factory.createAUserList(twitter.post(twitter.conf.getRestBaseURL() + "lists/create.json",
                httpParams.toArray(new HttpParameter[httpParams.size()])));
    }

    @Override
    public UserList showUserList(long listId) throws TwitterException {
        return twitter.factory.createAUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/show.json?list_id=" + listId));
    }

    @Override
    public UserList showUserList(long ownerId, String slug) throws TwitterException {
        return twitter.factory.createAUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/show.json?owner_id=" + ownerId + "&slug="
                + slug));
    }

    @Override
    public UserList showUserList(String ownerScreenName, String slug)
            throws TwitterException {
        return twitter.factory.createAUserList(twitter.get(twitter.conf.getRestBaseURL() + "lists/show.json",
                new HttpParameter[]{
                        new HttpParameter("owner_screen_name", ownerScreenName),
                        new HttpParameter("slug", slug)
                }
        ));
    }

    @Override
    public PagableResponseList<UserList> getUserListSubscriptions(String listSubscriberScreenName, long cursor) throws TwitterException {
        return getUserListSubscriptions(listSubscriberScreenName, 20, cursor);
    }

    @Override
    public PagableResponseList<UserList> getUserListSubscriptions(String listSubscriberScreenName, int count, long cursor) throws TwitterException {
        return twitter.factory.createPagableUserListList(twitter.get(twitter.conf.getRestBaseURL() + "lists/subscriptions.json",
                new HttpParameter("screen_name", listSubscriberScreenName)
                , new HttpParameter("count", count)
                , new HttpParameter("cursor", cursor)));
    }

    @Override
    public PagableResponseList<UserList> getUserListSubscriptions(long listSubscriberId, long cursor) throws TwitterException {
        return getUserListSubscriptions(listSubscriberId, 20, cursor);
    }

    @Override
    public PagableResponseList<UserList> getUserListSubscriptions(long listSubscriberId, int count, long cursor) throws TwitterException {
        return twitter.factory.createPagableUserListList(twitter.get(twitter.conf.getRestBaseURL() + "lists/subscriptions.json",
                new HttpParameter("user_id", listSubscriberId),
                new HttpParameter("count", count),
                new HttpParameter("cursor", cursor)));
    }

    public PagableResponseList<UserList> getUserListsOwnerships(String listOwnerScreenName, long cursor) throws TwitterException {
        return getUserListsOwnerships(listOwnerScreenName, 20, cursor);
    }

    @Override
    public PagableResponseList<UserList> getUserListsOwnerships(String listOwnerScreenName, int count, long cursor) throws TwitterException {
        return twitter.factory.createPagableUserListList(twitter.get(twitter.conf.getRestBaseURL() + "lists/ownerships.json",
                new HttpParameter("screen_name", listOwnerScreenName)
                , new HttpParameter("count", count)
                , new HttpParameter("cursor", cursor)));
    }

    @Override
    public PagableResponseList<UserList> getUserListsOwnerships(long listOwnerId, long cursor) throws TwitterException {
        return getUserListsOwnerships(listOwnerId, 20, cursor);
    }

    @Override
    public PagableResponseList<UserList> getUserListsOwnerships(long listOwnerId, int count, long cursor) throws TwitterException {
        return twitter.factory.createPagableUserListList(twitter.get(twitter.conf.getRestBaseURL() + "lists/ownerships.json",
                new HttpParameter("user_id", listOwnerId)
                , new HttpParameter("count", count)
                , new HttpParameter("cursor", cursor)));
    }

    /* Saved Searches Resources */

    @Override
    public ResponseList<SavedSearch> getSavedSearches() throws TwitterException {
        return twitter.factory.createSavedSearchList(twitter.get(twitter.conf.getRestBaseURL() + "saved_searches/list.json"));
    }

    @Override
    public SavedSearch showSavedSearch(long id) throws TwitterException {
        return twitter.factory.createSavedSearch(twitter.get(twitter.conf.getRestBaseURL() + "saved_searches/show/" + id
                + ".json"));
    }

    @Override
    public SavedSearch createSavedSearch(String query) throws TwitterException {
        return twitter.factory.createSavedSearch(twitter.post(twitter.conf.getRestBaseURL() + "saved_searches/create.json"
                , new HttpParameter("query", query)));
    }

    @Override
    public SavedSearch destroySavedSearch(long id) throws TwitterException {
        return twitter.factory.createSavedSearch(twitter.post(twitter.conf.getRestBaseURL()
                + "saved_searches/destroy/" + id + ".json"));
    }

    /* Places & Geo Resources */

    @Override
    public Place getGeoDetails(String placeId) throws TwitterException {
        return twitter.factory.createPlace(twitter.get(twitter.conf.getRestBaseURL() + "geo/id/" + placeId
                + ".json"));
    }

    @Override
    public ResponseList<Place> reverseGeoCode(GeoQuery query) throws TwitterException {
        try {
            return twitter.factory.createPlaceList(twitter.get(twitter.conf.getRestBaseURL()
                    + "geo/reverse_geocode.json", query.asHttpParameterArray()));
        } catch (TwitterException te) {
            if (te.getStatusCode() == 404) {
                return twitter.factory.createEmptyResponseList();
            } else {
                throw te;
            }
        }
    }

    @Override
    public ResponseList<Place> searchPlaces(GeoQuery query) throws TwitterException {
        return twitter.factory.createPlaceList(twitter.get(twitter.conf.getRestBaseURL()
                + "geo/search.json", query.asHttpParameterArray()));
    }

    @Override
    public ResponseList<Place> getSimilarPlaces(GeoLocation location, String name, String containedWithin, String streetAddress) throws TwitterException {
        List<HttpParameter> params = new ArrayList<HttpParameter>(3);
        params.add(new HttpParameter("lat", location.getLatitude()));
        params.add(new HttpParameter("long", location.getLongitude()));
        params.add(new HttpParameter("name", name));
        if (containedWithin != null) {
            params.add(new HttpParameter("contained_within", containedWithin));
        }
        if (streetAddress != null) {
            params.add(new HttpParameter("attribute:street_address", streetAddress));
        }
        return twitter.factory.createPlaceList(twitter.get(twitter.conf.getRestBaseURL()
                + "geo/similar_places.json", params.toArray(new HttpParameter[params.size()])));
    }

    /* Trends Resources */

    @Override
    public Trends getPlaceTrends(int woeid) throws TwitterException {
        return twitter.factory.createTrends(twitter.get(twitter.conf.getRestBaseURL()
                + "trends/place.json?id=" + woeid));
    }

    @Override
    public ResponseList<Location> getAvailableTrends() throws TwitterException {
        return twitter.factory.createLocationList(twitter.get(twitter.conf.getRestBaseURL()
                + "trends/available.json"));
    }

    @Override
    public ResponseList<Location> getClosestTrends(GeoLocation location) throws TwitterException {
        return twitter.factory.createLocationList(twitter.get(twitter.conf.getRestBaseURL()
                        + "trends/closest.json",
                new HttpParameter("lat", location.getLatitude())
                , new HttpParameter("long", location.getLongitude())));
    }

    /* Spam Reporting Resources */

    @Override
    public User reportSpam(long userId) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "users/report_spam.json?user_id=" + userId));
    }

    @Override
    public User reportSpam(String screenName) throws TwitterException {
        return twitter.factory.createUser(twitter.post(twitter.conf.getRestBaseURL() + "users/report_spam.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    /* Help Resources */

    @Override
    public TwitterAPIConfiguration getAPIConfiguration() throws TwitterException {
        return twitter.factory.createTwitterAPIConfiguration(twitter.get(twitter.conf.getRestBaseURL() + "help/configuration.json"));
    }

    @Override
    public ResponseList<Language> getLanguages() throws TwitterException {
        return twitter.factory.createLanguageList(twitter.get(twitter.conf.getRestBaseURL() + "help/languages.json"));
    }

    @Override
    public String getPrivacyPolicy() throws TwitterException {
        try {
            return twitter.get(twitter.conf.getRestBaseURL() + "help/privacy.json").asJSONObject().getString("privacy");
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public String getTermsOfService() throws TwitterException {
        try {
            return twitter.get(twitter.conf.getRestBaseURL() + "help/tos.json").asJSONObject().getString("tos");
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public Map<String, RateLimitStatus> getRateLimitStatus() throws TwitterException {
        return twitter.factory.createRateLimitStatuses(twitter.get(twitter.conf.getRestBaseURL() + "application/rate_limit_status.json"));
    }

    @Override
    public Map<String, RateLimitStatus> getRateLimitStatus(String... resources) throws TwitterException {
        return twitter.factory.createRateLimitStatuses(twitter.get(twitter.conf.getRestBaseURL() + "application/rate_limit_status.json?resources=" + StringUtil.join(resources)));
    }

}
