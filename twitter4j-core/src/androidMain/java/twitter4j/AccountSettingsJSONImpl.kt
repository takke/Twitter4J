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
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.9
 */
internal class AccountSettingsJSONImpl : TwitterResponseImpl, AccountSettings {
    override var isSleepTimeEnabled: Boolean = false
        private set
    override var sleepStartTime: String = ""
        private set
    override var sleepEndTime: String = ""
        private set
    override var trendLocations: Array<Location> = emptyArray()
        private set
    override var isGeoEnabled: Boolean = false
        private set
    override var language: String = ""
        private set
    override var isAlwaysUseHttps: Boolean = false
        private set
    override var isDiscoverableByEmail: Boolean = false
        private set
    // 元実装では time_zone が無い場合 null になりうるが、AccountSettings.timeZone は
    // 非nullのため内部で nullable 保持し、公開時に非nullとして返す。
    private var _timeZone: TimeZone? = null
    override var screenName: String = ""
        private set
    override var allowDmsFrom: String = ""
        private set

    override val timeZone: TimeZone
        get() = _timeZone!!

    @Throws(TwitterException::class)
    private constructor(res: HttpResponse?, json: JSONObject) : super(res) {
        try {
            val sleepTime = json.getJSONObject("sleep_time")
            isSleepTimeEnabled = ParseUtil.getBoolean("enabled", sleepTime)
            sleepStartTime = sleepTime.getString("start_time")
            sleepEndTime = sleepTime.getString("end_time")
            trendLocations = if (json.isNull("trend_location")) {
                emptyArray()
            } else {
                val locations = json.getJSONArray("trend_location")
                Array<Location>(locations.length()) { i -> LocationJSONImpl(locations.getJSONObject(i)) }
            }
            isGeoEnabled = ParseUtil.getBoolean("geo_enabled", json)
            language = json.getString("language")
            isAlwaysUseHttps = ParseUtil.getBoolean("always_use_https", json)
            isDiscoverableByEmail = ParseUtil.getBoolean("discoverable_by_email", json)
            // https://groups.google.com/forum/?fromgroups=#!topic/twitter4j/zPaAXP9gkU4
            _timeZone = if (json.isNull("time_zone")) {
                null
            } else {
                TimeZoneJSONImpl(json.getJSONObject("time_zone"))
            }
            screenName = json.getString("screen_name")
            allowDmsFrom = json.getString("allow_dms_from")
        } catch (e: JSONException) {
            throw TwitterException(e)
        }
    }

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: Configuration) : this(res, res.asJSONObject()) {
        if (conf.isJSONStoreEnabled) {
            TwitterObjectFactory.clearThreadLocalMap()
            TwitterObjectFactory.registerJSONObject(this, res.asJSONObject())
        }
    }

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : this(null, json)

    companion object {
        private const val serialVersionUID = 603189815663175766L
    }
}
