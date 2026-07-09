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
 * @author Alessandro Bahgat - ale.bahgat at gmail.com
 */
class TimeZoneJSONImpl
@Throws(TwitterException::class)
constructor(jSONObject: JSONObject) : TimeZone {

    private val NAME: String
    private val TZINFO_NAME: String
    private val UTC_OFFSET: Int

    init {
        try {
            UTC_OFFSET = ParseUtil.getInt("utc_offset", jSONObject)
            NAME = jSONObject.getString("name")
            TZINFO_NAME = jSONObject.getString("tzinfo_name")
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override val name: String
        get() = NAME

    override fun tzinfoName(): String {
        return TZINFO_NAME
    }

    override fun utcOffset(): Int {
        return UTC_OFFSET
    }

    companion object {
        private const val serialVersionUID = 81958969762484144L
    }
}
