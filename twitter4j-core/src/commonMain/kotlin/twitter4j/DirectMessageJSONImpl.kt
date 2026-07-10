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
import twitter4j.conf.JsonStoreConfiguration

/**
 * A data class representing sent/received direct message.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @author Hiroaki TAKEUCHI - takke30 at gmail.com
 */
internal class DirectMessageJSONImpl : TwitterResponseImpl, DirectMessage {
    override var id: Long = 0
        private set
    // 元実装では text は必ず設定されるが、DirectMessage.text は非nullのため既定値 "" とする。
    override var text: String = ""
        private set
    override var senderId: Long = 0
        private set
    override var recipientId: Long = 0
        private set
    override var createdAt: Date = Date(0)
        private set
    override var userMentionEntities: Array<UserMentionEntity> = emptyArray()
        private set
    override var uRLEntities: Array<URLEntity> = emptyArray()
        private set
    override var hashtagEntities: Array<HashtagEntity> = emptyArray()
        private set
    override var mediaEntities: Array<MediaEntity> = emptyArray()
        private set
    override var symbolEntities: Array<SymbolEntity> = emptyArray()
        private set
    override var quickReplies: Array<QuickReply> = emptyArray()
        private set
    // 元実装では quick_reply_response が無い場合 null になりうるが、DirectMessage.quickReplyResponse は
    // 非nullのため既定値 "" とする。
    override var quickReplyResponse: String = ""
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : super(res) {
        val json = res.asJSONObject()
        try {
            val event = json.getJSONObject("event")
            init(event)
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
                TwitterObjectFactory.registerJSONObject(this, event)
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        init(json)
    }

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        try {
            id = ParseUtil.getLong("id", json)
            val messageData: JSONObject
            if (!json.isNull("created_timestamp")) {
                createdAt = Date(json.getLong("created_timestamp"))
                val messageCreate = json.getJSONObject("message_create")
                recipientId = ParseUtil.getLong("recipient_id", messageCreate.getJSONObject("target"))
                senderId = ParseUtil.getLong("sender_id", messageCreate)
                messageData = messageCreate.getJSONObject("message_data")
            } else {
                // raw JSON data from Twitter4J 4.0.6 or before
                createdAt = ParseUtil.getDate("created_at", json) ?: Date(0)
                senderId = ParseUtil.getLong("sender_id", json)
                recipientId = ParseUtil.getLong("recipient_id", json)
                messageData = json
            }
            if (!messageData.isNull("entities")) {
                val entities = messageData.getJSONObject("entities")
                userMentionEntities = EntitiesParseUtil.getUserMentions(entities) ?: emptyArray()
                uRLEntities = EntitiesParseUtil.getUrls(entities) ?: emptyArray()
                hashtagEntities = EntitiesParseUtil.getHashtags(entities) ?: emptyArray()
                symbolEntities = EntitiesParseUtil.getSymbols(entities) ?: emptyArray()
            }

            if (!messageData.isNull("attachment")) {
                val attachment = messageData.getJSONObject("attachment")
                // force parsing "type" as "media"
                if (!attachment.isNull("media")) {
                    mediaEntities = arrayOf(MediaEntityJSONImpl(attachment.getJSONObject("media")))
                }
            }
            if (!messageData.isNull("quick_reply")) {
                // dm with quick reply options
                val options = messageData.getJSONObject("quick_reply").getJSONArray("options")
                val quickReplyList = ArrayList<QuickReply>()
                for (i in 0 until options.length()) {
                    val option = options.getJSONObject(i)
                    val description = if (option.isNull("description")) null else option.getString("description")
                    val metadata = if (option.isNull("metadata")) null else option.getString("metadata")
                    quickReplyList.add(QuickReply(option.getString("label"), description, metadata))
                }
                quickReplies = quickReplyList.toTypedArray()
            } else {
                quickReplies = emptyArray()
            }
            if (!messageData.isNull("quick_reply_response") && !messageData.getJSONObject("quick_reply_response").isNull("metadata")) {
                quickReplyResponse = messageData.getJSONObject("quick_reply_response").getString("metadata")
            }
            text = HTMLEntity.unescapeAndSlideEntityIncdices(messageData.getString("text"), userMentionEntities,
                    uRLEntities, hashtagEntities, mediaEntities)
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || this::class != o::class) return false

        val that = o as DirectMessageJSONImpl

        if (id != that.id) return false
        if (senderId != that.senderId) return false
        if (recipientId != that.recipientId) return false
        if (text != that.text) return false
        if (createdAt != that.createdAt) return false
        if (!userMentionEntities.contentEquals(that.userMentionEntities)) return false
        if (!uRLEntities.contentEquals(that.uRLEntities)) return false
        if (!hashtagEntities.contentEquals(that.hashtagEntities)) return false
        if (!mediaEntities.contentEquals(that.mediaEntities)) return false
        if (!symbolEntities.contentEquals(that.symbolEntities)) return false
        if (!quickReplies.contentEquals(that.quickReplies)) return false
        return quickReplyResponse == that.quickReplyResponse
    }

    override fun hashCode(): Int {
        var result = (id xor (id ushr 32)).toInt()
        result = 31 * result + text.hashCode()
        result = 31 * result + (senderId xor (senderId ushr 32)).toInt()
        result = 31 * result + (recipientId xor (recipientId ushr 32)).toInt()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + userMentionEntities.contentHashCode()
        result = 31 * result + uRLEntities.contentHashCode()
        result = 31 * result + hashtagEntities.contentHashCode()
        result = 31 * result + mediaEntities.contentHashCode()
        result = 31 * result + symbolEntities.contentHashCode()
        result = 31 * result + quickReplies.contentHashCode()
        result = 31 * result + quickReplyResponse.hashCode()
        return result
    }

    override fun toString(): String {
        return "DirectMessageJSONImpl{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", createdAt=" + createdAt +
                ", userMentionEntities=" + userMentionEntities.contentToString() +
                ", urlEntities=" + uRLEntities.contentToString() +
                ", hashtagEntities=" + hashtagEntities.contentToString() +
                ", mediaEntities=" + mediaEntities.contentToString() +
                ", symbolEntities=" + symbolEntities.contentToString() +
                ", quickReplies=" + quickReplies.contentToString() +
                ", quickReplyResponse='" + quickReplyResponse + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = 7092906238192790921L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createDirectMessageList(res: HttpResponse, conf: JsonStoreConfiguration): DirectMessageList {
            try {
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.clearThreadLocalMap()
                }
                val listAndMessages: Pair<JSONArray, DirectMessageList> = try {
                    val jsonObject = res.asJSONObject()
                    val events = jsonObject.getJSONArray("events")
                    events to DirectMessageListImpl(events.length(), jsonObject, res)
                } catch (te: TwitterException) {
                    if (te.cause != null && te.cause is JSONException) {
                        // serialized form from Twitter4J 4.0.6 or before
                        val events = res.asJSONArray()
                        events to DirectMessageListImpl(events.length(), res)
                    } else {
                        throw te
                    }
                }
                val list = listAndMessages.first
                val directMessages = listAndMessages.second
                for (i in 0 until list.length()) {
                    val json = list.getJSONObject(i)
                    val directMessage: DirectMessage = DirectMessageJSONImpl(json)
                    directMessages.add(directMessage)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(directMessage, json)
                    }
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(directMessages, list)
                }
                return directMessages
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}
