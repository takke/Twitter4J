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
 * KMP移行 Phase 4-4: `kotlin.jvm.Synchronized` は commonMain から直接使うと非JVMターゲットで
 * 「Synchronizing methods ... is not supported on platforms other than JVM」エラーになる。
 *
 * Kotlin 公式の推奨に従い、独自の optional-expectation アノテーションを用意し、
 * - JVM(Android): `actual typealias JvmSynchronized = kotlin.jvm.Synchronized`（従来どおりメソッド同期）
 * - iOS(Native): actual を持たない（[OptionalExpectation] により no-op 扱い）
 * とすることで、JVM 側の同期セマンティクスを維持したまま common 化できる。
 */
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.BINARY)
expect annotation class JvmSynchronized()
