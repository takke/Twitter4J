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
 * KMP移行 Phase 4-4: `conf.Configuration` の `getMediaProviderParameters()` が返す
 * `java.util.Properties` を common へ持ち上げるための不透明（opaque）型。
 *
 * mediaProvider 系（twitpic 等の旧世代画像アップロードプロバイダ設定）は TwitPane からは
 * 一切参照されておらず、Configuration では受け渡し（保持・返却）にのみ使用される。
 * そのため common では中身を持たない不透明な expect class として宣言し、
 * - JVM(Android): `actual typealias Properties = java.util.Properties`（バイナリシグネチャ `java/util/Properties` を維持）
 * - iOS(Native): 空の `actual class`
 * とすることで、公開JVMシグネチャ互換を保ちつつ commonMain へ移動可能にする。
 */
expect class Properties
