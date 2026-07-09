package twitter4j

import twitter4j.conf.ChunkedUploadConfiguration
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale

/**
 * Delegate class for chunked uploading
 *
 * @author Hiroaki Takeuchi - takke30 at gmail.com
 * @since Twitter4J 4.0.7
 */
internal class ChunkedUploadDelegate(private val twitter: TwitterImpl) {

    private lateinit var uploadConfiguration: ChunkedUploadConfiguration

    @Throws(TwitterException::class)
    fun uploadMediaChunked(uploadConfiguration: ChunkedUploadConfiguration): UploadedMedia {
        val mediaFile = uploadConfiguration.file
        this.uploadConfiguration = uploadConfiguration
        return if (mediaFile != null) {
            (twitter.v1Resources() as V1ResourcesImpl).checkFileValidity(mediaFile)
            try {
                upload(
                    uploadConfiguration.mediaType, uploadConfiguration.mediaCategory,
                    mediaFile.name, FileInputStream(mediaFile), mediaFile.length()
                )
            } catch (e: IOException) {
                throw TwitterException(e)
            }
        } else {
            upload(
                uploadConfiguration.mediaType, uploadConfiguration.mediaCategory,
                uploadConfiguration.filename!!, uploadConfiguration.stream, uploadConfiguration.length
            )
        }
    }

    @Throws(TwitterException::class)
    private fun upload(mediaType: String?, mediaCategory: String?, fileName: String, stream: InputStream?, mediaLength: Long): UploadedMedia {
        if (mediaLength > MAX_VIDEO_SIZE) {
            throw TwitterException(
                String.format(
                    Locale.US,
                    "video file can't be longer than: %d MBytes",
                    MAX_VIDEO_SIZE / (1024 * 1024)
                )
            )
        }
        try {
            onProgress("Initializing", 0, mediaLength, "", 0)
            val uploadedMedia = sendInit(mediaType, mediaCategory, mediaLength)
            onProgress("Initialized", 0, mediaLength, "", 0)
            val buffered = BufferedInputStream(stream)

            val segmentData = ByteArray(uploadConfiguration.segmentSizeBytes)
            var totalRead: Long = 0

            var bytesRead: Int
            var segmentIndex = 0
            while (buffered.read(segmentData).also { bytesRead = it } > 0) {
                totalRead += bytesRead.toLong()
                //no need to close ByteArrayInputStream
                sendAppend(fileName, ByteArrayInputStream(segmentData, 0, bytesRead), segmentIndex, uploadedMedia.mediaId)
                onProgress("Appended", totalRead, mediaLength, "", 0)
                ++segmentIndex
            }
            return sendAndWaitForFinalize(uploadedMedia.mediaId, mediaLength)
        } catch (e: IOException) {
            throw TwitterException(e)
        }
    }

    @Throws(TwitterException::class)
    private fun sendInit(mediaType: String?, mediaCategory: String?, size: Long): UploadedMedia {
        return UploadedMedia(
            twitter.post(
                twitter.conf.getUploadBaseURL() + "media/upload.json",
                HttpParameter("command", "INIT"),
                HttpParameter("media_type", mediaType),
                HttpParameter("media_category", mediaCategory),
                HttpParameter("total_bytes", size)
            ).asJSONObject()
        )
    }

    @Throws(TwitterException::class)
    private fun sendAppend(fileName: String, segmentData: InputStream, segmentIndex: Int, mediaId: Long) {
        twitter.post(
            twitter.conf.getUploadBaseURL() + "media/upload.json",
            HttpParameter("command", "APPEND"),
            HttpParameter("media_id", mediaId),
            HttpParameter("segment_index", segmentIndex),
            HttpParameter("media", fileName, segmentData)
        )
    }

    @Throws(TwitterException::class)
    private fun sendAndWaitForFinalize(mediaId: Long, mediaLength: Long): UploadedMedia {
        var tries = 0
        val maxTries = 20
        var lastProgressPercent = 0
        var currentProgressPercent = 0
        var totalWaitSec = 0
        var uploadedMedia = sendFinalize(mediaId)
        while (tries < maxTries) {
            if (lastProgressPercent == currentProgressPercent) {
                tries++
            }
            lastProgressPercent = currentProgressPercent

            val state = uploadedMedia.processingState
            if (state == null) {
                // uploaded jpeg or png
                onProgress("Finalized", mediaLength, mediaLength, state, uploadedMedia.progressPercent)
                return uploadedMedia
            }
            if (state == "failed") {
                if (uploadedMedia.processingErrorMessage != null) {
                    throw TwitterException(
                        uploadedMedia.processingErrorMessage + " (" + uploadedMedia.processingErrorName + ")",
                        uploadedMedia.processingErrorCode
                    )
                } else {
                    throw TwitterException("Failed to finalize the chunked upload.")
                }
            }
            if (state == "pending" || state == "in_progress") {
                currentProgressPercent = uploadedMedia.progressPercent
                val waitSec = uploadedMedia.processingCheckAfterSecs
                if (waitSec <= 0) {
                    throw TwitterException("Failed to finalize the chunked upload, invalid check_after_secs value $waitSec")
                }
                totalWaitSec += waitSec
                if (totalWaitSec > uploadConfiguration.finalizeTimeout) {
                    throw TwitterException("Failed to finalize the chunked upload, timed out after $totalWaitSec seconds")
                }

                onProgress(
                    "Finalizing... wait for:$waitSec sec", mediaLength, mediaLength,
                    state, if (currentProgressPercent < 0) 0 else currentProgressPercent
                )

                try {
                    Thread.sleep((waitSec * 1000).toLong())
                } catch (e: InterruptedException) {
                    throw TwitterException("Failed to finalize the chunked upload.", e)
                }
            }
            if (state == "succeeded") {
                onProgress("Finalized", mediaLength, mediaLength, state, uploadedMedia.progressPercent)
                return uploadedMedia
            }
            uploadedMedia = getStatus(mediaId)
        }
        throw TwitterException("Failed to finalize the chunked upload, progress has stopped, tried " + tries + 1 + " times.")
    }

    @Throws(TwitterException::class)
    private fun sendFinalize(mediaId: Long): UploadedMedia {
        return UploadedMedia(
            twitter.post(
                twitter.conf.getUploadBaseURL() + "media/upload.json",
                HttpParameter("command", "FINALIZE"),
                HttpParameter("media_id", mediaId)
            ).asJSONObject()
        )
    }

    @Throws(TwitterException::class)
    private fun getStatus(mediaId: Long): UploadedMedia {
        return UploadedMedia(
            twitter.get(
                twitter.conf.getUploadBaseURL() + "media/upload.json",
                HttpParameter("command", "STATUS"),
                HttpParameter("media_id", mediaId)
            ).asJSONObject()
        )
    }

    private fun onProgress(progress: String, uploadedBytes: Long, totalBytes: Long, finalizeProcessingState: String?, finalizeProgressPercent: Int) {
        if (uploadConfiguration.callback != null) {
            uploadConfiguration.callback!!.onProgress(progress, uploadedBytes, totalBytes, finalizeProcessingState, finalizeProgressPercent)
        }
    }

    companion object {
        private const val MAX_VIDEO_SIZE = 512 * 1024 * 1024 // 512MB is a constraint imposed by Twitter for video files
    }
}
