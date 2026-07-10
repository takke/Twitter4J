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

import kotlin.jvm.Transient
/**
 * Super interface of Twitter Response data interfaces which indicates that rate limit status is available.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see twitter4j.DirectMessage
 * @see twitter4j.Status
 * @see twitter4j.User
 */
abstract class TwitterResponseImpl : TwitterResponse, Serializable {

    @Transient
    final override val rateLimitStatus: RateLimitStatus?

    @Transient
    final override val accessLevel: Int

    constructor() {
        rateLimitStatus = null
        accessLevel = TwitterResponse.NONE
    }

    constructor(res: HttpResponse?) {
        rateLimitStatus = RateLimitStatusJSONImpl.createFromResponseHeader(res)
        accessLevel = ParseUtil.toAccessLevel(res)
    }

    companion object {
        private const val serialVersionUID = 7422171124869859808L
    }
}
