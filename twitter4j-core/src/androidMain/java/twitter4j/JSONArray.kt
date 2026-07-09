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

// Note: this class was written without inspecting the non-free org.json sourcecode.

/**
 * A dense indexed sequence of values. Values may be any mix of [JSONObject]s,
 * other [JSONArray]s, Strings, Booleans, Integers, Longs, Doubles, `null` or
 * [JSONObject.NULL]. Values may not be NaNs, infinities, or of any type not
 * listed here.
 *
 * `JSONArray` has the same type coercion behavior and optional/mandatory
 * accessors as [JSONObject]. See that class' documentation for details.
 *
 * **Warning:** this class represents null in two incompatible ways: the standard
 * Kotlin `null` reference, and the sentinel value [JSONObject.NULL].
 *
 * Instances of this class are not thread safe.
 */
class JSONArray {

    // KMP対応: java.util.ArrayList → kotlin.collections.ArrayList（共通型）。
    private val values: MutableList<Any?>

    /**
     * Creates a `JSONArray` with no values.
     */
    constructor() {
        values = ArrayList()
    }

    /**
     * Creates a new `JSONArray` by copying all values from the given collection.
     *
     * KMP対応: 元は raw type `Collection` を受けており null も許容していたが、
     * `JSONArray(Any)`（配列用コンストラクタ）とのオーバーロード曖昧性を避けるため
     * 非 null の `Collection<*>` とした（null を渡す呼び出しはリポジトリ内・TwitPane 共に無い）。
     *
     * @param copyFrom a collection whose values are of supported types.
     */
    constructor(copyFrom: Collection<*>) : this() {
        for (aCopyFrom in copyFrom) {
            put(JSONObject.wrap(aCopyFrom))
        }
    }

    /**
     * Creates a new `JSONArray` with values from the next array in the tokener.
     *
     * @param readFrom a tokener whose nextValue() method will yield a
     *                 `JSONArray`.
     * @throws JSONException if the parse fails or doesn't yield a `JSONArray`.
     */
    constructor(readFrom: JSONTokener) {
        /*
         * Getting the parser to populate this could get tricky. Instead, just
         * parse to temporary JSONArray and then steal the data from that.
         */
        val obj = readFrom.nextValue()
        values = if (obj is JSONArray) {
            obj.values
        } else {
            throw JSON.typeMismatch(obj, "JSONArray")
        }
    }

    /**
     * Creates a new `JSONArray` with values from the JSON string.
     *
     * @param json a JSON-encoded string containing an array.
     * @throws JSONException if the parse fails or doesn't yield a `JSONArray`.
     */
    constructor(json: String) : this(JSONTokener(json))

    /**
     * Creates a new `JSONArray` with values from the given primitive array.
     *
     * KMP対応: 元は java.lang.reflect.Array による汎用配列リフレクションを使用していたが、
     * 共通型判定（`is IntArray` 等）へ置き換えた。
     *
     * @param array The values to use.
     * @throws JSONException if any of the values are non-finite double values.
     */
    constructor(array: Any) {
        values = when (array) {
            is BooleanArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is ByteArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is CharArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is ShortArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is IntArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is LongArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is FloatArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is DoubleArray -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            is Array<*> -> ArrayList<Any?>(array.size).apply { for (v in array) add(JSONObject.wrap(v)) }
            else -> throw JSONException("Not a primitive array: " + array::class)
        }
    }

    /**
     * @return Returns the number of values in this array.
     */
    fun length(): Int {
        return values.size
    }

    /**
     * Appends `value` to the end of this array.
     */
    fun put(value: Boolean): JSONArray {
        values.add(value)
        return this
    }

    /**
     * Appends `value` to the end of this array.
     *
     * @param value a finite value. May not be NaNs or infinities.
     * @throws JSONException If the value is unacceptable.
     */
    fun put(value: Double): JSONArray {
        values.add(JSON.checkDouble(value))
        return this
    }

    /**
     * Appends `value` to the end of this array.
     */
    fun put(value: Int): JSONArray {
        values.add(value)
        return this
    }

    /**
     * Appends `value` to the end of this array.
     */
    fun put(value: Long): JSONArray {
        values.add(value)
        return this
    }

    /**
     * Appends `value` to the end of this array.
     *
     * @param value a [JSONObject], [JSONArray], String, Boolean, Integer, Long,
     *              Double, [JSONObject.NULL], or `null`. May not be NaNs or
     *              infinities.
     */
    fun put(value: Any?): JSONArray {
        values.add(value)
        return this
    }

    /**
     * Same as [put], with added validity checks.
     */
    internal fun checkedPut(value: Any?) {
        if (value is Number) {
            JSON.checkDouble(value.toDouble())
        }

        put(value)
    }

    /**
     * Sets the value at `index` to `value`, null padding this array to the
     * required length if necessary. If a value already exists at `index`, it
     * will be replaced.
     *
     * @throws JSONException This should never happen.
     */
    fun put(index: Int, value: Boolean): JSONArray {
        return put(index, value as Any?)
    }

    /**
     * Sets the value at `index` to `value`, null padding this array to the
     * required length if necessary.
     *
     * @param value a finite value. May not be NaNs or infinities.
     * @throws JSONException If the value is not a finite value.
     */
    fun put(index: Int, value: Double): JSONArray {
        return put(index, value as Any?)
    }

    /**
     * Sets the value at `index` to `value`, null padding this array to the
     * required length if necessary.
     *
     * @throws JSONException Should never actually happen.
     */
    fun put(index: Int, value: Int): JSONArray {
        return put(index, value as Any?)
    }

    /**
     * Sets the value at `index` to `value`, null padding this array to the
     * required length if necessary.
     *
     * @throws JSONException Should never actually happen.
     */
    fun put(index: Int, value: Long): JSONArray {
        return put(index, value as Any?)
    }

    /**
     * Sets the value at `index` to `value`, null padding this array to the
     * required length if necessary. If a value already exists at `index`, it
     * will be replaced.
     *
     * @param value a [JSONObject], [JSONArray], String, Boolean, Integer, Long,
     *              Double, [JSONObject.NULL], or `null`. May not be NaNs or
     *              infinities.
     * @throws JSONException If the value cannot be represented as a finite double value.
     */
    fun put(index: Int, value: Any?): JSONArray {
        if (value is Number) {
            // deviate from the original by checking all Numbers, not just floats & doubles
            JSON.checkDouble(value.toDouble())
        }
        while (values.size <= index) {
            values.add(null)
        }
        values[index] = value
        return this
    }

    /**
     * Returns true if this array has no value at `index`, or if its value is the
     * `null` reference or [JSONObject.NULL].
     */
    fun isNull(index: Int): Boolean {
        val value = opt(index)
        return value == null || value === JSONObject.NULL
    }

    /**
     * Returns the value at `index`.
     *
     * @throws JSONException if this array has no value at `index`, or if that
     *                       value is the `null` reference.
     */
    fun get(index: Int): Any {
        try {
            val value = values[index]
            if (value == null) {
                throw JSONException("Value at $index is null.")
            }
            return value
        } catch (e: IndexOutOfBoundsException) {
            throw JSONException("Index $index out of range [0.." + values.size + ")")
        }
    }

    /**
     * Returns the value at `index`, or null if the array has no value at
     * `index`.
     */
    fun opt(index: Int): Any? {
        if (index < 0 || index >= values.size) {
            return null
        }
        return values[index]
    }

    /**
     * Removes and returns the value at `index`, or null if the array has no
     * value at `index`.
     */
    fun remove(index: Int): Any? {
        if (index < 0 || index >= values.size) {
            return null
        }
        return values.removeAt(index)
    }

    /**
     * Returns the value at `index` if it exists and is a boolean or can be
     * coerced to a boolean.
     *
     * @throws JSONException if the value at `index` doesn't exist or cannot be
     *                       coerced to a boolean.
     */
    fun getBoolean(index: Int): Boolean {
        val obj = get(index)
        return JSON.toBoolean(obj) ?: throw JSON.typeMismatch(index, obj, "boolean")
    }

    /**
     * Returns the value at `index` if it exists and is a boolean or can be
     * coerced to a boolean. Returns `fallback` otherwise.
     */
    @JvmOverloads
    fun optBoolean(index: Int, fallback: Boolean = false): Boolean {
        val obj = opt(index)
        val result = JSON.toBoolean(obj)
        return result ?: fallback
    }

    /**
     * Returns the value at `index` if it exists and is a double or can be
     * coerced to a double.
     *
     * @throws JSONException if the value at `index` doesn't exist or cannot be
     *                       coerced to a double.
     */
    fun getDouble(index: Int): Double {
        val obj = get(index)
        return JSON.toDouble(obj) ?: throw JSON.typeMismatch(index, obj, "double")
    }

    /**
     * Returns the value at `index` if it exists and is a double or can be
     * coerced to a double. Returns `fallback` otherwise.
     */
    @JvmOverloads
    fun optDouble(index: Int, fallback: Double = Double.NaN): Double {
        val obj = opt(index)
        val result = JSON.toDouble(obj)
        return result ?: fallback
    }

    /**
     * Returns the value at `index` if it exists and is an int or can be coerced
     * to an int.
     *
     * @throws JSONException if the value at `index` doesn't exist or cannot be
     *                       coerced to a int.
     */
    fun getInt(index: Int): Int {
        val obj = get(index)
        return JSON.toInteger(obj) ?: throw JSON.typeMismatch(index, obj, "int")
    }

    /**
     * Returns the value at `index` if it exists and is an int or can be coerced
     * to an int. Returns `fallback` otherwise.
     */
    @JvmOverloads
    fun optInt(index: Int, fallback: Int = 0): Int {
        val obj = opt(index)
        val result = JSON.toInteger(obj)
        return result ?: fallback
    }

    /**
     * Returns the value at `index` if it exists and is a long or can be coerced
     * to a long.
     *
     * @throws JSONException if the value at `index` doesn't exist or cannot be
     *                       coerced to a long.
     */
    fun getLong(index: Int): Long {
        val obj = get(index)
        return JSON.toLong(obj) ?: throw JSON.typeMismatch(index, obj, "long")
    }

    /**
     * Returns the value at `index` if it exists and is a long or can be coerced
     * to a long. Returns `fallback` otherwise.
     */
    @JvmOverloads
    fun optLong(index: Int, fallback: Long = 0L): Long {
        val obj = opt(index)
        val result = JSON.toLong(obj)
        return result ?: fallback
    }

    /**
     * Returns the value at `index` if it exists, coercing it if necessary.
     *
     * @throws JSONException if no such value exists.
     */
    fun getString(index: Int): String {
        val obj = get(index)
        return JSON.toString(obj) ?: throw JSON.typeMismatch(index, obj, "String")
    }

    /**
     * Returns the value at `index` if it exists, coercing it if necessary.
     * Returns the empty string if no such value exists.
     */
    fun optString(index: Int): String {
        val obj = opt(index)
        return JSON.toString(obj) ?: ""
    }

    /**
     * Returns the value at `index` if it exists, coercing it if necessary.
     * Returns `fallback` if no such value exists.
     *
     * `fallback` は null 許容（元 org.json 同様）。戻り値も null 許容とする。
     * 非 null fallback 版は JVM シグネチャ衝突回避のため @JvmName を付与（詳細は
     * JSONObject.optString の互換性メモ参照）。
     */
    fun optString(index: Int, fallback: String?): String? {
        val obj = opt(index)
        return JSON.toString(obj) ?: fallback
    }

    /**
     * [optString] の非 null fallback 版。
     */
    @JvmName("optStringNonNull")
    fun optString(index: Int, fallback: String): String {
        val obj = opt(index)
        return JSON.toString(obj) ?: fallback
    }

    /**
     * Returns the value at `index` if it exists and is a [JSONArray].
     *
     * @throws JSONException if the value doesn't exist or is not a [JSONArray].
     */
    fun getJSONArray(index: Int): JSONArray {
        val obj = get(index)
        return obj as? JSONArray ?: throw JSON.typeMismatch(index, obj, "JSONArray")
    }

    /**
     * Returns the value at `index` if it exists and is a [JSONArray]. Returns
     * null otherwise.
     */
    fun optJSONArray(index: Int): JSONArray? {
        return opt(index) as? JSONArray
    }

    /**
     * Returns the value at `index` if it exists and is a [JSONObject].
     *
     * @throws JSONException if the value doesn't exist or is not a [JSONObject].
     */
    fun getJSONObject(index: Int): JSONObject {
        val obj = get(index)
        return obj as? JSONObject ?: throw JSON.typeMismatch(index, obj, "JSONObject")
    }

    /**
     * Returns the value at `index` if it exists and is a [JSONObject]. Returns
     * null otherwise.
     */
    fun optJSONObject(index: Int): JSONObject? {
        return opt(index) as? JSONObject
    }

    /**
     * Returns a new object whose values are the values in this array, and whose
     * names are the values in `names`. This method returns null if either array
     * is empty.
     *
     * @throws JSONException Should not be possible.
     */
    fun toJSONObject(names: JSONArray): JSONObject? {
        val result = JSONObject()
        val length = minOf(names.length(), values.size)
        if (length == 0) {
            return null
        }
        for (i in 0 until length) {
            val name = JSON.toString(names.opt(i))
            // 元実装は put(name, opt(i)) 経由で checkName により名前が null なら JSONException を投げる
            result.put(name ?: throw JSONException("Names must be non-null"), opt(i))
        }
        return result
    }

    /**
     * Returns a new string by alternating this array's values with `separator`.
     *
     * @throws JSONException Only if there is a coding error.
     */
    fun join(separator: String): String {
        val stringer = JSONStringer()
        stringer.open(JSONStringer.Scope.NULL, "")
        var i = 0
        val size = values.size
        while (i < size) {
            if (i > 0) {
                stringer.out.append(separator)
            }
            stringer.value(values[i])
            i++
        }
        stringer.close(JSONStringer.Scope.NULL, JSONStringer.Scope.NULL, "")
        return stringer.out.toString()
    }

    /**
     * Encodes this array as a compact JSON string, such as: `[94043,90210]`
     *
     * 元実装は JSONException 時に null を返していたが、Kotlin では toString() を
     * nullable に出来ないため空文字列を返す（実際には到達しない）。
     */
    override fun toString(): String {
        return try {
            val stringer = JSONStringer()
            writeTo(stringer)
            stringer.toString()
        } catch (e: JSONException) {
            ""
        }
    }

    /**
     * Encodes this array as a human readable JSON string for debugging.
     *
     * @param indentSpaces the number of spaces to indent for each level of
     *                     nesting.
     * @throws JSONException Only if there is a coding error.
     */
    fun toString(indentSpaces: Int): String {
        val stringer = JSONStringer(indentSpaces)
        writeTo(stringer)
        return stringer.toString()
    }

    internal fun writeTo(stringer: JSONStringer) {
        stringer.array()
        for (value in values) {
            stringer.value(value)
        }
        stringer.endArray()
    }

    override fun equals(other: Any?): Boolean {
        return other is JSONArray && other.values == values
    }

    override fun hashCode(): Int {
        // diverge from the original, which doesn't implement hashCode
        return values.hashCode()
    }
}
