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

import twitter4j.conf.ChunkedUploadConfiguration

/**
 * KMP移行 Phase 4-4: メディアアップロード（画像・動画のバイナリ送信）に伴う JVM 固有処理を
 * commonMain の呼び出し側（[StatusUpdate] / [V1ResourcesImpl]）から隔離するための expect オブジェクト。
 *
 * common 化を妨げていたのは以下の JVM(java.io) 依存であり、これらをここへ集約した:
 * - `java.io.File` / `java.io.InputStream` を持つ [HttpParameter] コンストラクタ（マルチパート添付）
 * - ファイルの実在チェック（`File.exists()` / `File.isFile`）
 * - チャンクアップロード（[ChunkedUploadDelegate]、ストリームの逐次読み出し）
 *
 * - JVM(Android): 従来どおり実処理を行う（[HttpParameter] の File/InputStream コンストラクタ、
 *   [ChunkedUploadDelegate] を使用）。
 * - iOS(Native): TwitPane の iOS 版（app_research 系）は閲覧専用でメディア投稿を行わないため、
 *   いずれも [NotImplementedError] を送出する。API 実行層（読み取り系）の iOS 動作には影響しない。
 */
internal expect object MediaUploadSupport {

    /** `java.io.File` ベースのマルチパート添付パラメータを生成する。 */
    fun mediaHttpParameter(name: String, mediaFile: File): HttpParameter

    /** `java.io.InputStream` ベースのマルチパート添付パラメータを生成する。 */
    fun mediaHttpParameter(name: String, fileName: String, media: InputStream): HttpParameter

    /** アップロード対象ファイルの実在・種別を検証する（不正なら [TwitterException] を送出）。 */
    fun checkFileValidity(file: File)

    /** チャンク（分割）アップロードを実行する。 */
    fun uploadMediaChunked(twitter: TwitterImpl, chunkedUploadConfiguration: ChunkedUploadConfiguration): UploadedMedia
}
