package com.google.ar.core.examples.java.helloar;


import android.app.Activity;
import android.view.MotionEvent;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.ar.core.examples.java.helloar.common.CustomViewUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HintModule {
    public interface Hint {
        void setUpHint(ShowcaseView sv);

        Runnable getOnCompleteAction();
    }

    public static abstract class NoCompleteHint implements Hint {
        @Override
        public Runnable getOnCompleteAction() {
            return new Runnable() {
                @Override
                public void run() {}
            };
        }
    }

    private ShowcaseView sv;
    private Map<Integer, Hint> hintMap;
    private Set<Integer> hintRequests;
    private Activity activity;

    @Provides
    @Singleton
    public HintModule provideHelperModule() {
        return new HintModule();
    }

    public HintModule() {
        hintMap = new HashMap<>();
        hintRequests = new HashSet<>();
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
        hintMap.put(id, hint);
        if (hintRequests.contains(id)) {
            hint.setUpHint(sv);
            sv.show();
            hintRequests.remove(id);  // hint should be run only once
        }
    }

    public void clearHints() {
        hintMap.clear();
    }

    public void showHintOnce(int hintName) {
        showHint(hintName);
        hintMap.remove(hintName);
    }

    public void showHint(int hintName) {
        if (sv == null) {
            return;
        }
        final Hint hint = hintMap.get(hintName);
        if (hint != null) {
            hint.setUpHint(sv);
            sv.setOnShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    hint.getOnCompleteAction().run();
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
            sv.show();
        }
    }
}
