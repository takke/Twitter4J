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
 * Super interface of Twitter Response data interfaces which indicates that rate limit status is available.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see twitter4j.DirectMessage
 *
 * @see twitter4j.Status
 *
 * @see twitter4j.User
 */
interface TwitterResponse : Serializable {
    /**
     * Returns the current rate limit status if available.
     *
     * @return current rate limit status
     * @since Twitter4J 2.1.0
     */
    val rateLimitStatus: RateLimitStatus?

    /**
     * @return application permission model
     * @see [Application Permission Model FAQ - How do we know what the access level of a user token is?](https://dev.twitter.com/pages/application-permission-model-faq.how-do-we-know-what-the-access-level-of-a-user-token-is)
     *
     * @since Twitter4J 2.2.3
     */
    val accessLevel: Int

    companion object {
        const val NONE = 0
        const val READ = 1
        const val READ_WRITE = 2
        const val READ_WRITE_DIRECTMESSAGES = 3
    }
}
