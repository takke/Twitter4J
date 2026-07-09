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

package twitter4j.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import twitter4j.HttpRequest;
import twitter4j.conf.Configuration;

/**
 * Cookie Authorization
 */
public class CookieAuthorization implements Authorization, java.io.Serializable {
    private static final long serialVersionUID = -886869424811858968L;
//    private final Configuration conf;
//    private transient static HttpClient http;

    private String rawCookie;
    private String bearerToken;

    // constructors

    /**
     * @param conf configuration
     */
    public CookieAuthorization(Configuration conf) {

//        this.conf = conf;
//        http = HttpClientFactory.getInstance(conf.getHttpClientConfiguration());

        rawCookie = conf.getCookie();
        bearerToken = conf.getBearerToken();
    }

    // implementations for Authorization
    @Override
    public String getAuthorizationHeader(HttpRequest req) {
        return "Bearer " + bearerToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return rawCookie != null;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        HashMap<String, String> map = new HashMap<>();

        String[] cookieParts = rawCookie.split(";");
        String csrfToken = null;
        String auth_token = null;
        for (String cookiePart : cookieParts) {
            String[] cookiePartParts = cookiePart.split("=");
            if (cookiePartParts.length == 2) {
                String key = cookiePartParts[0].trim();
                String value = cookiePartParts[1].trim();
                if (key.equals("ct0")) {
                    csrfToken = value;
                    break;
                }
                if (key.equals("auth_token")) {
                    auth_token = value;
                }
            }
        }

        map.put("x-twitter-auth-type", "OAuth2Session");
        map.put("x-csrf-token", csrfToken);    // ct0
        map.put("cookie", rawCookie);          // "ct0=${this.csrfToken}; auth_token=${this.AuthToken}"

//        map.put("cookie", "ct0=" + csrfToken + "; auth_token=" + auth_token);

        return map;
    }

    /*package*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CookieAuthorization that = (CookieAuthorization) o;
        return Objects.equals(rawCookie, that.rawCookie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawCookie);
    }

    @Override
    public String toString() {
        return "CookieAuthorization{" +
                "rawCookie='" + rawCookie + '\'' +
                '}';
    }
}
