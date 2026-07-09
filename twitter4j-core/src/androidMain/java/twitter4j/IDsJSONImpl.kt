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

/**
 * A data class representing array of numeric IDs.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
internal class IDsJSONImpl : TwitterResponseImpl, IDs {

    override var iDs: LongArray = LongArray(0)
        private set
    override var previousCursor: Long = -1
        private set
    override var nextCursor: Long = -1
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: Configuration) : super(res) {
        val json = res.asString()
        init(json)
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
            TwitterObjectFactory.registerJSONObject(this, json)
        }
    }

    @Throws(TwitterException::class)
    constructor(json: String) : super() {
        init(json)
    }

    @Throws(TwitterException::class)
    private fun init(jsonStr: String) {
        val idList: JSONArray
        try {
            if (jsonStr.startsWith("{")) {
                val json = JSONObject(jsonStr)
                idList = json.getJSONArray("ids")
                iDs = LongArray(idList.length())
                for (i in 0 until idList.length()) {
                    try {
                        iDs[i] = idList.getString(i).toLong()
                    } catch (nfe: NumberFormatException) {
                        throw TwitterException("Twitter API returned malformed response: $json", nfe)
                    }
                }
                previousCursor = ParseUtil.getLong("previous_cursor", json)
                nextCursor = ParseUtil.getLong("next_cursor", json)
            } else {
                idList = JSONArray(jsonStr)
                iDs = LongArray(idList.length())
                for (i in 0 until idList.length()) {
                    try {
                        iDs[i] = idList.getString(i).toLong()
                    } catch (nfe: NumberFormatException) {
                        throw TwitterException("Twitter API returned malformed response: $idList", nfe)
                    }
                }
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun hasPrevious(): Boolean {
        return 0L != previousCursor
    }

    override fun hasNext(): Boolean {
        return 0L != nextCursor
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is IDs) return false

        val iDs = o

        if (!this.iDs.contentEquals(iDs.iDs)) return false

        return true
    }

    override fun hashCode(): Int {
        return iDs.contentHashCode()
    }

    override fun toString(): String {
        return "IDsJSONImpl{" +
                "ids=" + iDs.contentToString() +
                ", previousCursor=" + previousCursor +
                ", nextCursor=" + nextCursor +
                '}'
    }

    companion object {
        private const val serialVersionUID = 6999637496007165672L
    }
}
