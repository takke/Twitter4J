package twitter4j

data class Card(
    val url: String,

    // header image url : the largest image of multiple images
    val imageUrl: String?,
    val imageWidth: Int,
    val imageHeight: Int,

    val title: String,
)