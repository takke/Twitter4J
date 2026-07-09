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
 * @author Hiroaki TAKEUCHI - takke30 at gmail.com
 * @since Twitter4J 4.0.7
 */
internal class DirectMessageListImpl : ResponseListImpl<DirectMessage>, DirectMessageList {

    // 元実装では next_cursor 不在時に null を返していたが、DirectMessageList.nextCursor は非nullのため "" を返す。
    override val nextCursor: String

    constructor(rateLimitStatus: RateLimitStatus?, accessLevel: Int) : super(rateLimitStatus, accessLevel) {
        nextCursor = ""
    }

    constructor(size: Int, json: JSONObject, res: HttpResponse?) : super(size, res) {
        this.nextCursor = ParseUtil.getRawString("next_cursor", json) ?: ""
    }

    constructor(size: Int, res: HttpResponse?) : super(size, res) {
        this.nextCursor = ""
    }

    companion object {
        private const val serialVersionUID = 8150060768287194508L
    }
}
