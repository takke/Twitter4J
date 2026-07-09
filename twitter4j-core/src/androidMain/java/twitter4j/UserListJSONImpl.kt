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

import twitter4j.conf.Configuration
import java.net.URI as JavaNetURI
import java.net.URISyntaxException
import java.util.Date

/**
 * A data class representing Basic list information element
 *
 * @author Dan Checkoway - dcheckoway at gmail.com
 */
internal class UserListJSONImpl : TwitterResponseImpl, UserList {

    override var id: Long = 0
        private set
    // 元実装では name/fullName/slug/description が null になりうるが、UserList の各プロパティは
    // 非nullのため既定値 "" とする。
    override var name: String = ""
        private set
    override var fullName: String = ""
        private set
    override var slug: String = ""
        private set
    override var description: String = ""
        private set
    override var subscriberCount: Int = 0
        private set
    override var memberCount: Int = 0
        private set
    private var uri: String? = null
    private var mode: Boolean = false
    // 元実装では user が無い場合 null になりうるが、UserList.user は非nullのため内部で nullable 保持する。
    private var _user: User? = null
    private var following: Boolean = false
    override var createdAt: Date = Date(0)
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: Configuration) : super(res) {
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
        }
        val json = res.asJSONObject()
        init(json)
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.registerJSONObject(this, json)
        }
    }

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        init(json)
    }

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        id = ParseUtil.getLong("id", json)
        name = ParseUtil.getRawString("name", json) ?: ""
        fullName = ParseUtil.getRawString("full_name", json) ?: ""
        slug = ParseUtil.getRawString("slug", json) ?: ""
        description = ParseUtil.getRawString("description", json) ?: ""
        subscriberCount = ParseUtil.getInt("subscriber_count", json)
        memberCount = ParseUtil.getInt("member_count", json)
        uri = ParseUtil.getRawString("uri", json)
        mode = "public" == ParseUtil.getRawString("mode", json)
        following = ParseUtil.getBoolean("following", json)
        createdAt = ParseUtil.getDate("created_at", json) ?: Date(0)

        try {
            if (!json.isNull("user")) {
                _user = UserJSONImpl(json.getJSONObject("user"))
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone.message + ":" + json.toString(), jsone)
        }
    }

    override fun compareTo(other: UserList?): Int {
        val delta = this.id - other!!.id
        return if (delta < Int.MIN_VALUE) {
            Int.MIN_VALUE
        } else if (delta > Int.MAX_VALUE) {
            Int.MAX_VALUE
        } else {
            delta.toInt()
        }
    }

    override val URI: JavaNetURI
        get() = try {
            JavaNetURI(uri)
        } catch (ex: URISyntaxException) {
            // 元実装は URISyntaxException 時に null を返していたが、UserList.URI は非nullのため空URIを返す。
            JavaNetURI("")
        }

    override val isPublic: Boolean
        get() = mode

    override val isFollowing: Boolean
        get() = following

    override val user: User
        get() = _user!!

    override fun hashCode(): Int {
        return id.toInt()
    }

    override fun equals(obj: Any?): Boolean {
        if (null == obj) {
            return false
        }
        if (this === obj) {
            return true
        }
        return obj is UserList && obj.id == this.id
    }

    override fun toString(): String {
        return "UserListJSONImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", slug='" + slug + '\'' +
                ", description='" + description + '\'' +
                ", subscriberCount=" + subscriberCount +
                ", memberCount=" + memberCount +
                ", uri='" + uri + '\'' +
                ", mode=" + mode +
                ", user=" + _user +
                ", following=" + following +
                '}'
    }

    companion object {
        private const val serialVersionUID = 449418980060197008L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createPagableUserListList(res: HttpResponse, conf: Configuration): PagableResponseList<UserList> {
            try {
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.clearThreadLocalMap()
                }
                val json = res.asJSONObject()
                val list = json.getJSONArray("lists")
                val size = list.length()
                val users: PagableResponseList<UserList> = PagableResponseListImpl(size, json, res)
                for (i in 0 until size) {
                    val userListJson = list.getJSONObject(i)
                    val userList: UserList = UserListJSONImpl(userListJson)
                    users.add(userList)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(userList, userListJson)
                    }
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(users, json)
                }
                return users
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }

        @JvmStatic
        @Throws(TwitterException::class)
        fun createUserListList(res: HttpResponse, conf: Configuration): ResponseList<UserList> {
            try {
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.clearThreadLocalMap()
                }
                val list = res.asJSONArray()
                val size = list.length()
                val users: ResponseList<UserList> = ResponseListImpl(size, res)
                for (i in 0 until size) {
                    val userListJson = list.getJSONObject(i)
                    val userList: UserList = UserListJSONImpl(userListJson)
                    users.add(userList)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(userList, userListJson)
                    }
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(users, list)
                }
                return users
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}
