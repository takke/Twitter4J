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
 * A data class that has detailed information about a relationship between two users
 *
 * @author Perry Sakkaris - psakkaris at gmail.com
 * @see <a href="https://dev.twitter.com/docs/api/1.1/get/friendships/show">GET friendships/show | Twitter Developers</a>
 * @since Twitter4J 2.1.0
 */
internal class RelationshipJSONImpl : TwitterResponseImpl, Relationship {

    override var targetUserId: Long = 0
        private set
    // 元実装では screen_name が null になりうるが、Relationship の各プロパティは非nullのため既定値 "" とする。
    override var targetUserScreenName: String = ""
        private set
    private var sourceBlockingTarget: Boolean = false
    private var sourceNotificationsEnabled: Boolean = false
    private var sourceFollowingTarget: Boolean = false
    private var sourceFollowedByTarget: Boolean = false
    private var sourceCanDm: Boolean = false
    private var sourceMutingTarget: Boolean = false
    override var sourceUserId: Long = 0
        private set
    override var sourceUserScreenName: String = ""
        private set
    private var wantRetweets: Boolean = false

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : this(res, res.asJSONObject()) {
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
            TwitterObjectFactory.registerJSONObject(this, res.asJSONObject())
        }
    }

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : this(null, json)

    @Throws(TwitterException::class)
    constructor(res: HttpResponse?, json: JSONObject) : super(res) {
        try {
            val relationship = json.getJSONObject("relationship")
            val sourceJson = relationship.getJSONObject("source")
            val targetJson = relationship.getJSONObject("target")
            sourceUserId = ParseUtil.getLong("id", sourceJson)
            targetUserId = ParseUtil.getLong("id", targetJson)
            sourceUserScreenName = ParseUtil.getUnescapedString("screen_name", sourceJson) ?: ""
            targetUserScreenName = ParseUtil.getUnescapedString("screen_name", targetJson) ?: ""
            sourceBlockingTarget = ParseUtil.getBoolean("blocking", sourceJson)
            sourceFollowingTarget = ParseUtil.getBoolean("following", sourceJson)
            sourceFollowedByTarget = ParseUtil.getBoolean("followed_by", sourceJson)
            sourceCanDm = ParseUtil.getBoolean("can_dm", sourceJson)
            sourceMutingTarget = ParseUtil.getBoolean("muting", sourceJson)
            sourceNotificationsEnabled = ParseUtil.getBoolean("notifications_enabled", sourceJson)
            wantRetweets = ParseUtil.getBoolean("want_retweets", sourceJson)
        } catch (jsone: JSONException) {
            throw TwitterException(jsone.message + ":" + json.toString(), jsone)
        }
    }

    override val isSourceBlockingTarget: Boolean
        get() = sourceBlockingTarget

    override val isSourceFollowingTarget: Boolean
        get() = sourceFollowingTarget

    override val isTargetFollowingSource: Boolean
        get() = sourceFollowedByTarget

    override val isSourceFollowedByTarget: Boolean
        get() = sourceFollowedByTarget

    override val isTargetFollowedBySource: Boolean
        get() = sourceFollowingTarget

    override fun canSourceDm(): Boolean {
        return sourceCanDm
    }

    override val isSourceMutingTarget: Boolean
        get() = sourceMutingTarget

    override val isSourceNotificationsEnabled: Boolean
        get() = sourceNotificationsEnabled

    override val isSourceWantRetweets: Boolean
        get() = wantRetweets

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || this::class != o::class) return false

        val that = o as RelationshipJSONImpl

        if (sourceBlockingTarget != that.sourceBlockingTarget) return false
        if (sourceCanDm != that.sourceCanDm) return false
        if (sourceFollowedByTarget != that.sourceFollowedByTarget) return false
        if (sourceFollowingTarget != that.sourceFollowingTarget) return false
        if (sourceMutingTarget != that.sourceMutingTarget) return false
        if (sourceNotificationsEnabled != that.sourceNotificationsEnabled) return false
        if (sourceUserId != that.sourceUserId) return false
        if (targetUserId != that.targetUserId) return false
        if (wantRetweets != that.wantRetweets) return false
        if (sourceUserScreenName != that.sourceUserScreenName) return false
        if (targetUserScreenName != that.targetUserScreenName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (targetUserId xor (targetUserId ushr 32)).toInt()
        result = 31 * result + targetUserScreenName.hashCode()
        result = 31 * result + (if (sourceBlockingTarget) 1 else 0)
        result = 31 * result + (if (sourceNotificationsEnabled) 1 else 0)
        result = 31 * result + (if (sourceFollowingTarget) 1 else 0)
        result = 31 * result + (if (sourceFollowedByTarget) 1 else 0)
        result = 31 * result + (if (sourceCanDm) 1 else 0)
        result = 31 * result + (if (sourceMutingTarget) 1 else 0)
        result = 31 * result + (sourceUserId xor (sourceUserId ushr 32)).toInt()
        result = 31 * result + sourceUserScreenName.hashCode()
        result = 31 * result + (if (wantRetweets) 1 else 0)
        return result
    }

    override fun toString(): String {
        return "RelationshipJSONImpl{" +
                "targetUserId=" + targetUserId +
                ", targetUserScreenName='" + targetUserScreenName + '\'' +
                ", sourceBlockingTarget=" + sourceBlockingTarget +
                ", sourceNotificationsEnabled=" + sourceNotificationsEnabled +
                ", sourceFollowingTarget=" + sourceFollowingTarget +
                ", sourceFollowedByTarget=" + sourceFollowedByTarget +
                ", sourceCanDm=" + sourceCanDm +
                ", sourceMutingTarget=" + sourceMutingTarget +
                ", sourceUserId=" + sourceUserId +
                ", sourceUserScreenName='" + sourceUserScreenName + '\'' +
                ", wantRetweets=" + wantRetweets +
                '}'
    }

    companion object {
        private const val serialVersionUID = -2001484553401916448L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createRelationshipList(res: HttpResponse, conf: JsonStoreConfiguration): ResponseList<Relationship> {
            try {
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.clearThreadLocalMap()
                }
                val list = res.asJSONArray()
                val size = list.length()
                val relationships: ResponseList<Relationship> = ResponseListImpl(size, res)
                for (i in 0 until size) {
                    val json = list.getJSONObject(i)
                    val relationship: Relationship = RelationshipJSONImpl(json)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(relationship, json)
                    }
                    relationships.add(relationship)
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(relationships, list)
                }
                return relationships
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}
