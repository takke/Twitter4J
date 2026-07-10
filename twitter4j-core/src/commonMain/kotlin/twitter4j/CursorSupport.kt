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
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
interface CursorSupport {
    /**
     * @since Twitter4J 2.0.10
     * @return has previous page
     */
    fun hasPrevious(): Boolean

    /**
     * @since Twitter4J 2.0.10
     * @return previous cursor
     */
    val previousCursor: Long

    /**
     * @since Twitter4J 2.0.10
     * @return has next
     */
    fun hasNext(): Boolean

    /**
     * @since Twitter4J 2.0.10
     * @return next cursor
     */
    val nextCursor: Long

    companion object {
        /**
         * @since Twitter4J 2.2.3
         */
        const val START = -1L
    }
}
