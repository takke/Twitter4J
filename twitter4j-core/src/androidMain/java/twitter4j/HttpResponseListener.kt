package twitter4j

/**
 * @author Andrew Hedges - andrew.hedges at gmail.com
 */
interface HttpResponseListener {

    fun httpResponseReceived(event: HttpResponseEvent)

}
