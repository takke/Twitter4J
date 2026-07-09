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

package twitter4j.auth

import twitter4j.BASE64Encoder
import twitter4j.HttpRequest

/**
 * An authentication implementation implements Basic authentication
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class BasicAuthorization(
    val userId: String?,
    val password: String?
) : Authorization, java.io.Serializable {

    private val basic: String? = encodeBasicAuthenticationString()

    private fun encodeBasicAuthenticationString(): String? {
        if (userId != null && password != null) {
            return "Basic " + BASE64Encoder.encode(("$userId:$password").toByteArray())
        }
        return null
    }

    override fun getAuthorizationHeader(req: HttpRequest): String? {
        return basic
    }

    /**
     * #{inheritDoc}
     */
    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAdditionalHeaders(): Map<String, String>? {
        return null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is BasicAuthorization) return false

        val that = o

        return basic == that.basic
    }

    override fun hashCode(): Int {
        return basic?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "BasicAuthorization{" +
                "userId='" + userId + '\'' +
                ", password='**********'\'" +
                '}'
    }

    companion object {
        private const val serialVersionUID = 7420629998989177351L
    }
}
