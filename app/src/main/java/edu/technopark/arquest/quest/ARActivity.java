package edu.technopark.arquest.quest;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.common.base.Function;
import com.viro.core.ARScene;
import com.viro.core.Box;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.HintModule;
import edu.technopark.arquest.MainActivity;
import edu.technopark.arquest.PermissionHelper;
import edu.technopark.arquest.R;
import edu.technopark.arquest.common.ContinuousAction;
import edu.technopark.arquest.game.InteractionResult;
import edu.technopark.arquest.game.InteractiveObject;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.slot.Slot;
import edu.technopark.arquest.model.Quest;
import edu.technopark.arquest.quest.items.ItemsListFragment;
import edu.technopark.arquest.quest.journal.JournalFragment;

public class ARActivity extends Activity {
    public static final String TAG = ARActivity.class.getSimpleName();

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

    private Snackbar messageSnackbar;

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

    public ARActivity() {
        super();
        App.getAppComponent().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (gameModule.isWithAR()) {
            viroView = new ViroViewARCore(this, new ViroViewARCore.StartupListener() {
                @Override
                public void onSuccess() {viroView.setScene(gameModule.getScene());}

                @Override
                public void onFailure(ViroViewARCore.StartupError startupError, String s) {
                    // todo add fail handling
                }
            });
            setContentView(viroView);
            View.inflate(this, R.layout.fragment_ar, viroView);
        } else {
            setContentView(R.layout.fragment_ar);
        }
        ButterKnife.bind(this);

        // todo use clicks on objects to detect interactions
//        interactBtn.setOnClickListener(interactor);

//        snackbarAction.startIfNotRunning();
//        setUpHints();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viroView != null) viroView.onActivityStarted(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (PermissionHelper.hasPermissions(this)) {
            if (viroView != null) viroView.onActivityResumed(this);
        } else {
            PermissionHelper.requestPermissions(this);
        }

        if (gameModule.isWithAR()) {
            loadPlace(gameModule.getCurrentPlace());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viroView != null) viroView.onActivityPaused(this);
        hideSnackbarMessage();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (viroView != null) viroView.onActivityStopped(this);
        snackbarAction.stopIfRunning();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viroView != null) viroView.onActivityDestroyed(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionHelper.hasPermissions(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            finish();
        }
    }

    private void loadPlace(Place place) {
        if (place == null) {
            return;
        }
        Node root = gameModule.getScene().getRootNode();
        for (Object3D object3D : place.getAll()) {
            root.addChildNode(object3D);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractionResult(InteractionResult interactionResult) {
        switch (interactionResult.getType()) {
            case NEW_ITEMS:
                onNewItemsResult(interactionResult, this);
                break;
            case TAKE_ITEMS:
                onTakeItemsResult(interactionResult, this);
                break;
            case JOURNAL_RECORD:
                onJournalUpdateResult(interactionResult, this);
                break;
            case MESSAGE:
                onMessageResult(interactionResult, this);
                break;
            case HINT:
                onHintResult(interactionResult, this);
                break;
            case NEXT_PURPOSE:
                onNextPurposeResult(interactionResult, this);
                break;
        }
    }

    @OnClick(R.id.inventory_btn)
    void toInventory() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(ItemsListFragment.TAG);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.journal_btn)
    void toJournal() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(JournalFragment.TAG);
        startActivity(intent);
        finish();
    }

    private void showSnackbarMessage(String message, boolean finishOnDismiss) {
        messageSnackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
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
                            finish();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageSnackbar.setText(purpose);
            }
        });
    }

    private HintModule.Hint getARScreenHint(final Function<ShowcaseView, Void> callable) {
        return new HintModule.Hint() {
            @Override
            public void setUpHint(final ShowcaseView sv) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callable.apply(sv);
                        hideSnackbarMessage();
                    }
                });
            }

            @Override
            public void onComplete() {
                final Quest quest = gameModule.getCurrentQuest();
                if (quest == null) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackbarMessage(quest.getCurrPurpose(), false);
                    }
                });
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
