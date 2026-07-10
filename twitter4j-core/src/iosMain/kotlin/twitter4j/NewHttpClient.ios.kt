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
 * [newHttpClient] の iOS(Native) actual。
 *
 * NSURLSession ベースの同期 [HttpClient] 実装（[NsUrlSessionHttpClient]）を返す（KMP移行 Phase 4-3b）。
 */
actual fun newHttpClient(conf: HttpClientConfiguration): HttpClient =
    NsUrlSessionHttpClient(conf)
