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

package twitter4j.conf

/**
 * 各 *JSONImpl が生JSON保存機能（[isJSONStoreEnabled]）の有無だけを参照するための、
 * commonMain から可視な最小構成インターフェイス。
 *
 * KMP移行 Phase 5-3 の背景:
 * - JSONパース層（*JSONImpl）は commonMain へ移動して iOS でも動作させたいが、本来 `res: HttpResponse` を
 *   受け取る二次コンストラクタ / `createXxxList(res, conf)` 系ファクトリは `conf.isJSONStoreEnabled` を参照
 *   するために `twitter4j.conf.Configuration` を要求していた。
 * - しかし [Configuration] は `AuthorizationConfiguration` / `HttpClientConfiguration` / `java.util.Properties`
 *   などに依存する巨大インターフェイスで、これらは HTTP/認証層（Phase 4）に属し commonMain へは移動できない。
 * - そこで *JSONImpl が実際に必要とする「生JSON保存が有効か」だけを切り出した本インターフェイスを commonMain に
 *   置き、[Configuration] にこれを継承させる。*JSONImpl 側の引数型を [JsonStoreConfiguration] に緩めることで、
 *   呼び出し側（[Configuration] を渡す）は部分型として従来どおり通り、公開シグネチャ上の実害なくパース層を
 *   commonMain へ移せる。
 *
 * ※ [Configuration] 以外がこのインターフェイスを実装することは想定していない。
 */
interface JsonStoreConfiguration {
    /** 取得したオブジェクトに対応する生JSON文字列を [twitter4j.TwitterObjectFactory] に保存するかどうか。 */
    val isJSONStoreEnabled: Boolean
}
