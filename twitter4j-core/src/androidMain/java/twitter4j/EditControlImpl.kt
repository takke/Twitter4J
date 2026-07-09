package twitter4j

class EditControlImpl
@Throws(TwitterException::class)
constructor(json: JSONObject) : EditControl {

    override val editTweetIds: LongArray
    override val editableUntilMsecs: Long
    override val editsRemaining: Int
    override val isEditEligible: Boolean

    init {
        val idList = json.getJSONArray("edit_tweet_ids")
        val ids = LongArray(idList.length())
        for (i in 0 until idList.length()) {
            try {
                ids[i] = idList.getString(i).toLong()
            } catch (nfe: NumberFormatException) {
                throw TwitterException("Twitter API returned malformed response: $json", nfe)
            }
        }
        editTweetIds = ids

        editableUntilMsecs = json.getLong("editable_until_msecs")
        editsRemaining = json.getInt("edits_remaining")
        isEditEligible = json.getBoolean("is_edit_eligible")
    }
}
