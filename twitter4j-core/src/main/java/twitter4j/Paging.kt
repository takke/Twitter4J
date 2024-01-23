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

import java.io.Serializable

/**
 * Controls pagination.<br></br>
 * It is possible to use the same Paging instance in a multi-threaded
 * context only if the instance is treated immutably.<br></br>
 * But basically instance of this class is NOT thread safe.
 * A client should instantiate Paging class per thread.<br></br>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class Paging : Serializable {
    private var _page = -1
    var page: Int
        get() = _page
        set(value) {
            require(value >= 1) { "page should be positive integer. passed:$_page" }
            _page = value
        }

    private var _count: Int = -1
    var count: Int
        get() = _count
        set(value) {
            require(value >= 1) { "count should be positive integer. passed:$_count" }

            _count = value
        }

    private var _sinceId: Long = -1
    var sinceId: Long
        get() = _sinceId
        set(value) {
            require(value >= 1) { "since_id should be positive integer. passed:$_sinceId" }

            _sinceId = value
        }

    private var _maxId: Long = -1
    var maxId: Long
        get() = _maxId
        set(value) {
            require(value >= 1) { "max_id should be positive integer. passed:$_maxId" }

            _maxId = value
        }

    /*package*/
    fun asPostParameterArray(): Array<HttpParameter?> {
        val list = asPostParameterList(SMCP, COUNT)
        return if (list.size == 0) {
            NULL_PARAMETER_ARRAY
        } else list.toTypedArray<HttpParameter?>()
    }

    /**
     * Converts the pagination parameters into a List of PostParameter.<br></br>
     * This method also Validates the preset parameters, and throws
     * IllegalStateException if any unsupported parameter is set.
     *
     * @param supportedParams  char array representation of supported parameters
     * @param perPageParamName name used for per-page parameter. getUserListStatuses() requires "per_page" instead of "count".
     * @return list of PostParameter
     */
    /*package*/ /*package*/ /*package*/
    @JvmOverloads
    fun asPostParameterList(supportedParams: CharArray = SMCP, perPageParamName: String = COUNT): List<HttpParameter?> {
        val pagingParams: MutableList<HttpParameter?> = ArrayList(supportedParams.size)
        addPostParameter(supportedParams, 's', pagingParams, "since_id", sinceId)
        addPostParameter(supportedParams, 'm', pagingParams, "max_id", maxId)
        addPostParameter(supportedParams, 'c', pagingParams, perPageParamName, count.toLong())
        addPostParameter(supportedParams, 'p', pagingParams, "page", page.toLong())
        return if (pagingParams.size == 0) {
            NULL_PARAMETER_LIST
        } else {
            pagingParams
        }
    }

    /**
     * Converts the pagination parameters into a List of PostParameter.<br></br>
     * This method also Validates the preset parameters, and throws
     * IllegalStateException if any unsupported parameter is set.
     *
     * @param supportedParams  char array representation of supported parameters
     * @param perPageParamName name used for per-page parameter. getUserListStatuses() requires "per_page" instead of "count".
     * @return list of PostParameter
     */
    /*package*/
    fun asPostParameterArray(supportedParams: CharArray, perPageParamName: String): Array<HttpParameter?> {
        val pagingParams: MutableList<HttpParameter?> = ArrayList(supportedParams.size)
        addPostParameter(supportedParams, 's', pagingParams, "since_id", sinceId)
        addPostParameter(supportedParams, 'm', pagingParams, "max_id", maxId)
        addPostParameter(supportedParams, 'c', pagingParams, perPageParamName, count.toLong())
        addPostParameter(supportedParams, 'p', pagingParams, "page", page.toLong())
        return if (pagingParams.size == 0) {
            NULL_PARAMETER_ARRAY
        } else {
            pagingParams.toTypedArray<HttpParameter?>()
        }
    }

    private fun addPostParameter(
        supportedParams: CharArray, paramKey: Char, pagingParams: MutableList<HttpParameter?>, paramName: String, paramValue: Long
    ) {
        var supported = false
        for (supportedParam in supportedParams) {
            if (supportedParam == paramKey) {
                supported = true
                break
            }
        }
        check(!(!supported && -1L != paramValue)) {
            ("Paging parameter [" + paramName
                    + "] is not supported with this operation.")
        }
        if (-1L != paramValue) {
            pagingParams.add(HttpParameter(paramName, paramValue.toString()))
        }
    }

    constructor()
    constructor(page: Int) {
        this.page = page
    }

    constructor(sinceId: Long) {
        this.sinceId = sinceId
    }

    constructor(page: Int, count: Int) : this(page) {
        this.count = count
    }

    constructor(page: Int, sinceId: Long) : this(page) {
        this.sinceId = sinceId
    }

    constructor(page: Int, count: Int, sinceId: Long) : this(page, count) {
        this.sinceId = sinceId
    }

    constructor(page: Int, count: Int, sinceId: Long, maxId: Long) : this(page, count, sinceId) {
        this.maxId = maxId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Paging) return false
        val paging = other
        if (count != paging.count) return false
        if (maxId != paging.maxId) return false
        if (page != paging.page) return false
        return if (sinceId != paging.sinceId) false else true
    }

    override fun hashCode(): Int {
        var result = page
        result = 31 * result + count
        result = 31 * result + (sinceId xor (sinceId ushr 32)).toInt()
        result = 31 * result + (maxId xor (maxId ushr 32)).toInt()
        return result
    }

    override fun toString(): String {
        return "Paging{" +
                "page=" + page +
                ", count=" + count +
                ", sinceId=" + sinceId +
                ", maxId=" + maxId +
                '}'
    }

    companion object {
        private const val serialVersionUID = -7226113618341047983L

        // since only
        @JvmField
        val S = charArrayOf('s')

        // since, max_id, count, page
        @JvmField
        val SMCP = charArrayOf('s', 'm', 'c', 'p')
        const val COUNT = "count"

        // somewhat GET list statuses requires "per_page" instead of "count"
        // @see <a href="https://dev.twitter.com/docs/api/1.1/get/:user/lists/:id/statuses">GET :user/lists/:id/statuses | Twitter Developers</a>
        const val PER_PAGE = "per_page"

        private val NULL_PARAMETER_ARRAY = arrayOfNulls<HttpParameter>(0)
        private val NULL_PARAMETER_LIST: List<HttpParameter?> = ArrayList(0)
    }
}
