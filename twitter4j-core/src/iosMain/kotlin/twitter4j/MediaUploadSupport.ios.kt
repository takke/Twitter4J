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
 * [MediaUploadSupport] の iOS(Native) 実装。
 *
 * iOS 版 TwitPane（app_research 系）は閲覧専用でありメディア投稿を行わないため、
 * いずれの操作も未サポートとして [NotImplementedError] を送出する。
 * 読み取り系 API（タイムライン取得等）はこれらを経由しないため影響しない。
 */
private const val NOT_SUPPORTED = "media upload is not supported on iOS (read-only client)"

internal actual object MediaUploadSupport {

    actual fun mediaHttpParameter(name: String, mediaFile: File): HttpParameter =
        throw NotImplementedError(NOT_SUPPORTED)

    actual fun mediaHttpParameter(name: String, fileName: String, media: InputStream): HttpParameter =
        throw NotImplementedError(NOT_SUPPORTED)

    actual fun checkFileValidity(file: File): Unit =
        throw NotImplementedError(NOT_SUPPORTED)

    actual fun uploadMediaChunked(twitter: TwitterImpl, chunkedUploadConfiguration: ChunkedUploadConfiguration): UploadedMedia =
        throw NotImplementedError(NOT_SUPPORTED)
}
