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
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class Version private constructor() {
    init {
        throw AssertionError()
    }

    companion object {
        @JvmStatic
        val version = "v4.0.8-20250316"

        private const val TITLE = "Twitter4J"

        /**
         * prints the version string
         *
         * @param args will be just ignored.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            println("$TITLE $version")
        }
    }
}
