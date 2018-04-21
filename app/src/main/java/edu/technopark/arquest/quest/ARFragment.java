package edu.technopark.arquest.quest;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.common.base.Function;
import com.viro.core.ARScene;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.HintModule;
import edu.technopark.arquest.PermissionHelper;
import edu.technopark.arquest.R;
import edu.technopark.arquest.common.ContinuousAction;
import edu.technopark.arquest.game.InteractionResult;
import edu.technopark.arquest.game.InteractiveObject;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.slot.Slot;
import edu.technopark.arquest.model.Quest;

public class ARFragment extends Fragment {
    public static final String TAG = ARFragment.class.getSimpleName();

    @BindView(R.id.viro_view)
    ViroView viroView;

    @BindView(R.id.collision_txt)
    TextView collisionText;

    @BindView(R.id.inventory_btn)
    Button toInventoryBtn;

    @BindView(R.id.journal_btn)
    Button toJournalBtn;

    @BindView(R.id.interact_btn)
    Button interactBtn;

    @Inject
    GameModule gameModule;

    @Inject
    HintModule hintModule;

    @Inject
    Context context;

    ContinuousAction snackbarAction = new ContinuousAction(
            new Runnable() {
                @Override
                public void run() {
                    showSnackbarMessage(getString(R.string.direct_camera_to_floor_str), false);
                    hideButtons();
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    Quest quest = gameModule.getCurrentQuest();
                    String currPurpose = null;
                    if (quest != null) {
                        currPurpose = quest.getCurrPurpose();
                    }
                    currPurpose = currPurpose == null ? "Найдите объект дополненной реальности неподалеку" : currPurpose;
                    setPurpose(currPurpose);
                    showButtons();
                }
            }
    );

    private boolean installRequested;
    private Snackbar messageSnackbar;

    private View.OnClickListener toInventoryOnClickListener;

    private List<InteractiveObject> collidedObjects = new ArrayList<>();

    // todo use clicks on objects to detect interactions
//    private DeferredClickListener interactor = new DeferredClickListener() {
//        private boolean needActualize = false;
//
//        @Override
//        public void actualize() {
//            if (needActualize) {
//                gameModule.getScene().getPhysicsWorld().
//                gameModule.getScene().getCollisions(gameModule.getPlayer().getCollider(), collidedObjects);
//                if (interact() == null) {   // user intended to release item
//                    gameModule.getPlayer().release();
//                }
//
//                collidedObjects.clear();
//                needActualize = false;
//            }
//        }
//
//        @Override
//        public void onClick(View v) {
//            needActualize = true;
//        }
//    };

    private View.OnClickListener toJournalOnClickListener;
    //private View.OnClickListener closeOnClickListener;

    public ARFragment() {
        super();
        App.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_ar, container, false);
        ButterKnife.bind(this, view);

        toInventoryBtn.setOnClickListener(toInventoryOnClickListener);
        toJournalBtn.setOnClickListener(toJournalOnClickListener);

        toInventoryBtn.setOnClickListener(toInventoryOnClickListener);

        // todo use clicks on objects to detect interactions
//        interactBtn.setOnClickListener(interactor);

        viroView = new ViroViewARCore(context, new ViroViewARCore.StartupListener() {
            @Override
            public void onSuccess() {
                ARScene scene = gameModule.getScene();
                if (scene == null) {
                    return;
                }
                viroView.dispose();
                viroView.setScene(scene);
            }

            @Override
            public void onFailure(ViroViewARCore.StartupError startupError, String s) {
                // todo add fail handling
            }
        });

        installRequested = false;

        snackbarAction.startIfNotRunning();

        setUpHints();
        return view;
    }

    public void setDecorations(Place place) {
        if (place != null) {
            gameModule.setCurrentPlace(place);
        }
    }

    public void setToInventoryOnClickListener(View.OnClickListener listener) {
        toInventoryOnClickListener = listener;
        if (toInventoryBtn != null) {
            toInventoryBtn.setOnClickListener(toInventoryOnClickListener);
        }
    }

    public void setToJournalOnClickListener(View.OnClickListener listener) {
        toJournalOnClickListener = listener;
        if (toJournalBtn != null) {
            toJournalBtn.setOnClickListener(toJournalOnClickListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (PermissionHelper.hasPermissions(getActivity())) {
            viroView.onActivityResumed(getActivity());
        } else {
            PermissionHelper.requestPermissions(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        viroView.onActivityPaused(getActivity());
        hideSnackbarMessage();
    }

    @Override
    public void onStop() {
        super.onStop();
        viroView.onActivityStopped(getActivity());
        snackbarAction.stopIfRunning();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionHelper.hasPermissions(getActivity())) {
            Toast.makeText(getActivity(), "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            getActivity().finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractionResult(InteractionResult interactionResult) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        switch (interactionResult.getType()) {
            case NEW_ITEMS:
                onNewItemsResult(interactionResult, activity);
                break;
            case TAKE_ITEMS:
                onTakeItemsResult(interactionResult, activity);
                break;
            case JOURNAL_RECORD:
                onJournalUpdateResult(interactionResult, activity);
                break;
            case MESSAGE:
                onMessageResult(interactionResult, activity);
                break;
            case HINT:
                onHintResult(interactionResult, activity);
                break;
            case NEXT_PURPOSE:
                onNextPurposeResult(interactionResult, activity);
                break;
        }
    }

    private void showSnackbarMessage(String message, boolean finishOnDismiss) {
        messageSnackbar =
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_INDEFINITE);
        messageSnackbar.getView().setBackgroundColor(0xbf323232);
        if (finishOnDismiss) {
            messageSnackbar.setAction(
                    "Dismiss",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messageSnackbar.dismiss();
                        }
                    });
            messageSnackbar.addCallback(
                    new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            Activity activity = getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                        }
                    });
        }
        messageSnackbar.show();
    }

    private void hideSnackbarMessage() {
        if (messageSnackbar != null) {
            messageSnackbar.dismiss();
        }
    }

    private void showButtons() {
        toInventoryBtn.setAlpha(1);
        toJournalBtn.setAlpha(1);
        interactBtn.setAlpha(1);
    }

    private void hideButtons() {
        toInventoryBtn.setAlpha(0);
        toJournalBtn.setAlpha(0);
        interactBtn.setAlpha(0);
    }

    private void setUpHints() {
        hintModule.addHint(R.id.interact_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText(getString(R.string.act_btn_hint_str));
                input.setTarget(new ViewTarget(interactBtn));
                return null;
            }
        }));

        hintModule.addHint(R.id.journal_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Нажмите на эту кнопку, чтобы посмотреть список событий данного квеста");
                input.setTarget(new ViewTarget(toJournalBtn));
                return null;
            }
        }));

        hintModule.addHint(R.id.inventory_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Нажмите на эту кнопку, чтобы посмотреть вещи в инвентаре");
                input.setTarget(new ViewTarget(toInventoryBtn));
                return null;
            }
        }));

        hintModule.addHint(R.id.release_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Нажмите на эту кнопку, чтобы вернуть предмет в инвентарь");
                input.setTarget(new ViewTarget(interactBtn));
                return null;
            }
        }));

        hintModule.requestHint(R.id.inventory_item_hint);
    }

    private void setPurpose(final String purpose) {
        if (purpose == null) {
            return;
        }
        gameModule.getCurrentQuest().setCurrPurpose(purpose);
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageSnackbar.setText(purpose);
                }
            });
        }
    }

    private HintModule.Hint getARScreenHint(final Function<ShowcaseView, Void> callable) {
        return new HintModule.Hint() {
            @Override
            public void setUpHint(final ShowcaseView sv) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callable.apply(sv);
                            hideSnackbarMessage();
                        }
                    });
                }
            }

            @Override
            public void onComplete() {
                final Quest quest = gameModule.getCurrentQuest();
                if (quest == null) {
                    return;
                }

                Activity activity = getActivity();
                if (activity != null && isVisible()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSnackbarMessage(quest.getCurrPurpose(), false);
                        }
                    });
                }
            }
        };
    }

    private void onNewItemsResult(final InteractionResult result, final Activity activity) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        showMsg(
                String.format(
                        Locale.ENGLISH,
                        activity.getString(R.string.inventory_updated_str),
                        repeatedItem.getCnt(), repeatedItem.getItem().getName()
                ), activity
        );
    }

    private void onTakeItemsResult(final InteractionResult result, final Activity activity) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        showMsg(
                String.format(
                        Locale.ENGLISH,
                        "%d instanses of %s were taken",
                        repeatedItem.getCnt(), repeatedItem.getItem().getName()
                ), activity
        );
    }

    private void onJournalUpdateResult(final InteractionResult result, final Activity activity) {
        showMsg(result.getMsg(), activity);
        showMsg(activity.getString(R.string.journal_updated_str), activity);
    }

    private void onMessageResult(final InteractionResult result, final Activity activity) {
        showMsg(result.getMsg(), activity);
    }

    private void onHintResult(final InteractionResult result, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hintModule.showHintOnce(result.getEntityID());
            }
        });
    }

    private void onNextPurposeResult(final InteractionResult result, final Activity activity) {
        setPurpose(result.getMsg());
    }

    private void showMsg(final String msg, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
