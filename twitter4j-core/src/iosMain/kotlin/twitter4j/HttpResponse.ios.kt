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
 * [HttpResponse] の iOS(Native) actual 実装（KMP移行 Phase 4-1）。
 *
 * 現段階では文字列ベースの最小実装であり、Phase 4-3 の NSURLSession ベース実装
 * （ヘッダ / ステータスコード / ボディを保持するサブクラス）の基底となる想定である。
 *
 * java.io ストリーム系メンバ（asStream / asReader）は Native には存在しないため持たない。
 * ヘッダ取得系は具象サブクラス側で実装する（abstract のまま）。
 */
actual abstract class HttpResponse {

    protected var statusCode: Int = 0

    protected var responseAsString: String? = null

    actual constructor()

    actual fun getStatusCode(): Int {
        return statusCode
    }

    actual abstract fun getResponseHeader(name: String): String?

    actual abstract fun getResponseHeaderFields(): MutableMap<String, MutableList<String>>?

    private var json: JSONObject? = null

    private var jsonArray: JSONArray? = null

    /**
     * レスポンスボディを文字列として返す。
     * iOS ではサブクラスが [responseAsString] を設定済みである前提の最小実装。
     */
    @Throws(TwitterException::class)
    actual open fun asString(): String {
        return responseAsString ?: ""
    }

    @Throws(TwitterException::class)
    actual open fun asJSONObject(): JSONObject {
        if (json == null) {
            try {
                json = JSONObject(asString())
            } catch (jsone: JSONException) {
                throw TwitterException(jsone.message, jsone)
            }
        }
        return json!!
    }

    @Throws(TwitterException::class)
    actual open fun asJSONArray(): JSONArray {
        if (jsonArray == null) {
            try {
                jsonArray = JSONArray(asString())
            } catch (jsone: JSONException) {
                throw TwitterException(jsone.message, jsone)
            }
        }
        return jsonArray!!
    }
}
