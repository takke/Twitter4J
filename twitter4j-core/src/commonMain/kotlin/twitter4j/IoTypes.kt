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
 * KMP移行 Phase 4-4: メディアアップロード系 API のシグネチャに現れる `java.io.File` / `java.io.InputStream`
 * を common へ持ち上げるための不透明（opaque）型。
 *
 * - JVM(Android): `actual typealias File = java.io.File` / `actual typealias InputStream = java.io.InputStream`
 *   とし、公開JVMシグネチャ（`java/io/File` / `java/io/InputStream`）およびバイナリ互換を完全に維持する。
 *   TwitPane(JVM) は従来どおり `java.io.File` を渡せる。
 * - iOS(Native): 空の `actual class`。iOS 版 TwitPane（app_research 系）は閲覧専用でありメディア
 *   アップロードを行わないため、これらの型は「シグネチャ上の存在」だけでよく、実アップロード経路は
 *   [MediaUploadSupport] の iOS actual で `NotImplementedError` を送出する。
 *
 * common 側でこれらの型のメンバへアクセスする必要がある処理（ファイルの存在チェック・length 取得・
 * ストリーム読み出し等）は、プラットフォーム差を [MediaUploadSupport] の expect/actual に隔離している。
 */
expect class File

// java.io.InputStream は abstract class のため、typealias 互換のため expect も abstract とする。
expect abstract class InputStream
