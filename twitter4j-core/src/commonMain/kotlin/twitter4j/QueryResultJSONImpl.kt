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

import twitter4j.conf.JsonStoreConfiguration

/**
 * A data class representing search API response
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
internal class QueryResultJSONImpl : TwitterResponseImpl, QueryResult {

    override var sinceId: Long = 0
        private set
    override var maxId: Long = 0
        private set
    // 元実装では refresh_url が null になりうるが、QueryResult.refreshURL は非nullのため既定値 "" とする。
    override var refreshURL: String = ""
        private set
    override var count: Int = 0
        private set
    override var completedIn: Double = 0.0
        private set
    // 元実装では query が null になりうるが、QueryResult.query は非nullのため既定値 "" とする。
    override var query: String = ""
        private set
    override var tweets: List<Status> = emptyList()
        private set
    private var nextResults: String? = null

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : super(res) {
        val json = res.asJSONObject()
        try {
            val searchMetaData = json.getJSONObject("search_metadata")
            completedIn = ParseUtil.getDouble("completed_in", searchMetaData)
            count = ParseUtil.getInt("count", searchMetaData)
            maxId = ParseUtil.getLong("max_id", searchMetaData)
            nextResults = if (searchMetaData.has("next_results")) searchMetaData.getString("next_results") else null
            query = ParseUtil.getURLDecodedString("query", searchMetaData) ?: ""
            refreshURL = ParseUtil.getUnescapedString("refresh_url", searchMetaData) ?: ""
            sinceId = ParseUtil.getLong("since_id", searchMetaData)

            val array = json.getJSONArray("statuses")
            val list = ArrayList<Status>(array.length())
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
            }
            for (i in 0 until array.length()) {
                val tweet = array.getJSONObject(i)
                list.add(StatusJSONImpl(tweet, conf))
            }
            tweets = list
        } catch (jsone: JSONException) {
            throw TwitterException(jsone.message + ":" + json.toString(), jsone)
        }
    }

    constructor(query: Query) : super() {
        sinceId = query.sinceId
        count = query.count
        tweets = ArrayList(0)
    }

    override fun nextQuery(): Query? {
        val nr = nextResults ?: return null
        return Query.createWithNextPageQuery(nr)
    }

    override fun hasNext(): Boolean {
        return nextResults != null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || this::class != o::class) return false

        val that = o as QueryResult

        if (that.completedIn.compareTo(completedIn) != 0) return false
        if (maxId != that.maxId) return false
        if (count != that.count) return false
        if (sinceId != that.sinceId) return false
        if (query != that.query) return false
        if (refreshURL != that.refreshURL) return false
        if (tweets != that.tweets) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (sinceId xor (sinceId ushr 32)).toInt()
        result = 31 * result + (maxId xor (maxId ushr 32)).toInt()
        result = 31 * result + refreshURL.hashCode()
        result = 31 * result + count
        val temp = if (completedIn != 0.0) completedIn.toBits() else 0L
        result = 31 * result + (temp xor (temp ushr 32)).toInt()
        result = 31 * result + query.hashCode()
        result = 31 * result + tweets.hashCode()
        return result
    }

    override fun toString(): String {
        return "QueryResultJSONImpl{" +
                "sinceId=" + sinceId +
                ", maxId=" + maxId +
                ", refreshUrl='" + refreshURL + '\'' +
                ", count=" + count +
                ", completedIn=" + completedIn +
                ", query='" + query + '\'' +
                ", tweets=" + tweets +
                '}'
    }

    companion object {
        private const val serialVersionUID = -5359566235429947156L
    }
}
