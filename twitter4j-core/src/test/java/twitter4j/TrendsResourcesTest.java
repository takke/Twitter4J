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
package twitter4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.4
 */
class TrendsResourcesTest extends TwitterTestBase {
    @Test
    void testLocalTrendsMethods() throws Exception {
        Trends trends = twitter2.v1Resources().getPlaceTrends(1);
        assertEquals("Worldwide", trends.getLocation().getName());
        ResponseList<Location> locations;
        locations = twitter1.v1Resources().getAvailableTrends();
        assertNotNull(TwitterObjectFactory.getRawJSON(locations));
        assertEquals(locations.get(0), TwitterObjectFactory.createLocation(TwitterObjectFactory.getRawJSON(locations.get(0))));
        assertTrue(locations.size() > 0);

        locations = twitter2.v1Resources().getClosestTrends(new GeoLocation(35.677248D, 139.72911D));
        assertEquals("Tokyo", locations.get(0).getName());
    }
}
