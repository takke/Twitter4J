package twitter4j;

import org.jetbrains.annotations.NotNull;

public interface EditControl {

    @NotNull
    long[] getEditTweetIds();

    long getEditableUntilMsecs();

    int getEditsRemaining();

    boolean isEditEligible();

}
