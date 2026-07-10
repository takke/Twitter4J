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

import twitter4j.conf.JsonStoreConfiguration

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.3
 */
internal class TwitterAPIConfigurationJSONImpl : TwitterResponseImpl, TwitterAPIConfiguration {
    override var photoSizeLimit: Int = 0
        private set
    override var shortURLLength: Int = 0
        private set
    override var shortURLLengthHttps: Int = 0
        private set
    override var charactersReservedPerMedia: Int = 0
        private set
    override var dmTextCharacterLimit: Int = 0
        private set
    override var photoSizes: Map<Int, MediaEntity.Size> = emptyMap()
        private set
    override var nonUsernamePaths: Array<String> = emptyArray()
        private set
    override var maxMediaPerUpload: Int = 0
        private set

    @Throws(TwitterException::class)
    constructor(res: HttpResponse, conf: JsonStoreConfiguration) : super(res) {
        try {
            val json = res.asJSONObject()
            photoSizeLimit = ParseUtil.getInt("photo_size_limit", json)
            shortURLLength = ParseUtil.getInt("short_url_length", json)
            shortURLLengthHttps = ParseUtil.getInt("short_url_length_https", json)
            charactersReservedPerMedia = ParseUtil.getInt("characters_reserved_per_media", json)
            dmTextCharacterLimit = ParseUtil.getInt("dm_text_character_limit", json)

            val sizes = json.getJSONObject("photo_sizes")
            val photoSizesMap = HashMap<Int, MediaEntity.Size>(4)
            photoSizesMap[MediaEntity.Size.LARGE] = MediaEntityJSONImpl.Size(sizes.getJSONObject("large"))
            val medium: JSONObject
            // http://code.google.com/p/twitter-api/issues/detail?id=2230
            medium = if (sizes.isNull("med")) {
                sizes.getJSONObject("medium")
            } else {
                sizes.getJSONObject("med")
            }
            photoSizesMap[MediaEntity.Size.MEDIUM] = MediaEntityJSONImpl.Size(medium)
            photoSizesMap[MediaEntity.Size.SMALL] = MediaEntityJSONImpl.Size(sizes.getJSONObject("small"))
            photoSizesMap[MediaEntity.Size.THUMB] = MediaEntityJSONImpl.Size(sizes.getJSONObject("thumb"))
            photoSizes = photoSizesMap
            if (conf.isJSONStoreEnabled) {
                TwitterObjectFactory.clearThreadLocalMap()
                TwitterObjectFactory.registerJSONObject(this, res.asJSONObject())
            }
            val nonUsernamePathsJSONArray = json.getJSONArray("non_username_paths")
            nonUsernamePaths = Array(nonUsernamePathsJSONArray.length()) { i -> nonUsernamePathsJSONArray.getString(i) }
            maxMediaPerUpload = ParseUtil.getInt("max_media_per_upload", json)
        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is TwitterAPIConfigurationJSONImpl) return false

        val that = o

        if (charactersReservedPerMedia != that.charactersReservedPerMedia) return false
        if (dmTextCharacterLimit != that.dmTextCharacterLimit) return false
        if (maxMediaPerUpload != that.maxMediaPerUpload) return false
        if (photoSizeLimit != that.photoSizeLimit) return false
        if (shortURLLength != that.shortURLLength) return false
        if (shortURLLengthHttps != that.shortURLLengthHttps) return false
        if (!nonUsernamePaths.contentEquals(that.nonUsernamePaths)) return false
        if (photoSizes != that.photoSizes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photoSizeLimit
        result = 31 * result + shortURLLength
        result = 31 * result + shortURLLengthHttps
        result = 31 * result + charactersReservedPerMedia
        result = 32 * result + dmTextCharacterLimit
        result = 31 * result + photoSizes.hashCode()
        result = 31 * result + nonUsernamePaths.contentHashCode()
        result = 31 * result + maxMediaPerUpload
        return result
    }

    override fun toString(): String {
        return "TwitterAPIConfigurationJSONImpl{" +
                "photoSizeLimit=" + photoSizeLimit +
                ", shortURLLength=" + shortURLLength +
                ", shortURLLengthHttps=" + shortURLLengthHttps +
                ", charactersReservedPerMedia=" + charactersReservedPerMedia +
                ", dmTextCharacterLimit=" + dmTextCharacterLimit +
                ", photoSizes=" + photoSizes +
                ", nonUsernamePaths=" + nonUsernamePaths.asList() +
                ", maxMediaPerUpload=" + maxMediaPerUpload +
                '}'
    }

    companion object {
        private const val serialVersionUID = -3588904550808591686L
    }
}
