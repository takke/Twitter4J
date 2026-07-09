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
 * A data class representing Trend.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.0.2
 */
@Suppress("PropertyName")
internal class TrendJSONImpl : Trend, java.io.Serializable {

    override val name: String
    override val URL: String
    override val query: String
    override val tweetVolume: Int

    constructor(json: JSONObject, storeJSON: Boolean) {
        // 元実装では url/query が不在時 null になりうるが、Trend の各プロパティは非nullのため "" とする。
        this.name = ParseUtil.getRawString("name", json) ?: ""
        this.URL = ParseUtil.getRawString("url", json) ?: ""
        this.query = ParseUtil.getRawString("query", json) ?: ""
        this.tweetVolume = ParseUtil.getInt("tweet_volume", json)
        if (storeJSON) {
            TwitterObjectFactory.registerJSONObject(this, json)
        }
    }

    constructor(json: JSONObject) : this(json, false)

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Trend) return false

        val trend = o

        if (name != trend.name) return false
        if (query != trend.query) return false
        if (URL != trend.URL) return false
        if (tweetVolume != trend.tweetVolume) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + URL.hashCode()
        result = 31 * result + query.hashCode()
        result = 31 * result + tweetVolume
        return result
    }

    override fun toString(): String {
        return "TrendJSONImpl{" +
                "name='" + name + '\'' +
                ", url='" + URL + '\'' +
                ", query='" + query + '\'' +
                ", tweet_volume=" + tweetVolume +
                '}'
    }

    companion object {
        private const val serialVersionUID = -4353426776065521132L
    }
}
