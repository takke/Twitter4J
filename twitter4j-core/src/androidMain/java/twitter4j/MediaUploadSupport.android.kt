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
import java.io.FileNotFoundException
import java.io.IOException

/**
 * [MediaUploadSupport] の JVM(Android) 実装。従来 V1ResourcesImpl / StatusUpdate に散在していた
 * java.io 依存のメディアアップロード処理をそのまま移設したもの。
 */
internal actual object MediaUploadSupport {

    actual fun mediaHttpParameter(name: String, mediaFile: File): HttpParameter =
        HttpParameter(name, mediaFile)

    actual fun mediaHttpParameter(name: String, fileName: String, media: InputStream): HttpParameter =
        HttpParameter(name, fileName, media)

    actual fun checkFileValidity(file: File) {
        if (!file.exists()) {
            // http://help.twitter.com/forums/10711/entries/62592
            throw TwitterException(FileNotFoundException("$file is not found."))
        }
        if (!file.isFile) {
            // as per twitter forum (https://dev.twitter.com/discussions/1350)
            throw TwitterException(IOException("$file is not a file."))
        }
    }

    actual fun uploadMediaChunked(twitter: TwitterImpl, chunkedUploadConfiguration: ChunkedUploadConfiguration): UploadedMedia =
        ChunkedUploadDelegate(twitter).uploadMediaChunked(chunkedUploadConfiguration)
}
