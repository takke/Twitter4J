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
 * @since Twitter4J 2.1.3
 */
internal open class ResponseListImpl<T> : ArrayList<T>, ResponseList<T> {

    @Transient
    private var _rateLimitStatus: RateLimitStatus? = null

    @Transient
    private var _accessLevel: Int = 0

    override val rateLimitStatus: RateLimitStatus?
        get() = _rateLimitStatus

    override val accessLevel: Int
        get() = _accessLevel

    constructor(res: HttpResponse?) : super() {
        init(res)
    }

    constructor(size: Int, res: HttpResponse?) : super(size) {
        init(res)
    }

    constructor(rateLimitStatus: RateLimitStatus?, accessLevel: Int) : super() {
        this._rateLimitStatus = rateLimitStatus
        this._accessLevel = accessLevel
    }

    private fun init(res: HttpResponse?) {
        _rateLimitStatus = RateLimitStatusJSONImpl.createFromResponseHeader(res)
        _accessLevel = ParseUtil.toAccessLevel(res)
    }

    companion object {
        private const val serialVersionUID = 9105950888010803544L
    }
}
