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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URLDecoder

/**
 * ParseUtil に追加した純 Kotlin 実装の URL デコード（commonMain 版）が、
 * 従来の [java.net.URLDecoder] と同じ結果になることを検証する。
 *
 * ここでは公開 API の [ParseUtil.getURLDecodedString] を通して検証する
 * （JSON に "query" として値を詰め、デコード結果を確認する）。
 */
class ParseUtilUrlDecodeTest {

    private fun decodeViaParseUtil(raw: String): String? {
        // getURLDecodedString は JSONObject 経由で値を取得してデコードする
        val json = JSONObject().put("query", raw)
        return ParseUtil.getURLDecodedString("query", json)
    }

    private fun assertSameAsJavaDecoder(raw: String) {
        val expected = URLDecoder.decode(raw, "UTF-8")
        assertEquals(expected, decodeViaParseUtil(raw), "input=$raw")
    }

    @Test
    fun testPlainAsciiUnchanged() {
        assertSameAsJavaDecoder("hello_world")
    }

    @Test
    fun testPlusToSpace() {
        assertSameAsJavaDecoder("hello+world")
    }

    @Test
    fun testPercentEncodedAscii() {
        // "%23test%20abc" -> "#test abc"
        assertSameAsJavaDecoder("%23test%20abc")
    }

    @Test
    fun testMultiByteUtf8() {
        // 日本語（3バイト UTF-8）の %XX 連続デコード
        // "%E3%81%BB%E3%81%92" -> "ほげ"
        assertSameAsJavaDecoder("%E3%81%BB%E3%81%92")
    }

    @Test
    fun testSurrogatePairEmoji() {
        // 絵文字（4バイト UTF-8 / サロゲートペア）
        // "%F0%9F%87%A7" -> 🇧 の一部（Regional Indicator Symbol Letter B）
        assertSameAsJavaDecoder("%F0%9F%87%A7")
    }

    @Test
    fun testMixedPercentPlusAndLiteral() {
        assertSameAsJavaDecoder("a+b%20c%E3%81%BBd")
    }
}
