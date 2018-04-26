package edu.technopark.arquest;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HintModule {
    @Inject
    Context context;

    public interface Hint {
        void setUpHint(ShowcaseView sv);

        void onComplete();
    }

    public static abstract class NoCompleteHint implements Hint {
        @Override
        public void onComplete() {

        }
    }

    private ShowcaseView sv;
    private Map<Integer, Hint> hintMap;
    private Set<Integer> hintRequests;
    private Set<Integer> finishedHints;
    private Set<String> callerSet;  // enables to call hints once for all instances of activity
    private Activity activity;
    private boolean enabled;

    @Provides
    @Singleton
    public HintModule provideHelperModule() {
        App.getAppComponent().inject(this);
        return this;
    }

    public HintModule() {
        hintMap = new HashMap<>();
        hintRequests = new HashSet<>();
        finishedHints = new HashSet<>();
        callerSet = new HashSet<>();
        enabled = true;
    }

    public Set<String> getCallerSet() {
        return callerSet;
    }

    public void requestHint(int hintID) {
        if (enabled && hintMap.containsKey(hintID)) {
            showHintOnce(hintID);
        } else {
            hintRequests.add(hintID);
        }
    }

    public void setActivity(Activity activity) {
        sv = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setContentTitle("Помощь при прохождении")
                .setShowcaseEventListener(OnShowcaseEventListener.NONE)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        sv.hide();
        Button btn = (Button) sv.getChildAt(0);
        btn.setBackgroundColor(context.getColor(R.color.transparent));
        btn.setTextColor(context.getColor(R.color.transparent));
        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv.hide();
                return true;
            }
        });
        this.activity = activity;
    }

    public void resetActivity() {
        sv = null;
    }

    public void clearHintShowHistory() {
        hintRequests.clear();
        finishedHints.clear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void replaceHint(int id, Hint hint) {
        addHint(id, hint, true);
    }

    public void addHint(int id, Hint hint) {
        addHint(id, hint, false);
    }

    public void clearHints() {
        hintMap.clear();
    }

    public void showHintOnce(int hintID) {
        showHintOnce(hintID, null);
    }

    public void showHintChainOnce(int... hintIDs) {
        showHintChainInner(hintIDs, true);
    }

    public void showHintChain(int... hintIDs) {
        showHintChainInner(hintIDs, false);
    }

    private void showHintChainInner(final int[] hintIDs, final boolean isOnce) {
        if (hintIDs.length == 0) {
            return;
        }

        showHint(hintIDs[0], new OnShowcaseEventListener() {
            int localCounter = 0;
            long lastTime = System.currentTimeMillis();

            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {}

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                if (!enabled || localCounter >= hintIDs.length) {
                    sv.setOnShowcaseEventListener(null);

                    if (isOnce) {
                        for (int id : hintIDs) {
                            finishedHints.add(id);
                        }
                    }
                    return;
                }
                showHint(hintIDs[localCounter], this);
            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {
                long currTime = System.currentTimeMillis();
                if (currTime - lastTime < 200) {
                    return;
                } else {
                    lastTime = currTime;
                }
                ++localCounter;
            }

            @Override
            public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {}
        });
    }

    private void showHintOnce(int hintID, OnShowcaseEventListener outerListener) {
        if (!enabled || finishedHints.contains(hintID)) {
            return;
        }
        showHint(hintID, outerListener);
        finishedHints.add(hintID);
    }

    private void showHint(int hintID) {
        showHint(hintID, null);
    }

    private void showHint(int hintID, OnShowcaseEventListener outerListener) {
        if (!enabled) return;

        Hint hint = setUpHint(hintID, outerListener);
        if (hint != null) {
            sv.show();
        }
    }

    private Hint setUpHint(int hintName, final OnShowcaseEventListener outerListener) {
        if (sv == null) {
            return null;
        }
        final Hint hint = hintMap.get(hintName);
        if (hint != null) {
            hint.setUpHint(sv);
            sv.setOnShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    if (outerListener != null) outerListener.onShowcaseViewHide(showcaseView);
                    hint.onComplete();
                }

                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                    if (outerListener != null) outerListener.onShowcaseViewDidHide(showcaseView);
                }

                @Override
                public void onShowcaseViewShow(ShowcaseView showcaseView) {
                    if (outerListener != null) outerListener.onShowcaseViewShow(showcaseView);
                }

                @Override
                public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
                    if (outerListener != null) outerListener.onShowcaseViewTouchBlocked(motionEvent);
                }
            });
            return hint;
        }
        return null;
    }

    private void addHint(int id, Hint hint, boolean needReplace) {
        if (finishedHints.contains(id)) {
            return;
        }
        if (needReplace || !hintMap.containsKey(id)) {
            hintMap.put(id, hint);
        }
        if (hintRequests.contains(id)) {
            showHintOnce(id);
        }
    }
}
