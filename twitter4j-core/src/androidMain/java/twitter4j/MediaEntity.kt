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
 * @since Twitter4J 2.2.3
 */
interface MediaEntity : URLEntity {
    /**
     * Returns the id of the media.
     *
     * @return the id of the media
     */
    val id: Long

    /**
     * Returns the media URL.
     *
     * @return the media URL
     */
    val mediaURL: String

    /**
     * Returns the media secure URL.
     *
     * @return the media secure URL
     */
    val mediaURLHttps: String

    /**
     * Returns size variations of the media.
     *
     * @return size variations of the media
     */
    val sizes: Map<Int, Size>

    interface Size : Serializable {
        val width: Int

        val height: Int

        val resize: Int

        companion object {
            const val THUMB = 0
            const val SMALL = 1
            const val MEDIUM = 2
            const val LARGE = 3
            const val FIT = 100
            const val CROP = 101
        }
    }

    /**
     * Returns the media type ("photo", "video", "animated_gif").
     *
     * @return the media type ("photo", "video", "animated_gif").
     */
    val type: String

    val videoAspectRatioWidth: Int

    val videoAspectRatioHeight: Int

    val videoDurationMillis: Long

    interface Variant : Serializable {

        val bitrate: Int

        val contentType: String

        val url: String
    }

    val videoVariants: Array<Variant>

    val extAltText: String

    val additionalMediaTitle: String?

    val additionalMediaDescription: String?

    val additionalMediaEmbeddable: Boolean?

    val additionalMediaMonetizable: Boolean?

    val additionalMediaSourceUser: User?

}
