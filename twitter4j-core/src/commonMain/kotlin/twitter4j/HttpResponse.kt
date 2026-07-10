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
 * HTTP レスポンスを表す共通抽象（KMP移行 Phase 4-1）。
 *
 * 後続 Phase 5 で JSONImpl 群を commonMain へ移動するにあたり、これらが参照する
 * `HttpResponse` を commonMain から見える状態にするため expect/actual 化した。
 *
 * ここには「JSONImpl 群 / 共通コードが必要とする最小メンバのみ」を宣言する。
 * java.io ストリーム系（asStream / asReader）や `HttpClientConfiguration` を引数に取る
 * コンストラクタなど、JVM(Android) 実装固有のメンバは actual 側にのみ存在する追加メンバとして
 * 維持し、この expect 宣言には含めない。
 *
 * - JVM(Android): actual は従来の `HttpResponse.kt`。java.net / java.io ベースの実装を温存し、
 *   Java サブクラス（HttpResponseImpl / OkHttpResponse / MockHttpResponse 等）を無修正で受け入れる。
 * - iOS(Native): actual は文字列ベースの最小実装。Phase 4-3 の NSURLSession 実装の基底となる想定。
 */
expect abstract class HttpResponse {

    /**
     * expect 側の共通コンストラクタ。
     * actual 側にはこのほか `HttpClientConfiguration` を引数に取るコンストラクタ（JVM専用）が
     * 追加メンバとして存在する。
     */
    protected constructor()

    /**
     * HTTP ステータスコードを返す。
     */
    fun getStatusCode(): Int

    /**
     * 指定名のレスポンスヘッダを返す。
     */
    abstract fun getResponseHeader(name: String): String?

    /**
     * レスポンスヘッダ一覧を返す。
     */
    abstract fun getResponseHeaderFields(): MutableMap<String, MutableList<String>>?

    /**
     * レスポンスボディを文字列として返す。
     *
     * @throws TwitterException レスポンスボディ消費時にネットワーク等の問題があった場合
     */
    @Throws(TwitterException::class)
    open fun asString(): String

    /**
     * レスポンスボディを [JSONObject] として返す。
     *
     * @throws TwitterException レスポンスボディが JSON Object 形式でない場合
     */
    @Throws(TwitterException::class)
    open fun asJSONObject(): JSONObject

    /**
     * レスポンスボディを [JSONArray] として返す。
     *
     * @throws TwitterException レスポンスボディが JSON Array 形式でない場合
     */
    @Throws(TwitterException::class)
    open fun asJSONArray(): JSONArray
}
