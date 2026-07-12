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

import kotlin.time.Instant

/**
 * A data interface representing sent/received direct message.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
interface DirectMessage : TwitterResponse, EntitySupport, Serializable {

    val id: Long

    val text: String

    val senderId: Long

    val recipientId: Long

    /**
     * @return created_at
     * @since Twitter4J 1.1.0
     */
    val createdAt: Instant

    /**
     *
     * @return quick reply options
     * @since Twitter4J 4.0.7
     */
    val quickReplies: Array<QuickReply>

    /**
     *
     * @return quick reply response metadata
     * @since Twitter4J 4.0.7
     */
    val quickReplyResponse: String

    // currently type is always "message_create". So we're not providing a getter for that.
}
