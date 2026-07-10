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
 * A data class representing a Saved Search
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.0.8
 */
internal class SavedSearchJSONImpl : TwitterResponseImpl, SavedSearch {

    // 元実装では createdAt/query/name が null になりうるが、SavedSearch の各プロパティは非nullのため既定値を用いる。
    override var createdAt: Date = Date(0)
        private set
    override var query: String = ""
        private set
    override var position: Int = 0
        private set
    override var name: String = ""
        private set
    override var id: Long = 0
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : super(res) {
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
    constructor(savedSearch: JSONObject) : super() {
        init(savedSearch)
    }

    @Throws(TwitterException::class)
    private fun init(savedSearch: JSONObject) {
        createdAt = ParseUtil.getDate("created_at", savedSearch, "EEE MMM dd HH:mm:ss z yyyy") ?: Date(0)
        query = ParseUtil.getUnescapedString("query", savedSearch) ?: ""
        position = ParseUtil.getInt("position", savedSearch)
        name = ParseUtil.getUnescapedString("name", savedSearch) ?: ""
        id = ParseUtil.getLong("id", savedSearch)
    }

    override fun compareTo(other: SavedSearch): Int {
        return (this.id - other.id).toInt()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is SavedSearch) return false

        val that = o

        if (id != that.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = createdAt.hashCode()
        result = 31 * result + query.hashCode()
        result = 31 * result + position
        result = 31 * result + name.hashCode()
        result = 31 * result + id.toInt()
        return result
    }

    override fun toString(): String {
        return "SavedSearchJSONImpl{" +
                "createdAt=" + createdAt +
                ", query='" + query + '\'' +
                ", position=" + position +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}'
    }

    companion object {
        private const val serialVersionUID = -2281949861485441692L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createSavedSearchList(res: HttpResponse, conf: JsonStoreConfiguration): ResponseList<SavedSearch> {
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
            }
            val json = res.asJSONArray()
            val savedSearches: ResponseList<SavedSearch>
            try {
                savedSearches = ResponseListImpl(json.length(), res)
                for (i in 0 until json.length()) {
                    val savedSearchesJSON = json.getJSONObject(i)
                    val savedSearch: SavedSearch = SavedSearchJSONImpl(savedSearchesJSON)
                    savedSearches.add(savedSearch)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(savedSearch, savedSearchesJSON)
                    }
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(savedSearches, json)
                }
                return savedSearches
            } catch (jsone: JSONException) {
                throw TwitterException(jsone.message + ":" + res.asString(), jsone)
            }
        }
    }
}
