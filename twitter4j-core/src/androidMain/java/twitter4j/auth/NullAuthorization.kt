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

import twitter4j.Serializable

import java.io.ObjectStreamException
import twitter4j.HttpRequest

/**
 * An interface represents credentials.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class NullAuthorization private constructor() : Authorization, Serializable {

    override fun getAuthorizationHeader(req: HttpRequest): String? {
        return null
    }

    override fun isEnabled(): Boolean {
        return false
    }

    override fun getAdditionalHeaders(): Map<String, String>? {
        return null
    }

    override fun equals(o: Any?): Boolean {
        return SINGLETON === o
    }

    override fun toString(): String {
        return "NullAuthentication{SINGLETON}"
    }

    @Throws(ObjectStreamException::class)
    private fun readResolve(): Any {
        return SINGLETON
    }

    companion object {
        private const val serialVersionUID = -7704668493278727510L
        private val SINGLETON = NullAuthorization()

        @JvmStatic
        fun getInstance(): NullAuthorization {
            return SINGLETON
        }
    }
}
