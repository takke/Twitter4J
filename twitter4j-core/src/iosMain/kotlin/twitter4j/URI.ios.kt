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
 * iOS(Native) には `java.net.URI` が存在しないため、URI 文字列のみを保持する最小実装で actual 化する。
 *
 * `java.net.URI` のような構文解析・正規化は行わず、共通コードが必要とする範囲（型としての参照と文字列保持）
 * だけを提供する。
 */
actual class URI actual constructor(str: String) {

    /** 保持している URI 文字列。 */
    val rawString: String = str

    override fun equals(other: Any?): Boolean = other is URI && other.rawString == rawString

    override fun hashCode(): Int = rawString.hashCode()

    override fun toString(): String = rawString
}

/**
 * iOS(Native) では URI 構文検証を行わず、文字列をそのまま保持する。null は空文字列扱いとする。
 */
actual fun createURI(str: String?): URI = URI(str ?: "")
