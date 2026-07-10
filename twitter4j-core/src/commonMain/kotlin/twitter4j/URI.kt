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
 * KMP互換の URI 型。
 *
 * `java.net.URI` は Kotlin/Native には存在しないため、[UserList.URI] を commonMain から参照できるよう
 * expect/actual で抽象化する（[Date] / [Serializable] と同じ戦略）。
 *
 * - JVM(Android): actual は `java.net.URI` への typealias（[URI.android.kt]）。よって [UserList.URI] が返す型は
 *   従来どおり `java.net.URI` のままで、公開APIシグネチャや `getURI()` の JVM シグネチャは一切変わらない。
 *   （TwitPane 側の `UserListGraphqlImpl.getURI(): java.net.URI` オーバーライドもそのまま成立する。）
 * - iOS(Native): actual は URI 文字列のみを保持する最小実装（[URI.ios.kt]）。
 *
 * メンバは現状 commonMain のコードが必要とする範囲に絞る。commonMain 内では [createURI] による生成のみ行い、
 * URI に対するメソッド呼び出しは行わない（JVM 側の消費コードは `java.net.URI` を直接扱う）。
 * `java.net.URI` の `URI(String)` コンストラクタと対応させるため、単一文字列引数のコンストラクタのみ宣言する。
 */
expect class URI(str: String)

/**
 * URI 文字列から [URI] を生成する。
 *
 * 旧 `UserListJSONImpl.URI` ゲッターにあった「不正URI時は空URIにフォールバック」する挙動を
 * プラットフォーム側 actual に隔離するためのファクトリ。
 *
 * - JVM(Android): `java.net.URI(str)` を用い、`URISyntaxException` 時は空URI（`URI("")`）を返す（旧挙動を完全維持）。
 *   ※ [str] が null の場合の挙動も旧実装と同じ（`java.net.URI((String)null)` が `NullPointerException` を送出）。
 * - iOS(Native): 文字列をそのまま保持する（null は空文字扱い）。
 */
expect fun createURI(str: String?): URI
