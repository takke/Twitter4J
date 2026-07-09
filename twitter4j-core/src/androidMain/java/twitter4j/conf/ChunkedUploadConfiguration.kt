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

package twitter4j.conf

import java.io.File
import java.io.InputStream

import twitter4j.TwitterException

/**
 * Specification for {@link twitter4j.api.TweetsResources}.uploadMediaChunked
 *
 * @author Hiroaki Takeuchi - takke30 at gmail.com
 * @since Twitter4J 4.0.7
 */
class ChunkedUploadConfiguration {

    /**
     * Stream type, e.g. "video/mp4", "video/gif", "image/jpeg", ...
     *
     * @return media type
     */
    var mediaType: String? = null
        private set

    /**
     * Stream category, e.g. "tweet_video", "tweet_gif" or "tweet_image"
     *
     * @return media category
     */
    var mediaCategory: String? = null
        private set

    var file: File? = null
        private set

    var filename: String? = null
        private set

    var stream: InputStream? = null
        private set

    var length: Long = -1
        private set

    /**
     * Timeout (in seconds) for chunked upload finalization.
     *
     * @return Timeout (in seconds) for chunked upload finalization.
     */
    var finalizeTimeout = 30           // When doing a chunked upload, bail out after 30 seconds by default
        private set

    /**
     * Size of segments (in bytes) when performing a chunked upload
     *
     * @return Size of segments (in bytes) when performing a chunked upload
     */
    var segmentSizeBytes = 1024 * 1024 // Segment size defaults to 1MB
        private set

    var callback: Callback? = null
        private set

    class Builder {

        private val conf = ChunkedUploadConfiguration()

        fun tweetVideo(): Builder {
            conf.mediaType = "video/mp4"
            conf.mediaCategory = "tweet_video"
            return this
        }

        fun tweetGif(): Builder {
            conf.mediaType = "video/gif"
            conf.mediaCategory = "tweet_gif"
            return this
        }

        fun tweetJpeg(): Builder {
            conf.mediaType = "video/jpeg"
            conf.mediaCategory = "tweet_image"
            return this
        }

        fun tweetPng(): Builder {
            conf.mediaType = "video/png"
            conf.mediaCategory = "tweet_image"
            return this
        }

        fun dmVideo(): Builder {
            conf.mediaType = "video/mp4"
            conf.mediaCategory = "dm_video"
            return this
        }

        fun dmGif(): Builder {
            conf.mediaType = "video/gif"
            conf.mediaCategory = "dm_gif"
            return this
        }

        fun from(file: File): Builder {
            conf.file = file
            return this
        }

        fun from(filename: String, stream: InputStream, length: Long): Builder {
            conf.filename = filename
            conf.stream = stream
            conf.length = length
            return this
        }

        fun callback(callback: Callback): Builder {
            conf.callback = callback
            return this
        }

        fun finalizeTimeout(timeout: Int): Builder {
            conf.finalizeTimeout = timeout
            return this
        }

        fun segmentSizeBytes(segmentSizeBytes: Int): Builder {
            conf.segmentSizeBytes = segmentSizeBytes
            return this
        }

        @Throws(TwitterException::class)
        fun build(): ChunkedUploadConfiguration {

            // must specify file or filename/stream/length
            if (conf.file == null && conf.filename == null) {
                throw TwitterException("specify file")
            }
            if (conf.file != null && conf.filename != null) {
                throw TwitterException("specify file with File or Filename")
            }

            if (conf.mediaType == null) {
                throw TwitterException("specify media type")
            }

            return conf
        }
    }

    fun interface Callback {
        fun onProgress(progress: String?, uploadedBytes: Long, totalBytes: Long, finalizeProcessingState: String?, finalizeProgressPercent: Int)
    }
}
