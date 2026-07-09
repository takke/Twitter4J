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
 * Represents result of "/1.1/media/upload.json"
 *
 * @author Hiroaki TAKEUCHI - takke30 at gmail.com
 * @since Twitter4J 4.0.2
 */
class UploadedMedia @Throws(TwitterException::class) internal constructor(json: JSONObject) : Serializable {

    var imageWidth = 0
        private set
    var imageHeight = 0
        private set
    var imageType: String? = null
        private set
    var mediaId: Long = 0
        private set
    var size: Long = 0
        private set

    // chunked uploading
    var processingState: String? = null
        private set
    var processingCheckAfterSecs = 0
        private set
    var progressPercent = 0
        private set
    var processingErrorMessage: String? = null
        private set
    var processingErrorName: String? = null
        private set
    var processingErrorCode = -1
        private set

    var videoType: String? = null
        private set

    init {
        mediaId = ParseUtil.getLong("media_id", json)
        size = ParseUtil.getLong("size", json)
        try {
            if (!json.isNull("image")) {
                val image = json.getJSONObject("image")
                imageWidth = ParseUtil.getInt("w", image)
                imageHeight = ParseUtil.getInt("h", image)
                imageType = ParseUtil.getUnescapedString("image_type", image)
            }

            if (!json.isNull("video")) {
                val video = json.getJSONObject("video")
                videoType = ParseUtil.getUnescapedString("video_type", video)
            }

            if (!json.isNull("processing_info")) {
                val processingInfo = json.getJSONObject("processing_info")
                processingState = ParseUtil.getUnescapedString("state", processingInfo)
                processingCheckAfterSecs = ParseUtil.getInt("check_after_secs", processingInfo)
                progressPercent = ParseUtil.getInt("progress_percent", processingInfo)

                if (!processingInfo.isNull("error")) {
                    val error = processingInfo.getJSONObject("error")
                    processingErrorCode = ParseUtil.getInt("code", error)
                    processingErrorName = error.getString("name")
                    processingErrorMessage = error.getString("message")
                }
            }

        } catch (jsone: JSONException) {
            throw TwitterException(jsone)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as UploadedMedia

        if (imageWidth != that.imageWidth) return false
        if (imageHeight != that.imageHeight) return false
        if (mediaId != that.mediaId) return false
        if (size != that.size) return false
        if (processingCheckAfterSecs != that.processingCheckAfterSecs) return false
        if (progressPercent != that.progressPercent) return false
        if (processingErrorCode != that.processingErrorCode) return false
        if (if (imageType != null) imageType != that.imageType else that.imageType != null)
            return false
        if (if (processingState != null) processingState != that.processingState else that.processingState != null)
            return false
        if (if (processingErrorMessage != null) processingErrorMessage != that.processingErrorMessage else that.processingErrorMessage != null)
            return false
        return if (processingErrorName != null) processingErrorName == that.processingErrorName else that.processingErrorName == null
    }

    override fun hashCode(): Int {
        var result = imageWidth
        result = 31 * result + imageHeight
        result = 31 * result + (imageType?.hashCode() ?: 0)
        result = 31 * result + (mediaId xor (mediaId ushr 32)).toInt()
        result = 31 * result + (size xor (size ushr 32)).toInt()
        result = 31 * result + (processingState?.hashCode() ?: 0)
        result = 31 * result + processingCheckAfterSecs
        result = 31 * result + progressPercent
        result = 31 * result + (processingErrorMessage?.hashCode() ?: 0)
        result = 31 * result + (processingErrorName?.hashCode() ?: 0)
        result = 31 * result + processingErrorCode
        return result
    }

    override fun toString(): String {
        return ("UploadedMedia{" +
                "imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", imageType='" + imageType + '\'' +
                ", mediaId=" + mediaId +
                ", size=" + size +
                ", processingState='" + processingState + '\'' +
                ", processingCheckAfterSecs=" + processingCheckAfterSecs +
                ", progressPercent=" + progressPercent +
                ", processingErrorMessage='" + processingErrorMessage + '\'' +
                ", processingErrorName='" + processingErrorName + '\'' +
                ", processingErrorCode=" + processingErrorCode +
                '}')
    }

    companion object {
        private const val serialVersionUID = 5393092535610604718L
    }
}
