package twitter4a;

import java.util.Date;

/**
 * @author Hiroaki TAKEUCHI - takke30 at gmail.com
 * @since Twitter4J 4.1.0-beta1
 */
public interface DirectMessageEvent extends TwitterResponse {

    String getType();

    long getId();

    Date getCreatedTimestamp();

    long getRecipientId();

    long getSenderId();

    String getText();

    UserMentionEntity[] getUserMentionEntities();

    URLEntity[] getUrlEntities();

    HashtagEntity[] getHashtagEntities();

    MediaEntity[] getMediaEntities();

    SymbolEntity[] getSymbolEntities();
}
