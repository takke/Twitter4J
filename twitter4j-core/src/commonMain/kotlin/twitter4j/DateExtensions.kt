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
 * [Date]（epoch millis 保持）を [kotlin.time.Instant] に変換する基盤拡張。
 *
 * KMP移行の過渡期において、公開APIは当面 [Date] を返し続けるが、消費側は本拡張および
 * 各モデルの `xxxAsInstant` 拡張を用いて順次 [Instant] ベースへ移行できる。
 * 将来的には公開APIそのものを [Instant] へ統一する予定。
 *
 * ⚠️ JVM(Android)では `java.util.Date` にも同名の `toInstant()`（`java.time.Instant` を返す）が
 *    メンバとして存在し、そちらが優先される。そのため本拡張は common/iOS 向けであり、
 *    JVM 側で [kotlin.time.Instant] が欲しい場合は各モデルの `xxxAsInstant` 拡張を用いること。
 */
fun Date.toInstant(): Instant = Instant.fromEpochMilliseconds(getTime())
