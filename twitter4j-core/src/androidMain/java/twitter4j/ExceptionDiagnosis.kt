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
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.3
 */
internal class ExceptionDiagnosis : Serializable {
    private var stackLineHash = 0
    private var lineNumberHash = 0
    private var hexString = ""

    constructor(th: Throwable) : this(th, arrayOf())

    constructor(th: Throwable, inclusionFilter: Array<String>) {
        val stackTrace = th.stackTrace
        stackLineHash = 0
        lineNumberHash = 0
        for (i in stackTrace.indices.reversed()) {
            val line = stackTrace[i]
            for (filter in inclusionFilter) {
                if (line.className.startsWith(filter)) {
                    val hash = line.className.hashCode() + line.methodName.hashCode()
                    stackLineHash = 31 * stackLineHash + hash
                    lineNumberHash = 31 * lineNumberHash + line.lineNumber
                    break
                }
            }
        }
        hexString += toHexString(stackLineHash) + "-" + toHexString(lineNumberHash)
        if (th.cause != null) {
            hexString += " " + ExceptionDiagnosis(th.cause!!, inclusionFilter).asHexString()
        }
    }

    fun getStackLineHash(): Int {
        return stackLineHash
    }

    fun getStackLineHashAsHex(): String {
        return toHexString(stackLineHash)
    }

    fun getLineNumberHash(): Int {
        return lineNumberHash
    }

    fun getLineNumberHashAsHex(): String {
        return toHexString(lineNumberHash)
    }

    fun asHexString(): String {
        return hexString
    }

    private fun toHexString(value: Int): String {
        val str = "0000000" + Integer.toHexString(value)
        return str.substring(str.length - 8, str.length)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as ExceptionDiagnosis

        if (lineNumberHash != that.lineNumberHash) return false
        if (stackLineHash != that.stackLineHash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stackLineHash
        result = 31 * result + lineNumberHash
        return result
    }

    override fun toString(): String {
        return "ExceptionDiagnosis{" +
                "stackLineHash=" + stackLineHash +
                ", lineNumberHash=" + lineNumberHash +
                '}'
    }

    companion object {
        private const val serialVersionUID = 8501009773274399369L
    }
}
