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
 * 指定の [HttpClientConfiguration] に対応する [HttpClient] を生成する共通ファクトリ（KMP移行 Phase 4-3a）。
 *
 * JVM の `Class.forName` ベースの実装選択（`HttpClientFactory`）は Native では利用できないため、
 * プラットフォームごとの実装選択を expect/actual 関数に再設計した。
 *
 * - JVM(Android): actual は既存の [HttpClientFactory.getInstance] へ委譲し、従来の
 *   `AlternativeHttpClientImpl` / `HttpClientImpl` 選択ロジックとインスタンスキャッシュを温存する。
 * - iOS(Native): actual は Phase 4-3b で NSURLSession ベースの同期 [HttpClient] 実装を返す予定
 *   （本バッチではコンパイルを通すための TODO スタブ）。
 */
expect fun newHttpClient(conf: HttpClientConfiguration): HttpClient
