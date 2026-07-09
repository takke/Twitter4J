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
abstract class EntityIndex : Comparable<EntityIndex>, Serializable {
    // TwitPane(MyHTMLEntity 等)から `entity.start = x` の形で外部モジュールから更新するため public な var とする。
    // Java 側(旧 setStart/setEnd)や Kotlin プロパティアクセスの双方から利用できるように getter/setter を公開する。
    open var start: Int = -1
    open var end: Int = -1

    override fun compareTo(other: EntityIndex): Int {
        val delta = (this.start - other.start).toLong()
        if (delta < Int.MIN_VALUE) {
            return Int.MIN_VALUE
        } else if (delta > Int.MAX_VALUE) {
            return Int.MAX_VALUE
        }
        return delta.toInt()
    }

    companion object {
        private const val serialVersionUID = 3757474748266170719L
    }
}
