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

import kotlin.jvm.JvmStatic

/**
 * Twitter4J 内部で使用するシンプルなロガー。
 *
 * KMP移行 Phase 3-2 でロギングを再設計した。
 * 従来は Class.forName による動的検出（SLF4J / Log4J / Commons Logging / JUL / StdOut / Null）で
 * ロギングバックエンドを切り替えていたが、消費側アプリ（TwitPane）は独自の MyLog 系を使っており
 * Twitter4J 内部ロガーへ依存していないため、シンプルな println ベースの実装へ置き換えた。
 *
 * デフォルトでは [enabled] が false のため一切出力しない（従来の「ロガー未検出時は NullLogger」相当の静粛性）。
 * デバッグ時は `Logger.enabled = true` でグローバルに有効化できる。
 *
 * commonMain に配置しているため Android / iOS / Desktop の全プラットフォームで利用可能。
 */
class Logger private constructor(private val name: String) {

    /** debug レベルのログが有効かどうか */
    fun isDebugEnabled(): Boolean = enabled

    /** info レベルのログが有効かどうか */
    fun isInfoEnabled(): Boolean = enabled

    /** warn レベルのログが有効かどうか */
    fun isWarnEnabled(): Boolean = enabled

    /** error レベルのログが有効かどうか */
    fun isErrorEnabled(): Boolean = enabled

    fun debug(message: String?) = log("DEBUG", message, null)
    fun debug(message: String?, message2: String?) = log("DEBUG", "$message$message2", null)

    fun info(message: String?) = log("INFO", message, null)
    fun info(message: String?, message2: String?) = log("INFO", "$message$message2", null)

    fun warn(message: String?) = log("WARN", message, null)
    fun warn(message: String?, message2: String?) = log("WARN", "$message$message2", null)
    fun warn(message: String?, th: Throwable?) = log("WARN", message, th)

    fun error(message: String?) = log("ERROR", message, null)
    fun error(message: String?, th: Throwable?) = log("ERROR", message, th)

    // 出力の実体。enabled=false のときは何もしない（呼び出し元での null 引数も許容する）。
    private fun log(level: String, message: String?, th: Throwable?) {
        if (!enabled) return
        println("[twitter4j] $level $name - $message")
        if (th != null) {
            println(th.stackTraceToString())
        }
    }

    companion object {
        /**
         * ロガー出力をグローバルに有効化するフラグ。
         * デフォルトは false（無音）。従来の NullLogger 相当の静粛性を既定とする。
         */
        @JvmStatic
        var enabled: Boolean = false

        /**
         * 指定クラスに紐づく Logger を返す。
         *
         * Java 呼び出し元（HttpClientImpl 等）は `Logger.getLogger(Xxx.class)` の形で、
         * Kotlin 呼び出し元は `Logger.getLogger(Xxx::class.java)` の形で使用する。
         * commonMain では JVM の `Class` 型を直接参照できないため、引数型は [Any] とし、
         * ログ名の導出だけに用いる（JVM の Class もそのまま渡せる）。
         */
        @JvmStatic
        fun getLogger(clazz: Any): Logger = Logger(clazz.toString())
    }
}
