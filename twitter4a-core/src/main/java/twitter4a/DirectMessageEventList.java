package twitter4a;

/**
 * List of {@link DirectMessageEvent}
 *
 * like string cursor version of {@link PagableResponseList}
 *
 * @author Hiroaki TAKEUCHI - takke30 at gmail.com
 * @since Twitter4J 4.1.0-beta1
 */
public interface DirectMessageEventList extends ResponseList<DirectMessageEvent> {

    String getNextCursor();
}
