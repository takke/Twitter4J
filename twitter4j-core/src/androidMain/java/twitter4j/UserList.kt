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

import java.util.Date

/**
 * A data interface representing Basic list information element
 *
 * @author Dan Checkoway - dcheckoway at gmail.com
 */
interface UserList : Comparable<UserList?>, TwitterResponse, Serializable {
    /**
     * Returns the id of the list
     *
     * @return the id of the list
     */
    val id: Long

    /**
     * Returns the name of the list
     *
     * @return the name of the list
     */
    val name: String

    /**
     * Returns the full name of the list
     *
     * @return the full name of the list
     */
    val fullName: String

    /**
     * Returns the slug of the list
     *
     * @return the slug of the list
     */
    val slug: String

    /**
     * Returns the description of the list
     *
     * @return the description of the list
     */
    val description: String

    /**
     * Returns the subscriber count of the list
     *
     * @return the subscriber count of the list
     */
    val subscriberCount: Int

    /**
     * Returns the member count of the list
     *
     * @return the member count of the list
     */
    val memberCount: Int

    /**
     * Returns the uri of the list
     *
     * @return the uri of the list
     */
    @Suppress("PropertyName")
    val URI: java.net.URI

    /**
     * tests if the list is public
     *
     * @return if the list is public
     */
    val isPublic: Boolean

    /**
     * Returns the user of the list
     *
     * @return the user of the list
     */
    val user: User

    /**
     * Returns if the authenticated user is following the list
     *
     * @return if the authenticated user is following the list
     */
    val isFollowing: Boolean

    /**
     * @return the date created the list
     * @since Twitter4J 4.0.1
     */
    val createdAt: Date
}
