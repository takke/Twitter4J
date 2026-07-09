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

/**
 * A data interface representing one single user mention entity.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.9
 */
class UserMentionEntityJSONImpl : EntityIndex, UserMentionEntity {

    // 元実装では name/screenName が null になりうるが、UserMentionEntity の各プロパティは非nullのため既定値 "" とする。
    override var name: String = ""
        private set
    override var screenName: String = ""
        private set
    override var id: Long = 0
        private set

    override val text: String
        get() = screenName

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        init(json)
    }

    constructor(start: Int, end: Int, name: String, screenName: String, id: Long) : super() {
        this.start = start
        this.end = end
        this.name = name
        this.screenName = screenName
        this.id = id
    }

    /* For serialization purposes only. */
    constructor() : super()

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        try {
            val indicesArray = json.getJSONArray("indices")
            start = indicesArray.getInt(0)
            end = indicesArray.getInt(1)

            if (!json.isNull("name")) {
                this.name = json.getString("name")
            }
            if (!json.isNull("screen_name")) {
                this.screenName = json.getString("screen_name")
            }
            if (json.has("id_str")) {
                this.id = ParseUtil.getLong("id_str", json)
            } else {
                this.id = ParseUtil.getLong("id", json)
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as UserMentionEntityJSONImpl

        if (id != that.id) return false
        if (name != that.name) return false
        if (screenName != that.screenName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + screenName.hashCode()
        result = 31 * result + (id xor (id ushr 32)).toInt()
        return result
    }

    override fun toString(): String {
        return "UserMentionEntityJSONImpl{" +
                "name='" + name + '\'' +
                ", screenName='" + screenName + '\'' +
                ", id=" + id +
                '}'
    }

    companion object {
        private const val serialVersionUID = 6060510953676673013L
    }
}
