package twitter4j

/**
 * List of [DirectMessage]
 *
 * like string cursor version of [PagableResponseList]
 *
 * @author Hiroaki TAKEUCHI - takke30 at gmail.com
 * @since Twitter4J 4.0.7
 */
interface DirectMessageList : ResponseList<DirectMessage> {

    val nextCursor: String
}
