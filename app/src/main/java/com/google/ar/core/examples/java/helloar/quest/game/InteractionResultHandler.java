package com.google.ar.core.examples.java.helloar.quest.game;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.GameModule;
import com.google.ar.core.examples.java.helloar.HintModule;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.game.InteractionResult;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.script.ScriptAction;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public class InteractionResultHandler {
    @Inject
    GameModule gameModule;

    @Inject
    HintModule hintModule;

    public InteractionResultHandler() {
        App.getAppComponent().inject(this);
    }

    public void onInteractionResult(final InteractionResult result, final Activity activity) {
        switch (result.getType()) {
            case MESSAGE:
                onMessageResult(result, activity);
                break;
            case JOURNAL_RECORD:
                onJournalUpdateResult(result, activity);
                break;
            case ERROR:
                onMessageResult(result, activity);
                break;
            case NEW_ITEMS:
                onNewItemsResult(result, activity);
                break;
            case TAKE_ITEMS:
                onTakeItemsResult(result, activity);
                break;
            case TRANSITIONS:
                onTransitionsResult(result, activity);
                break;
            case HINT:
                onHintResult(result, activity);
                break;
            default:
                onResultFallback(result, activity);
        }
    }

    private void onTransitionsResult(final InteractionResult result, final Activity activity) {
        Place currPlace = gameModule.getPlayer().getPlace();
        Map<Integer, InteractiveObject> interactiveObjectMap = currPlace.getInteractiveObjects();
        for (ScriptAction.StateTransition transition : result.getTransitions()) {
            interactiveObjectMap
                    .get(transition.getTargetObjectID())
                    .setCurrentStateID(transition.getTargetStateID());
        }
    }

    private void onNewItemsResult(final InteractionResult result, final Activity activity) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        gameModule.getCurrentInventory().put(repeatedItem);
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
        gameModule.getCurrentInventory().remove(repeatedItem.getItem().getId(), repeatedItem.getCnt());
        gameModule.getPlayer().release();
        showMsg(
                String.format(
                        Locale.ENGLISH,
                        "%d instanses of %s were taken",
                        repeatedItem.getCnt(), repeatedItem.getItem().getName()
                ), activity
        );
    }

    private void onJournalUpdateResult(final InteractionResult result, final Activity activity) {
        gameModule.getCurrentJournal().addNow(result.getMsg());
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

    private void onResultFallback(final InteractionResult result, final Activity activity) {
        String msg = String.format(
                Locale.ENGLISH,
                "Got interaction result with type %s",
                result.getType()
        );
        Log.i("INTERACTION", msg);
        showMsg(msg, activity);
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
