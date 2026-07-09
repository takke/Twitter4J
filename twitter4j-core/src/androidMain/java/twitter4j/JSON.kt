/*
 * Copyright (C) 2010 The Android Open Source Project
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

// KMP対応: 元 package-private class JSON を internal object 化。
// これらのヘルパは同モジュール内の JSON 系 Kotlin ファイルからのみ参照される。
internal object JSON {
    /**
     * Returns the input if it is a JSON-permissible value; throws otherwise.
     */
    fun checkDouble(d: Double): Double {
        if (d.isInfinite() || d.isNaN()) {
            throw JSONException("Forbidden numeric value: $d")
        }
        return d
    }

    fun toBoolean(value: Any?): Boolean? {
        if (value is Boolean) {
            return value
        } else if (value is String) {
            if ("true".equals(value, ignoreCase = true)) {
                return true
            } else if ("false".equals(value, ignoreCase = true)) {
                return false
            }
        }
        return null
    }

    fun toDouble(value: Any?): Double? {
        if (value is Double) {
            return value
        } else if (value is Number) {
            return value.toDouble()
        } else if (value is String) {
            // Double.valueOf 相当。パース不能なら null。
            return value.toDoubleOrNull()
        }
        return null
    }

    fun toInteger(value: Any?): Int? {
        if (value is Int) {
            return value
        } else if (value is Number) {
            return value.toInt()
        } else if (value is String) {
            // 元実装同様、いったん double としてパースしてから int へキャスト。
            return value.toDoubleOrNull()?.toInt()
        }
        return null
    }

    fun toLong(value: Any?): Long? {
        if (value is Long) {
            return value
        } else if (value is Number) {
            return value.toLong()
        } else if (value is String) {
            return value.toLongOrNull()
        }
        return null
    }

    fun toString(value: Any?): String? {
        if (value is String) {
            return value
        } else if (value != null) {
            return value.toString()
        }
        return null
    }

    // 元実装は throws していたが、呼び出し側が常に `throw JSON.typeMismatch(...)` の形で
    // 使うため、ここでは例外を生成して返すだけにする（結果は同一）。
    fun typeMismatch(indexOrName: Any?, actual: Any?, requiredType: String): JSONException {
        return if (actual == null) {
            JSONException("Value at $indexOrName is null.")
        } else {
            JSONException(
                "Value " + actual + " at " + indexOrName
                        + " of type " + actual::class.simpleName
                        + " cannot be converted to " + requiredType
            )
        }
    }

    fun typeMismatch(actual: Any?, requiredType: String): JSONException {
        return if (actual == null) {
            JSONException("Value is null.")
        } else {
            JSONException(
                "Value " + actual
                        + " of type " + actual::class.simpleName
                        + " cannot be converted to " + requiredType
            )
        }
    }
}
