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

import twitter4j.api.HelpResources
import twitter4j.conf.Configuration

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.3
 */
class LanguageJSONImpl
@Throws(TwitterException::class)
constructor(json: JSONObject) : HelpResources.Language {

    private var name: String? = null
    private var code: String? = null
    private var status: String? = null

    init {
        init(json)
    }

    @Throws(TwitterException::class)
    private fun init(json: JSONObject) {
        try {
            name = json.getString("name")
            code = json.getString("code")
            status = json.getString("status")
        } catch (jsone: JSONException) {
            throw TwitterException(jsone.message + ":" + json.toString(), jsone)
        }
    }

    override fun getName(): String? {
        return name
    }

    override fun getCode(): String? {
        return code
    }

    override fun getStatus(): String? {
        return status
    }

    companion object {
        private const val serialVersionUID = 7494362811767097342L

        @JvmStatic
        @Throws(TwitterException::class)
        fun createLanguageList(res: HttpResponse, conf: Configuration): ResponseList<HelpResources.Language> {
            return createLanguageList(res.asJSONArray(), res, conf)
        }

        @JvmStatic
        @Throws(TwitterException::class)
        fun createLanguageList(
            list: JSONArray,
            res: HttpResponse,
            conf: Configuration
        ): ResponseList<HelpResources.Language> {
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
            }
            try {
                val size = list.length()
                val languages: ResponseList<HelpResources.Language> =
                    ResponseListImpl(size, res)
                for (i in 0 until size) {
                    val json = list.getJSONObject(i)
                    val language: HelpResources.Language = LanguageJSONImpl(json)
                    languages.add(language)
                    if (conf.isJSONStoreEnabled) {
                        TwitterObjectFactory.registerJSONObject(language, json)
                    }
                }
                if (conf.isJSONStoreEnabled) {
                    TwitterObjectFactory.registerJSONObject(languages, list)
                }
                return languages
            } catch (jsone: JSONException) {
                throw TwitterException(jsone)
            }
        }
    }
}
