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

/**
 * OAuth 1.0a 署名（HMAC-SHA1）のための純Kotlin実装。
 *
 * KMP移行 Phase 4-2:
 * OAuth署名で使用していた `javax.crypto.Mac` (HmacSHA1) は JVM(Android) 専用APIであり、
 * iOS(Native)/Desktop の commonMain からは利用できない。
 * かつ、暗号ライブラリの導入は過去に iOS AOT リンク衝突を起こした前例があるため採用しない。
 *
 * そこで SHA-1 (RFC 3174) と HMAC (RFC 2104) を依存ライブラリなしの手書き純Kotlinで実装し、
 * commonMain に配置して全プラットフォームで同一の署名を得られるようにする。
 *
 * 本オブジェクトは OAuthAuthorization からのみ使用するため `internal` とする。
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc3174">RFC 3174 - US Secure Hash Algorithm 1 (SHA1)</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc2104">RFC 2104 - HMAC: Keyed-Hashing for Message Authentication</a>
 */
internal object HmacSha1 {

    /** SHA-1 のブロックサイズ（バイト）。HMAC のパディングにも使用する。 */
    private const val BLOCK_SIZE = 64

    /** SHA-1 のダイジェスト長（バイト）。 */
    private const val DIGEST_SIZE = 20

    /**
     * RFC 2104 準拠の HMAC-SHA1 を計算する。
     *
     * 手順:
     *  1. 鍵がブロックサイズ(64byte)を超える場合は SHA-1 でハッシュして短縮する。
     *  2. 鍵をブロックサイズまで 0 パディングする。
     *  3. ipad(0x36) / opad(0x5c) と XOR して内側・外側パッドを作る。
     *  4. SHA1( opad || SHA1( ipad || message ) ) を返す。
     *
     * @param key     署名鍵（OAuth では "consumerSecret&tokenSecret" の UTF-8 バイト列）
     * @param message 署名対象データ（OAuth 署名ベース文字列の UTF-8 バイト列）
     * @return 20バイトの HMAC-SHA1 ダイジェスト
     */
    fun mac(key: ByteArray, message: ByteArray): ByteArray {
        // 1. 鍵がブロックサイズを超える場合はハッシュして短縮（RFC 2104）
        val normalizedKey = if (key.size > BLOCK_SIZE) sha1(key) else key

        // 2〜3. 内側/外側パッドを作成
        val innerPad = ByteArray(BLOCK_SIZE)
        val outerPad = ByteArray(BLOCK_SIZE)
        for (i in 0 until BLOCK_SIZE) {
            val keyByte = if (i < normalizedKey.size) normalizedKey[i].toInt() else 0
            innerPad[i] = (keyByte xor 0x36).toByte()
            outerPad[i] = (keyByte xor 0x5c).toByte()
        }

        // 4. SHA1( opad || SHA1( ipad || message ) )
        val innerHash = sha1(innerPad + message)
        return sha1(outerPad + innerHash)
    }

    /**
     * RFC 3174 準拠の SHA-1 ダイジェストを計算する。
     *
     * @param message 入力メッセージ
     * @return 20バイトのダイジェスト
     */
    fun sha1(message: ByteArray): ByteArray {
        // 初期ハッシュ値 H0..H4（RFC 3174 §6.1）
        var h0 = 0x67452301
        var h1 = 0xEFCDAB89.toInt()
        var h2 = 0x98BADCFE.toInt()
        var h3 = 0x10325476
        var h4 = 0xC3D2E1F0.toInt()

        // --- パディング（RFC 3174 §4）---
        // メッセージ長（ビット単位、64bit big-endian）
        val messageBitLength = message.size.toLong() * 8
        // 0x80 を1つ付与し、その後 (総長 % 64 == 56) となるまで 0 を詰め、最後に長さ8バイトを付ける
        val zeroPadCount = ((56 - (message.size + 1) % BLOCK_SIZE) + BLOCK_SIZE) % BLOCK_SIZE
        val totalLength = message.size + 1 + zeroPadCount + 8
        val padded = ByteArray(totalLength)
        message.copyInto(padded)
        padded[message.size] = 0x80.toByte()
        // メッセージ長を big-endian で末尾8バイトに書き込む
        for (i in 0 until 8) {
            padded[totalLength - 1 - i] = ((messageBitLength ushr (8 * i)) and 0xff).toByte()
        }

        // --- 512bit(64byte) ブロックごとに処理 ---
        val w = IntArray(80)
        var offset = 0
        while (offset < totalLength) {
            // 先頭16ワードをブロックから big-endian で読み込む
            for (i in 0 until 16) {
                val j = offset + i * 4
                w[i] = ((padded[j].toInt() and 0xff) shl 24) or
                    ((padded[j + 1].toInt() and 0xff) shl 16) or
                    ((padded[j + 2].toInt() and 0xff) shl 8) or
                    (padded[j + 3].toInt() and 0xff)
            }
            // 残り64ワードを拡張（RFC 3174 §6.1 (b)）
            for (i in 16 until 80) {
                w[i] = rotateLeft(w[i - 3] xor w[i - 8] xor w[i - 14] xor w[i - 16], 1)
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4

            for (i in 0 until 80) {
                val f: Int
                val k: Int
                when {
                    i < 20 -> {
                        f = (b and c) or (b.inv() and d)
                        k = 0x5A827999
                    }
                    i < 40 -> {
                        f = b xor c xor d
                        k = 0x6ED9EBA1
                    }
                    i < 60 -> {
                        f = (b and c) or (b and d) or (c and d)
                        k = 0x8F1BBCDC.toInt()
                    }
                    else -> {
                        f = b xor c xor d
                        k = 0xCA62C1D6.toInt()
                    }
                }
                val temp = rotateLeft(a, 5) + f + e + k + w[i]
                e = d
                d = c
                c = rotateLeft(b, 30)
                b = a
                a = temp
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            offset += BLOCK_SIZE
        }

        // --- ダイジェスト出力（big-endian）---
        val digest = ByteArray(DIGEST_SIZE)
        writeIntBigEndian(digest, 0, h0)
        writeIntBigEndian(digest, 4, h1)
        writeIntBigEndian(digest, 8, h2)
        writeIntBigEndian(digest, 12, h3)
        writeIntBigEndian(digest, 16, h4)
        return digest
    }

    /** 32bit 左ローテート。 */
    private fun rotateLeft(value: Int, bits: Int): Int =
        (value shl bits) or (value ushr (32 - bits))

    /** Int を big-endian で 4バイト書き込む。 */
    private fun writeIntBigEndian(out: ByteArray, offset: Int, value: Int) {
        out[offset] = (value ushr 24).toByte()
        out[offset + 1] = (value ushr 16).toByte()
        out[offset + 2] = (value ushr 8).toByte()
        out[offset + 3] = value.toByte()
    }
}
