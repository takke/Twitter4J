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

/*
 * Date→Instant完全統一により、公開APIは [kotlin.time.Instant] を直接返すようになった。
 * 以下の `xxxAsInstant` 拡張は移行期の消費側コード（TwitPane等）との互換のために残している
 * エイリアスであり、新規コードでは各プロパティを直接参照してよい。
 */

/*
 * ---- 各モデルの `xxxAsInstant` 拡張群 ----
 *
 * Phase 5-1 でモデルIF（Status / User / DirectMessage / SavedSearch / Trends 等）が commonMain へ
 * 移動したため、旧 androidMain の ModelDateExtensions.kt から本ファイルへ集約した。
 *
 * commonMain の [Date] は expect クラスであり `toInstant()` メンバを持たない（JVMメンバ衝突が無い）ため、
 * ここでは上記 [Date.toInstant] 拡張を直接使用できる。
 *
 * ※ Phase 5-3 で UserList の `java.net.URI` 依存を expect/actual の [URI] へ移設し UserList / UserListJSONImpl も
 *   commonMain へ移動したため、旧 androidMain の ModelDateExtensions.kt にあった [UserList.createdAtAsInstant] も
 *   本ファイルへ統合した。
 */

/** [Status.createdAt] を [Instant] として取得する。 */
val Status.createdAtAsInstant: Instant
    get() = createdAt

/** [User.createdAt] を [Instant] として取得する（createdAt が null の場合は null）。 */
val User.createdAtAsInstant: Instant?
    get() = createdAt

/** [DirectMessage.createdAt] を [Instant] として取得する。 */
val DirectMessage.createdAtAsInstant: Instant
    get() = createdAt

/** [SavedSearch.createdAt] を [Instant] として取得する。 */
val SavedSearch.createdAtAsInstant: Instant
    get() = createdAt

/** [Trends.asOf] を [Instant] として取得する。 */
val Trends.asOfAsInstant: Instant
    get() = asOf

/** [Trends.trendAt] を [Instant] として取得する。 */
val Trends.trendAtAsInstant: Instant
    get() = trendAt

/** [UserList.createdAt] を [Instant] として取得する。 */
val UserList.createdAtAsInstant: Instant
    get() = createdAt
