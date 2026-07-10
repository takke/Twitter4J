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
 * UserList の `createdAtAsInstant` 拡張（androidMain 残置分）。
 *
 * Phase 5-1 で大半のモデルIFは commonMain へ移動し、対応する `xxxAsInstant` 拡張も
 * [DateExtensions.kt]（commonMain）へ集約した。しかし [UserList] は `java.net.URI`（JVM専用型）を
 * 公開シグネチャに持つため commonMain へ移動できず、本拡張のみ androidMain に残している。
 *
 * ⚠️ 実装で `Date.toInstant()`（[DateExtensions.kt]）を使わないのは、JVM 上では `java.util.Date` の
 *    メンバ `toInstant()`（`java.time.Instant` を返す）が優先され、[kotlin.time.Instant] にならないため。
 *    ここでは `Instant.fromEpochMilliseconds(date.time)` で明示的に [kotlin.time.Instant] を組み立てる。
 */

/** [UserList.createdAt] を [Instant] として取得する。 */
val UserList.createdAtAsInstant: Instant
    get() = Instant.fromEpochMilliseconds(createdAt.time)
