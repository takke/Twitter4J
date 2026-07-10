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
@Suppress("PropertyName")
class MediaEntityJSONImpl : EntityIndex, MediaEntity {
    override var id: Long = 0
        private set
    // 元実装では url/mediaURL/mediaURLHttps/expandedURL/displayURL/type/extAltText が null になりうるが、
    // MediaEntity(URLEntity) の各プロパティは非nullのため既定値 "" とする。
    private var url: String = ""
    override var mediaURL: String = ""
        private set
    override var mediaURLHttps: String = ""
        private set
    override var expandedURL: String = ""
        private set
    override var displayURL: String = ""
        private set
    override var sizes: Map<Int, MediaEntity.Size> = emptyMap()
        private set
    override var type: String = ""
        private set
    override var videoAspectRatioWidth: Int = 0
        private set
    override var videoAspectRatioHeight: Int = 0
        private set
    override var videoDurationMillis: Long = 0
        private set
    override var videoVariants: Array<MediaEntity.Variant> = emptyArray()
        private set
    // 元実装(getExtAltText)は ext_alt_text 欠損時に null を返す（テスト testVideoInfo/testAnimatedGifs が null を期待）。
    // インターフェース MediaEntity.extAltText は非null宣言だが実体は null を返しうるため backing で保持し getter で透過させる。
    private var extAltTextRaw: String? = null
    override var additionalMediaTitle: String? = null
        private set
    override var additionalMediaDescription: String? = null
        private set
    override var additionalMediaEmbeddable: Boolean? = null
        private set
    override var additionalMediaMonetizable: Boolean? = null
        private set
    override var additionalMediaSourceUser: User? = null
        private set

    override val text: String
        get() = url

    override val URL: String
        get() = url

    override val extAltText: String
        get() = uncheckedToNonNull(extAltTextRaw)

    @Throws(TwitterException::class)
    constructor(json: JSONObject) : super() {
        try {
            val indicesArray = json.getJSONArray("indices")
            start = indicesArray.getInt(0)
            end = indicesArray.getInt(1)

            // some json doesn't have "id"
            this.id = ParseUtil.getLong("id_str", json)

            this.url = json.getString("url")
            this.expandedURL = json.getString("expanded_url")

            // Use "media_url_https" instead of "media_url". some media_url values are not accessible.
            this.mediaURLHttps = json.getString("media_url_https")
            this.mediaURL = this.mediaURLHttps
            this.displayURL = json.getString("display_url")

            val sizesJSON = json.getJSONObject("sizes")
            val sizesMap = HashMap<Int, MediaEntity.Size>(4)
            // thumbworkarounding API side issue
            addMediaEntitySizeIfNotNull(sizesMap, sizesJSON, MediaEntity.Size.LARGE, "large")
            addMediaEntitySizeIfNotNull(sizesMap, sizesJSON, MediaEntity.Size.MEDIUM, "medium")
            addMediaEntitySizeIfNotNull(sizesMap, sizesJSON, MediaEntity.Size.SMALL, "small")
            addMediaEntitySizeIfNotNull(sizesMap, sizesJSON, MediaEntity.Size.THUMB, "thumb")
            this.sizes = sizesMap
            if (!json.isNull("type")) {
                this.type = json.getString("type")
            }

            if (json.has("video_info")) {
                val videoInfo = json.getJSONObject("video_info")
                val aspectRatio = videoInfo.getJSONArray("aspect_ratio")
                this.videoAspectRatioWidth = aspectRatio.getInt(0)
                this.videoAspectRatioHeight = aspectRatio.getInt(1)

                // not in animated_gif
                if (!videoInfo.isNull("duration_millis")) {
                    this.videoDurationMillis = videoInfo.getLong("duration_millis")
                }

                val variants = videoInfo.getJSONArray("variants")
                this.videoVariants = Array<MediaEntity.Variant>(variants.length()) { i ->
                    Variant(variants.getJSONObject(i))
                }
            } else {
                this.videoVariants = emptyArray()
            }

            if (json.has("ext_alt_text")) {
                this.extAltTextRaw = json.getString("ext_alt_text")
            }

            if (json.has("additional_media_info")) {
                val additionalMediaInfo = json.getJSONObject("additional_media_info")
                this.additionalMediaTitle = additionalMediaInfo.optString("title", null)
                this.additionalMediaDescription = additionalMediaInfo.optString("description", null)
                this.additionalMediaEmbeddable = additionalMediaInfo.optBoolean("embeddable", false)
                this.additionalMediaMonetizable = additionalMediaInfo.optBoolean("monetizable", false)
                if (!additionalMediaInfo.isNull("source_user")) {
                    this.additionalMediaSourceUser = UserJSONImpl(additionalMediaInfo.getJSONObject("source_user"))
                }
            }
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    @Throws(JSONException::class)
    private fun addMediaEntitySizeIfNotNull(sizes: MutableMap<Int, MediaEntity.Size>, sizesJSON: JSONObject, size: Int, key: String) {
        if (!sizesJSON.isNull(key)) {
            sizes[size] = Size(sizesJSON.getJSONObject(key))
        }
    }

    /* For serialization purposes only. */
    constructor() : super()

    internal class Size : MediaEntity.Size {
        override var width: Int = 0
            private set
        override var height: Int = 0
            private set
        override var resize: Int = 0
            private set

        /* For serialization purposes only. */
        constructor()

        @Throws(JSONException::class)
        constructor(json: JSONObject) {
            width = json.getInt("w")
            height = json.getInt("h")
            resize = if ("fit" == json.getString("resize")) MediaEntity.Size.FIT else MediaEntity.Size.CROP
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o !is Size) return false

            val size = o

            if (height != size.height) return false
            if (resize != size.resize) return false
            if (width != size.width) return false

            return true
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + resize
            return result
        }

        override fun toString(): String {
            return "Size{" +
                    "width=" + width +
                    ", height=" + height +
                    ", resize=" + resize +
                    '}'
        }

        companion object {
            private const val serialVersionUID = -2515842281909325169L
        }
    }

    internal class Variant : MediaEntity.Variant {
        override var bitrate: Int = 0
            private set
        override var contentType: String = ""
            private set
        override var url: String = ""
            private set

        @Throws(JSONException::class)
        constructor(json: JSONObject) {
            bitrate = if (json.has("bitrate")) json.getInt("bitrate") else 0
            contentType = json.getString("content_type")
            url = json.getString("url")
        }

        /* For serialization purposes only. */
        constructor()

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o !is Variant) return false

            val variant = o

            if (bitrate != variant.bitrate) return false
            if (contentType != variant.contentType) return false
            if (url != variant.url) return false

            return true
        }

        override fun hashCode(): Int {
            var result = bitrate
            result = 31 * result + contentType.hashCode()
            result = 31 * result + url.hashCode()
            return result
        }

        override fun toString(): String {
            return "Variant{" +
                    "bitrate=" + bitrate +
                    ", contentType=" + contentType +
                    ", url=" + url +
                    '}'
        }

        companion object {
            private const val serialVersionUID = 1027236588556797980L
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is MediaEntityJSONImpl) return false

        val that = o

        if (id != that.id) return false

        return true
    }

    override fun hashCode(): Int {
        return (id xor (id ushr 32)).toInt()
    }

    override fun toString(): String {
        return "MediaEntityJSONImpl{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", mediaURL='" + mediaURL + '\'' +
                ", mediaURLHttps='" + mediaURLHttps + '\'' +
                ", expandedURL='" + expandedURL + '\'' +
                ", displayURL='" + displayURL + '\'' +
                ", sizes=" + sizes +
                ", type='" + type + '\'' +
                ", videoAspectRatioWidth=" + videoAspectRatioWidth +
                ", videoAspectRatioHeight=" + videoAspectRatioHeight +
                ", videoDurationMillis=" + videoDurationMillis +
                ", videoVariants=" + videoVariants.size +
                ", extAltText='" + extAltTextRaw + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = 1571961225214439778L
    }
}

/**
 * 非null宣言のプロパティで、Java実装同様に実際は null を返しうる値をそのまま透過させるためのヘルパー。
 * 型パラメータ T は型消去されるため `value as T` は実行時にキャストチェックが行われず（null でも例外を投げない）、
 * 元の Java 実装が持っていた「非null契約だが実体は null」という挙動を保持する。
 */
@Suppress("UNCHECKED_CAST")
private fun <T> uncheckedToNonNull(value: T?): T = value as T
