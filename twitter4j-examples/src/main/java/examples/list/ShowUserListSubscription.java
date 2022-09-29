/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package examples.list;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;

/**
 * Checks list subscription.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class ShowUserListSubscription {
    /**
     * Usage: java twitter4j.examples.list.ShowUserListSubscription [list id] [user id]
     *
     * @param args message
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java twitter4j.examples.list.ShowUserListSubscription [list id] [user id]");
            System.exit(-1);
        }
        try {
            Twitter twitter = Twitter.getInstance();
            long listId = Long.parseLong(args[0]);
            UserList list = twitter.showUserList(listId);
            long userId = Integer.parseInt(args[1]);
            User user = twitter.showUser(userId);
            try {
                twitter.showUserListSubscription(listId, userId);
                System.out.println("@" + user.getScreenName() + " is subscribing the list:" + list.getName());
            } catch (TwitterException te) {
                if (te.getStatusCode() == 404) {
                    System.out.println("@" + user.getScreenName() + " is not subscribing  the list:" + list.getName());
                }
            }
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to check user subscription: " + te.getMessage());
            System.exit(-1);
        }
    }
}
