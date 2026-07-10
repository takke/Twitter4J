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
 * A data class representing one single Hashtag entity.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.9
 */
class HashtagEntityJSONImpl : EntityIndex, HashtagEntity, SymbolEntity {

    // 元実装では text が null になりうるが、HashtagEntity.text は非nullのため既定値 "" とする。
    override var text: String = ""
        private set

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        init(json)
    }

    constructor(start: Int, end: Int, text: String) : super() {
        this.start = start
        this.end = end
        this.text = text
    }

    /* For serialization purposes only. */
    constructor() : super()

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        try {
            val indicesArray = json.getJSONArray("indices")
            start = indicesArray.getInt(0)
            end = indicesArray.getInt(1)

            if (!json.isNull("text")) {
                this.text = json.getString("text")
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || this::class != o::class) return false

        val that = o as HashtagEntityJSONImpl

        if (text != that.text) return false

        return true
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }

    override fun toString(): String {
        return "HashtagEntityJSONImpl{" +
                "text='" + text + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = -5317828991902848906L
    }
}
