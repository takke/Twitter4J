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

import twitter4j.Serializable

import java.util.Properties

import twitter4j.HttpClientConfiguration
import twitter4j.auth.AuthorizationConfiguration

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
interface Configuration : AuthorizationConfiguration, Serializable {

    fun isDebugEnabled(): Boolean

    fun isApplicationOnlyAuthEnabled(): Boolean

    // methods for HttpClientConfiguration
    fun getHttpClientConfiguration(): HttpClientConfiguration

    fun getHttpStreamingReadTimeout(): Int

    // oauth related setter/getters

    fun getOAuth2Scope(): String?

    fun getRestBaseURL(): String

    fun getUploadBaseURL(): String

    fun getStreamBaseURL(): String

    fun getOAuthRequestTokenURL(): String

    fun getOAuthAuthorizationURL(): String

    fun getOAuthAccessTokenURL(): String

    fun getOAuthAuthenticationURL(): String

    fun getOAuth2TokenURL(): String

    fun getOAuth2InvalidateTokenURL(): String

    fun getUserStreamBaseURL(): String

    fun getSiteStreamBaseURL(): String

    fun isIncludeMyRetweetEnabled(): Boolean

    val isJSONStoreEnabled: Boolean

    fun isMBeanEnabled(): Boolean

    fun isUserStreamRepliesAllEnabled(): Boolean

    fun isUserStreamWithFollowingsEnabled(): Boolean

    fun isStallWarningsEnabled(): Boolean

    fun getMediaProvider(): String

    fun getMediaProviderAPIKey(): String?

    fun getMediaProviderParameters(): Properties?

    fun getAsyncNumThreads(): Int

    fun getContributingTo(): Long

    fun isIncludeEntitiesEnabled(): Boolean

    fun isTrimUserEnabled(): Boolean

    fun isIncludeExtAltTextEnabled(): Boolean

    fun isTweetModeExtended(): Boolean

    fun isIncludeExtEditControl(): Boolean

    fun isDaemonEnabled(): Boolean

    fun isIncludeEmailEnabled(): Boolean

    fun getStreamThreadName(): String
}
