/*
 * Copyright (C) 2007 Yusuke Yamamoto
 * Copyright (C) 2011 Twitter, Inc.
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
 * `geo` / `coordinates` 要素を [GeoLocation] へパースするヘルパ群。
 *
 * KMP移行 Phase 5-3 での分離理由:
 * - これらは元々 `JSONImplFactory` の companion（`@JvmStatic`）に置かれていたが、[StatusJSONImpl] /
 *   [PlaceJSONImpl]（commonMain へ移動）から参照される一方、`JSONImplFactory` 自身は `Query` / `QueryResult`
 *   （HttpParameter 依存で Phase 4-3/4-4 まで androidMain 残置）に依存するため commonMain へ移動できない。
 * - そこで、パース層が必要とする GeoLocation 生成ヘルパのみを本 commonMain オブジェクトへ切り出した。
 * - いずれも `internal`（旧 `JSONImplFactory` は `internal class`）であり、TwitPane 等の外部からは参照されない。
 *   パース挙動は旧実装と完全に同一。
 */
internal object GeoLocationJSONImplFactory {

    /**
     * "geo" 要素が見つかれば [GeoLocation] を返す。
     *
     * @param json パース対象の [JSONObject]
     * @return [GeoLocation]（coordinates が無ければ null）
     * @throws TwitterException coordinates が geo 要素に含まれない場合（API 側の問題）
     */
    @Throws(TwitterException::class)
    fun createGeoLocation(json: JSONObject): GeoLocation? {
        try {
            if (!json.isNull("coordinates")) {
                var coordinates = json.getJSONObject("coordinates")
                        .getString("coordinates")
                coordinates = coordinates.substring(1, coordinates.length - 1)
                val point = coordinates.split(",".toRegex()).toTypedArray()
                return GeoLocation(point[1].toDouble(), point[0].toDouble())
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
        return null
    }

    @Throws(TwitterException::class)
    fun coordinatesAsGeoLocationArray(coordinates: JSONArray): Array<Array<GeoLocation>> {
        try {
            return Array(coordinates.length()) { i ->
                val array = coordinates.getJSONArray(i)
                Array(array.length()) { j ->
                    val coordinate = array.getJSONArray(j)
                    GeoLocation(coordinate.getDouble(1), coordinate.getDouble(0))
                }
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }
}
