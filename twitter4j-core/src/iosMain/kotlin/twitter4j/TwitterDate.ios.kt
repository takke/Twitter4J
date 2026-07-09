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
 * iOS(Native) には `java.util.Date` が存在しないため、epoch millis のみを保持する最小実装で actual 化する。
 *
 * `java.util.Date` の完全な機能は持たず、共通コードが参照する範囲（[TwitterDate.kt] の expect 宣言）
 * だけを実装する。文字列化は ISO-8601（[Instant] 形式）で行う。
 */
actual class Date actual constructor(time: Long) : Comparable<Date> {

    private val timeMillis: Long = time

    actual fun getTime(): Long = timeMillis

    actual override fun compareTo(other: Date): Int = timeMillis.compareTo(other.getTime())

    actual override fun equals(other: Any?): Boolean = other is Date && other.getTime() == timeMillis

    actual override fun hashCode(): Int = timeMillis.hashCode()

    actual override fun toString(): String = Instant.fromEpochMilliseconds(timeMillis).toString()
}
