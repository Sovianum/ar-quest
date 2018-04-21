package edu.technopark.arquest;


import android.app.Activity;
import android.content.Context;
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
