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

package twitter4j;

import static twitter4j.ParseUtil.getDate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Date;

import twitter4j.conf.Configuration;

/**
 * A data class representing one single status of a user.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
/*package*/ final class StatusJSONImpl extends TwitterResponseImpl implements Status, java.io.Serializable {
    private static final Logger logger = Logger.getLogger(StatusJSONImpl.class);
    private static final long serialVersionUID = -6461195536943679985L;

    private Date createdAt;
    private long id;
    private String text;
    private int displayTextRangeStart = -1;
    private int displayTextRangeEnd = -1;
    private String source;
    private boolean isTruncated;
    private long inReplyToStatusId;
    private long inReplyToUserId;
    private boolean isFavorited;
    private boolean isRetweeted;
    private int favoriteCount;
    private String inReplyToScreenName;
    private GeoLocation geoLocation = null;
    private Place place = null;
    // this field should be int in theory, but left as long for the serialized form compatibility - TFJ-790
    private long retweetCount;
    private boolean isPossiblySensitive;
    private String lang;

    private long[] contributorsIDs;

    private Status retweetedStatus;
    private UserMentionEntity[] userMentionEntities;
    private URLEntity[] urlEntities;
    private HashtagEntity[] hashtagEntities;
    private MediaEntity[] mediaEntities;
    private SymbolEntity[] symbolEntities;
    private long currentUserRetweetId = -1L;
    private Scopes scopes;
    private User user = null;
    private String[] withheldInCountries = null;
    private Status quotedStatus;
    private long quotedStatusId = -1L;
    private URLEntity quotedStatusPermalink;
    private EditControl editControl = null;
    private long initialTweetId = -1L;

    /*package*/StatusJSONImpl(HttpResponse res, Configuration conf) throws TwitterException {
        super(res);
        JSONObject json = res.asJSONObject();
        init(json);
        if (conf.isJSONStoreEnabled()) {
            TwitterObjectFactory.clearThreadLocalMap();
            TwitterObjectFactory.registerJSONObject(this, json);
        }
    }

    /*package*/StatusJSONImpl(JSONObject json, Configuration conf) throws TwitterException {
        super();
        init(json);
        if (conf.isJSONStoreEnabled()) {
            TwitterObjectFactory.registerJSONObject(this, json);
        }
    }

    /*package*/ StatusJSONImpl(JSONObject json) throws TwitterException {
        super();
        init(json);
    }

    /* Only for serialization purposes. */
    /*package*/ StatusJSONImpl() {

    }

    private void init(JSONObject json) throws TwitterException {
        id = ParseUtil.getLong("id", json);
        source = ParseUtil.getUnescapedString("source", json);
        createdAt = getDate("created_at", json);
        isTruncated = ParseUtil.getBoolean("truncated", json);
        inReplyToStatusId = ParseUtil.getLong("in_reply_to_status_id", json);
        inReplyToUserId = ParseUtil.getLong("in_reply_to_user_id", json);
        isFavorited = ParseUtil.getBoolean("favorited", json);
        isRetweeted = ParseUtil.getBoolean("retweeted", json);
        inReplyToScreenName = ParseUtil.getUnescapedString("in_reply_to_screen_name", json);
        retweetCount = ParseUtil.getLong("retweet_count", json);
        favoriteCount = ParseUtil.getInt("favorite_count", json);
        isPossiblySensitive = ParseUtil.getBoolean("possibly_sensitive", json);
        try {
            if (!json.isNull("user")) {
                user = new UserJSONImpl(json.getJSONObject("user"));
            }
            geoLocation = JSONImplFactory.createGeoLocation(json);
            if (!json.isNull("place")) {
                place = new PlaceJSONImpl(json.getJSONObject("place"));
            }

            if (!json.isNull("retweeted_status")) {
                retweetedStatus = new StatusJSONImpl(json.getJSONObject("retweeted_status"));
            }
            if (!json.isNull("contributors")) {
                JSONArray contributorsArray = json.getJSONArray("contributors");
                contributorsIDs = new long[contributorsArray.length()];
                for (int i = 0; i < contributorsArray.length(); i++) {
                    contributorsIDs[i] = Long.parseLong(contributorsArray.getString(i));
                }
            } else {
                contributorsIDs = new long[0];
            }

            collectEntities(json);
            mergeExtendedEntities(json);
            if (!json.isNull("quoted_status")) {
                quotedStatus = new StatusJSONImpl(json.getJSONObject("quoted_status"));
            }
            if (!json.isNull("quoted_status_id")) {
                quotedStatusId = ParseUtil.getLong("quoted_status_id", json);
            }

            // edit controls
            JSONObject extEditControlJson = json.optJSONObject("ext_edit_control");
            if (extEditControlJson != null) {
                if (!extEditControlJson.isNull("initial")) {
                    editControl = new EditControlImpl(extEditControlJson.getJSONObject("initial"));
                } else if (!extEditControlJson.isNull("edit")) {
                    JSONObject edit = extEditControlJson.getJSONObject("edit");
                    if (!edit.isNull("edit_control_initial")) {
                        editControl = new EditControlImpl(edit.getJSONObject("edit_control_initial"));
                    }
                    initialTweetId = edit.getLong("initial_tweet_id");
                }
            }

            if (!json.isNull("quoted_status")) {
                quotedStatus = new StatusJSONImpl(json.getJSONObject("quoted_status"));
            }

            if (!json.isNull("display_text_range")) {
                JSONArray indicesArray = json.getJSONArray("display_text_range");
                displayTextRangeStart = indicesArray.getInt(0);
                displayTextRangeEnd = indicesArray.getInt(1);
            }

            userMentionEntities = userMentionEntities == null ? new UserMentionEntity[0] : userMentionEntities;
            urlEntities = urlEntities == null ? new URLEntity[0] : urlEntities;
            hashtagEntities = hashtagEntities == null ? new HashtagEntity[0] : hashtagEntities;
            symbolEntities = symbolEntities == null ? new SymbolEntity[0] : symbolEntities;
            mediaEntities = mediaEntities == null ? new MediaEntity[0] : mediaEntities;
            if (!json.isNull("text")) {
                text = HTMLEntity.unescapeAndSlideEntityIncdices(json.getString("text"), userMentionEntities,
                        urlEntities, hashtagEntities, mediaEntities);
            }
            if (!json.isNull("full_text")) {
                text = HTMLEntity.unescapeAndSlideEntityIncdices(json.getString("full_text"), userMentionEntities,
                        urlEntities, hashtagEntities, mediaEntities);
            }

            if (!json.isNull("extended_tweet")) {
                mergeExtendedTweet(json.getJSONObject("extended_tweet"));
            }

            if (!json.isNull("current_user_retweet")) {
                currentUserRetweetId = json.getJSONObject("current_user_retweet").getLong("id");
            }
            if (!json.isNull("lang")) {
                lang = ParseUtil.getUnescapedString("lang", json);
            }

            if (!json.isNull("scopes")) {
                JSONObject scopesJson = json.getJSONObject("scopes");
                if (!scopesJson.isNull("place_ids")) {
                    JSONArray placeIdsArray = scopesJson.getJSONArray("place_ids");
                    int len = placeIdsArray.length();
                    String[] placeIds = new String[len];
                    for (int i = 0; i < len; i++) {
                        placeIds[i] = placeIdsArray.getString(i);
                    }
                    scopes = new ScopesImpl(placeIds);
                }
            }
            if (!json.isNull("withheld_in_countries")){
                JSONArray withheld_in_countries = json.getJSONArray("withheld_in_countries");
                int length = withheld_in_countries.length();
                withheldInCountries = new String[length];
                for (int i = 0 ; i < length; i ++) {
                    withheldInCountries[i] = withheld_in_countries.getString(i);
                }
            }
        } catch (JSONException jsone) {
            throw new TwitterException(jsone);
        }
    }

    private void collectEntities(JSONObject json) throws JSONException, TwitterException {
        if (!json.isNull("entities")) {
            JSONObject entities = json.getJSONObject("entities");
            userMentionEntities = EntitiesParseUtil.getUserMentions(entities);
            urlEntities = EntitiesParseUtil.getUrls(entities);
            hashtagEntities = EntitiesParseUtil.getHashtags(entities);
            symbolEntities = EntitiesParseUtil.getSymbols(entities);
            mediaEntities = EntitiesParseUtil.getMedia(entities);
        }
        if (!json.isNull("quoted_status")) {
            quotedStatus = new StatusJSONImpl(json.getJSONObject("quoted_status"));
        }
        if (!json.isNull("quoted_status_id")) {
            quotedStatusId = ParseUtil.getLong("quoted_status_id", json);
        }
        if (!json.isNull("quoted_status_permalink")) {
            quotedStatusPermalink = new QuotedStatusPermalinkJSONImpl(json.getJSONObject("quoted_status_permalink"));
        }
    }

    private void mergeExtendedEntities(JSONObject json) throws JSONException, TwitterException {
        if (!json.isNull("extended_entities")) {
            JSONObject extendedEntities = json.getJSONObject("extended_entities");
            if (!extendedEntities.isNull("media")) {
                JSONArray mediaArray = extendedEntities.getJSONArray("media");
                final int len = mediaArray.length();
                mediaEntities = new MediaEntity[len];
                for (int i = 0; i < len; i++) {
                    mediaEntities[i] = new MediaEntityJSONImpl(mediaArray.getJSONObject(i));
                }
            }
        }
    }

    private void mergeExtendedTweet(JSONObject extendedTweet) throws TwitterException {
        try {
            JSONArray indicesArray = extendedTweet.getJSONArray("display_text_range");
            displayTextRangeStart = indicesArray.getInt(0);
            displayTextRangeEnd = indicesArray.getInt(1);

            collectEntities(extendedTweet);

            userMentionEntities = userMentionEntities == null ? new UserMentionEntity[0] : userMentionEntities;
            urlEntities = urlEntities == null ? new URLEntity[0] : urlEntities;
            hashtagEntities = hashtagEntities == null ? new HashtagEntity[0] : hashtagEntities;
            symbolEntities = symbolEntities == null ? new SymbolEntity[0] : symbolEntities;
            mediaEntities = mediaEntities == null ? new MediaEntity[0] : mediaEntities;
            text = HTMLEntity.unescapeAndSlideEntityIncdices(extendedTweet.getString("full_text"), userMentionEntities,
                    urlEntities, hashtagEntities, mediaEntities);
        } catch (JSONException jsone) {
            throw new TwitterException(jsone);
        }
    }

    @Override
    public int compareTo(Status that) {
        long delta = this.id - that.getId();
        if (delta < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else if (delta > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) delta;
    }

    @Override
    @NotNull
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    @NotNull
    public String getText() {
        return this.text;
    }

    @Override
    public int getDisplayTextRangeStart() {
        return displayTextRangeStart;
    }

    @Override
    public int getDisplayTextRangeEnd() {
        return displayTextRangeEnd;
    }

    @Override
    @Nullable
    public String getSource() {
        return this.source;
    }

    @Override
    public void setSource(@Nullable String s) {
        this.source = s;
    }

    @Override
    public boolean isTruncated() {
        return isTruncated;
    }

    @Override
    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    @Override
    public void setInReplyToStatusId(long l) {
        this.inReplyToStatusId = l;
    }

    @Override
    public long getInReplyToUserId() {
        return inReplyToUserId;
    }

    @Override
    public void setInReplyToUserId(long l) {
        this.inReplyToUserId = l;
    }

    @Override
    @Nullable
    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    @Override
    public void setInReplyToScreenName(@Nullable String s) {
        this.inReplyToScreenName = s;
    }

    @Override
    @Nullable
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    @Override
    @Nullable
    public Place getPlace() {
        return place;
    }

    @Override
    @NotNull
    public long[] getContributors() {
        return contributorsIDs;
    }

    @Override
    public boolean isFavorited() {
        return isFavorited;
    }

    @Override
    public boolean isRetweeted() {
        return isRetweeted;
    }

    @Override
    public int getFavoriteCount() {
        return favoriteCount;
    }

    @Override
    @Nullable
    public User getUser() {
        return user;
    }

    @Override
    public boolean isRetweet() {
        return retweetedStatus != null;
    }

    @Override
    @Nullable
    public Status getRetweetedStatus() {
        return retweetedStatus;
    }

    @Override
    public int getRetweetCount() {
        return (int) retweetCount;
    }

    @Override
    public boolean isRetweetedByMe() {
        return currentUserRetweetId != -1L;
    }

    @Override
    public long getCurrentUserRetweetId() {
        return currentUserRetweetId;
    }

    @Override
    public boolean isPossiblySensitive() {
        return isPossiblySensitive;
    }

    @Override
    public UserMentionEntity[] getUserMentionEntities() {
        return userMentionEntities;
    }

    @Override
    public URLEntity[] getURLEntities() {
        return urlEntities;
    }

    @Override
    public HashtagEntity[] getHashtagEntities() {
        return hashtagEntities;
    }

    @Override
    public MediaEntity[] getMediaEntities() {
        return mediaEntities;
    }

    @Override
    public SymbolEntity[] getSymbolEntities() {
        return symbolEntities;
    }

    @Override
    @Nullable
    public Scopes getScopes() {
        return scopes;
    }

    @Override
    @Nullable
    public String[] getWithheldInCountries() {
        return withheldInCountries;
    }

    @Override
    public long getQuotedStatusId() {
        return quotedStatusId;
    }

    @Override
    @Nullable
    public Status getQuotedStatus() {
        return quotedStatus;
    }

    @Override
    public void setQuotedStatus(@Nullable Status status) {
        this.quotedStatus = status;
    }

    @Override
    @Nullable
    public URLEntity getQuotedStatusPermalink() {
        return quotedStatusPermalink;
    }

    @Override
    @Nullable
    public EditControl getEditControl() {
        return editControl;
    }

    @Override
    public long getInitialTweetId() {
        return initialTweetId;
    }

    @Override
    @Nullable
    public String getLang() {
        return lang;
    }

    @Override
    public boolean isPromoted() {
        return false;
    }

    @Nullable
    @Override
    public Card getCard() {
        return null;
    }

    /*package*/
    static ResponseList<Status> createStatusList(HttpResponse res, Configuration conf) throws TwitterException {
        try {
            if (conf.isJSONStoreEnabled()) {
                TwitterObjectFactory.clearThreadLocalMap();
            }
            JSONArray list = res.asJSONArray();
            int size = list.length();
            ResponseList<Status> statuses = new ResponseListImpl<Status>(size, res);
            for (int i = 0; i < size; i++) {
                JSONObject json = list.getJSONObject(i);
                Status status = new StatusJSONImpl(json);
                if (conf.isJSONStoreEnabled()) {
                    TwitterObjectFactory.registerJSONObject(status, json);
                }
                statuses.add(status);
            }
            if (conf.isJSONStoreEnabled()) {
                TwitterObjectFactory.registerJSONObject(statuses, list);
            }
            return statuses;
        } catch (JSONException jsone) {
            throw new TwitterException(jsone);
        }
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Status && ((Status) obj).getId() == this.id;
    }

    @Override
    public String toString() {
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
                ", retweetCount=" + retweetCount +
                ", isPossiblySensitive=" + isPossiblySensitive +
                ", lang='" + lang + '\'' +
                ", contributorsIDs=" + Arrays.toString(contributorsIDs) +
                ", retweetedStatus=" + retweetedStatus +
                ", userMentionEntities=" + Arrays.toString(userMentionEntities) +
                ", urlEntities=" + Arrays.toString(urlEntities) +
                ", hashtagEntities=" + Arrays.toString(hashtagEntities) +
                ", mediaEntities=" + Arrays.toString(mediaEntities) +
                ", symbolEntities=" + Arrays.toString(symbolEntities) +
                ", currentUserRetweetId=" + currentUserRetweetId +
                ", user=" + user +
                ", withHeldInCountries=" + Arrays.toString(withheldInCountries)+
                ", quotedStatusId=" + quotedStatusId +
                ", quotedStatus=" + quotedStatus +
                '}';
    }

}
