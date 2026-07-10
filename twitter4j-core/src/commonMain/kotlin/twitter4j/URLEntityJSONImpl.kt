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
 * A data class representing one single URL entity.
 *
 * @author Mocel - mocel at guma.jp
 * @since Twitter4J 2.1.9
 */
@Suppress("PropertyName")
class URLEntityJSONImpl : EntityIndex, URLEntity {

    // 元実装では url/expandedURL/displayURL が null になりうるが、URLEntity の各プロパティは非nullのため既定値 "" とする。
    private var url: String = ""
    override var expandedURL: String = ""
        private set
    override var displayURL: String = ""
        private set

    override val text: String
        get() = url

    override val URL: String
        get() = url

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        init(json)
    }

    constructor(start: Int, end: Int, url: String, expandedURL: String, displayURL: String) : super() {
        this.start = start
        this.end = end
        this.url = url
        this.expandedURL = expandedURL
        this.displayURL = displayURL
    }

    /* For serialization purposes only. */
    constructor() : super()

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        try {
            val indicesArray = json.getJSONArray("indices")
            start = indicesArray.getInt(0)
            end = indicesArray.getInt(1)

            if (!json.isNull("url")) {
                this.url = json.getString("url")
            }

            expandedURL = if (!json.isNull("expanded_url")) {
                // sets expandedURL to url if expanded_url is null
                // http://jira.twitter4j.org/browse/TFJ-704
                json.getString("expanded_url")
            } else {
                url
            }

            displayURL = if (!json.isNull("display_url")) {
                // sets displayURL to url if expanded_url is null
                // http://jira.twitter4j.org/browse/TFJ-704
                json.getString("display_url")
            } else {
                url
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || this::class != o::class) return false

        val that = o as URLEntityJSONImpl

        if (displayURL != that.displayURL) return false
        if (expandedURL != that.expandedURL) return false
        if (url != that.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + expandedURL.hashCode()
        result = 31 * result + displayURL.hashCode()
        return result
    }

    override fun toString(): String {
        return "URLEntityJSONImpl{" +
                "url='" + url + '\'' +
                ", expandedURL='" + expandedURL + '\'' +
                ", displayURL='" + displayURL + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = 7333552738058031524L
    }
}
