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
 * TwitterAPI 呼び出しが失敗したときに投げられる例外の共通抽象（KMP移行 Phase 4-1）。
 *
 * 後続 Phase 5 で JSONImpl 群 / ParseUtil を commonMain へ移動するにあたり、これらが
 * 生成・参照する `TwitterException` を commonMain から見える状態にするため expect/actual 化した。
 *
 * ここには「共通コードが必要とする最小メンバのみ」を宣言する。
 * `ExceptionDiagnosis`（JVMスタックトレース依存）を用いる `getExceptionCode()` などや、
 * JVM(Android) の `TwitterResponse` / `HttpResponseCode` 実装に伴う override メンバは
 * actual 側にのみ存在する追加メンバとして維持し、この expect 宣言には含めない。
 *
 * - JVM(Android): actual は従来の `TwitterException.kt`。`Exception, TwitterResponse, HttpResponseCode`
 *   を実装し、JVMの公開シグネチャを完全に維持する。
 * - iOS(Native): actual は最小実装。ExceptionDiagnosis 相当は持たず、rateLimitStatus は null を返す等、
 *   Native で成立する範囲に簡略化している。
 */
expect open class TwitterException : Exception {

    constructor(message: String?, cause: Throwable?)
    constructor(message: String?)
    constructor(cause: Exception)
    constructor(message: String?, res: HttpResponse)
    constructor(message: String?, cause: Exception?, statusCode: Int)
    constructor(message: String?, errorCode: Int)

    val statusCode: Int
    val errorCode: Int
    val errorMessage: String?

    /**
     * 合成された例外メッセージ（ステータスコードの説明 + エラーメッセージ + コード）を返す。
     * `Throwable.message`（nullable）を非nullに narrowing して override している。
     */
    override val message: String

    /**
     * 指定名のレスポンスヘッダ（例外発生時のレスポンスに紐づくもの）を返す。
     */
    fun getResponseHeader(name: String): String?

    /**
     * 存在しないリソースが原因かどうか（HTTP 404）を返す。
     */
    fun resourceNotFound(): Boolean

    /**
     * この例外に紐づくレスポンスから取得できる場合のレート制限状態を返す。
     */
    val rateLimitStatus: RateLimitStatus?

    /**
     * アプリケーションのアクセスレベル。
     */
    val accessLevel: Int
}
