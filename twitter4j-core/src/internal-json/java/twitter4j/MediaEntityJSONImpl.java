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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.3
 */
public class MediaEntityJSONImpl extends EntityIndex implements MediaEntity {
    private static final long serialVersionUID = 1571961225214439778L;
    protected long id;
    protected String url;
    private String mediaURL;
    private String mediaURLHttps;
    private String expandedURL;
    private String displayURL;
    private Map<Integer, MediaEntity.Size> sizes;
    protected String type;
    private int videoAspectRatioWidth;
    private int videoAspectRatioHeight;
    private long videoDurationMillis;
    private Variant[] videoVariants;
    private String extAltText;
    private String additionalMediaTitle;
    private String additionalMediaDescription;
    private Boolean additionalMediaEmbeddable;
    private Boolean additionalMediaMonetizable;
    private User additionalMediaSourceUser;


    MediaEntityJSONImpl(JSONObject json) throws TwitterException {
        try {
            JSONArray indicesArray = json.getJSONArray("indices");
            setStart(indicesArray.getInt(0));
            setEnd(indicesArray.getInt(1));

            // some json doesn't have "id"
            this.id = ParseUtil.getLong("id_str", json);

            this.url = json.getString("url");
            this.expandedURL = json.getString("expanded_url");

            // Use "media_url_https" instead of "media_url". some media_url values are not accessible.
            this.mediaURL = this.mediaURLHttps = json.getString("media_url_https");
            this.displayURL = json.getString("display_url");

            JSONObject sizes = json.getJSONObject("sizes");
            this.sizes = new HashMap<Integer, MediaEntity.Size>(4);
            // thumbworkarounding API side issue
            addMediaEntitySizeIfNotNull(this.sizes, sizes, MediaEntity.Size.LARGE, "large");
            addMediaEntitySizeIfNotNull(this.sizes, sizes, MediaEntity.Size.MEDIUM, "medium");
            addMediaEntitySizeIfNotNull(this.sizes, sizes, MediaEntity.Size.SMALL, "small");
            addMediaEntitySizeIfNotNull(this.sizes, sizes, MediaEntity.Size.THUMB, "thumb");
            if (!json.isNull("type")) {
                this.type = json.getString("type");
            }

            if (json.has("video_info")) {
                JSONObject videoInfo = json.getJSONObject("video_info");
                JSONArray aspectRatio = videoInfo.getJSONArray("aspect_ratio");
                this.videoAspectRatioWidth = aspectRatio.getInt(0);
                this.videoAspectRatioHeight = aspectRatio.getInt(1);

                // not in animated_gif
                if (!videoInfo.isNull("duration_millis")) {
                    this.videoDurationMillis = videoInfo.getLong("duration_millis");
                }

                JSONArray variants = videoInfo.getJSONArray("variants");
                this.videoVariants = new Variant[variants.length()];
                for (int i=0; i<variants.length(); i++) {
                    this.videoVariants[i] = new Variant(variants.getJSONObject(i));
                }
            } else {
                this.videoVariants = new Variant[0];
            }

            if (json.has("ext_alt_text")) {
                this.extAltText = json.getString("ext_alt_text");
            }

            if (json.has("additional_media_info")) {
                JSONObject additionalMediaInfo = json.getJSONObject("additional_media_info");
                this.additionalMediaTitle = additionalMediaInfo.optString("title", null);
                this.additionalMediaDescription = additionalMediaInfo.optString("description", null);
                this.additionalMediaEmbeddable = additionalMediaInfo.optBoolean("embeddable", false);
                this.additionalMediaMonetizable = additionalMediaInfo.optBoolean("monetizable", false);
                if (!additionalMediaInfo.isNull("source_user")) {
                    this.additionalMediaSourceUser = new UserJSONImpl(additionalMediaInfo.getJSONObject("source_user"));
                }
            }

        } catch (JSONException jsone) {
            throw new TwitterException(jsone);
        }
    }

    private void addMediaEntitySizeIfNotNull(Map<Integer, MediaEntity.Size> sizes, JSONObject sizesJSON, Integer size, String key) throws JSONException {
        if (!sizesJSON.isNull(key)) {
            sizes.put(size, new Size(sizesJSON.getJSONObject(key)));
        }
    }

    /* For serialization purposes only. */
    /* package */ MediaEntityJSONImpl() {

    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getMediaURL() {
        return mediaURL;
    }

    @Override
    public String getMediaURLHttps() {
        return mediaURLHttps;
    }

    @Override
    public String getText() {
        return url;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getDisplayURL() {
        return displayURL;
    }

    @Override
    public String getExpandedURL() {
        return expandedURL;
    }

    @Override
    public Map<Integer, MediaEntity.Size> getSizes() {
        return sizes;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getStart() {
        return super.getStart();
    }

    @Override
    public int getEnd() {
        return super.getEnd();
    }

    static class Size implements MediaEntity.Size {
        private static final long serialVersionUID = -2515842281909325169L;
        int width;
        int height;
        int resize;
        
        /* For serialization purposes only. */
		/* package */
		Size() {
		}

        Size(JSONObject json) throws JSONException {
            width = json.getInt("w");
            height = json.getInt("h");
            resize = "fit".equals(json.getString("resize")) ? MediaEntity.Size.FIT : MediaEntity.Size.CROP;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getResize() {
            return resize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Size)) return false;

            Size size = (Size) o;

            if (height != size.height) return false;
            if (resize != size.resize) return false;
            if (width != size.width) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            result = 31 * result + resize;
            return result;
        }

        @Override
        public String toString() {
            return "Size{" +
                    "width=" + width +
                    ", height=" + height +
                    ", resize=" + resize +
                    '}';
        }
    }

    @Override
    public int getVideoAspectRatioWidth() {
        return videoAspectRatioWidth;
    }

    @Override
    public int getVideoAspectRatioHeight() {
        return videoAspectRatioHeight;
    }

    @Override
    public long getVideoDurationMillis() {
        return videoDurationMillis;
    }

    @Override
    public String getExtAltText() {
        return extAltText;
    }

    @Override
    public String getAdditionalMediaTitle() {
        return additionalMediaTitle;
    }

    @Override
    public String getAdditionalMediaDescription() {
        return additionalMediaDescription;
    }

    @Override
    public Boolean getAdditionalMediaEmbeddable() {
        return additionalMediaEmbeddable;
    }

    @Override
    public Boolean getAdditionalMediaMonetizable() {
        return additionalMediaMonetizable;
    }

    @Override
    public User getAdditionalMediaSourceUser() {
        return additionalMediaSourceUser;
    }

    @Override
    public MediaEntity.Variant[] getVideoVariants() {
        return videoVariants;
    }

    static class Variant implements MediaEntity.Variant {
        private static final long serialVersionUID = 1027236588556797980L;
        int bitrate;
        String contentType;
        String url;

        Variant(JSONObject json) throws JSONException {
            bitrate = json.has("bitrate") ? json.getInt("bitrate") : 0;
            contentType = json.getString("content_type");
            url =json.getString("url");
        }

        /* For serialization purposes only. */
        /* package */ Variant() {
        }

        @Override
        public int getBitrate() {
            return bitrate;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Variant)) return false;

            Variant variant = (Variant) o;

            if (bitrate != variant.bitrate) return false;
            if (!contentType.equals(variant.contentType)) return false;
            if (!url.equals(variant.url)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = bitrate;
            result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
            result = 31 * result + (url != null ? url.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Variant{" +
                    "bitrate=" + bitrate +
                    ", contentType=" + contentType +
                    ", url=" + url +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaEntityJSONImpl)) return false;

        MediaEntityJSONImpl that = (MediaEntityJSONImpl) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
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
                ", videoVariants=" + videoVariants.length +
                ", extAltText='" + extAltText + '\'' +
                '}';
    }
}
