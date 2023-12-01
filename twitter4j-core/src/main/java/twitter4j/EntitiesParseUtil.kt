package twitter4j

object EntitiesParseUtil {

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getUserMentions(entities: JSONObject): Array<UserMentionEntity?>? {
        return if (!entities.isNull("user_mentions")) {
            val userMentionsArray = entities.getJSONArray("user_mentions")
            val len = userMentionsArray.length()
            val userMentionEntities = arrayOfNulls<UserMentionEntity>(len)
            for (i in 0 until len) {
                userMentionEntities[i] = UserMentionEntityJSONImpl(userMentionsArray.getJSONObject(i))
            }
            userMentionEntities
        } else {
            null
        }
    }

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getUrls(entities: JSONObject): Array<URLEntity?>? {
        return if (!entities.isNull("urls")) {
            val urlsArray = entities.getJSONArray("urls")
            val len = urlsArray.length()
            val urlEntities = arrayOfNulls<URLEntity>(len)
            for (i in 0 until len) {
                urlEntities[i] = URLEntityJSONImpl(urlsArray.getJSONObject(i))
            }
            urlEntities
        } else {
            null
        }
    }

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getHashtags(entities: JSONObject): Array<HashtagEntity?>? {
        return if (!entities.isNull("hashtags")) {
            val hashtagsArray = entities.getJSONArray("hashtags")
            val len = hashtagsArray.length()
            val hashtagEntities = arrayOfNulls<HashtagEntity>(len)
            for (i in 0 until len) {
                hashtagEntities[i] = HashtagEntityJSONImpl(hashtagsArray.getJSONObject(i))
            }
            hashtagEntities
        } else {
            null
        }
    }

    @JvmStatic
    @Throws(JSONException::class, TwitterException::class)
    fun getSymbols(entities: JSONObject): Array<SymbolEntity?>? {
        return if (!entities.isNull("symbols")) {
            val symbolsArray = entities.getJSONArray("symbols")
            val len = symbolsArray.length()
            val symbolEntities = arrayOfNulls<SymbolEntity>(len)
            for (i in 0 until len) {
                // HashtagEntityJSONImpl also implements SymbolEntities
                symbolEntities[i] = HashtagEntityJSONImpl(symbolsArray.getJSONObject(i))
            }
            symbolEntities
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
