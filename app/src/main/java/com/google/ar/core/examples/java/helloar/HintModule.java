package com.google.ar.core.examples.java.helloar;


import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.ar.core.examples.java.helloar.common.CustomViewUtils;

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
    private Activity activity;
    private boolean enabled;

    @Provides
    @Singleton
    public HintModule provideHelperModule() {
        return new HintModule();
    }

    public HintModule() {
        hintMap = new HashMap<>();
        hintRequests = new HashSet<>();
        finishedHints = new HashSet<>();
        enabled = false;
    }

    public void requestHint(int hintID) {
        if (enabled && hintMap.containsKey(hintID)) {
            showHintOnce(hintID);
        } else {
            hintRequests.add(hintID);
        }
    }

    public void setActivity(Activity activity) {
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // This aligns button to the bottom left side of screen
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // Set margins to the button, we add 16dp margins here
        int margin = ((Number) (activity.getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin * 5);

        sv = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setContentTitle("Помощь при прохождении")
                .setShowcaseEventListener(OnShowcaseEventListener.NONE)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        sv.hide();
        // Set declared button position to ShowcaseView
        sv.setButtonPosition(lps);
        CustomViewUtils.disableAllTouches(sv);
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
        if (!enabled || finishedHints.contains(hintID)) {
            return;
        }
        showHint(hintID);
        finishedHints.add(hintID);
    }

    private void showHint(int hintID) {
        if (!enabled) return;

        Hint hint = setUpHint(hintID);
        if (hint != null) {
            sv.show();
        }
    }

    private Hint setUpHint(int hintName) {
        if (sv == null) {
            return null;
        }
        final Hint hint = hintMap.get(hintName);
        if (hint != null) {
            hint.setUpHint(sv);
            sv.setOnShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    hint.onComplete();
                }

                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                }

                @Override
                public void onShowcaseViewShow(ShowcaseView showcaseView) {

                }

                @Override
                public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

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
