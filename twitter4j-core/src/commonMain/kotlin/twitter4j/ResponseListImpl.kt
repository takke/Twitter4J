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
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.3
 *
 * KMP移行 Phase 5-3:
 * 旧実装は `ArrayList<T>` を継承していたが、Kotlin/Native では `kotlin.collections.ArrayList` が final のため
 * 継承できない。そこで内部の `ArrayList`（[backing]）へ [MutableList] を委譲する構成に変更した。
 * JVM(Android) 側では `backing == other` により従来の `AbstractList` ベースの equals/hashCode（要素等価）を
 * 維持しており、リストとしての公開挙動は変わらない。
 */
internal open class ResponseListImpl<T> private constructor(
    private val backing: MutableList<T>
) : ResponseList<T>, MutableList<T> by backing {

    @Transient
    private var _rateLimitStatus: RateLimitStatus? = null

    @Transient
    private var _accessLevel: Int = 0

    override val rateLimitStatus: RateLimitStatus?
        get() = _rateLimitStatus

    override val accessLevel: Int
        get() = _accessLevel

    constructor(res: HttpResponse?) : this(ArrayList<T>()) {
        init(res)
    }

    constructor(size: Int, res: HttpResponse?) : this(ArrayList<T>(size)) {
        init(res)
    }

    constructor(rateLimitStatus: RateLimitStatus?, accessLevel: Int) : this(ArrayList<T>()) {
        this._rateLimitStatus = rateLimitStatus
        this._accessLevel = accessLevel
    }

    private fun init(res: HttpResponse?) {
        _rateLimitStatus = RateLimitStatusJSONImpl.createFromResponseHeader(res)
        _accessLevel = ParseUtil.toAccessLevel(res)
    }

    // 旧 ArrayList 継承時の要素等価ベースの equals/hashCode/toString を維持する。
    override fun equals(other: Any?): Boolean = backing == other

    override fun hashCode(): Int = backing.hashCode()

    override fun toString(): String = backing.toString()

    companion object {
        private const val serialVersionUID = 9105950888010803544L
    }
}
