package twitter4j

object EntitiesParseUtil {

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getUserMentions(entities: JSONObject): Array<UserMentionEntity>? {
        return if (!entities.isNull("user_mentions")) {
            val userMentionsArray = entities.getJSONArray("user_mentions")
            val len = userMentionsArray.length()
            Array(len) {
                UserMentionEntityJSONImpl(userMentionsArray.getJSONObject(it))
            }
        } else {
            null
        }
    }

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getUrls(entities: JSONObject): Array<URLEntity>? {
        return if (!entities.isNull("urls")) {
            val urlsArray = entities.getJSONArray("urls")
            val len = urlsArray.length()
            Array(len) {
                URLEntityJSONImpl(urlsArray.getJSONObject(it))
            }
        } else {
            null
        }
    }

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getHashtags(entities: JSONObject): Array<HashtagEntity>? {
        return if (!entities.isNull("hashtags")) {
            val hashtagsArray = entities.getJSONArray("hashtags")
            val len = hashtagsArray.length()
            Array(len) {
                HashtagEntityJSONImpl(hashtagsArray.getJSONObject(it))
            }
        } else {
            null
        }
    }

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getSymbols(entities: JSONObject): Array<SymbolEntity>? {
        return if (!entities.isNull("symbols")) {
            val symbolsArray = entities.getJSONArray("symbols")
            val len = symbolsArray.length()
            Array(len) {
                // HashtagEntityJSONImpl also implements SymbolEntities
                HashtagEntityJSONImpl(symbolsArray.getJSONObject(it))
            }
        } else {
            null
        }
    }

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getMedia(entities: JSONObject): Array<MediaEntity>? {
        return if (!entities.isNull("media")) {
            val mediaArray = entities.getJSONArray("media")
            val len = mediaArray.length()
            Array(len) {
                MediaEntityJSONImpl(mediaArray.getJSONObject(it))
            }
        } else {
            null
        }
    }
}
