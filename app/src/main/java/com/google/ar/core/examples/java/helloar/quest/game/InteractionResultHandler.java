package com.google.ar.core.examples.java.helloar.quest.game;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.examples.java.helloar.GameApi;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.game.InteractionResult;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;

import java.util.Locale;

public class InteractionResultHandler {
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
                onInventoryUpdateResult(result, activity);
                break;
            default:
                onResultFallback(result, activity);
        }
    }

    private void onInventoryUpdateResult(final InteractionResult result, final Activity activity) {
        Slot.RepeatedItem repeatedItem = result.getItems();
        GameApi.getInventories().getCurrentInventory().put(repeatedItem);
        showMsg(
                String.format(
                        Locale.ENGLISH,
                        activity.getString(R.string.inventory_updated_str),
                        repeatedItem.getCnt(), repeatedItem.getItem().getName()
                ), activity
        );
    }

    private void onJournalUpdateResult(final InteractionResult result, final Activity activity) {
        GameApi.getJournals().getCurrentJournal().addNow(result.getMsg());
        showMsg(activity.getString(R.string.journal_updated_str), activity);
    }

    private void onMessageResult(final InteractionResult result, final Activity activity) {
        showMsg(result.getMsg(), activity);
    }

    private void onResultFallback(final InteractionResult result, final Activity activity) {
        String msg = String.format(
                Locale.ENGLISH,
                "Got interaction result with type %s, msg \"%s\", and %d items",
                result.getType(), result.getMsg(), result.getItems().getCnt()
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
