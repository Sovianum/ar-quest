package edu.technopark.arquest.game;


import android.util.Log;

import com.google.common.base.Function;

import java.util.List;

public class InteractionResultChain {
    private List<InteractionResult> results;
    private int currID = -1;
    private Function<InteractionResultChain, Void> callback;

    public InteractionResultChain(List<InteractionResult> results, Function<InteractionResultChain, Void> callback) {
        this.results = results;
        this.callback = callback;
    }

    public InteractionResult getCurrent() {
        return results.get(currID);
    }

    public void onNext() {
        Log.e("NEXT", "called on next");
        ++currID;
        if (results == null || currID >= results.size()) return;
        callback.apply(this);
    }
}
