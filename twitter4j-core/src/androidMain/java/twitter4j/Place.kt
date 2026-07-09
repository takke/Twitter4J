package twitter4j

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.1
 */
interface Place : TwitterResponse, Comparable<Place>, java.io.Serializable {
    val name: String

    val streetAddress: String

    val countryCode: String

    val id: String

    val country: String

    val placeType: String

    val URL: String

    val fullName: String

    val boundingBoxType: String

    val boundingBoxCoordinates: Array<Array<GeoLocation>>

    val geometryType: String

    val geometryCoordinates: Array<Array<GeoLocation>>

    val containedWithIn: Array<Place>
}
