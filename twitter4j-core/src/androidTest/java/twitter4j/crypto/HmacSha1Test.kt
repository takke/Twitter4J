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

package twitter4j.crypto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Random
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 純Kotlin実装 [HmacSha1]（SHA-1 / HMAC-SHA1）の検証テスト。
 *
 * KMP移行 Phase 4-2:
 * OAuth 1.0a 署名の HMAC-SHA1 を javax.crypto から純Kotlin実装へ切り替えたため、
 * 実装が標準テストベクタ（RFC 2202 / FIPS 180-1）および JVM 標準実装（javax.crypto.Mac）と
 * 1バイトも違わないことを保証する。
 */
class HmacSha1Test {

    // -------------------------------------------------------------------------
    // ヘルパー
    // -------------------------------------------------------------------------

    /** 16進文字列をバイト列へ変換する。 */
    private fun hex(s: String): ByteArray {
        val clean = s.replace(" ", "")
        val out = ByteArray(clean.length / 2)
        for (i in out.indices) {
            out[i] = clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return out
    }

    /** バイト列を小文字16進文字列へ変換する。 */
    private fun toHex(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            val v = b.toInt() and 0xff
            sb.append("0123456789abcdef"[v ushr 4])
            sb.append("0123456789abcdef"[v and 0x0f])
        }
        return sb.toString()
    }

    /** 指定バイトを count 回繰り返したバイト列を作る。 */
    private fun repeatByte(value: Int, count: Int): ByteArray {
        val out = ByteArray(count)
        for (i in 0 until count) out[i] = value.toByte()
        return out
    }

    private fun ascii(s: String): ByteArray = s.toByteArray(Charsets.US_ASCII)

    // -------------------------------------------------------------------------
    // RFC 2202: HMAC-SHA1 テストベクタ（test_case 1〜7）
    // https://www.rfc-editor.org/rfc/rfc2202
    // -------------------------------------------------------------------------

    @Test
    fun rfc2202_testCase1() {
        // key = 0x0b x20, data = "Hi There"
        val mac = HmacSha1.mac(repeatByte(0x0b, 20), ascii("Hi There"))
        assertEquals("b617318655057264e28bc0b6fb378c8ef146be00", toHex(mac))
    }

    @Test
    fun rfc2202_testCase2() {
        // key = "Jefe", data = "what do ya want for nothing?"
        val mac = HmacSha1.mac(ascii("Jefe"), ascii("what do ya want for nothing?"))
        assertEquals("effcdf6ae5eb2fa2d27416d5f184df9c259a7c79", toHex(mac))
    }

    @Test
    fun rfc2202_testCase3() {
        // key = 0xaa x20, data = 0xdd x50
        val mac = HmacSha1.mac(repeatByte(0xaa, 20), repeatByte(0xdd, 50))
        assertEquals("125d7342b9ac11cd91a39af48aa17b4f63f175d3", toHex(mac))
    }

    @Test
    fun rfc2202_testCase4() {
        // key = 0x0102...0x19 (25 bytes), data = 0xcd x50
        val key = hex("0102030405060708090a0b0c0d0e0f10111213141516171819")
        val mac = HmacSha1.mac(key, repeatByte(0xcd, 50))
        assertEquals("4c9007f4026250c6bc8414f9bf50c86c2d7235da", toHex(mac))
    }

    @Test
    fun rfc2202_testCase5() {
        // key = 0x0c x20, data = "Test With Truncation"
        val mac = HmacSha1.mac(repeatByte(0x0c, 20), ascii("Test With Truncation"))
        assertEquals("4c1a03424b55e07fe7f27be1d58bb9324a9a5a04", toHex(mac))
    }

    @Test
    fun rfc2202_testCase6() {
        // key = 0xaa x80（ブロックサイズ64超 → 鍵をSHA-1で短縮する経路）
        // data = "Test Using Larger Than Block-Size Key - Hash Key First"
        val mac = HmacSha1.mac(repeatByte(0xaa, 80), ascii("Test Using Larger Than Block-Size Key - Hash Key First"))
        assertEquals("aa4ae5e15272d00e95705637ce8a3b55ed402112", toHex(mac))
    }

    @Test
    fun rfc2202_testCase7() {
        // key = 0xaa x80（ブロックサイズ超）
        // data = ブロックサイズ超の長文
        val data = "Test Using Larger Than Block-Size Key and Larger Than One Block-Size Data"
        val mac = HmacSha1.mac(repeatByte(0xaa, 80), ascii(data))
        assertEquals("e8e99d0f45237d786d6bbaa7965c7808bbff1a91", toHex(mac))
    }

    // -------------------------------------------------------------------------
    // FIPS 180-1: SHA-1 単体テストベクタ
    // -------------------------------------------------------------------------

    @Test
    fun sha1_abc() {
        val digest = HmacSha1.sha1(ascii("abc"))
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", toHex(digest))
    }

    @Test
    fun sha1_twoBlockMessage() {
        val digest = HmacSha1.sha1(ascii("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"))
        assertEquals("84983e441c3bd26ebaae4aa1f95129e5e54670f1", toHex(digest))
    }

    @Test
    fun sha1_emptyString() {
        // 空文字列（SHA-1 の既知値）
        val digest = HmacSha1.sha1(ByteArray(0))
        assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", toHex(digest))
    }

    @Test
    fun sha1_oneMillionA() {
        // FIPS 180-1: "a" を 1,000,000 回（複数ブロック・長文経路の検証）
        val message = repeatByte('a'.code, 1_000_000)
        val digest = HmacSha1.sha1(message)
        assertEquals("34aa973cd4c4daa4f61eeb2bdbad27316534016f", toHex(digest))
    }

    // -------------------------------------------------------------------------
    // javax.crypto.Mac（JVM標準実装）との出力一致テスト
    // -------------------------------------------------------------------------

    /** JVM標準の HmacSHA1 で参照値を計算する。 */
    private fun jvmHmacSha1(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key, "HmacSHA1"))
        return mac.doFinal(data)
    }

    /** JVM標準の SHA-1 で参照値を計算する。 */
    private fun jvmSha1(data: ByteArray): ByteArray {
        return java.security.MessageDigest.getInstance("SHA-1").digest(data)
    }

    @Test
    fun matchesJvmMac_randomInputs() {
        // 乱数入力の HMAC-SHA1 を JVM 標準実装と多数件照合する。
        // 鍵長・データ長を 0〜200 で振り、ブロックサイズ境界(64)前後も網羅されるようにする。
        val rnd = Random(20260710L) // 再現性のため固定シード
        repeat(500) {
            val keyLen = rnd.nextInt(201)
            val dataLen = rnd.nextInt(201)
            val key = ByteArray(keyLen).also { rnd.nextBytes(it) }
            val data = ByteArray(dataLen).also { rnd.nextBytes(it) }

            // JVMのMacは空鍵を許容しないため、空鍵のときはSHA-1単体で照合する
            if (keyLen == 0) {
                assertEquals(toHex(jvmSha1(data)), toHex(HmacSha1.sha1(data)),
                    "SHA-1 mismatch: dataLen=$dataLen")
            } else {
                assertEquals(toHex(jvmHmacSha1(key, data)), toHex(HmacSha1.mac(key, data)),
                    "HMAC-SHA1 mismatch: keyLen=$keyLen dataLen=$dataLen")
            }
        }
    }

    @Test
    fun matchesJvmSha1_blockBoundaries() {
        // パディング境界（55,56,57,63,64,65 バイト 等）で JVM SHA-1 と照合する。
        val lengths = intArrayOf(0, 1, 55, 56, 57, 63, 64, 65, 119, 120, 127, 128, 129, 1000)
        for (len in lengths) {
            val data = ByteArray(len) { (it and 0xff).toByte() }
            assertEquals(toHex(jvmSha1(data)), toHex(HmacSha1.sha1(data)),
                "SHA-1 mismatch at length=$len")
        }
    }
}
