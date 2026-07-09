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

import java.io.File
import java.io.InputStream
import java.util.Arrays

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.1
 */
class StatusUpdate(val status: String) : Serializable {

    var inReplyToStatusId: Long = -1L
    var location: GeoLocation? = null
    var placeId: String? = null
    var displayCoordinates: Boolean = true
    var possiblySensitive: Boolean = false
    private var mediaName: String? = null

    @Transient
    private var mediaBody: InputStream? = null
    private var mediaFile: File? = null
    private var mediaIds: LongArray? = null
    var autoPopulateReplyMetadata: Boolean = false
    var attachmentUrl: String? = null
    private var excludeReplyUserIds: LongArray? = null

    /**
     * @param file media file
     * @since Twitter4J 2.2.5
     */
    fun setMedia(file: File?) {
        this.mediaFile = file
    }

    /**
     * @param name name
     * @param body media body as stream
     * @since Twitter4J 2.2.5
     */
    fun setMedia(name: String?, body: InputStream?) {
        this.mediaName = name
        this.mediaBody = body
    }

    /**
     * @param mediaIds media ids
     * @since Twitter4J 4.0.2
     */
    fun setMediaIds(vararg mediaIds: Long) {
        this.mediaIds = mediaIds
    }

    /*package*/
    fun isForUpdateWithMedia(): Boolean {
        return mediaFile != null || mediaName != null
    }

    /**
     * @param excludeReplyUserIds enable specific IDs (apart from the leading one) to be excluded from a reply if auto_populate_reply_metadata enabled
     * @since Twitter4J 4.0.x
     */
    fun setExcludeReplyUserIds(vararg excludeReplyUserIds: Long) {
        this.excludeReplyUserIds = excludeReplyUserIds
    }

    /*package*/
    fun asHttpParameterArray(): Array<HttpParameter> {
        val params = ArrayList<HttpParameter>()
        appendParameter("status", status, params)
        if (-1L != inReplyToStatusId) {
            appendParameter("in_reply_to_status_id", inReplyToStatusId, params)
        }
        val location = this.location
        if (location != null) {
            appendParameter("lat", location.latitude, params)
            appendParameter("long", location.longitude, params)
        }
        appendParameter("place_id", placeId, params)
        if (!displayCoordinates) {
            appendParameter("display_coordinates", "false", params)
        }
        val mediaFile = this.mediaFile
        val mediaName = this.mediaName
        val mediaBody = this.mediaBody
        val mediaIds = this.mediaIds
        if (null != mediaFile) {
            params.add(HttpParameter("media[]", mediaFile))
            params.add(HttpParameter("possibly_sensitive", possiblySensitive))
        } else if (mediaName != null && mediaBody != null) {
            params.add(HttpParameter("media[]", mediaName, mediaBody))
            params.add(HttpParameter("possibly_sensitive", possiblySensitive))
        } else if (mediaIds != null && mediaIds.size >= 1) {
            params.add(HttpParameter("media_ids", StringUtil.join(mediaIds)))
        }
        if (autoPopulateReplyMetadata) {
            appendParameter("auto_populate_reply_metadata", "true", params)
        }
        appendParameter("attachment_url", attachmentUrl, params)
        val excludeReplyUserIds = this.excludeReplyUserIds
        if (excludeReplyUserIds != null && excludeReplyUserIds.size >= 1) {
            params.add(HttpParameter("exclude_reply_user_ids", StringUtil.join(excludeReplyUserIds)))
        }
        return params.toTypedArray()
    }

    private fun appendParameter(name: String, value: String?, params: MutableList<HttpParameter>) {
        if (value != null) {
            params.add(HttpParameter(name, value))
        }
    }

    private fun appendParameter(name: String, value: Double, params: MutableList<HttpParameter>) {
        params.add(HttpParameter(name, value.toString()))
    }

    private fun appendParameter(name: String, value: Long, params: MutableList<HttpParameter>) {
        params.add(HttpParameter(name, value.toString()))
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as StatusUpdate

        if (inReplyToStatusId != that.inReplyToStatusId) return false
        if (displayCoordinates != that.displayCoordinates) return false
        if (possiblySensitive != that.possiblySensitive) return false
        if (autoPopulateReplyMetadata != that.autoPopulateReplyMetadata) return false
        if (status != that.status) return false
        if (location != that.location) return false
        if (placeId != that.placeId) return false
        if (mediaName != that.mediaName) return false
        if (mediaBody != that.mediaBody) return false
        if (mediaFile != that.mediaFile) return false
        if (!Arrays.equals(mediaIds, that.mediaIds)) return false
        return attachmentUrl == that.attachmentUrl
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (inReplyToStatusId xor (inReplyToStatusId ushr 32)).toInt()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (placeId?.hashCode() ?: 0)
        result = 31 * result + (if (displayCoordinates) 1 else 0)
        result = 31 * result + (if (possiblySensitive) 1 else 0)
        result = 31 * result + (mediaName?.hashCode() ?: 0)
        result = 31 * result + (mediaBody?.hashCode() ?: 0)
        result = 31 * result + (mediaFile?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(mediaIds)
        result = 31 * result + (if (autoPopulateReplyMetadata) 1 else 0)
        result = 31 * result + (attachmentUrl?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "StatusUpdate{" +
                "status='" + status + '\'' +
                ", inReplyToStatusId=" + inReplyToStatusId +
                ", location=" + location +
                ", placeId='" + placeId + '\'' +
                ", displayCoordinates=" + displayCoordinates +
                ", possiblySensitive=" + possiblySensitive +
                ", mediaName='" + mediaName + '\'' +
                ", mediaBody=" + mediaBody +
                ", mediaFile=" + mediaFile +
                ", mediaIds=" + Arrays.toString(mediaIds) +
                ", autoPopulateReplyMetadata=" + autoPopulateReplyMetadata +
                ", attachmentUrl='" + attachmentUrl + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = 7422094739799350035L
    }
}
