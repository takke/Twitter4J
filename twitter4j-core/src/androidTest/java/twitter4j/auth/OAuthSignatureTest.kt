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

package twitter4j.auth

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import twitter4j.HttpParameter
import twitter4j.conf.ConfigurationBuilder

/**
 * OAuth 1.0a 署名生成の同値検証テスト（ネットワーク不要）。
 *
 * OAuthAuthorization の署名ロジック（HMAC-SHA1・署名ベース文字列組み立て・
 * パーセントエンコード）が Java 実装から Kotlin 実装への変換前後で
 * 1バイトも変わらないことを固定入力（consumerKey/secret・nonce・timestamp 固定）で保証する。
 */
class OAuthSignatureTest {

    private fun newOAuth(): OAuthAuthorization {
        val conf = ConfigurationBuilder()
            .setOAuthConsumerKey("dpf43f3p2l4k3l03")
            .setOAuthConsumerSecret("kd94hf93k423kf44")
            .build()
        return OAuthAuthorization(conf)
    }

    @Test
    fun testGenerateSignature() {
        val oauth = newOAuth()
        val token = AccessToken("nnch734d00sl2jdk", "pfkkdhi9sl3r4s00")
        val sig = oauth.generateSignature("GET&http%3A%2F%2Fphotos.example.net%2Fphotos&foo%3Dbar", token)
        // Java 実装時に採取した基準値（変換前後で不変であること）
        assertEquals("+LR/weJvZa4q9D+tpom5HV/LiRE=", sig)
    }

    @Test
    fun testGenerateAuthorizationHeader() {
        val oauth = newOAuth()
        val token = AccessToken("nnch734d00sl2jdk", "pfkkdhi9sl3r4s00")
        val params = arrayOf(
            HttpParameter("status", "Hello Ladies + Gentlemen, a signed OAuth request!"),
            HttpParameter("include_entities", "true")
        )
        val header = oauth.generateAuthorizationHeader(
            "POST",
            "https://api.twitter.com/1.1/statuses/update.json",
            params,
            "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg",
            "1318622958",
            token
        )
        // Java 実装時に採取した基準値（変換前後で不変であること）
        assertEquals(
            "OAuth oauth_consumer_key=\"dpf43f3p2l4k3l03\"," +
                "oauth_signature_method=\"HMAC-SHA1\"," +
                "oauth_timestamp=\"1318622958\"," +
                "oauth_nonce=\"kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg\"," +
                "oauth_version=\"1.0\"," +
                "oauth_token=\"nnch734d00sl2jdk\"," +
                "oauth_signature=\"kMnxFtYdT3F%2FuWKbAjmhZzVQbz4%3D\"",
            header
        )
    }
}
