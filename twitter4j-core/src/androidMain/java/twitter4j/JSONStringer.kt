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
 * Implements [JSONObject.toString] and [JSONArray.toString]. Most
 * application developers should use those methods directly and disregard this
 * API. For example:
 * ```
 * JSONObject object = ...
 * String json = object.toString();
 * ```
 *
 * Stringers only encode well-formed JSON strings. Calls that would result in a
 * malformed JSON string will fail with a [JSONException].
 *
 * This class provides no facility for pretty-printing (ie. indenting)
 * output. To encode indented output, use [JSONObject.toString] or
 * [JSONArray.toString] with an indent argument.
 *
 * Each stringer may be used to encode a single top level value. Instances of
 * this class are not thread safe.
 */
class JSONStringer {

    /**
     * The output data, containing at most one top-level array or object.
     */
    // 元は package-private フィールド。JSONArray.join() から参照されるため internal。
    internal val out = StringBuilder()

    /**
     * Lexical scoping elements within this stringer, necessary to insert the
     * appropriate separator characters (ie. commas and colons) and to detect
     * nesting errors.
     */
    internal enum class Scope {

        /**
         * An array with no elements requires no separators or newlines before
         * it is closed.
         */
        EMPTY_ARRAY,

        /**
         * A array with at least one value requires a comma and newline before
         * the next element.
         */
        NONEMPTY_ARRAY,

        /**
         * An object with no keys or values requires no separators or newlines
         * before it is closed.
         */
        EMPTY_OBJECT,

        /**
         * An object whose most recent element is a key. The next element must
         * be a value.
         */
        DANGLING_KEY,

        /**
         * An object with at least one name/value pair requires a comma and
         * newline before the next element.
         */
        NONEMPTY_OBJECT,

        /**
         * A special bracketless array needed by JSONStringer.join() and
         * JSONObject.quote() only. Not used for JSON encoding.
         */
        NULL,
    }

    /**
     * Unlike the original implementation, this stack isn't limited to 20
     * levels of nesting.
     */
    private val stack = ArrayList<Scope>()

    /**
     * A string containing a full set of spaces for a single level of
     * indentation, or null for no pretty printing.
     */
    private val indent: String?

    constructor() {
        indent = null
    }

    internal constructor(indentSpaces: Int) {
        indent = " ".repeat(indentSpaces)
    }

    /**
     * Begins encoding a new array. Each call to this method must be paired with
     * a call to [endArray].
     */
    fun array(): JSONStringer {
        return open(Scope.EMPTY_ARRAY, "[")
    }

    /**
     * Ends encoding the current array.
     */
    fun endArray(): JSONStringer {
        return close(Scope.EMPTY_ARRAY, Scope.NONEMPTY_ARRAY, "]")
    }

    /**
     * Begins encoding a new object. Each call to this method must be paired
     * with a call to [endObject].
     */
    fun `object`(): JSONStringer {
        return open(Scope.EMPTY_OBJECT, "{")
    }

    /**
     * Ends encoding the current object.
     */
    fun endObject(): JSONStringer {
        return close(Scope.EMPTY_OBJECT, Scope.NONEMPTY_OBJECT, "}")
    }

    /**
     * Enters a new scope by appending any necessary whitespace and the given
     * bracket.
     */
    internal fun open(empty: Scope, openBracket: String): JSONStringer {
        if (stack.isEmpty() && out.isNotEmpty()) {
            throw JSONException("Nesting problem: multiple top-level roots")
        }
        beforeValue()
        stack.add(empty)
        out.append(openBracket)
        return this
    }

    /**
     * Closes the current scope by appending any necessary whitespace and the
     * given bracket.
     */
    internal fun close(empty: Scope, nonempty: Scope, closeBracket: String): JSONStringer {
        val context = peek()
        if (context != nonempty && context != empty) {
            throw JSONException("Nesting problem")
        }

        stack.removeAt(stack.size - 1)
        if (context == nonempty) {
            newline()
        }
        out.append(closeBracket)
        return this
    }

    /**
     * Returns the value on the top of the stack.
     */
    private fun peek(): Scope {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        return stack[stack.size - 1]
    }

    /**
     * Replace the value on the top of the stack with the given value.
     */
    private fun replaceTop(topOfStack: Scope) {
        stack[stack.size - 1] = topOfStack
    }

    /**
     * Encodes `value`.
     *
     * @param value a [JSONObject], [JSONArray], String, Boolean,
     *              Integer, Long, Double or null. May not be NaNs or infinities.
     * @return this stringer.
     */
    fun value(value: Any?): JSONStringer {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }

        if (value is JSONArray) {
            value.writeTo(this)
            return this
        } else if (value is JSONObject) {
            value.writeTo(this)
            return this
        }

        beforeValue()

        if (value == null
            || value is Boolean
            || value === JSONObject.NULL
        ) {
            out.append(value)
        } else if (value is Number) {
            out.append(JSONObject.numberToString(value))
        } else {
            string(value.toString())
        }

        return this
    }

    /**
     * Encodes `value` to this stringer.
     */
    fun value(value: Boolean): JSONStringer {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        beforeValue()
        out.append(value)
        return this
    }

    /**
     * Encodes `value` to this stringer.
     *
     * @param value a finite value. May not be NaNs or infinities.
     */
    fun value(value: Double): JSONStringer {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        beforeValue()
        out.append(JSONObject.numberToString(value))
        return this
    }

    /**
     * Encodes `value` to this stringer.
     */
    fun value(value: Long): JSONStringer {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        beforeValue()
        out.append(value)
        return this
    }

    private fun string(value: String) {
        out.append("\"")
        var i = 0
        val length = value.length
        while (i < length) {
            val c = value[i]

            /*
             * From RFC 4627, "All Unicode characters may be placed within the
             * quotation marks except for the characters that must be escaped:
             * quotation mark, reverse solidus, and the control characters
             * (U+0000 through U+001F)."
             */
            when (c) {
                '"', '\\', '/' -> out.append('\\').append(c)

                '\t' -> out.append("\\t")

                '\b' -> out.append("\\b")

                '\n' -> out.append("\\n")

                '\r' -> out.append("\\r")

                '\u000C' -> out.append("\\f") // form feed（Kotlin には \f エスケープが無い）

                else -> if (c.code <= 0x1F) {
                    // 元実装の String.format("\\u%04x", (int) c) 相当（小文字4桁hex）
                    out.append("\\u").append(c.code.toString(16).padStart(4, '0'))
                } else {
                    out.append(c)
                }
            }
            i++
        }
        out.append("\"")
    }

    private fun newline() {
        if (indent == null) {
            return
        }

        out.append("\n")
        for (i in 0 until stack.size) {
            out.append(indent)
        }
    }

    /**
     * Encodes the key (property name) to this stringer.
     *
     * @param name the name of the forthcoming value. May not be null.
     * @return this stringer.
     */
    fun key(name: String?): JSONStringer {
        if (name == null) {
            throw JSONException("Names must be non-null")
        }
        beforeKey()
        string(name)
        return this
    }

    /**
     * Inserts any necessary separators and whitespace before a name. Also
     * adjusts the stack to expect the key's value.
     */
    private fun beforeKey() {
        val context = peek()
        if (context == Scope.NONEMPTY_OBJECT) { // first in object
            out.append(',')
        } else if (context != Scope.EMPTY_OBJECT) { // not in an object!
            throw JSONException("Nesting problem")
        }
        newline()
        replaceTop(Scope.DANGLING_KEY)
    }

    /**
     * Inserts any necessary separators and whitespace before a literal value,
     * inline array, or inline object. Also adjusts the stack to expect either a
     * closing bracket or another element.
     */
    private fun beforeValue() {
        if (stack.isEmpty()) {
            return
        }

        val context = peek()
        if (context == Scope.EMPTY_ARRAY) { // first in array
            replaceTop(Scope.NONEMPTY_ARRAY)
            newline()
        } else if (context == Scope.NONEMPTY_ARRAY) { // another in array
            out.append(',')
            newline()
        } else if (context == Scope.DANGLING_KEY) { // value for key
            out.append(if (indent == null) ":" else ": ")
            replaceTop(Scope.NONEMPTY_OBJECT)
        } else if (context != Scope.NULL) {
            throw JSONException("Nesting problem")
        }
    }

    /**
     * Returns the encoded JSON string.
     *
     * If invoked with unterminated arrays or unclosed objects, this method's
     * return value is undefined.
     *
     * 元実装はデータが空のとき null を返していたが、Kotlin では Any.toString() の
     * オーバーライドを nullable に出来ないため、空のときは空文字列 "" を返す。
     * （実際には object()/array()/value() 後にのみ呼ばれ out は空にならないため差異は生じない）
     */
    override fun toString(): String {
        return out.toString()
    }
}
