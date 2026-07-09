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

package twitter4j.conf;

import org.junit.jupiter.api.Test;
import twitter4j.auth.RequestToken;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class ConfigurationTest {


    @Test
    void testGetInstance() throws Exception {
        Configuration conf = ConfigurationContext.getInstance();
        assertNotNull(conf);
    }

    @Test
    void testFixURL() throws Exception {
        assertEquals("http://www.bea.com", ConfigurationBase.fixURL(false, "http://www.bea.com"));
        assertEquals("http://www.bea.com", ConfigurationBase.fixURL(false, "https://www.bea.com"));
        assertEquals("https://www.bea.com", ConfigurationBase.fixURL(true, "http://www.bea.com"));
        assertEquals("https://www.bea.com", ConfigurationBase.fixURL(true, "https://www.bea.com"));
        assertNull(ConfigurationBase.fixURL(false, null));
        assertNull(ConfigurationBase.fixURL(true, null));
    }

    // KMP移行 Phase 3-2 で PropertyConfiguration（twitter4j.properties / System property 読み込み）を
    // 削除したため、それに依存していた testConfiguration / testSSL / testTwitter4jPrefixOmittable /
    // testTreeConfiguration を削除した。ConfigurationBuilder のプロパティファイル読み込みに依存する
    // アサーションも削除している。

    @Test
    void testConfigurationBuilder() throws Exception {
        ConfigurationBuilder builder;
        Configuration conf;
        builder = new ConfigurationBuilder();
        conf = builder.build();

        Configuration t = (Configuration) serializeDeserialize(conf);

        assertEquals(conf, (Configuration) serializeDeserialize(conf));

        assertTrue(0 == conf.getRestBaseURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthAuthenticationURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthAuthorizationURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthAccessTokenURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthRequestTokenURL().indexOf("https://"));

        builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey("key");
        builder.setOAuthConsumerSecret("secret");
        conf = builder.build();
        assertTrue(0 == conf.getRestBaseURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthAuthenticationURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthAuthorizationURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthAccessTokenURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthRequestTokenURL().indexOf("https://"));

        RequestToken rt = new RequestToken("key", "secret");

        // TFJ-328 RequestToken.getAuthenticationURL()/getAuthorizationURL() should return URLs starting with https:// for security reasons
        assertTrue(0 == rt.getAuthenticationURL().indexOf("https://"));
        assertTrue(0 == rt.getAuthorizationURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthAccessTokenURL().indexOf("https://"));
        assertTrue(0 == conf.getOAuthRequestTokenURL().indexOf("https://"));
    }


    private static Object serializeDeserialize(Object obj) throws Exception {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(byteOutputStream);
        oos.writeObject(obj);
        byteOutputStream.close();
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(byteInputStream);
        Object that = ois.readObject();
        byteInputStream.close();
        ois.close();
        return that;
    }

    @Test
    void testEnvironmentVariableBasedConfiguration() throws Exception {
        Configuration conf = ConfigurationContext.getInstance();
        // perquisite: export twitter4j.debug=true
//        assertTrue(conf.isDebugEnabled());
        // perquisite: export twitter4j.debug=false
//        assertFalse(conf.isDebugEnabled());
    }
}
