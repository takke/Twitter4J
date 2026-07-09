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
 * A utility class encodes byte array into String using Base64 encoding scheme.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see HttpClient
 */
object BASE64Encoder {
    private const val last2byte = 0b00000011 // 3
    private const val last4byte = 0b00001111 // 15
    private const val last6byte = 0b00111111 // 63
    private const val lead6byte = 0b11111100 // 252
    private const val lead4byte = 0b11110000 // 240
    private const val lead2byte = 0b11000000 // 192
    private val encodeTable = charArrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    )

    @JvmStatic
    fun encode(from: ByteArray): String {
        val to = StringBuilder((from.size * 1.34).toInt() + 3)
        var num = 0
        var currentByte = 0
        for (i in from.indices) {
            num %= 8
            while (num < 8) {
                when (num) {
                    0 -> {
                        currentByte = from[i].toInt() and lead6byte
                        currentByte = currentByte ushr 2
                    }
                    2 -> {
                        currentByte = from[i].toInt() and last6byte
                    }
                    4 -> {
                        currentByte = from[i].toInt() and last4byte
                        currentByte = currentByte shl 2
                        if ((i + 1) < from.size) {
                            currentByte = currentByte or ((from[i + 1].toInt() and lead2byte) ushr 6)
                        }
                    }
                    6 -> {
                        currentByte = from[i].toInt() and last2byte
                        currentByte = currentByte shl 4
                        if ((i + 1) < from.size) {
                            currentByte = currentByte or ((from[i + 1].toInt() and lead4byte) ushr 4)
                        }
                    }
                }
                to.append(encodeTable[currentByte])
                num += 6
            }
        }
        if (to.length % 4 != 0) {
            var i = 4 - to.length % 4
            while (i > 0) {
                to.append("=")
                i--
            }
        }
        return to.toString()
    }
}
