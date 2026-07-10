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
 * `multipart/form-data` ボディの組み立てロジック（KMP移行 Phase 4-3b）。
 *
 * JVM の [HttpClientImpl] は `DataOutputStream` へ逐次書き込みしてマルチパートボディを生成しているが、
 * この組み立て自体はプラットフォーム非依存の純ロジックである。iOS(NSURLSession) 実装が
 * `NSData`（= [ByteArray]）としてボディ全体を必要とするため、ここに純関数として切り出し、
 * androidTest（JVM）でバイト単位の同値をユニットテストできるようにする。
 *
 * 生成されるバイト列は JVM 実装（[HttpClientImpl.handleRequest] のマルチパート分岐）と 1 バイトも
 * 変わらないことを意図している:
 * - 各パートは区切り `--<boundary>\r\n` で始まる
 * - ファイルパート: `Content-Disposition: form-data; name="..."; filename="..."\r\n` +
 *   `Content-Type: <contentType>\r\n\r\n` + `<本体バイト列>` + `\r\n`
 * - テキストパート: `Content-Disposition: form-data; name="..."\r\n` +
 *   `Content-Type: text/plain; charset=UTF-8\r\n\r\n` + `<値の UTF-8 バイト列>` + `\r\n`
 * - 末尾は `--<boundary>--\r\n` + `\r\n`
 */

/** JVM 実装が用いるマルチパート境界文字列のプレフィックス（`----Twitter4J-upload`）。 */
internal const val HTTP_MULTIPART_BOUNDARY_PREFIX = "----Twitter4J-upload"

/**
 * マルチパート境界文字列を生成する。
 *
 * JVM 実装は `"----Twitter4J-upload" + System.currentTimeMillis()` を用いる。ここでは一意化サフィックスを
 * 呼び出し側（プラットフォーム）から受け取ることで、テスト時に決定的な境界を指定できるようにする。
 */
internal fun buildMultipartBoundary(uniqueSuffix: String): String =
    HTTP_MULTIPART_BOUNDARY_PREFIX + uniqueSuffix

/**
 * マルチパートボディの各パートを表す純データモデル。
 * プラットフォーム固有のファイル表現（`java.io.File` / iOS の `fileName`+`fileBody`）はここに持ち込まず、
 * 呼び出し側で「ファイル名 / Content-Type / 本体バイト列」に還元してから渡す。
 */
internal sealed interface MultipartPart {
    val name: String

    /** テキストフィールド（`text/plain; charset=UTF-8`）。 */
    data class Text(override val name: String, val value: String) : MultipartPart

    /** ファイルフィールド。 */
    class FileData(
        override val name: String,
        val fileName: String,
        val contentType: String,
        val body: ByteArray
    ) : MultipartPart
}

/**
 * 指定の境界文字列とパート列から `multipart/form-data` ボディ全体を [ByteArray] として組み立てる。
 *
 * @param boundary `--` を含まない生の境界文字列（Content-Type ヘッダに載せるものと同一）
 * @param parts    パート列
 */
internal fun buildMultipartBody(boundary: String, parts: List<MultipartPart>): ByteArray {
    val dashBoundary = "--$boundary"
    val chunks = ArrayList<ByteArray>()
    for (part in parts) {
        when (part) {
            is MultipartPart.FileData -> {
                chunks.add("$dashBoundary\r\n".encodeToByteArray())
                chunks.add(
                    ("Content-Disposition: form-data; name=\"${part.name}\"; filename=\"${part.fileName}\"\r\n")
                        .encodeToByteArray()
                )
                chunks.add("Content-Type: ${part.contentType}\r\n\r\n".encodeToByteArray())
                chunks.add(part.body)
                chunks.add("\r\n".encodeToByteArray())
            }

            is MultipartPart.Text -> {
                chunks.add("$dashBoundary\r\n".encodeToByteArray())
                chunks.add(
                    ("Content-Disposition: form-data; name=\"${part.name}\"\r\n").encodeToByteArray()
                )
                chunks.add("Content-Type: text/plain; charset=UTF-8\r\n\r\n".encodeToByteArray())
                chunks.add(part.value.encodeToByteArray())
                chunks.add("\r\n".encodeToByteArray())
            }
        }
    }
    chunks.add("$dashBoundary--\r\n".encodeToByteArray())
    chunks.add("\r\n".encodeToByteArray())

    // 全チャンクを 1 本の ByteArray へ連結する（commonMain には ByteArrayOutputStream が無いため手組み）
    var total = 0
    for (c in chunks) total += c.size
    val result = ByteArray(total)
    var pos = 0
    for (c in chunks) {
        c.copyInto(result, pos)
        pos += c.size
    }
    return result
}
