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

import twitter4j.auth.AccessToken
import twitter4j.auth.Authorization
import twitter4j.auth.AuthorizationFactory
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationContext
import java.lang.reflect.Constructor

/**
 * A factory class for Twitter.
 * <br>An instance of this class is completely thread safe and can be re-used and used concurrently.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.0
 */
class TwitterFactory
/**
 * Creates a TwitterFactory with the given configuration.
 *
 * @param conf the configuration to use
 * @since Twitter4J 2.1.1
 */
(private val conf: Configuration) : Serializable {

    /**
     * Creates a TwitterFactory with the root configuration.
     */
    constructor() : this(ConfigurationContext.getInstance())

    /**
     * Creates a TwitterFactory with a specified config tree
     *
     * @param configTreePath the path
     */
    constructor(configTreePath: String) : this(ConfigurationContext.getInstance(configTreePath))

    /**
     * Returns a instance associated with the configuration bound to this factory.
     *
     * @return default singleton instance
     */
    val instance: Twitter
        get() = getInstance(AuthorizationFactory.getInstance(conf))

    /**
     * Returns a OAuth Authenticated instance.<br>
     * consumer key and consumer Secret must be provided by twitter4j.properties, or system properties.<br>
     * Unlike [Twitter.setOAuthAccessToken], this factory method potentially returns a cached instance.
     *
     * @param accessToken access token
     * @return an instance
     * @since Twitter4J 2.1.9
     */
    fun getInstance(accessToken: AccessToken): Twitter {
        val consumerKey = conf.oAuthConsumerKey
        val consumerSecret = conf.oAuthConsumerSecret
        if (null == consumerKey && null == consumerSecret) {
            throw IllegalStateException("Consumer key and Consumer secret not supplied.")
        }
        val oauth = OAuthAuthorization(conf)
        oauth.setOAuthAccessToken(accessToken)
        return getInstance(oauth)
    }

    fun getInstance(auth: Authorization): Twitter {
        try {
            return TWITTER_CONSTRUCTOR.newInstance(conf, auth)
        } catch (e: InstantiationException) {
            throw AssertionError(e)
        } catch (e: IllegalAccessException) {
            throw AssertionError(e)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw AssertionError(e)
        }
    }

    companion object {
        private const val serialVersionUID = -563983536986910054L

        private val TWITTER_CONSTRUCTOR: Constructor<Twitter>

        /*AsyncTwitterFactory and TWitterStream will access this field*/
        @JvmField
        internal val DEFAULT_AUTHORIZATION: Authorization = AuthorizationFactory.getInstance(ConfigurationContext.getInstance())

        private val SINGLETON: Twitter

        init {
            // detecting Google App Engine
            var gaeDetected: Boolean
            try {
                Class.forName("com.google.appengine.api.urlfetch.URLFetchService")
                gaeDetected = true
            } catch (cnfe: ClassNotFoundException) {
                gaeDetected = false
            }

            var className: String? = null
            if (gaeDetected) {
                val APP_ENGINE_TWITTER_IMPL = "twitter4j.AppEngineTwitterImpl"
                try {
                    Class.forName(APP_ENGINE_TWITTER_IMPL)
                    className = APP_ENGINE_TWITTER_IMPL
                } catch (ignore: ClassNotFoundException) {
                }
            }
            val resolvedClassName = className ?: "twitter4j.TwitterImpl"
            val constructor: Constructor<Twitter>
            val clazz: Class<*>
            try {
                clazz = Class.forName(resolvedClassName)
                @Suppress("UNCHECKED_CAST")
                constructor = clazz.getDeclaredConstructor(
                    Configuration::class.java, Authorization::class.java
                ) as Constructor<Twitter>
            } catch (e: NoSuchMethodException) {
                throw AssertionError(e)
            } catch (e: ClassNotFoundException) {
                throw AssertionError(e)
            }
            TWITTER_CONSTRUCTOR = constructor

            try {
                SINGLETON = TWITTER_CONSTRUCTOR.newInstance(ConfigurationContext.getInstance(), DEFAULT_AUTHORIZATION)
            } catch (e: InstantiationException) {
                throw AssertionError(e)
            } catch (e: IllegalAccessException) {
                throw AssertionError(e)
            } catch (e: java.lang.reflect.InvocationTargetException) {
                throw AssertionError(e)
            }
        }

        /**
         * Returns default singleton Twitter instance.
         *
         * @return default singleton Twitter instance
         * @since Twitter4J 2.2.4
         */
        @JvmStatic
        fun getSingleton(): Twitter {
            return SINGLETON
        }
    }
}
