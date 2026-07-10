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
 * [TwitterObjectFactory] が生JSON保存に用いる「スレッドローカルなマップ」の KMP 互換ホルダ。
 *
 * `java.lang.ThreadLocal` は Kotlin/Native には存在しないため expect/actual で抽象化する。
 *
 * - JVM(Android): actual は `ThreadLocal<MutableMap<Any?, Any?>>` に委譲し、旧実装と完全に同じ
 *   「スレッドごとに独立したマップ」挙動を維持する（[RawJsonThreadLocalMap.android.kt]）。
 * - iOS(Native): actual は単一の共有マップで代替する（[RawJsonThreadLocalMap.ios.kt]）。
 *   TwitPane iOS の利用形態（Dispatchers.Default 中心、生JSON保存は主にデバッグ/一時参照用途）では
 *   厳密なスレッド分離は不要なため、これで十分。
 */
internal expect class RawJsonThreadLocalMap() {
    /** 現在のスレッド（iOS では共有）に対応する生JSON保存用マップを返す。 */
    fun get(): MutableMap<Any?, Any?>
}
