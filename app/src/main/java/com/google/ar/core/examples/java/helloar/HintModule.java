package com.google.ar.core.examples.java.helloar;


import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

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

    @Provides
    @Singleton
    public HintModule provideHelperModule() {
        return new HintModule();
    }

    public HintModule() {
        hintMap = new HashMap<>();
        hintRequests = new HashSet<>();
        finishedHints = new HashSet<>();
    }

    public ShowcaseView getSV() {
        return sv;
    }

    public void requestHint(int hintID) {
        hintRequests.add(hintID);
    }

    public void setActivity(Activity activity) {
        sv = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setContentTitle("Помощь при прохождении")
                .setShowcaseEventListener(OnShowcaseEventListener.NONE)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        CustomViewUtils.disableAllTouches(sv);
        this.activity = activity;
    }

    public void resetActivity() {
        sv = null;
    }

    public void addAndShow(Hint hint) {
        hint.setUpHint(sv);
        sv.show();
    }

    public void addHint(int id, Hint hint) {
        if (finishedHints.contains(id)) {
            return;
        }
        hintMap.put(id, hint);
        if (hintRequests.contains(id)) {
            hint.setUpHint(sv);
            sv.show();
            hintRequests.remove(id);  // hint should be run only once
            finishedHints.add(id);
        }
    }

    public void clearHints() {
        hintMap.clear();
    }

    public void showHintOnce(int hintID) {
        if (finishedHints.contains(hintID)) {
            return;
        }
        showHint(hintID);
        hintMap.remove(hintID);
        finishedHints.add(hintID);
    }

    public void showHint(int hintID) {
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
}
