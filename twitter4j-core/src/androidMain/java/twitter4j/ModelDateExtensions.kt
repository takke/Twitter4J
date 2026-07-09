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

@file:OptIn(ExperimentalTime::class)

package twitter4j

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Date を返す各モデルの公開プロパティに対応する `xxxAsInstant` 拡張群。
 *
 * KMP移行の過渡期において、消費側（TwitPane 本体等）が [Date] から [kotlin.time.Instant] へ
 * 段階的に移行できるよう提供する。null 許容性はモデル側の宣言に合わせている。
 *
 * ⚠️ 現時点では対象モデル（Status / User / ...）が androidMain に存在するため本拡張も androidMain に置く。
 *    Phase 3-5 以降でモデルが commonMain へ移動した際は、本ファイルも commonMain の
 *    [DateExtensions.kt] へ集約すること。
 *
 * ⚠️ 実装で `Date.toInstant()`（[DateExtensions.kt]）を使わないのは、JVM 上では `java.util.Date` の
 *    メンバ `toInstant()`（`java.time.Instant` を返す）が優先され、[kotlin.time.Instant] にならないため。
 *    ここでは `Instant.fromEpochMilliseconds(date.time)` で明示的に [kotlin.time.Instant] を組み立てる。
 */

/** [Status.createdAt] を [Instant] として取得する。 */
val Status.createdAtAsInstant: Instant
    get() = Instant.fromEpochMilliseconds(createdAt.time)

/** [User.createdAt] を [Instant] として取得する（createdAt が null の場合は null）。 */
val User.createdAtAsInstant: Instant?
    get() = createdAt?.let { Instant.fromEpochMilliseconds(it.time) }

/** [UserList.createdAt] を [Instant] として取得する。 */
val UserList.createdAtAsInstant: Instant
    get() = Instant.fromEpochMilliseconds(createdAt.time)

/** [DirectMessage.createdAt] を [Instant] として取得する。 */
val DirectMessage.createdAtAsInstant: Instant
    get() = Instant.fromEpochMilliseconds(createdAt.time)

/** [SavedSearch.createdAt] を [Instant] として取得する。 */
val SavedSearch.createdAtAsInstant: Instant
    get() = Instant.fromEpochMilliseconds(createdAt.time)

/** [Trends.asOf] を [Instant] として取得する。 */
val Trends.asOfAsInstant: Instant
    get() = Instant.fromEpochMilliseconds(asOf.time)

/** [Trends.trendAt] を [Instant] として取得する。 */
val Trends.trendAtAsInstant: Instant
    get() = Instant.fromEpochMilliseconds(trendAt.time)
