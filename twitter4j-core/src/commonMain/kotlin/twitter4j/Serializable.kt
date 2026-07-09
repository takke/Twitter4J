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
 * KMP互換の Serializable 抽象。
 *
 * `java.io.Serializable` は Kotlin/Native には存在しないため、共通コードから参照できるよう
 * expect/actual で抽象化する。
 *
 * - JVM(Android): actual は `java.io.Serializable` への typealias（[Serializable.android.kt]）。
 *   よってバイトコード上は従来通り `java.io.Serializable` を implements し、Javaシリアライズや
 *   JVM側の挙動は一切変わらない。
 * - iOS(Native): actual は空の interface（[Serializable.ios.kt]）。マーカーとしての意味のみ。
 *
 * メンバは持たない純粋なマーカーインターフェイスである。
 */
expect interface Serializable
