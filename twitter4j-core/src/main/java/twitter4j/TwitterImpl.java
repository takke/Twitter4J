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

package twitter4j;

import static twitter4j.HttpParameter.getParameterArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.api.DirectMessagesResources;
import twitter4j.api.FavoritesResources;
import twitter4j.api.FriendsFollowersResources;
import twitter4j.api.HelpResources;
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
import twitter4j.auth.Authorization;
import twitter4j.conf.ChunkedUploadConfiguration;
import twitter4j.conf.Configuration;

/**
 * A java representation of the <a href="https://dev.twitter.com/docs/api">Twitter REST API</a><br>
 * This class is thread safe and can be cached/re-used and used concurrently.<br>
 * Currently this class is not carefully designed to be extended. It is suggested to extend this class only for mock testing purpose.<br>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class TwitterImpl extends TwitterBaseImpl implements Twitter {
    private static final long serialVersionUID = 9170943084096085770L;

    private final String IMPLICIT_PARAMS_STR;
    private final HttpParameter[] IMPLICIT_PARAMS;
    private final HttpParameter INCLUDE_MY_RETWEET;

    private static final ConcurrentHashMap<Configuration, HttpParameter[]> implicitParamsMap = new ConcurrentHashMap<Configuration, HttpParameter[]>();
    private static final ConcurrentHashMap<Configuration, String> implicitParamsStrMap = new ConcurrentHashMap<Configuration, String>();

    /*package*/
    TwitterImpl(Configuration conf, Authorization auth) {
        super(conf, auth);
        INCLUDE_MY_RETWEET = new HttpParameter("include_my_retweet", conf.isIncludeMyRetweetEnabled());
        if (implicitParamsMap.containsKey(conf)) {
            this.IMPLICIT_PARAMS = implicitParamsMap.get(conf);
            this.IMPLICIT_PARAMS_STR = implicitParamsStrMap.get(conf);
        } else {
            String implicitParamsStr = conf.isIncludeEntitiesEnabled() ? "include_entities=" + true : "";
            boolean contributorsEnabled = conf.getContributingTo() != -1L;
            if (contributorsEnabled) {
                if (!"".equals(implicitParamsStr)) {
                    implicitParamsStr += "&";
                }
                implicitParamsStr += "contributingto=" + conf.getContributingTo();
            }

            if (conf.isTweetModeExtended()) {
                if (!"".equals(implicitParamsStr)) {
                    implicitParamsStr += "&";
                }
                implicitParamsStr += "tweet_mode=extended";
            }
            if (conf.isIncludeExtEditControl()) {
                if (!"".equals(implicitParamsStr)) {
                    implicitParamsStr += "&";
                }
                implicitParamsStr += "include_ext_edit_control=true";
            }

            List<HttpParameter> params = new ArrayList<HttpParameter>(3);
            if (conf.isIncludeEntitiesEnabled()) {
                params.add(new HttpParameter("include_entities", "true"));
            }
            if (contributorsEnabled) {
                params.add(new HttpParameter("contributingto", conf.getContributingTo()));
            }
            if (conf.isTrimUserEnabled()) {
                params.add(new HttpParameter("trim_user", "1"));
            }
            if (conf.isIncludeExtAltTextEnabled()) {
                params.add(new HttpParameter("include_ext_alt_text", "true"));
            }
            if (conf.isTweetModeExtended()) {
                params.add(new HttpParameter("tweet_mode", "extended"));
            }
            if (conf.isIncludeExtEditControl()) {
                params.add(new HttpParameter("include_ext_edit_control", "true"));
            }
            HttpParameter[] implicitParams = params.toArray(new HttpParameter[params.size()]);

            // implicitParamsMap.containsKey() is evaluated in the above if clause.
            // thus implicitParamsStrMap needs to be initialized first
            implicitParamsStrMap.putIfAbsent(conf, implicitParamsStr);
            implicitParamsMap.putIfAbsent(conf, implicitParams);

            this.IMPLICIT_PARAMS = implicitParams;
            this.IMPLICIT_PARAMS_STR = implicitParamsStr;
        }
    }

    @Override
    public V1Resources v1Resources() {
        return new V1ResourcesImpl(this);
    }

    /* Timelines Resources */

    @Override
    public ResponseList<Status> getMentionsTimeline() throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "statuses/mentions_timeline.json"));
    }

    @Override
    public ResponseList<Status> getMentionsTimeline(Paging paging) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL()
                + "statuses/mentions_timeline.json", paging.asPostParameterArray()));
    }

    @Override
    public ResponseList<Status> getHomeTimeline() throws
            TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL()
                + "statuses/home_timeline.json", INCLUDE_MY_RETWEET));
    }

    @Override
    public ResponseList<Status> getHomeTimeline(Paging paging) throws
            TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL()
                + "statuses/home_timeline.json", mergeParameters(paging.asPostParameterArray(), new HttpParameter[]{INCLUDE_MY_RETWEET})));
    }

    @Override
    public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL()
                + "statuses/retweets_of_me.json"));
    }

    @Override
    public ResponseList<Status> getRetweetsOfMe(Paging paging) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL()
                + "statuses/retweets_of_me.json", paging.asPostParameterArray()));
    }

    @Override
    public ResponseList<Status> getUserTimeline(String screenName, Paging paging)
            throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL()
                        + "statuses/user_timeline.json",
                mergeParameters(new HttpParameter[]{new HttpParameter("screen_name", screenName)
                        , INCLUDE_MY_RETWEET}
                        , paging.asPostParameterArray())
        ));
    }

    @Override
    public ResponseList<Status> getUserTimeline(long userId, Paging paging)
            throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL()
                        + "statuses/user_timeline.json",
                mergeParameters(new HttpParameter[]{new HttpParameter("user_id", userId)
                        , INCLUDE_MY_RETWEET}
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
        return factory.createStatusList(get(conf.getRestBaseURL() +
                        "statuses/user_timeline.json",
                mergeParameters(new HttpParameter[]{INCLUDE_MY_RETWEET}
                        , paging.asPostParameterArray())
        ));
    }

    /* Tweets Resources */

    @Override
    public ResponseList<Status> getRetweets(long statusId) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "statuses/retweets/" + statusId
                + ".json?count=100"));
    }

    @Override
    public IDs getRetweeterIds(long statusId, long cursor) throws TwitterException {
        return getRetweeterIds(statusId, 100, cursor);
    }

    @Override
    public IDs getRetweeterIds(long statusId, int count, long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "statuses/retweeters/ids.json?id=" + statusId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public Status showStatus(long id) throws TwitterException {
        return factory.createStatus(get(conf.getRestBaseURL() + "statuses/show/" + id + ".json", new HttpParameter[]{INCLUDE_MY_RETWEET}));
    }

    @Override
    public Status destroyStatus(long statusId) throws TwitterException {
        return factory.createStatus(post(conf.getRestBaseURL() + "statuses/destroy/" + statusId + ".json"));
    }

    @Override
    public Status updateStatus(String status) throws TwitterException {
        return factory.createStatus(post(conf.getRestBaseURL() + "statuses/update.json",
                new HttpParameter[]{new HttpParameter("status", status)}));
    }

    @Override
    public Status updateStatus(StatusUpdate status) throws TwitterException {
        String url = conf.getRestBaseURL() + (status.isForUpdateWithMedia() ?
                "statuses/update_with_media.json" : "statuses/update.json");
        return factory.createStatus(post(url, status.asHttpParameterArray()));
    }

    @Override
    public Status retweetStatus(long statusId) throws TwitterException {
        return factory.createStatus(post(conf.getRestBaseURL() + "statuses/retweet/" + statusId + ".json"));
    }

    @Override
    public Status unRetweetStatus(long statusId) throws TwitterException {
        return factory.createStatus(post(conf.getRestBaseURL() + "statuses/unretweet/" + statusId + ".json"));
    }

    @Override
    public OEmbed getOEmbed(OEmbedRequest req) throws TwitterException {
        return factory.createOEmbed(get(conf.getRestBaseURL()
                + "statuses/oembed.json", req.asHttpParameterArray()));
    }

    @Override
    public ResponseList<Status> lookup(long... ids) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "statuses/lookup.json?id=" + StringUtil.join(ids)));
    }

    @Override
    public UploadedMedia uploadMedia(File image) throws TwitterException {
        checkFileValidity(image);
        return new UploadedMedia(post(conf.getUploadBaseURL() + "media/upload.json"
                , new HttpParameter("media", image)).asJSONObject());
    }

    @Override
    public UploadedMedia uploadMedia(String fileName, InputStream image) throws TwitterException {
        return new UploadedMedia(post(conf.getUploadBaseURL() + "media/upload.json"
                , new HttpParameter("media", fileName, image)).asJSONObject());
    }

    @Override
    public int createMediaMetadata(long mediaId, String altText) throws TwitterException {
        try {
            JSONObject json = new JSONObject()
                    .put("media_id", ""+mediaId)
                    .put("alt_text", new JSONObject().put("text", altText));
            return post(conf.getUploadBaseURL() + "media/metadata/create.json", json).getStatusCode();
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public UploadedMedia uploadMediaChunked(ChunkedUploadConfiguration uploadConfiguration) throws TwitterException {

        return new ChunkedUploadDelegate(this).uploadMediaChunked(uploadConfiguration);
    }

    /* Search Resources */

    @Override
    public QueryResult search(Query query) throws TwitterException {
        if (query.nextPage() != null) {
            return factory.createQueryResult(get(conf.getRestBaseURL()
                    + "search/tweets.json" + query.nextPage()), query);
        } else {
            return factory.createQueryResult(get(conf.getRestBaseURL()
                    + "search/tweets.json", query.asHttpParameterArray()), query);
        }
    }

    /* Direct Messages Resources */


    @Override
    public DirectMessageList getDirectMessages(int count) throws TwitterException {
        return factory.createDirectMessageList(get(conf.getRestBaseURL() + "direct_messages/events/list.json"
                , new HttpParameter("count", count) ));
    }

    @Override
    public DirectMessageList getDirectMessages(int count, String cursor) throws TwitterException {
        return factory.createDirectMessageList(get(conf.getRestBaseURL() + "direct_messages/events/list.json"
                , new HttpParameter("count", count)
                , new HttpParameter("cursor", cursor)));
    }


    @Override
    public DirectMessage showDirectMessage(long id) throws TwitterException {
        return factory.createDirectMessage(get(conf.getRestBaseURL() + "direct_messages/events/show.json?id=" + id));
    }

    @Override
    public void destroyDirectMessage(long id) throws TwitterException {
        ensureAuthorizationEnabled();
        http.delete(conf.getRestBaseURL() + "direct_messages/events/destroy.json?id=" + id, null, auth, null);
    }

    @Override
    public DirectMessage sendDirectMessage(long recipientId, String text, QuickReply... quickReplies)
            throws TwitterException {
        try {
            return factory.createDirectMessage(post(conf.getRestBaseURL() + "direct_messages/events/new.json",
                    createMessageCreateJsonObject(recipientId, text, -1L,  null, quickReplies)));
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }
    @Override
    public DirectMessage sendDirectMessage(long recipientId, String text, String quickReplyResponse)
            throws TwitterException {
        try {
            return factory.createDirectMessage(post(conf.getRestBaseURL() + "direct_messages/events/new.json",
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
            return factory.createDirectMessage(post(conf.getRestBaseURL() + "direct_messages/events/new.json",
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
        return get(url).asStream();
    }

    /* Friends & Followers Resources */

    @Override
    public IDs getNoRetweetsFriendships() throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friendships/no_retweets/ids.json"));
    }

    @Override
    public IDs getFriendsIDs(long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friends/ids.json?cursor=" + cursor));
    }

    @Override
    public IDs getFriendsIDs(long userId, long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friends/ids.json?user_id=" + userId +
                "&cursor=" + cursor));
    }

    @Override
    public IDs getFriendsIDs(long userId, long cursor, int count) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friends/ids.json?user_id=" + userId +
                "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public IDs getFriendsIDs(String screenName, long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friends/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor)));
    }

    @Override
    public IDs getFriendsIDs(String screenName, long cursor, int count) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friends/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public IDs getFollowersIDs(long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "followers/ids.json?cursor=" + cursor));
    }

    @Override
    public IDs getFollowersIDs(long userId, long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "followers/ids.json?user_id=" + userId
                + "&cursor=" + cursor));
    }

    @Override
    public IDs getFollowersIDs(long userId, long cursor, int count) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "followers/ids.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public IDs getFollowersIDs(String screenName, long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "followers/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor)));
    }

    @Override
    public IDs getFollowersIDs(String screenName, long cursor, int count) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "followers/ids.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public ResponseList<Friendship> lookupFriendships(long... ids) throws TwitterException {
        return factory.createFriendshipList(get(conf.getRestBaseURL() + "friendships/lookup.json?user_id=" + StringUtil.join(ids)));
    }

    @Override
    public ResponseList<Friendship> lookupFriendships(String... screenNames) throws TwitterException {
        return factory.createFriendshipList(get(conf.getRestBaseURL()
                + "friendships/lookup.json?screen_name=" + StringUtil.join(screenNames)));
    }

    @Override
    public IDs getIncomingFriendships(long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friendships/incoming.json?cursor=" + cursor));
    }

    @Override
    public IDs getOutgoingFriendships(long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "friendships/outgoing.json?cursor=" + cursor));
    }

    @Override
    public User createFriendship(long userId) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "friendships/create.json?user_id=" + userId));
    }

    @Override
    public User createFriendship(String screenName) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "friendships/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public User createFriendship(long userId, boolean follow) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "friendships/create.json?user_id=" + userId + "&follow="
                + follow));
    }

    @Override
    public User createFriendship(String screenName, boolean follow) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "friendships/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName),
                        new HttpParameter("follow", follow)
                }
        ));
    }

    @Override
    public User destroyFriendship(long userId) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "friendships/destroy.json?user_id=" + userId));
    }

    @Override
    public User destroyFriendship(String screenName) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "friendships/destroy.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public Relationship updateFriendship(long userId, boolean enableDeviceNotification
            , boolean retweets) throws TwitterException {
        return factory.createRelationship((post(conf.getRestBaseURL() + "friendships/update.json",
                new HttpParameter("user_id", userId),
                new HttpParameter("device", enableDeviceNotification),
                new HttpParameter("retweets", retweets))));
    }

    @Override
    public Relationship updateFriendship(String screenName, boolean enableDeviceNotification
            , boolean retweets) throws TwitterException {
        return factory.createRelationship(post(conf.getRestBaseURL() + "friendships/update.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("device", enableDeviceNotification),
                new HttpParameter("retweets", retweets)));
    }

    @Override
    public Relationship showFriendship(long sourceId, long targetId) throws TwitterException {
        return factory.createRelationship(get(conf.getRestBaseURL() + "friendships/show.json"
                , new HttpParameter("source_id", sourceId), new HttpParameter("target_id", targetId)));
    }

    @Override
    public Relationship showFriendship(String sourceScreenName, String targetScreenName) throws TwitterException {
        return factory.createRelationship(get(conf.getRestBaseURL() + "friendships/show.json",
                getParameterArray("source_screen_name", sourceScreenName, "target_screen_name", targetScreenName)));
    }

    @Override
    public PagableResponseList<User> getFriendsList(long userId, long cursor) throws TwitterException {
        return getFriendsList(userId, cursor, 20);
    }

    @Override
    public PagableResponseList<User> getFriendsList(long userId, long cursor, int count) throws TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "friends/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public PagableResponseList<User> getFriendsList(String screenName, long cursor) throws TwitterException {
        return getFriendsList(screenName, cursor, 20);
    }

    @Override
    public PagableResponseList<User> getFriendsList(String screenName, long cursor, int count) throws TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "friends/list.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public PagableResponseList<User> getFriendsList(long userId, long cursor, int count, boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "friends/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count
                + "&skip_status=" + skipStatus + "&include_user_entities=" + includeUserEntities));
    }

    @Override
    public PagableResponseList<User> getFriendsList(String screenName, long cursor, int count,
                                                    boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "friends/list.json",
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
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "followers/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count));
    }

    @Override
    public PagableResponseList<User> getFollowersList(String screenName, long cursor, int count) throws TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "followers/list.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count)));
    }

    @Override
    public PagableResponseList<User> getFollowersList(long userId, long cursor, int count,
                                                      boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "followers/list.json?user_id=" + userId
                + "&cursor=" + cursor + "&count=" + count
                + "&skip_status=" + skipStatus + "&include_user_entities=" + includeUserEntities));
    }

    @Override
    public PagableResponseList<User> getFollowersList(String screenName, long cursor, int count,
                                                      boolean skipStatus, boolean includeUserEntities) throws TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "followers/list.json",
                new HttpParameter("screen_name", screenName),
                new HttpParameter("cursor", cursor),
                new HttpParameter("count", count),
                new HttpParameter("skip_status", skipStatus),
                new HttpParameter("include_user_entities", includeUserEntities)));
    }

    /* Users Resources */

    @Override
    public AccountSettings getAccountSettings() throws TwitterException {
        return factory.createAccountSettings(get(conf.getRestBaseURL() + "account/settings.json"));
    }

    @Override
    public User verifyCredentials() throws TwitterException {
        return super.fillInIDAndScreenName(
                new HttpParameter[]{new HttpParameter("include_email", conf.isIncludeEmailEnabled())});
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
        return factory.createAccountSettings(post(conf.getRestBaseURL() + "account/settings.json"
                , profile.toArray(new HttpParameter[profile.size()])));

    }

    @Override
    public AccountSettings updateAllowDmsFrom(String allowDmsFrom) throws TwitterException {
        return factory.createAccountSettings(post(conf.getRestBaseURL() + "account/settings.json?allow_dms_from=" + allowDmsFrom));
    }

    @Override
    public User updateProfile(String name, String url
            , String location, String description) throws TwitterException {
        List<HttpParameter> profile = new ArrayList<HttpParameter>(4);
        addParameterToList(profile, "name", name);
        addParameterToList(profile, "url", url);
        addParameterToList(profile, "location", location);
        addParameterToList(profile, "description", description);
        return factory.createUser(post(conf.getRestBaseURL() + "account/update_profile.json"
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
        return factory.createUser(post(conf.getRestBaseURL()
                + "account/update_profile_image.json"
                , new HttpParameter[]{new HttpParameter("image", image)}));
    }

    @Override
    public User updateProfileImage(InputStream image) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL()
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
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "blocks/list.json?cursor=" + cursor));
    }

    @Override
    public IDs getBlocksIDs() throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "blocks/ids.json"));
    }

    @Override
    public IDs getBlocksIDs(long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "blocks/ids.json?cursor=" + cursor));
    }

    @Override
    public User createBlock(long userId) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "blocks/create.json?user_id=" + userId));
    }

    @Override
    public User createBlock(String screenName) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "blocks/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public User destroyBlock(long userId) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "blocks/destroy.json?user_id=" + userId));
    }

    @Override
    public User destroyBlock(String screenName) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "blocks/destroy.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName),
                }
        ));
    }


    @Override
    public PagableResponseList<User> getMutesList(long cursor) throws
            TwitterException {
        return factory.createPagableUserList(get(conf.getRestBaseURL() + "mutes/users/list.json?cursor=" + cursor));
    }

    @Override
    public IDs getMutesIDs(long cursor) throws TwitterException {
        return factory.createIDs(get(conf.getRestBaseURL() + "mutes/users/ids.json?cursor=" + cursor));
    }

    @Override
    public User createMute(long userId) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "mutes/users/create.json?user_id=" + userId));
    }

    @Override
    public User createMute(String screenName) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "mutes/users/create.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public User destroyMute(long userId) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "mutes/users/destroy.json?user_id=" + userId));
    }

    @Override
    public User destroyMute(String screenName) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "mutes/users/destroy.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public ResponseList<User> lookupUsers(long... ids) throws TwitterException {
        return factory.createUserList(get(conf.getRestBaseURL() + "users/lookup.json"
                , new HttpParameter("user_id", StringUtil.join(ids))));
    }

    @Override
    public ResponseList<User> lookupUsers(String... screenNames) throws TwitterException {
        return factory.createUserList(get(conf.getRestBaseURL() + "users/lookup.json"
                , new HttpParameter("screen_name", StringUtil.join(screenNames))));
    }

    @Override
    public User showUser(long userId) throws TwitterException {
        return factory.createUser(get(conf.getRestBaseURL() + "users/show.json?user_id=" + userId));
    }

    @Override
    public User showUser(String screenName) throws TwitterException {
        return factory.createUser(get(conf.getRestBaseURL() + "users/show.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    @Override
    public ResponseList<User> searchUsers(String query, int page) throws TwitterException {
        return factory.createUserList(get(conf.getRestBaseURL() + "users/search.json"
                , new HttpParameter("q", query), new HttpParameter("per_page", 20)
                , new HttpParameter("page", page)));
    }

    @Override
    public ResponseList<User> getContributees(long userId) throws TwitterException {
        return factory.createUserList(get(conf.getRestBaseURL() + "users/contributees.json?user_id=" + userId));
    }

    @Override
    public ResponseList<User> getContributees(String screenName) throws TwitterException {
        return factory.createUserList(get(conf.getRestBaseURL() + "users/contributees.json",
                new HttpParameter("screen_name", screenName)));
    }

    @Override
    public ResponseList<User> getContributors(long userId) throws TwitterException {
        return factory.createUserList(get(conf.getRestBaseURL() + "users/contributors.json?user_id=" + userId));
    }

    @Override
    public ResponseList<User> getContributors(String screenName) throws TwitterException {
        return factory.createUserList(get(conf.getRestBaseURL() + "users/contributors.json",
                new HttpParameter("screen_name", screenName)));
    }

    @Override
    public void removeProfileBanner() throws TwitterException {
        post(conf.getRestBaseURL()
                + "account/remove_profile_banner.json");
    }

    @Override
    public void updateProfileBanner(File image) throws TwitterException {
        checkFileValidity(image);
        post(conf.getRestBaseURL()
                + "account/update_profile_banner.json"
                , new HttpParameter("banner", image));
    }

    @Override
    public void updateProfileBanner(InputStream image) throws TwitterException {
        post(conf.getRestBaseURL()
                + "account/update_profile_banner.json"
                , new HttpParameter("banner", "banner", image));
    }

    /* Suggested Users Resources */

    @Override
    public ResponseList<User> getUserSuggestions(String categorySlug) throws TwitterException {
        HttpResponse res;
        try {
            res = get(conf.getRestBaseURL() + "users/suggestions/" + URLEncoder.encode(categorySlug, "UTF-8") + ".json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return factory.createUserListFromJSONArray_Users(res);
    }

    @Override
    public ResponseList<Category> getSuggestedUserCategories() throws TwitterException {
        return factory.createCategoryList(get(conf.getRestBaseURL() + "users/suggestions.json"));
    }

    @Override
    public ResponseList<User> getMemberSuggestions(String categorySlug) throws TwitterException {
        HttpResponse res;
        try {
            res = get(conf.getRestBaseURL() + "users/suggestions/" + URLEncoder.encode(categorySlug, "UTF-8") + "/members.json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return factory.createUserListFromJSONArray(res);
    }

    /* Favorites Resources */

    @Override
    public ResponseList<Status> getFavorites() throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "favorites/list.json"));
    }

    @Override
    public ResponseList<Status> getFavorites(long userId) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "favorites/list.json?user_id=" + userId));
    }

    @Override
    public ResponseList<Status> getFavorites(String screenName) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "favorites/list.json",
                new HttpParameter("screen_name", screenName)));
    }

    @Override
    public ResponseList<Status> getFavorites(Paging paging) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "favorites/list.json", paging.asPostParameterArray()));
    }

    @Override
    public ResponseList<Status> getFavorites(long userId, Paging paging) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "favorites/list.json",
                mergeParameters(new HttpParameter[]{new HttpParameter("user_id", userId)}
                        , paging.asPostParameterArray())
        ));
    }

    @Override
    public ResponseList<Status> getFavorites(String screenName, Paging paging) throws TwitterException {
        return factory.createStatusList(get(conf.getRestBaseURL() + "favorites/list.json",
                mergeParameters(new HttpParameter[]{new HttpParameter("screen_name", screenName)}
                        , paging.asPostParameterArray())
        ));
    }

    @Override
    public Status destroyFavorite(long id) throws TwitterException {
        return factory.createStatus(post(conf.getRestBaseURL() + "favorites/destroy.json?id=" + id));
    }

    @Override
    public Status createFavorite(long id) throws TwitterException {
        return factory.createStatus(post(conf.getRestBaseURL() + "favorites/create.json?id=" + id));
    }

    /* Saved Searches Resources */

    @Override
    public ResponseList<SavedSearch> getSavedSearches() throws TwitterException {
        return factory.createSavedSearchList(get(conf.getRestBaseURL() + "saved_searches/list.json"));
    }

    @Override
    public SavedSearch showSavedSearch(long id) throws TwitterException {
        return factory.createSavedSearch(get(conf.getRestBaseURL() + "saved_searches/show/" + id
                + ".json"));
    }

    @Override
    public SavedSearch createSavedSearch(String query) throws TwitterException {
        return factory.createSavedSearch(post(conf.getRestBaseURL() + "saved_searches/create.json"
                , new HttpParameter("query", query)));
    }

    @Override
    public SavedSearch destroySavedSearch(long id) throws TwitterException {
        return factory.createSavedSearch(post(conf.getRestBaseURL()
                + "saved_searches/destroy/" + id + ".json"));
    }

    /* Places & Geo Resources */

    @Override
    public Place getGeoDetails(String placeId) throws TwitterException {
        return factory.createPlace(get(conf.getRestBaseURL() + "geo/id/" + placeId
                + ".json"));
    }

    @Override
    public ResponseList<Place> reverseGeoCode(GeoQuery query) throws TwitterException {
        try {
            return factory.createPlaceList(get(conf.getRestBaseURL()
                    + "geo/reverse_geocode.json", query.asHttpParameterArray()));
        } catch (TwitterException te) {
            if (te.getStatusCode() == 404) {
                return factory.createEmptyResponseList();
            } else {
                throw te;
            }
        }
    }

    @Override
    public ResponseList<Place> searchPlaces(GeoQuery query) throws TwitterException {
        return factory.createPlaceList(get(conf.getRestBaseURL()
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
        return factory.createPlaceList(get(conf.getRestBaseURL()
                + "geo/similar_places.json", params.toArray(new HttpParameter[params.size()])));
    }

    /* Trends Resources */

    @Override
    public Trends getPlaceTrends(int woeid) throws TwitterException {
        return factory.createTrends(get(conf.getRestBaseURL()
                + "trends/place.json?id=" + woeid));
    }

    @Override
    public ResponseList<Location> getAvailableTrends() throws TwitterException {
        return factory.createLocationList(get(conf.getRestBaseURL()
                + "trends/available.json"));
    }

    @Override
    public ResponseList<Location> getClosestTrends(GeoLocation location) throws TwitterException {
        return factory.createLocationList(get(conf.getRestBaseURL()
                        + "trends/closest.json",
                new HttpParameter("lat", location.getLatitude())
                , new HttpParameter("long", location.getLongitude())));
    }

    /* Spam Reporting Resources */

    @Override
    public User reportSpam(long userId) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "users/report_spam.json?user_id=" + userId));
    }

    @Override
    public User reportSpam(String screenName) throws TwitterException {
        return factory.createUser(post(conf.getRestBaseURL() + "users/report_spam.json",
                new HttpParameter[]{
                        new HttpParameter("screen_name", screenName)
                }
        ));
    }

    /* Help Resources */

    @Override
    public TwitterAPIConfiguration getAPIConfiguration() throws TwitterException {
        return factory.createTwitterAPIConfiguration(get(conf.getRestBaseURL() + "help/configuration.json"));
    }

    @Override
    public ResponseList<Language> getLanguages() throws TwitterException {
        return factory.createLanguageList(get(conf.getRestBaseURL() + "help/languages.json"));
    }

    @Override
    public String getPrivacyPolicy() throws TwitterException {
        try {
            return get(conf.getRestBaseURL() + "help/privacy.json").asJSONObject().getString("privacy");
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public String getTermsOfService() throws TwitterException {
        try {
            return get(conf.getRestBaseURL() + "help/tos.json").asJSONObject().getString("tos");
        } catch (JSONException e) {
            throw new TwitterException(e);
        }
    }

    @Override
    public Map<String, RateLimitStatus> getRateLimitStatus() throws TwitterException {
        return factory.createRateLimitStatuses(get(conf.getRestBaseURL() + "application/rate_limit_status.json"));
    }

    @Override
    public Map<String, RateLimitStatus> getRateLimitStatus(String... resources) throws TwitterException {
        return factory.createRateLimitStatuses(get(conf.getRestBaseURL() + "application/rate_limit_status.json?resources=" + StringUtil.join(resources)));
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

    private HttpResponse get(String url) throws TwitterException {
        ensureAuthorizationEnabled();
        if (IMPLICIT_PARAMS_STR.length() > 0) {
            if (url.contains("?")) {
                url = url + "&" + IMPLICIT_PARAMS_STR;
            } else {
                url = url + "?" + IMPLICIT_PARAMS_STR;
            }
        }
        if (!conf.isMBeanEnabled()) {
            return http.get(url, null, auth, this);
        } else {
            // intercept HTTP call for monitoring purposes
            HttpResponse response = null;
            long start = System.currentTimeMillis();
            try {
                response = http.get(url, null, auth, this);
            } finally {
                long elapsedTime = System.currentTimeMillis() - start;
                TwitterAPIMonitor.getInstance().methodCalled(url, elapsedTime, isOk(response));
            }
            return response;
        }
    }

    /*package*/ HttpResponse get(String url, HttpParameter... params) throws TwitterException {
        ensureAuthorizationEnabled();
        if (!conf.isMBeanEnabled()) {
            return http.get(url, mergeImplicitParams(params), auth, this);
        } else {
            // intercept HTTP call for monitoring purposes
            HttpResponse response = null;
            long start = System.currentTimeMillis();
            try {
                response = http.get(url, mergeImplicitParams(params), auth, this);
            } finally {
                long elapsedTime = System.currentTimeMillis() - start;
                TwitterAPIMonitor.getInstance().methodCalled(url, elapsedTime, isOk(response));
            }
            return response;
        }
    }

    private HttpResponse post(String url) throws TwitterException {
        ensureAuthorizationEnabled();
        if (!conf.isMBeanEnabled()) {
            return http.post(url, IMPLICIT_PARAMS, auth, this);
        } else {
            // intercept HTTP call for monitoring purposes
            HttpResponse response = null;
            long start = System.currentTimeMillis();
            try {
                response = http.post(url, IMPLICIT_PARAMS, auth, this);
            } finally {
                long elapsedTime = System.currentTimeMillis() - start;
                TwitterAPIMonitor.getInstance().methodCalled(url, elapsedTime, isOk(response));
            }
            return response;
        }
    }

    /*package*/ HttpResponse post(String url, HttpParameter... params) throws TwitterException {
        ensureAuthorizationEnabled();
        if (!conf.isMBeanEnabled()) {
            return http.post(url, mergeImplicitParams(params), auth, this);
        } else {
            // intercept HTTP call for monitoring purposes
            HttpResponse response = null;
            long start = System.currentTimeMillis();
            try {
                response = http.post(url, mergeImplicitParams(params), auth, this);
            } finally {
                long elapsedTime = System.currentTimeMillis() - start;
                TwitterAPIMonitor.getInstance().methodCalled(url, elapsedTime, isOk(response));
            }
            return response;
        }
    }

    private HttpResponse post(String url, JSONObject json) throws TwitterException {
        ensureAuthorizationEnabled();
        if (!conf.isMBeanEnabled()) {
            return http.post(url, new HttpParameter[]{new HttpParameter(json)}, auth, this);
        } else {
            // intercept HTTP call for monitoring purposes
            HttpResponse response = null;
            long start = System.currentTimeMillis();
            try {
                response = http.post(url, new HttpParameter[]{new HttpParameter(json)}, auth, this);
            } finally {
                long elapsedTime = System.currentTimeMillis() - start;
                TwitterAPIMonitor.getInstance().methodCalled(url, elapsedTime, isOk(response));
            }
            return response;
        }
    }

    HttpParameter[] mergeParameters(HttpParameter[] params1, HttpParameter[] params2) {
        if (params1 != null && params2 != null) {
            HttpParameter[] params = new HttpParameter[params1.length + params2.length];
            System.arraycopy(params1, 0, params, 0, params1.length);
            System.arraycopy(params2, 0, params, params1.length, params2.length);
            return params;
        }
        if (null == params1 && null == params2) {
            return new HttpParameter[0];
        }
        if (params1 != null) {
            return params1;
        } else {
            return params2;
        }
    }

    HttpParameter[] mergeParameters(HttpParameter[] params1, HttpParameter params2) {
        if (params1 != null && params2 != null) {
            HttpParameter[] params = new HttpParameter[params1.length + 1];
            System.arraycopy(params1, 0, params, 0, params1.length);
            params[params.length - 1] = params2;
            return params;
        }
        if (null == params1 && null == params2) {
            return new HttpParameter[0];
        }
        if (params1 != null) {
            return params1;
        } else {
            return new HttpParameter[]{params2};
        }
    }

    private HttpParameter[] mergeImplicitParams(HttpParameter... params) {
        return mergeParameters(params, IMPLICIT_PARAMS);
    }

    private boolean isOk(HttpResponse response) {
        return response != null && response.getStatusCode() < 300;
    }

    @Override
    public String toString() {
        return "TwitterImpl{" +
                "INCLUDE_MY_RETWEET=" + INCLUDE_MY_RETWEET +
                '}';
    }
}
