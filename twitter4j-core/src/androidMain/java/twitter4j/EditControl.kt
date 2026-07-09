package twitter4j

interface EditControl {

    val editTweetIds: LongArray

    val editableUntilMsecs: Long

    val editsRemaining: Int

    val isEditEligible: Boolean

}
