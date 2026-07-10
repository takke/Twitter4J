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

import twitter4j.auth.Authorization
import twitter4j.auth.BasicAuthorization

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.3
 */
class XAuthAuthorization(private val basic: BasicAuthorization) : Authorization, Serializable {

    var consumerKey: String? = null
        private set
    var consumerSecret: String? = null
        private set

    override fun getAuthorizationHeader(req: HttpRequest): String? {
        return basic.getAuthorizationHeader(req)
    }

    fun getUserId(): String? {
        return basic.userId
    }

    fun getPassword(): String? {
        return basic.password
    }

    @JvmSynchronized
    fun setOAuthConsumer(consumerKey: String?, consumerSecret: String?) {
        this.consumerKey = consumerKey
        this.consumerSecret = consumerSecret
    }

    override fun isEnabled(): Boolean {
        return basic.isEnabled()
    }

    override fun getAdditionalHeaders(): Map<String, String>? {
        return null
    }

    companion object {
        private const val serialVersionUID = -7260372598870697494L
    }
}
