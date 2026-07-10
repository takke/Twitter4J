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

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * commonMain の `multipart/form-data` 組み立て純関数（[buildMultipartBody] / [buildMultipartBoundary]）が
 * JVM の [HttpClientImpl] のマルチパート出力とバイト単位で一致することを検証する。
 *
 * iOS(NSURLSession) 実装はこの純関数を用いてボディ全体（NSData）を組み立てるため、ここでの
 * バイト同値検証が iOS のマルチパート正当性の担保となる。
 */
class HttpMultipartTest {

    @Test
    fun testBoundaryPrefix() {
        val boundary = buildMultipartBoundary("1234567890")
        assertEquals("----Twitter4J-upload1234567890", boundary)
    }

    @Test
    fun testTextOnlyBodyMatchesJvmFormat() {
        val boundary = "----Twitter4J-uploadXYZ"
        val body = buildMultipartBody(
            boundary,
            listOf(
                MultipartPart.Text("status", "hello world"),
                MultipartPart.Text("lang", "ja")
            )
        )

        // JVM 実装（HttpClientImpl.handleRequest）と同一書式を素朴に再現した期待値
        val expected =
            "--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"status\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n\r\n" +
                "hello world\r\n" +
                "--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"lang\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n\r\n" +
                "ja\r\n" +
                "--$boundary--\r\n" +
                "\r\n"

        assertArrayEquals(expected.encodeToByteArray(), body)
    }

    @Test
    fun testFilePartPreservesBinaryBytes() {
        val boundary = "----Twitter4J-uploadFILE"
        // 0x00 や 0xFF を含む「文字列にできない」バイナリ本体
        val fileBytes = byteArrayOf(0x00, 0x01, 0x7F, 0xFF.toByte(), 0x80.toByte(), 0x0A, 0x0D)
        val body = buildMultipartBody(
            boundary,
            listOf(
                MultipartPart.FileData(
                    name = "media",
                    fileName = "photo.png",
                    contentType = "image/png",
                    body = fileBytes
                )
            )
        )

        val header =
            "--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"media\"; filename=\"photo.png\"\r\n" +
                "Content-Type: image/png\r\n\r\n"
        val footer = "\r\n--$boundary--\r\n\r\n"

        val expected = header.encodeToByteArray() + fileBytes + footer.encodeToByteArray()
        assertArrayEquals(expected, body)

        // バイナリ本体がそのまま含まれること（文字列化による破壊がないこと）を明示的に確認
        val headerLen = header.encodeToByteArray().size
        val embedded = body.copyOfRange(headerLen, headerLen + fileBytes.size)
        assertArrayEquals(fileBytes, embedded)
    }

    @Test
    fun testMixedTextAndFile() {
        val boundary = "----Twitter4J-uploadMIX"
        val fileBytes = byteArrayOf(0x11, 0x22, 0x33)
        val body = buildMultipartBody(
            boundary,
            listOf(
                MultipartPart.Text("status", "caption"),
                MultipartPart.FileData("media", "a.jpg", "image/jpeg", fileBytes)
            )
        )
        val text = body.decodeToString()
        assertTrue(text.contains("--$boundary\r\nContent-Disposition: form-data; name=\"status\"\r\n"))
        assertTrue(text.contains("name=\"media\"; filename=\"a.jpg\""))
        assertTrue(text.contains("Content-Type: image/jpeg\r\n\r\n"))
        assertTrue(text.endsWith("--$boundary--\r\n\r\n"))
    }

    @Test
    fun testMultibyteTextEncodedAsUtf8() {
        val boundary = "----Twitter4J-uploadJP"
        val body = buildMultipartBody(
            boundary,
            listOf(MultipartPart.Text("status", "ほげ"))
        )
        // "ほげ" が UTF-8（3バイト×2）でエンコードされていること
        val expected =
            ("--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"status\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n\r\n").encodeToByteArray() +
                "ほげ".encodeToByteArray() +
                ("\r\n--$boundary--\r\n\r\n").encodeToByteArray()
        assertArrayEquals(expected, body)
    }
}
