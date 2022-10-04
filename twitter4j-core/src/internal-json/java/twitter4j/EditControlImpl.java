package twitter4j;

import org.jetbrains.annotations.NotNull;

public class EditControlImpl implements EditControl {

    private final long[] editTweetIds;
    private final long editableUntilMsecs;
    private final int editsRemaining;
    private final boolean editEligible;

    public EditControlImpl(JSONObject json) throws TwitterException {
        JSONArray idList = json.getJSONArray("edit_tweet_ids");
        long[] ids = new long[idList.length()];
        for (int i = 0; i < idList.length(); i++) {
            try {
                ids[i] = Long.parseLong(idList.getString(i));
            } catch (NumberFormatException nfe) {
                throw new TwitterException("Twitter API returned malformed response: " + json, nfe);
            }
        }
        editTweetIds = ids;

        editableUntilMsecs = json.getLong("editable_until_msecs");
        editsRemaining = json.getInt("edits_remaining");
        editEligible = json.getBoolean("is_edit_eligible");

    }

    @NotNull
    @Override
    public long[] getEditTweetIds() {
        return editTweetIds;
    }

    @Override
    public long getEditableUntilMsecs() {
        return editableUntilMsecs;
    }

    @Override
    public int getEditsRemaining() {
        return editsRemaining;
    }

    @Override
    public boolean isEditEligible() {
        return editEligible;
    }
}
