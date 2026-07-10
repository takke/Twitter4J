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
 * A data class representing permalink of the quoted status
 *
 * @author Hiroaki Takeuchi - takke30 at gmail.com
 * @since Twitter4J 4.0.7
 */
@Suppress("PropertyName")
class QuotedStatusPermalinkJSONImpl : EntityIndex, URLEntity {

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

    /* For serialization purposes only. */
    constructor() : super()

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        try {
            if (!json.isNull("url")) {
                this.url = json.getString("url")
            }

            expandedURL = if (!json.isNull("expanded")) {
                json.getString("expanded")
            } else {
                url
            }

            displayURL = if (!json.isNull("display")) {
                json.getString("display")
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

        val that = o as QuotedStatusPermalinkJSONImpl

        if (url != that.url) return false
        if (expandedURL != that.expandedURL) return false
        return displayURL == that.displayURL
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + expandedURL.hashCode()
        result = 31 * result + displayURL.hashCode()
        return result
    }

    override fun toString(): String {
        return "QuotedStatusPermalinkJSONImpl{" +
                "url='" + url + '\'' +
                ", expandedURL='" + expandedURL + '\'' +
                ", displayURL='" + displayURL + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = -9029983811168784541L
    }
}
