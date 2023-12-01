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
 * A data class represents search query.<br></br>
 * An instance of this class is NOT thread safe.<br></br>
 * Instances can be shared across threads, but should not be mutated while a search is ongoing.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see [GET search | Twitter Developers](https://dev.twitter.com/docs/api/1.1/get/search)
 *
 * @see [Twitter API / Search Operators](http://search.twitter.com/operators)
 */
class Query : Serializable {

    /**
     * Cursor for some queries
     */
    var cursor: String? = null

    /**
     * the query string
     *
     * @param query the query string
     * @see [GET search | Twitter Developers](https://dev.twitter.com/docs/api/1.1/get/search)
     * @see [Twitter API / Search Operators](http://search.twitter.com/operators)
     */
    @JvmField
    var query: String? = null

    /**
     * restricts tweets to the given language, given by an [ISO 639-1 code](http://en.wikipedia.org/wiki/ISO_639-1)
     *
     * @param lang an [ISO 639-1 code](http://en.wikipedia.org/wiki/ISO_639-1)
     */
    @JvmField
    var lang: String? = null

    /**
     * Specify the language of the query you are sending (only ja is currently effective). This is intended for language-specific clients and the default should work in the majority of cases.
     *
     * @since Twitter4J 2.1.1
     */
    var locale: String? = null

    /**
     * If specified, returns tweets with status ids less than the given id.
     *
     * @since Twitter4J 2.1.1
     */
    @JvmField
    var maxId = -1L

    /**
     * the number of tweets to return per page
     */
    @JvmField
    var count = -1

    /**
     * If specified, returns tweets with since the given date.  Date should be formatted as YYYY-MM-DD
     *
     * @since Twitter4J 2.1.1
     */
    var since: String? = null

    /**
     * returns tweets with status ids greater than the given id.
     */
    @JvmField
    var sinceId: Long = -1

    /**
     * Returns the specified geocode
     *
     * @return geocode
     */
    var geocode: String? = null
        private set

    /**
     * If specified, returns tweets with generated before the given date.  Date should be formatted as YYYY-MM-DD
     *
     * @since Twitter4J 2.1.1
     */
    var until: String? = null

    /**
     * Default value is Query.MIXED if parameter not specified
     *
     * @since Twitter4J 2.1.3
     */
    @JvmField
    var resultType: ResultType? = null

    private var nextPageQuery: String? = null

    constructor()

    constructor(query: String?) {
        this.query = query
    }

    /**
     * Sets the query string
     *
     * @param query the query string
     * @return the instance
     * @see [GET search | Twitter Developers](https://dev.twitter.com/docs/api/1.1/get/search)
     *
     * @see [Twitter API / Search Operators](http://search.twitter.com/operators)
     *
     * @since Twitter4J 2.1.0
     */
    fun query(query: String?): Query {
        this.query = query
        return this
    }

    /**
     * restricts tweets to the given language, given by an [ISO 639-1 code](http://en.wikipedia.org/wiki/ISO_639-1)
     *
     * @param lang an [ISO 639-1 code](http://en.wikipedia.org/wiki/ISO_639-1)
     * @return the instance
     * @since Twitter4J 2.1.0
     */
    fun lang(lang: String?): Query {
        this.lang = lang
        return this
    }

    /**
     * Specify the language of the query you are sending (only ja is currently effective). This is intended for language-specific clients and the default should work in the majority of cases.
     *
     * @param locale the locale
     * @return the instance
     * @since Twitter4J 2.1.1
     */
    fun locale(locale: String?): Query {
        this.locale = locale
        return this
    }

    /**
     * If specified, returns tweets with status ids less than the given id.
     *
     * @param maxId maxId
     * @return this instance
     * @since Twitter4J 2.1.1
     */
    fun maxId(maxId: Long): Query {
        this.maxId = maxId
        return this
    }

    /**
     * sets the number of tweets to return per page, up to a max of 100
     *
     * @param count the number of tweets to return per page
     * @return the instance
     * @since Twitter4J 2.1.0
     */
    fun count(count: Int): Query {
        this.count = count
        return this
    }

    /**
     * If specified, returns tweets with since the given date.  Date should be formatted as YYYY-MM-DD
     *
     * @param since since
     * @return since
     * @since Twitter4J 2.1.1
     */
    fun since(since: String?): Query {
        this.since = since
        return this
    }

    /**
     * returns tweets with status ids greater than the given id.
     *
     * @param sinceId returns tweets with status ids greater than the given id
     * @return the instance
     * @since Twitter4J 2.1.0
     */
    fun sinceId(sinceId: Long): Query {
        this.sinceId = sinceId
        return this
    }

    enum class Unit {
        mi,
        km
    }

    /**
     * returns tweets by users located within a given radius of the given latitude/longitude, where the user's location is taken from their Twitter profile
     *
     * @param location geo location
     * @param radius   radius
     * @param unit     Query.MILES or Query.KILOMETERS
     * @since Twitter4J 4.0.1
     */
    fun setGeoCode(
        location: GeoLocation, radius: Double, unit: Unit
    ) {
        geocode = location.latitude.toString() + "," + location.longitude + "," + radius + unit.name
    }

    /**
     * returns tweets by users located within a given radius of the given latitude/longitude, where the user's location is taken from their Twitter profile
     *
     * @param location geo location
     * @param radius   radius
     * @param unit     Query.MILES or Query.KILOMETERS
     * @return the instance
     * @since Twitter4J 4.0.7
     */
    fun geoCode(
        location: GeoLocation, radius: Double, unit: Unit
    ): Query {
        setGeoCode(location, radius, unit)
        return this
    }

    /**
     * If specified, returns tweets with generated before the given date.  Date should be formatted as YYYY-MM-DD
     *
     * @param until until
     * @return the instance
     * @since Twitter4J 2.1.1
     */
    fun until(until: String?): Query {
        this.until = until
        return this
    }

    enum class ResultType {
        popular,
        mixed,
        recent
    }

    /**
     * If specified, returns tweets included popular or real time or both in the response
     *
     * @param resultType resultType
     * @return the instance
     * @since Twitter4J 2.1.3
     */
    fun resultType(resultType: ResultType?): Query {
        this.resultType = resultType
        return this
    }

    /*package*/
    fun asHttpParameterArray(): Array<HttpParameter?> {
        val params = ArrayList<HttpParameter>(12)
        appendParameter("q", query, params)
        appendParameter("lang", lang, params)
        appendParameter("locale", locale, params)
        appendParameter("max_id", maxId, params)
        appendParameter("count", count.toLong(), params)
        appendParameter("since", since, params)
        appendParameter("since_id", sinceId, params)
        appendParameter("geocode", geocode, params)
        appendParameter("until", until, params)
        if (resultType != null) {
            params.add(HttpParameter("result_type", resultType!!.name))
        }
        params.add(WITH_TWITTER_USER_ID)
        val paramArray = arrayOfNulls<HttpParameter>(params.size)
        return params.toArray(paramArray)
    }

    private fun appendParameter(name: String, value: String?, params: MutableList<HttpParameter>) {
        if (value != null) {
            params.add(HttpParameter(name, value))
        }
    }

    private fun appendParameter(name: String, value: Long, params: MutableList<HttpParameter>) {
        if (0 <= value) {
            params.add(HttpParameter(name, value.toString()))
        }
    }

    /*package*/
    fun nextPage(): String? {
        return nextPageQuery
    }

    // for serialization use cases
    /*package*/
    fun setNextPage(nextPageQuery: String?) {
        this.nextPageQuery = nextPageQuery
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val query1 = o as Query
        if (maxId != query1.maxId) return false
        if (count != query1.count) return false
        if (sinceId != query1.sinceId) return false
        if (if (geocode != null) geocode != query1.geocode else query1.geocode != null) return false
        if (if (lang != null) lang != query1.lang else query1.lang != null) return false
        if (if (locale != null) locale != query1.locale else query1.locale != null) return false
        if (if (nextPageQuery != null) nextPageQuery != query1.nextPageQuery else query1.nextPageQuery != null) return false
        if (if (query != null) query != query1.query else query1.query != null) return false
        if (if (resultType != null) resultType != query1.resultType else query1.resultType != null) return false
        if (if (since != null) since != query1.since else query1.since != null) return false
        return if (if (until != null) until != query1.until else query1.until != null) false else true
    }

    override fun hashCode(): Int {
        var result = if (query != null) query.hashCode() else 0
        result = 31 * result + if (lang != null) lang.hashCode() else 0
        result = 31 * result + if (locale != null) locale.hashCode() else 0
        result = 31 * result + (maxId xor (maxId ushr 32)).toInt()
        result = 31 * result + count
        result = 31 * result + if (since != null) since.hashCode() else 0
        result = 31 * result + (sinceId xor (sinceId ushr 32)).toInt()
        result = 31 * result + if (geocode != null) geocode.hashCode() else 0
        result = 31 * result + if (until != null) until.hashCode() else 0
        result = 31 * result + if (resultType != null) resultType.hashCode() else 0
        result = 31 * result + if (nextPageQuery != null) nextPageQuery.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "Query(cursor=$cursor, query=$query, lang=$lang, locale=$locale, maxId=$maxId, count=$count, since=$since, sinceId=$sinceId, geocode=$geocode, until=$until, resultType=$resultType, nextPageQuery=$nextPageQuery)"
    }

    companion object {
        private const val serialVersionUID = 7196404519192910019L

        /* package */
        @JvmStatic
        fun createWithNextPageQuery(nextPageQuery: String?): Query {
            val query = Query()
            query.nextPageQuery = nextPageQuery
            if (nextPageQuery != null) {
                val nextPageParameters = nextPageQuery.substring(1, nextPageQuery.length)
                val params: MutableMap<String, String> = LinkedHashMap()
                for (param in HttpParameter.decodeParameters(nextPageParameters)) {
                    // Yes, we'll overwrite duplicate parameters, but we should not
                    // get duplicate parameters from this endpoint.
                    params[param.name] = param.value
                }
                if (params.containsKey("q")) query.query = params["q"]
                if (params.containsKey("lang")) query.lang = params["lang"]
                if (params.containsKey("locale")) query.locale = params["locale"]
                if (params.containsKey("max_id")) query.maxId = params["max_id"]!!.toLong()
                if (params.containsKey("count")) query.count = params["count"]!!.toInt()
                if (params.containsKey("geocode")) {
                    val parts = params["geocode"]!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val latitude = parts[0].toDouble()
                    val longitude = parts[1].toDouble()
                    var radius = 0.0
                    var unit: Unit? = null
                    val radiusstr = parts[2]
                    for (value in Unit.entries) if (radiusstr.endsWith(value.name)) {
                        radius = radiusstr.substring(0, radiusstr.length - 2).toDouble()
                        unit = value
                        break
                    }
                    requireNotNull(unit) { "unrecognized geocode radius: $radiusstr" }
                    query.setGeoCode(GeoLocation(latitude, longitude), radius, unit)
                }
                if (params.containsKey("result_type")) query.resultType = ResultType.valueOf(params["result_type"]!!)

                // We don't pull out since, until -- they get pushed into the query
            }
            return query
        }

        val MILES = Unit.mi
        @JvmField
        val KILOMETERS = Unit.km

        /**
         * mixed: Include both popular and real time results in the response.
         */
        val MIXED = ResultType.mixed

        /**
         * popular: return only the most popular results in the response.
         */
        @JvmField
        val POPULAR = ResultType.popular

        /**
         * recent: return only the most recent results in the response
         */
        val RECENT = ResultType.recent
        private val WITH_TWITTER_USER_ID = HttpParameter("with_twitter_user_id", "true")
    }
}
