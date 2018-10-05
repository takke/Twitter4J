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

package twitter4a.examples.directmessage;

import twitter4a.DirectMessage;
import twitter4a.Twitter;
import twitter4a.TwitterException;
import twitter4a.TwitterFactory;

/**
 * Example application that gets a specified direct message.<br>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class ShowDirectMessage {
    /**
     * Usage: java twitter4a.examples.directmessage.ShowDirectMessage [message id]
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java twitter4a.examples.directmessage.ShowDirectMessage [message id]");
            System.exit(-1);
        }
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            DirectMessage message = twitter.showDirectMessage(Long.parseLong(args[0]));
            System.out.println("From: @" + message.getSenderScreenName() + " id:" + message.getId() + " - "
                    + message.getText());
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get message: " + te.getMessage());
            System.exit(-1);
        }
    }
}