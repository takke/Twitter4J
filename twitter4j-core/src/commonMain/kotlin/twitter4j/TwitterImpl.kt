/*
 * Copyright (C) 2007 Yusuke Yamamoto
 * Copyright (C) 2011 Twitter, Inc.
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

import twitter4j.api.V1Resources
import twitter4j.auth.Authorization
import twitter4j.conf.Configuration

/**
 * A java representation of the <a href="https://dev.twitter.com/docs/api">Twitter REST API</a><br>
 * This class is thread safe and can be cached/re-used and used concurrently.<br>
 * Currently this class is not carefully designed to be extended. It is suggested to extend this class only for mock testing purpose.<br>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
// TwitPane(shared/core_tw)が `twitter is TwitterImpl` として参照し、TwitterFactory が
// リフレクションでコンストラクタ (Configuration, Authorization) を解決するため public class とする。
class TwitterImpl /*package*/ internal constructor(conf: Configuration, auth: Authorization?) :
    TwitterBaseImpl(conf, auth), Twitter {

    internal val IMPLICIT_PARAMS_STR: String
    internal val IMPLICIT_PARAMS: Array<HttpParameter>
    internal val INCLUDE_MY_RETWEET: HttpParameter

    init {
        INCLUDE_MY_RETWEET = HttpParameter("include_my_retweet", conf.isIncludeMyRetweetEnabled())
        if (implicitParamsMap.containsKey(conf)) {
            this.IMPLICIT_PARAMS = implicitParamsMap[conf]!!
            this.IMPLICIT_PARAMS_STR = implicitParamsStrMap[conf]!!
        } else {
            var implicitParamsStr = if (conf.isIncludeEntitiesEnabled()) "include_entities=" + true else ""
            val contributorsEnabled = conf.getContributingTo() != -1L
            if (contributorsEnabled) {
                if ("" != implicitParamsStr) {
                    implicitParamsStr += "&"
                }
                implicitParamsStr += "contributingto=" + conf.getContributingTo()
            }

            if (conf.isTweetModeExtended()) {
                if ("" != implicitParamsStr) {
                    implicitParamsStr += "&"
                }
                implicitParamsStr += "tweet_mode=extended"
            }
            if (conf.isIncludeExtEditControl()) {
                if ("" != implicitParamsStr) {
                    implicitParamsStr += "&"
                }
                implicitParamsStr += "include_ext_edit_control=true"
            }

            val params: MutableList<HttpParameter> = ArrayList(3)
            if (conf.isIncludeEntitiesEnabled()) {
                params.add(HttpParameter("include_entities", "true"))
            }
            if (contributorsEnabled) {
                params.add(HttpParameter("contributingto", conf.getContributingTo()))
            }
            if (conf.isTrimUserEnabled()) {
                params.add(HttpParameter("trim_user", "1"))
            }
            if (conf.isIncludeExtAltTextEnabled()) {
                params.add(HttpParameter("include_ext_alt_text", "true"))
            }
            if (conf.isTweetModeExtended()) {
                params.add(HttpParameter("tweet_mode", "extended"))
            }
            if (conf.isIncludeExtEditControl()) {
                params.add(HttpParameter("include_ext_edit_control", "true"))
            }
            val implicitParams = params.toTypedArray()

            // implicitParamsMap.containsKey() is evaluated in the above if clause.
            // thus implicitParamsStrMap needs to be initialized first
            // KMP移行 Phase 4-4: putIfAbsent は common の MutableMap に存在しないため通常の put に置換。
            // 値は conf に対して決定的なため、競合で上書きされても同一値であり無害（JVM 側は
            // ConcurrentHashMap のため put 自体はスレッドセーフ）。
            implicitParamsStrMap[conf] = implicitParamsStr
            implicitParamsMap[conf] = implicitParams

            this.IMPLICIT_PARAMS = implicitParams
            this.IMPLICIT_PARAMS_STR = implicitParamsStr
        }
    }

    override fun v1Resources(): V1Resources {
        return V1ResourcesImpl(this)
    }

    @Throws(TwitterException::class)
    internal fun get(url: String): HttpResponse {
        ensureAuthorizationEnabled()
        var u = url
        if (IMPLICIT_PARAMS_STR.isNotEmpty()) {
            u = if (u.contains("?")) {
                "$u&$IMPLICIT_PARAMS_STR"
            } else {
                "$u?$IMPLICIT_PARAMS_STR"
            }
        }
        return http.get(u, null, auth, this)
    }

    @Throws(TwitterException::class)
    internal fun get(url: String, vararg params: HttpParameter?): HttpResponse {
        ensureAuthorizationEnabled()
        @Suppress("UNCHECKED_CAST")
        return http.get(url, mergeImplicitParams(params) as Array<HttpParameter>?, auth, this)
    }

    @Throws(TwitterException::class)
    internal fun post(url: String): HttpResponse {
        ensureAuthorizationEnabled()
        return http.post(url, IMPLICIT_PARAMS, auth, this)
    }

    @Throws(TwitterException::class)
    internal fun post(url: String, vararg params: HttpParameter?): HttpResponse {
        ensureAuthorizationEnabled()
        @Suppress("UNCHECKED_CAST")
        return http.post(url, mergeImplicitParams(params) as Array<HttpParameter>?, auth, this)
    }

    @Throws(TwitterException::class)
    internal fun post(url: String, json: JSONObject): HttpResponse {
        ensureAuthorizationEnabled()
        return http.post(url, arrayOf(HttpParameter(json)), auth, this)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun mergeParameters(params1: Array<out HttpParameter?>?, params2: Array<out HttpParameter?>?): Array<HttpParameter?> {
        if (params1 != null && params2 != null) {
            val params = arrayOfNulls<HttpParameter>(params1.size + params2.size)
            params1.copyInto(params, 0, 0, params1.size)
            params2.copyInto(params, params1.size, 0, params2.size)
            return params
        }
        if (null == params1 && null == params2) {
            return arrayOfNulls(0)
        }
        return (params1 ?: params2) as Array<HttpParameter?>
    }

    internal fun mergeParameters(params1: Array<out HttpParameter?>?, params2: HttpParameter?): Array<HttpParameter?> {
        if (params1 != null && params2 != null) {
            val params = arrayOfNulls<HttpParameter>(params1.size + 1)
            params1.copyInto(params, 0, 0, params1.size)
            params[params.size - 1] = params2
            return params
        }
        if (null == params1 && null == params2) {
            return arrayOfNulls(0)
        }
        return if (params1 != null) {
            @Suppress("UNCHECKED_CAST")
            params1 as Array<HttpParameter?>
        } else {
            arrayOf(params2)
        }
    }

    private fun mergeImplicitParams(params: Array<out HttpParameter?>): Array<HttpParameter?> {
        return mergeParameters(params, IMPLICIT_PARAMS)
    }

    override fun toString(): String {
        return "TwitterImpl{" +
                "INCLUDE_MY_RETWEET=" + INCLUDE_MY_RETWEET +
                '}'
    }

    companion object {
        private const val serialVersionUID = 9170943084096085770L

        private val implicitParamsMap = newConcurrentMap<Configuration, Array<HttpParameter>>()
        private val implicitParamsStrMap = newConcurrentMap<Configuration, String>()
    }
}
