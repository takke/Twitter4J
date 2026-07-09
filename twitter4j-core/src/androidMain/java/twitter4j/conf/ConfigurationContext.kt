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

package twitter4j.conf

/**
 * Static factory of Configuration. This class wraps ConfigurationFactory implementations.<br></br>
 * By default, twitter4j.conf.PropertyConfigurationFactory will be used and can be changed with -Dtwitter4j.configurationFactory system property.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
object ConfigurationContext {
    private const val DEFAULT_CONFIGURATION_FACTORY = "twitter4j.conf.PropertyConfigurationFactory"
    private const val CONFIGURATION_IMPL = "twitter4j.configurationFactory"
    private val factory: ConfigurationFactory

    init {
        val configImpl: String = try {
            System.getProperty(CONFIGURATION_IMPL, DEFAULT_CONFIGURATION_FACTORY)
        } catch (ignore: SecurityException) {
            // Unsigned applets are not allowed to access System properties
            DEFAULT_CONFIGURATION_FACTORY
        }

        factory = try {
            Class.forName(configImpl).newInstance() as ConfigurationFactory
        } catch (cnfe: ClassNotFoundException) {
            throw AssertionError(cnfe)
        } catch (ie: InstantiationException) {
            throw AssertionError(ie)
        } catch (iae: IllegalAccessException) {
            throw AssertionError(iae)
        }
    }

    @JvmStatic
    fun getInstance(): Configuration {
        return factory.getInstance()
    }

    @JvmStatic
    fun getInstance(configTreePath: String): Configuration {
        return factory.getInstance(configTreePath)
    }
}
