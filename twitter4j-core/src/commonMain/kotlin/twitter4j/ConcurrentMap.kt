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
 * KMP移行 Phase 4-4: [TwitterImpl] の implicit-params キャッシュに使われていた
 * `java.util.concurrent.ConcurrentHashMap` を common 化するためのファクトリ。
 *
 * - JVM(Android): `ConcurrentHashMap` を返し、従来どおりマップ構造自体のスレッドセーフ性を維持する。
 * - iOS(Native): 通常の [LinkedHashMap] を返す。当該キャッシュは同一 Configuration に対して
 *   決定的な値を格納する冪等キャッシュであり、競合しても再計算・上書きされるだけで害はない。
 */
internal expect fun <K : Any, V : Any> newConcurrentMap(): MutableMap<K, V>
