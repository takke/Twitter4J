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
 * KMP互換の日時型（epoch millis 保持）。
 *
 * `java.util.Date` は Kotlin/Native には存在しないため、共通コードから参照できるよう
 * expect/actual で抽象化する。
 *
 * - JVM(Android): actual は `java.util.Date` への typealias（[TwitterDate.android.kt]）。
 *   よってバイトコード・公開APIシグネチャは従来通り `java.util.Date` のままで、
 *   `getCreatedAt()` 等が返す型も一切変わらない。
 * - iOS(Native): actual は epoch millis のみを保持する最小実装（[TwitterDate.ios.kt]）。
 *
 * メンバは現状コードとテストが使う範囲のみに絞っている:
 * - コンストラクタ `Date(time: Long)`（`Date(0)` / `Date(json.getLong(...))` 等で使用）
 * - `getTime()`（epoch millis。TimeSpanConverter が `date.time` として参照。
 *   ※ expect は `val time` ではなく `fun getTime()` として宣言する。`java.util.Date.getTime()` は
 *   Java のメソッドであり、typealias actual との expect/actual マッチングでは Kotlin プロパティ
 *   `val time` とは対応付かない（"has no corresponding members" エラーになる）ため。）
 * - `equals` / `hashCode`（各 *JSONImpl の equals()/hashCode() で使用）
 * - `compareTo`（TrendsJSONImpl が `trendAt.compareTo(...)` で使用。`java.util.Date` は
 *   `Comparable<Date>` を実装しているため typealias でも成立する）
 *
 * ※ 将来的には公開APIも [kotlin.time.Instant] へ統一予定（本バッチでは暫定的に Date を維持）。
 */
expect class Date(time: Long) : Comparable<Date> {

    /** epoch からの経過ミリ秒（`java.util.Date.getTime()` 相当）。 */
    fun getTime(): Long

    override fun compareTo(other: Date): Int

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}
