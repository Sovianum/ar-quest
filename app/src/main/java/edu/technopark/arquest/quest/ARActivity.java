package edu.technopark.arquest.quest;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.common.base.Function;
import com.viro.core.ARHitTestListener;
import com.viro.core.ARHitTestResult;
import com.viro.core.ARScene;
import com.viro.core.CameraListener;
import com.viro.core.Vector;
import com.viro.core.ViroViewARCore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
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
import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.slot.Slot;
import edu.technopark.arquest.model.Quest;
import edu.technopark.arquest.quest.items.ItemAdapter;
import edu.technopark.arquest.quest.items.ItemsListFragment;
import edu.technopark.arquest.quest.journal.JournalFragment;
import edu.technopark.arquest.quest.place.PlaceFragment;
import edu.technopark.arquest.quest.quests.QuestsListFragment;
import edu.technopark.arquest.settings.SettingsFragment;

public class ARActivity extends AppCompatActivity {
    public static final String TAG = ARActivity.class.getSimpleName();

    public static class CameraUpdateEvent {
        public Vector position;
        public Vector rotation;
        public Vector forward;

        public CameraUpdateEvent(Vector position, Vector rotation, Vector forward) {
            this.position = position;
            this.rotation = rotation;
            this.forward = forward;
        }
    }

    private ARHitTestListener planeDetector = new ARHitTestListener() {
        @Override
        public void onHitTestFinished(ARHitTestResult[] arHitTestResults) {
            if (arHitTestResults == null || arHitTestResults.length ==0) {
                return;
            }

            final Vector cameraPos  = viroView.getLastCameraPositionRealtime();

            // Grab the closest ar hit target
            float closestDistance = Float.MAX_VALUE;
            ARHitTestResult result = null;
            for (ARHitTestResult currentResult : arHitTestResults) {
                float distance = currentResult.getPosition().distance(cameraPos);
                if (distance < closestDistance && distance > .3 && distance < 5) {
                    result = currentResult;
                    closestDistance = distance;
                }
            }

            // if found plane, stop tracking planes
            if (result != null){
                snackbarAction.stopIfRunning();
                gameModule.getScene().displayPointCloud(false);
                gameModule.loadCurrentPlace(result.getPosition());
                viroView.setCameraARHitTestListener(null);
            }
        }
    };

    ViroViewARCore viroView;

    @BindView(R.id.collision_txt)
    TextView collisionText;

    @BindView(R.id.inventory_btn)
    ImageButton toInventoryBtn;

    @BindView(R.id.journal_btn)
    ImageButton toJournalBtn;

    @BindView(R.id.interact_btn)
    ImageButton interactBtn;

    @BindView(R.id.return_inventory_btn)
    ImageButton returnItemToInventoryBtn;

    @BindView(R.id.close_btn)
    ImageButton closeBtn;

    @BindView(R.id.help_btn)
    ImageButton helpBtn;

    @BindView(R.id.inventory_help_text)
    TextView inventoryHelpTextView;

    @BindView(R.id.journal_help_text)
    TextView journalHelpTextView;

    @BindView(R.id.interact_help_text)
    TextView interactHelpTextView;

    @BindView(R.id.return_inventory_help_text)
    TextView returnItemToInventoryHelpTextView;

    @BindView(R.id.toolbar_actionbar)
    Toolbar toolBar;

    @Inject
    GameModule gameModule;

    @Inject
    HintModule hintModule;

    ContinuousAction snackbarAction = new ContinuousAction(
            new Runnable() {
                @Override
                public void run() {
                    showSnackbarMessage(getString(R.string.direct_camera_to_floor_str), false);
//                    hideButtons();
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    Place place = gameModule.getCurrentPlace();
                    if (place == null) {
                        return;
                    }
                    String currPurpose = place.getStartPurpose();
                    if (currPurpose != null) {
                        setPurpose(currPurpose);
                    } else {
                        setPurpose("Осмотритесь и попытайте счастье :)");
                    }
                    showButtons();
                    hintModule.showHintChain(R.id.interact_btn_hint);
                }
            }
    );

    private ItemAdapter.OnItemClickListener chooseItemOnClickListener = new ItemAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Item item) {
            gameModule.takePlayerItem(item);
            Toast.makeText(ARActivity.this, "Вы выбрали: " + item.getName(), Toast.LENGTH_SHORT).show();
            //EventBus.getDefault().post();
            showReturnItemViews();
            //TODO action to choose element
        }
    };

    private Snackbar messageSnackbar;
    private ItemsListFragment itemsListFragment;
    private JournalFragment journalFragment;

    public ARActivity() {
        super();
        App.getAppComponent().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        snackbarAction.startIfNotRunning();

        if (gameModule.isWithAR()) {
            viroView = new ViroViewARCore(this, new ViroViewARCore.StartupListener() {
                @Override
                public void onSuccess() {
                    ARScene scene = gameModule.getNewFreeScene();
                    scene.displayPointCloud(true);
                    viroView.setScene(scene);
                    viroView.setCameraARHitTestListener(planeDetector);
                }

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
        try {
            initFragments();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        changeToActivityLayout();
        setUpHints();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viroView != null) viroView.onActivityStarted(this);
        EventBus.getDefault().register(this);
        hintModule.setActivity(this);
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
//            gameModule.loadCurrentPlace();

            viroView.setCameraListener(new CameraListener() {
                @Override
                public void onTransformUpdate(Vector position, Vector rotation, Vector forward) {
                    EventBus.getDefault().post(new CameraUpdateEvent(position, rotation, forward));
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viroView != null) {
            viroView.onActivityPaused(this);
            viroView.setCameraListener(null);
        }
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
        if (viroView != null) {
            viroView.onActivityDestroyed(this);
//            viroView.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionHelper.hasPermissions(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            changeToActivityLayout();
        }
        super.onBackPressed();
        if (returnItemToInventoryBtn.getVisibility() == View.VISIBLE) {
            hintModule.showHintOnce(R.id.release_item_hint);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCanInteract(GameModule.CanInteract canInteractEvent) {
        if (canInteractEvent.canInteract) {
            interactBtnAndTextViewSetEnable(true);
            return;
        }
        //if (gameModule.getPlayer().getItem() != null) {
        //    interactBtnAndTextViewSetEnable(true);
        //    return;
        //}
        interactBtnAndTextViewSetEnable(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractionResult(InteractionResult interactionResult) {
        switch (interactionResult.getType()) {
            case NEW_ITEMS:
                onNewItemsResult(interactionResult);
                break;
            case TAKE_ITEMS:
                onTakeItemsResult(interactionResult);
                hideReturnItemViews();
                break;
            case JOURNAL_RECORD:
                onJournalUpdateResult(interactionResult);
                break;
            case MESSAGE:
                onMessageResult(interactionResult);
                break;
            case HINT:
                onHintResult(interactionResult);
                break;
            case NEXT_PURPOSE:
                onNextPurposeResult(interactionResult);
                break;
            case QUEST_END:
                showCongratulation();
                Intent intent = new Intent(ARActivity.this, MainActivity.class);
                intent.setAction(QuestsListFragment.TAG);
                startActivity(intent);
                overridePendingTransition( R.anim.from_down_to_center, R.anim.from_center_to_up_anim);
                finish();
                break;
        }
    }

    @OnClick(R.id.interact_btn)
    void interact() {
        gameModule.interactLastCollided();
    }

    @OnClick(R.id.return_inventory_btn)
    void interactReturn() {
        gameModule.getPlayer().release();
        hideReturnItemViews();
    }

    @OnClick(R.id.inventory_btn)
    void toInventory() {
        changeToFragmentLayout();
        selectFragment(itemsListFragment, ItemsListFragment.TAG);
    }

    @OnClick(R.id.journal_btn)
    void toJournal() {
        changeToFragmentLayout();
        selectFragment(journalFragment, JournalFragment.TAG);
    }


    @OnClick(R.id.help_btn)
    public void onHelpClickListener() {
        hintModule.showHintChain(R.id.interact_btn_hint, R.id.inventory_btn_hint, R.id.journal_btn_hint);
    }

    @OnClick(R.id.close_btn)
    public void onCloseClickListener() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(QuestFragment.TAG);
        startActivity(intent);
        overridePendingTransition( R.anim.from_down_to_center, R.anim.from_center_to_up_anim);
        //finish();
    }

    private void initFragments() throws FileNotFoundException {
        journalFragment = new JournalFragment();

        itemsListFragment = new ItemsListFragment();
        itemsListFragment.setOnItemClickListener(chooseItemOnClickListener);
    }

    private void showReturnItemViews() {
        returnItemToInventoryBtn.setVisibility(View.VISIBLE);
        if (inventoryHelpTextView.getVisibility() == View.VISIBLE) {
            returnItemToInventoryHelpTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideReturnItemViews() {
        returnItemToInventoryBtn.setVisibility(View.GONE);
        returnItemToInventoryHelpTextView.setVisibility(View.GONE);
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
        toInventoryBtn.setVisibility(View.VISIBLE);
        toJournalBtn.setVisibility(View.VISIBLE);
        interactBtn.setVisibility(View.VISIBLE);

        EventBus.getDefault().post(InteractionResult.hintResult(R.id.interact_btn_hint));
    }

    private void hideButtons() {
        toInventoryBtn.setVisibility(View.GONE);
        toJournalBtn.setVisibility(View.GONE);
        interactBtn.setVisibility(View.GONE);
    }

    private void setUpHints() {
        hintModule.replaceHint(R.id.interact_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText(getString(R.string.act_btn_hint_str));
                input.setTarget(new ViewTarget(interactBtn));
                return null;
            }
        }));

        hintModule.replaceHint(R.id.journal_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Нажмите на эту кнопку, чтобы посмотреть список событий данного квеста");
                input.setTarget(new ViewTarget(toJournalBtn));
                return null;
            }
        }));

        hintModule.replaceHint(R.id.inventory_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Нажмите на эту кнопку, чтобы посмотреть вещи в инвентаре");
                input.setTarget(new ViewTarget(toInventoryBtn));
                return null;
            }
        }));

        hintModule.replaceHint(R.id.release_btn_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Нажмите на эту кнопку, чтобы вернуть предмет в инвентарь");
                input.setTarget(new ViewTarget(returnItemToInventoryBtn));
                return null;
            }
        }));
        hintModule.replaceHint(R.id.first_item_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Вы получили предмет. Для того, чтобы взять его в руки, перейдите в инвентарь и нажмите на карточку предмета");
                input.setTarget(new ViewTarget(toInventoryBtn));
                return null;
            }
        }));
        hintModule.replaceHint(R.id.first_journal_message_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Ваш журнал обновлен. Для того, чтобы посмотреть записи, нажмите на эту кнопку");
                input.setTarget(new ViewTarget(toJournalBtn));
                return null;
            }
        }));
        hintModule.replaceHint(R.id.release_item_hint, getARScreenHint(new Function<ShowcaseView, Void>() {
            @Override
            public Void apply(@NonNull ShowcaseView input) {
                input.setContentText("Вы держите предмет в руках. Если вы хотите применить его к виртуальному объекту, подойдите к нему и нажмите на кнопку действия. Если вы хотите положить его обратно в инвентарь, нажмите на указанную кнопку");
                input.setTarget(new ViewTarget(returnItemToInventoryBtn));
                return null;
            }
        }));

        hintModule.requestHint(R.id.inventory_item_hint);
    }

    private void setPurpose(final String purpose) {
        if (purpose == null) {
            return;
        }
        messageSnackbar.setText(purpose);
        gameModule.getCurrentQuest().setCurrPurpose(purpose);
    }

    private HintModule.Hint getARScreenHint(final Function<ShowcaseView, Void> callable) {
        return new HintModule.Hint() {
            @Override
            public void setUpHint(final ShowcaseView sv) {
                callable.apply(sv);
            }

            @Override
            public void onComplete() {
                final Quest quest = gameModule.getCurrentQuest();
                if (quest == null) {
                    return;
                }
                showSnackbarMessage(quest.getCurrPurpose(), false);
            }
        };
    }

    private void onNewItemsResult(final InteractionResult result) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        showMsg(
                String.format(
                        Locale.ENGLISH,
                        getString(R.string.inventory_updated_str),
                        repeatedItem.getCnt(), repeatedItem.getItem().getName()
                )
        );
        hintModule.showHintOnce(R.id.first_item_hint);
    }

    private void onTakeItemsResult(final InteractionResult result) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        showMsg(
                String.format(
                        Locale.ENGLISH,
                        "%d %s изъяты из инвентаря",
                        repeatedItem.getCnt(), repeatedItem.getItem().getName()
                )
        );
    }

    private void onJournalUpdateResult(final InteractionResult result) {
        showMsg(result.getMsg());
        showMsg(getString(R.string.journal_updated_str));
        hintModule.showHintOnce(R.id.first_journal_message_hint);
    }

    private void onMessageResult(final InteractionResult result) {
        showMsg(result.getMsg());
    }

    private void onHintResult(final InteractionResult result) {
        hintModule.showHintOnce(result.getEntityID());
    }

    private void onNextPurposeResult(final InteractionResult result) {
        setPurpose(result.getMsg());
    }

    private void showMsg(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void selectFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;

        boolean needAdd = true;
        if (index >= 0) {
            if (isFragmentInBackstack(fragmentManager,tag)) {
                fragmentManager.popBackStackImmediate(tag, 0);
                needAdd = false;
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ar_fragment_container, fragment, tag);

        if (needAdd) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        setToolBarByFragment(tag);
    }

    private void changeToFragmentLayout() {
        findViewById(R.id.ar_buttons_layout).setVisibility(View.GONE);
        findViewById(R.id.ar_controls_layout).setVisibility(View.GONE);
        findViewById(R.id.ar_interact_button_layout).setVisibility(View.GONE);
        findViewById(R.id.return_item_layout).setVisibility(View.GONE);
        findViewById(R.id.ar_fragment_container).setVisibility(View.VISIBLE);
    }

    private void changeToActivityLayout() {
        findViewById(R.id.ar_buttons_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.ar_controls_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.return_item_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.ar_interact_button_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.ar_fragment_container).setVisibility(View.GONE);
    }

    private static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getName())) {
                return true;
            }
        }
        return false;
    }

    private void setToolBarByFragment(String fragmentTag) {
        if (QuestsListFragment.TAG.equals(fragmentTag)) {
            toolBar.setTitle(getString(R.string.quest_list_fragment_title));

        } else if (QuestFragment.TAG.equals(fragmentTag)) {
            toolBar.setTitle(getString(R.string.quest_fragment_title));

        } else if (JournalFragment.TAG.equals(fragmentTag)) {
            toolBar.setTitle(getString(R.string.journal_fragment_title));
            goBackByNavigationIcon();

        } else if (ItemsListFragment.TAG.equals(fragmentTag)) {
            toolBar.setTitle(getString(R.string.items_list_fragment));
            goBackByNavigationIcon();

        } else if (PlaceFragment.TAG.equals(fragmentTag)) {
            toolBar.setTitle(getString(R.string.place_fragment_title));
            goBackByNavigationIcon();

        } else if (SettingsFragment.TAG.equals(fragmentTag)) {
            toolBar.setTitle(getString(R.string.settings_fragment_title));
        }
    }

    private void goBackByNavigationIcon() {
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                toolBar.setNavigationIcon(null);
                toolBar.setNavigationOnClickListener(null);
            }
        });
    }

    private void interactBtnAndTextViewSetEnable(boolean enable) {
        interactBtn.setEnabled(enable);
        if (enable) {
            interactHelpTextView.setBackground(getResources().getDrawable(
                    R.drawable.round_text_view_style, this.getTheme()));
        } else {
            interactHelpTextView.setBackground(getResources().getDrawable(
                    R.drawable.round_text_view_disable_style, this.getTheme()));
        }
    }

    private void showCongratulation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.congrat_msg)
                .setTitle(R.string.congrat_title)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                gameModule.getPlayer().release();
                                gameModule.getCurrentInventory().clear();
                                gameModule.getCurrentJournal().clear();
                                gameModule.resetCurrentQuest();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
