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

@file:OptIn(ExperimentalTime::class)

package twitter4j

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Twitter が返す固定の日付フォーマット（英語ロケール固定）を解析する純Kotlin実装のパーサ。
 *
 * 旧実装は `java.text.SimpleDateFormat` を使っていたが、KMP(commonMain / iOS)では利用できないため、
 * フォーマット文字列を [SimpleDateFormat] 風のパターンとして自前でトークン化して解析する。
 *
 * 月名・曜日名は英語固定（Jan..Dec / Mon..Sun）なのでロケール非依存で解析でき、`+0000` などの
 * タイムゾーンオフセットにも対応する。組み立てには [kotlinx.datetime] を用いる。
 *
 * 対応実績のあるフォーマット（ParseUtil から渡されるもの）:
 * - "EEE MMM d HH:mm:ss z yyyy"      … Status.created_at 等（例: "Tue Nov 12 14:15:41 +0000 2013"）
 * - "EEE MMM dd HH:mm:ss z yyyy"     … User / SavedSearch の created_at
 * - "yyyy-MM-dd'T'HH:mm:ss'Z'"       … Trends の as_of（例: "2011-07-11T05:31:52Z"）
 * - "EEE, d MMM yyyy HH:mm:ss z"     … Trends の as_of（RFC1123 風）
 * - "yyyy-MM-dd HH:mm:ss" / "yyyy-MM-dd HH:mm" / "yyyy-MM-dd" … Trends の trendAt
 */
internal object TwitterDateParser {

    /** フォーマット文字列を解析して得られるトークン。 */
    private sealed class Token {
        /** 連続する同一のパターン文字（例: 'y' が 4 個で "yyyy"）。 */
        class Pattern(val letter: Char, val count: Int) : Token()

        /** そのまま一致を要求するリテラル（区切り文字やクォートで囲まれた文字）。 */
        class Literal(val text: String) : Token()
    }

    private val MONTH_ABBREVIATIONS = listOf(
        "jan", "feb", "mar", "apr", "may", "jun",
        "jul", "aug", "sep", "oct", "nov", "dec"
    )

    /**
     * [dateString] を [format]（SimpleDateFormat 風パターン）で解析し、UTC 基準の [Instant] を返す。
     *
     * 解析に失敗した場合は [IllegalArgumentException] を送出する（呼び出し側で TwitterException に変換する想定）。
     */
    fun parse(dateString: String, format: String): Instant {
        val tokens = tokenize(format)

        var pos = 0
        var year = 1970
        var month = 1
        var day = 1
        var hour = 0
        var minute = 0
        var second = 0
        var offsetSeconds = 0

        fun readDigits(max: Int): Int {
            val start = pos
            while (pos < dateString.length && pos - start < max && dateString[pos].isDigit()) {
                pos++
            }
            require(pos > start) { "expected digits at index $start in \"$dateString\"" }
            return dateString.substring(start, pos).toInt()
        }

        fun readLetters(): String {
            val start = pos
            while (pos < dateString.length && dateString[pos].isLetter()) {
                pos++
            }
            return dateString.substring(start, pos)
        }

        fun readOffset() {
            if (pos >= dateString.length) return
            val c = dateString[pos]
            when {
                c == '+' || c == '-' -> {
                    val sign = if (c == '-') -1 else 1
                    pos++
                    val h = readDigits(2)
                    if (pos < dateString.length && dateString[pos] == ':') pos++
                    var m = 0
                    if (pos < dateString.length && dateString[pos].isDigit()) {
                        m = readDigits(2)
                    }
                    offsetSeconds = sign * (h * 3600 + m * 60)
                }
                c.isLetter() -> {
                    // "GMT" / "UTC" などの名前は UTC(0) 扱い。末尾に "+09:00" 等が続けば加味する。
                    readLetters()
                    if (pos < dateString.length && (dateString[pos] == '+' || dateString[pos] == '-')) {
                        readOffset()
                    }
                }
                else -> {
                    // 何もしない（オフセット省略）
                }
            }
        }

        for (token in tokens) {
            when (token) {
                is Token.Literal -> {
                    for (ch in token.text) {
                        require(pos < dateString.length && dateString[pos] == ch) {
                            "expected '$ch' at index $pos in \"$dateString\""
                        }
                        pos++
                    }
                }

                is Token.Pattern -> when (token.letter) {
                    'E' -> readLetters() // 曜日名は無視
                    'a' -> readLetters() // AM/PM（実フォーマットには出現しない）
                    'M' -> {
                        month = if (token.count >= 3) {
                            monthFromName(readLetters())
                        } else {
                            readDigits(2)
                        }
                    }
                    'd' -> day = readDigits(2)
                    'y' -> year = readDigits(4)
                    'H', 'h' -> hour = readDigits(2)
                    'm' -> minute = readDigits(2)
                    's' -> second = readDigits(2)
                    'z', 'Z', 'X' -> readOffset()
                    else -> {
                        // 未対応パターン文字は無視（同数の文字を読み飛ばす）
                        var skip = token.count
                        while (skip > 0 && pos < dateString.length) {
                            pos++
                            skip--
                        }
                    }
                }
            }
        }

        val localDateTime = LocalDateTime(year, month, day, hour, minute, second, 0)
        return localDateTime.toInstant(UtcOffset(seconds = offsetSeconds))
    }

    /**
     * SimpleDateFormat 風のフォーマット文字列をトークン列に分解する。
     * - 連続する同一のアルファベットはパターントークン
     * - シングルクォートで囲まれた部分はリテラル（'' はシングルクォート1文字）
     * - それ以外の文字（区切り記号・空白等）はリテラル
     */
    private fun tokenize(format: String): List<Token> {
        val tokens = ArrayList<Token>()
        val literal = StringBuilder()

        fun flushLiteral() {
            if (literal.isNotEmpty()) {
                tokens.add(Token.Literal(literal.toString()))
                literal.clear()
            }
        }

        var i = 0
        while (i < format.length) {
            val c = format[i]
            when {
                c == '\'' -> {
                    i++
                    if (i < format.length && format[i] == '\'') {
                        // '' → シングルクォート1文字
                        literal.append('\'')
                        i++
                    } else {
                        while (i < format.length && format[i] != '\'') {
                            literal.append(format[i])
                            i++
                        }
                        if (i < format.length) i++ // 閉じクォートを読み飛ばす
                    }
                }
                c.isLetter() -> {
                    flushLiteral()
                    var j = i
                    while (j < format.length && format[j] == c) j++
                    tokens.add(Token.Pattern(c, j - i))
                    i = j
                }
                else -> {
                    literal.append(c)
                    i++
                }
            }
        }
        flushLiteral()
        return tokens
    }

    private fun monthFromName(name: String): Int {
        val key = name.lowercase().take(3)
        val index = MONTH_ABBREVIATIONS.indexOf(key)
        require(index >= 0) { "unknown month name: \"$name\"" }
        return index + 1
    }
}
