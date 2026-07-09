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
 * A modifiable set of name/value mappings. Names are unique, non-null strings.
 * Values may be any mix of [JSONObject]s, [JSONArray]s, Strings, Booleans,
 * Integers, Longs, Doubles or [NULL]. Values may not be `null`, NaNs,
 * infinities, or of any type not listed here.
 *
 * This class can coerce values to another type when requested, and can look up
 * both mandatory (`getType()`) and optional (`optType()`) values.
 *
 * **Warning:** this class represents null in two incompatible ways: the standard
 * Kotlin `null` reference, and the sentinel value [NULL].
 *
 * Instances of this class are not thread safe.
 */
class JSONObject {

    // KMP対応: java.util.LinkedHashMap → kotlin.collections.LinkedHashMap（共通型）。
    // 挿入順を保持する点は元と同じ。値は wrap() 失敗時に null が入りうるため Any? とする。
    private val nameValuePairs: LinkedHashMap<String, Any?>

    /**
     * Creates a `JSONObject` with no name/value mappings.
     */
    constructor() {
        nameValuePairs = LinkedHashMap()
    }

    /**
     * Creates a new `JSONObject` by copying all name/value mappings from
     * the given map.
     *
     * @param copyFrom a map whose keys are of type String and whose
     *                 values are of supported types.
     * @throws NullPointerException if any of the map's keys are null.
     */
    constructor(copyFrom: Map<*, *>) : this() {
        for (entry in copyFrom.entries) {
            /*
             * Deviate from the original by checking that keys are non-null and
             * of the proper type. (We still defer validating the values).
             */
            val rawKey = entry.key ?: throw NullPointerException("key == null")
            val key = rawKey as String
            nameValuePairs[key] = wrap(entry.value)
        }
    }

    /**
     * Creates a new `JSONObject` with name/value mappings from the next
     * object in the tokener.
     *
     * @param readFrom a tokener whose nextValue() method will yield a
     *                 `JSONObject`.
     * @throws JSONException if the parse fails or doesn't yield a `JSONObject`.
     */
    constructor(readFrom: JSONTokener) {
        /*
         * Getting the parser to populate this could get tricky. Instead, just
         * parse to temporary JSONObject and then steal the data from that.
         */
        val obj = readFrom.nextValue()
        nameValuePairs = if (obj is JSONObject) {
            obj.nameValuePairs
        } else {
            throw JSON.typeMismatch(obj, "JSONObject")
        }
    }

    /**
     * Creates a new `JSONObject` with name/value mappings from the JSON string.
     *
     * @param json a JSON-encoded string containing an object.
     * @throws JSONException if the parse fails or doesn't yield a `JSONObject`.
     */
    constructor(json: String) : this(JSONTokener(json))

    /**
     * Creates a new `JSONObject` by copying mappings for the listed names
     * from the given object. Names that aren't present in `copyFrom` will
     * be skipped.
     *
     * @param copyFrom The source object.
     * @param names    The names of the fields to copy.
     * @throws JSONException On internal errors. Shouldn't happen.
     */
    constructor(copyFrom: JSONObject, names: Array<String>) : this() {
        for (name in names) {
            val value = copyFrom.opt(name)
            if (value != null) {
                nameValuePairs[name] = value
            }
        }
    }

    /**
     * Returns the number of name/value mappings in this object.
     */
    fun length(): Int {
        return nameValuePairs.size
    }

    /**
     * Maps `name` to `value`, clobbering any existing name/value mapping with
     * the same name.
     */
    fun put(name: String, value: Boolean): JSONObject {
        nameValuePairs[name] = value
        return this
    }

    /**
     * Maps `name` to `value`, clobbering any existing name/value mapping with
     * the same name.
     *
     * @param value a finite value. May not be NaNs or infinities.
     * @throws JSONException if value is NaN or infinite.
     */
    fun put(name: String, value: Double): JSONObject {
        nameValuePairs[name] = JSON.checkDouble(value)
        return this
    }

    /**
     * Maps `name` to `value`, clobbering any existing name/value mapping with
     * the same name.
     */
    fun put(name: String, value: Int): JSONObject {
        nameValuePairs[name] = value
        return this
    }

    /**
     * Maps `name` to `value`, clobbering any existing name/value mapping with
     * the same name.
     */
    fun put(name: String, value: Long): JSONObject {
        nameValuePairs[name] = value
        return this
    }

    /**
     * Maps `name` to `value`, clobbering any existing name/value mapping with
     * the same name. If the value is `null`, any existing mapping for `name` is
     * removed.
     *
     * @param value a [JSONObject], [JSONArray], String, Boolean, Integer, Long,
     *              Double, [NULL], or `null`. May not be NaNs or infinities.
     * @throws JSONException if the value is an invalid double (infinite or NaN).
     */
    fun put(name: String, value: Any?): JSONObject {
        if (value == null) {
            nameValuePairs.remove(name)
            return this
        }
        if (value is Number) {
            // deviate from the original by checking all Numbers, not just floats & doubles
            JSON.checkDouble(value.toDouble())
        }
        nameValuePairs[name] = value
        return this
    }

    /**
     * Equivalent to `put(name, value)` when both parameters are non-null;
     * does nothing otherwise.
     *
     * @throws JSONException if the value is an invalid double (infinite or NaN).
     */
    fun putOpt(name: String?, value: Any?): JSONObject {
        if (name == null || value == null) {
            return this
        }
        return put(name, value)
    }

    /**
     * Appends `value` to the array already mapped to `name`. If this object has
     * no mapping for `name`, this inserts a new mapping. If the mapping exists
     * but its value is not an array, the existing and new values are inserted in
     * order into a new array which is itself mapped to `name`.
     *
     * @throws JSONException If the object being added is an invalid number.
     */
    fun accumulate(name: String, value: Any?): JSONObject {
        val current = nameValuePairs[name]
        if (current == null) {
            return put(name, value)
        }

        if (current is JSONArray) {
            current.checkedPut(value)
        } else {
            val array = JSONArray()
            array.checkedPut(current)
            array.checkedPut(value)
            nameValuePairs[name] = array
        }
        return this
    }

    /**
     * Appends values to the array mapped to `name`. A new [JSONArray] mapping
     * for `name` will be inserted if no mapping exists. If the existing mapping
     * for `name` is not a [JSONArray], a [JSONException] will be thrown.
     *
     * @throws JSONException if the mapping for `name` is non-null and is not a
     *                       [JSONArray].
     */
    fun append(name: String, value: Any?): JSONObject {
        val current = nameValuePairs[name]

        val array: JSONArray
        if (current is JSONArray) {
            array = current
        } else if (current == null) {
            val newArray = JSONArray()
            nameValuePairs[name] = newArray
            array = newArray
        } else {
            throw JSONException("Key $name is not a JSONArray")
        }

        array.checkedPut(value)

        return this
    }

    /**
     * Removes the named mapping if it exists; does nothing otherwise.
     *
     * @return the value previously mapped by `name`, or null if there was
     * no such mapping.
     */
    fun remove(name: String?): Any? {
        return if (name == null) null else nameValuePairs.remove(name)
    }

    /**
     * Returns true if this object has no mapping for `name` or if it has a
     * mapping whose value is [NULL].
     */
    fun isNull(name: String): Boolean {
        val value = nameValuePairs[name]
        return value == null || value === NULL
    }

    /**
     * Returns true if this object has a mapping for `name`. The mapping may be
     * [NULL].
     */
    fun has(name: String): Boolean {
        return nameValuePairs.containsKey(name)
    }

    /**
     * Returns the value mapped by `name`, or throws if no such mapping exists.
     *
     * @throws JSONException if no such mapping exists.
     */
    fun get(name: String): Any {
        return nameValuePairs[name] ?: throw JSONException("No value for $name")
    }

    /**
     * Returns the value mapped by `name`, or null if no such mapping exists.
     */
    fun opt(name: String?): Any? {
        return if (name == null) null else nameValuePairs[name]
    }

    /**
     * Returns the value mapped by `name` if it exists and is a boolean or can be
     * coerced to a boolean, or throws otherwise.
     *
     * @throws JSONException if the mapping doesn't exist or cannot be coerced to
     *                       a boolean.
     */
    fun getBoolean(name: String): Boolean {
        val obj = get(name)
        return JSON.toBoolean(obj) ?: throw JSON.typeMismatch(name, obj, "boolean")
    }

    /**
     * Returns the value mapped by `name` if it exists and is a boolean or can be
     * coerced to a boolean, or `fallback` otherwise.
     */
    @JvmOverloads
    fun optBoolean(name: String, fallback: Boolean = false): Boolean {
        val obj = opt(name)
        val result = JSON.toBoolean(obj)
        return result ?: fallback
    }

    /**
     * Returns the value mapped by `name` if it exists and is a double or can be
     * coerced to a double, or throws otherwise.
     *
     * @throws JSONException if the mapping doesn't exist or cannot be coerced to
     *                       a double.
     */
    fun getDouble(name: String): Double {
        val obj = get(name)
        return JSON.toDouble(obj) ?: throw JSON.typeMismatch(name, obj, "double")
    }

    /**
     * Returns the value mapped by `name` if it exists and is a double or can be
     * coerced to a double, or `fallback` otherwise.
     */
    @JvmOverloads
    fun optDouble(name: String, fallback: Double = Double.NaN): Double {
        val obj = opt(name)
        val result = JSON.toDouble(obj)
        return result ?: fallback
    }

    /**
     * Returns the value mapped by `name` if it exists and is an int or can be
     * coerced to an int, or throws otherwise.
     *
     * @throws JSONException if the mapping doesn't exist or cannot be coerced to
     *                       an int.
     */
    fun getInt(name: String): Int {
        val obj = get(name)
        return JSON.toInteger(obj) ?: throw JSON.typeMismatch(name, obj, "int")
    }

    /**
     * Returns the value mapped by `name` if it exists and is an int or can be
     * coerced to an int, or `fallback` otherwise.
     */
    @JvmOverloads
    fun optInt(name: String, fallback: Int = 0): Int {
        val obj = opt(name)
        val result = JSON.toInteger(obj)
        return result ?: fallback
    }

    /**
     * Returns the value mapped by `name` if it exists and is a long or can be
     * coerced to a long, or throws otherwise.
     *
     * @throws JSONException if the mapping doesn't exist or cannot be coerced to
     *                       a long.
     */
    fun getLong(name: String): Long {
        val obj = get(name)
        return JSON.toLong(obj) ?: throw JSON.typeMismatch(name, obj, "long")
    }

    /**
     * Returns the value mapped by `name` if it exists and is a long or can be
     * coerced to a long, or `fallback` otherwise.
     */
    @JvmOverloads
    fun optLong(name: String, fallback: Long = 0L): Long {
        val obj = opt(name)
        val result = JSON.toLong(obj)
        return result ?: fallback
    }

    /**
     * Returns the value mapped by `name` if it exists, coercing it if necessary,
     * or throws if no such mapping exists.
     *
     * @throws JSONException if no such mapping exists.
     */
    fun getString(name: String): String {
        val obj = get(name)
        return JSON.toString(obj) ?: throw JSON.typeMismatch(name, obj, "String")
    }

    /**
     * Returns the value mapped by `name` if it exists, coercing it if necessary,
     * or the empty string if no such mapping exists.
     */
    fun optString(name: String): String {
        val obj = opt(name)
        return JSON.toString(obj) ?: ""
    }

    /**
     * Returns the value mapped by `name` if it exists, coercing it if necessary,
     * or `fallback` if no such mapping exists.
     *
     * `fallback` は null 許容（元 org.json 同様）。値が無く fallback が null の場合は
     * null を返すため、戻り値も null 許容とする。
     *
     * 【互換性メモ】Java の `optString(String, String)` は本来プラットフォーム型で
     * 「非 null fallback → 非 null 戻り」「null fallback → null 戻り」の両方を許していた。
     * Kotlin では 1 シグネチャで両立できないため、fallback の null 許容性で 2 つの
     * オーバーロードに分ける。JVM シグネチャ衝突を避けるため非 null 版に @JvmName を付与。
     * - Java からの呼び出しはすべてこちら（JVM 名 `optString`）に束縛される（従来通り）。
     * - Kotlin からは fallback が非 null リテラルなら下の非 null 版が選択される。
     */
    fun optString(name: String, fallback: String?): String? {
        val obj = opt(name)
        return JSON.toString(obj) ?: fallback
    }

    /**
     * [optString] の非 null fallback 版。Kotlin から非 null の fallback を渡した場合に
     * 非 null の String を返す（呼び出し側の非 null 期待を満たすため）。
     */
    @JvmName("optStringNonNull")
    fun optString(name: String, fallback: String): String {
        val obj = opt(name)
        return JSON.toString(obj) ?: fallback
    }

    /**
     * Returns the value mapped by `name` if it exists and is a [JSONArray], or
     * throws otherwise.
     *
     * @throws JSONException if the mapping doesn't exist or is not a [JSONArray].
     */
    fun getJSONArray(name: String): JSONArray {
        val obj = get(name)
        return obj as? JSONArray ?: throw JSON.typeMismatch(name, obj, "JSONArray")
    }

    /**
     * Returns the value mapped by `name` if it exists and is a [JSONArray], or
     * null otherwise.
     */
    fun optJSONArray(name: String): JSONArray? {
        return opt(name) as? JSONArray
    }

    /**
     * Returns the value mapped by `name` if it exists and is a [JSONObject], or
     * throws otherwise.
     *
     * @throws JSONException if the mapping doesn't exist or is not a
     *                       [JSONObject].
     */
    fun getJSONObject(name: String): JSONObject {
        val obj = get(name)
        return obj as? JSONObject ?: throw JSON.typeMismatch(name, obj, "JSONObject")
    }

    /**
     * Returns the value mapped by `name` if it exists and is a [JSONObject], or
     * null otherwise.
     */
    fun optJSONObject(name: String): JSONObject? {
        return opt(name) as? JSONObject
    }

    /**
     * Returns an array with the values corresponding to `names`. The array
     * contains null for names that aren't mapped. This method returns null if
     * `names` is either null or empty.
     *
     * @throws JSONException On internal errors. Shouldn't happen.
     */
    fun toJSONArray(names: JSONArray?): JSONArray? {
        val result = JSONArray()
        if (names == null) {
            return null
        }
        val length = names.length()
        if (length == 0) {
            return null
        }
        for (i in 0 until length) {
            val name = JSON.toString(names.opt(i))
            result.put(opt(name))
        }
        return result
    }

    /**
     * Returns an iterator of the String names in this object.
     */
    fun keys(): MutableIterator<String> {
        return nameValuePairs.keys.iterator()
    }

    /**
     * Returns the set of String names in this object.
     */
    fun keySet(): MutableSet<String> {
        return nameValuePairs.keys
    }

    /**
     * Returns an array containing the string names in this object. This method
     * returns null if this object contains no mappings.
     */
    fun names(): JSONArray? {
        return if (nameValuePairs.isEmpty()) {
            null
        } else {
            JSONArray(ArrayList(nameValuePairs.keys))
        }
    }

    /**
     * Encodes this object as a compact JSON string, such as:
     * `{"query":"Pizza","locations":[94043,90210]}`
     *
     * 元実装は内部で JSONException が発生した場合 null を返していたが、Kotlin では
     * toString() を nullable に出来ないため空文字列を返す（object() を必ず書き出すため
     * 実際には到達しない）。
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
     * Encodes this object as a human readable JSON string for debugging.
     *
     * @param indentSpaces the number of spaces to indent for each level of
     *                     nesting.
     * @throws JSONException On internal errors. Shouldn't happen.
     */
    fun toString(indentSpaces: Int): String {
        val stringer = JSONStringer(indentSpaces)
        writeTo(stringer)
        return stringer.toString()
    }

    internal fun writeTo(stringer: JSONStringer) {
        stringer.`object`()
        for ((key, value) in nameValuePairs) {
            stringer.key(key).value(value)
        }
        stringer.endObject()
    }

    companion object {

        private val NEGATIVE_ZERO = -0.0

        /**
         * A sentinel value used to explicitly define a name with no value.
         *
         * This value violates the general contract of [Any.equals] by returning
         * true when compared to `null`. Its [toString] method returns "null".
         */
        @JvmField
        val NULL: Any = object {
            override fun equals(other: Any?): Boolean {
                return other === this || other == null // API specifies this broken equals implementation
            }

            // at least make the broken equals(null) consistent with a null hashCode.
            override fun hashCode(): Int {
                return 0
            }

            override fun toString(): String {
                return "null"
            }
        }

        /**
         * Encodes the number as a JSON string.
         *
         * @param number a finite value. May not be NaNs or infinities.
         * @throws JSONException On internal errors. Shouldn't happen.
         */
        @JvmStatic
        fun numberToString(number: Number?): String {
            if (number == null) {
                throw JSONException("Number must be non-null")
            }

            val doubleValue = number.toDouble()
            JSON.checkDouble(doubleValue)

            // the original returns "-0" instead of "-0.0" for negative zero
            if (number == NEGATIVE_ZERO) {
                return "-0"
            }

            val longValue = number.toLong()
            if (doubleValue == longValue.toDouble()) {
                return longValue.toString()
            }

            return number.toString()
        }

        /**
         * Encodes `data` as a JSON string. This applies quotes and any necessary
         * character escaping.
         *
         * @param data the string to encode. Null will be interpreted as an empty
         *             string.
         * @return the quoted string.
         */
        @JvmStatic
        fun quote(data: String?): String {
            if (data == null) {
                return "\"\""
            }
            return try {
                val stringer = JSONStringer()
                stringer.open(JSONStringer.Scope.NULL, "")
                stringer.value(data)
                stringer.close(JSONStringer.Scope.NULL, JSONStringer.Scope.NULL, "")
                stringer.toString()
            } catch (e: JSONException) {
                throw AssertionError()
            }
        }

        /**
         * Wraps the given object if necessary.
         *
         * If the object is null, returns [NULL].
         * If the object is a [JSONArray] or [JSONObject], no wrapping is necessary.
         * If the object is [NULL], no wrapping is necessary.
         * If the object is an array or [Collection], returns an equivalent [JSONArray].
         * If the object is a [Map], returns an equivalent [JSONObject].
         * If the object is a primitive wrapper type or String, returns the object.
         * Otherwise returns null.
         *
         * KMP対応: 元実装は `getClass().isArray()` や `getClass().getPackage()` の
         * リフレクションを使っていたが、共通型判定（`is IntArray` 等）へ置き換えた。
         * java.* パッケージ由来オブジェクトの toString フォールバックは削除した
         * （リポジトリ内・TwitPane からの参照はいずれも無いことを確認済み）。
         */
        @JvmStatic
        fun wrap(o: Any?): Any? {
            if (o == null) {
                return NULL
            }
            if (o is JSONArray || o is JSONObject) {
                return o
            }
            if (o == NULL) {
                return o
            }
            try {
                return when (o) {
                    is Collection<*> -> JSONArray(o)
                    is Map<*, *> -> JSONObject(o)
                    is BooleanArray, is ByteArray, is CharArray, is ShortArray,
                    is IntArray, is LongArray, is FloatArray, is DoubleArray,
                    is Array<*> -> JSONArray(o)
                    is Boolean, is Byte, is Char, is Double, is Float,
                    is Int, is Long, is Short, is String -> o
                    else -> null
                }
            } catch (ignored: Exception) {
            }
            return null
        }
    }
}
