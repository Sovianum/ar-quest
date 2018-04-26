package edu.technopark.arquest.game;

import java.util.Collection;

import edu.technopark.arquest.game.script.ScriptAction;
import edu.technopark.arquest.game.slot.Slot;

public class InteractionResult {
    public enum Type {
        NEW_ITEMS,
        TAKE_ITEMS,
        TRANSITIONS,
        JOURNAL_RECORD,
        NEW_PLACE,
        QUEST_END,
        MESSAGE,
        HINT,
        NEXT_PURPOSE,
        ERROR,
        LOSE
    }

    public static InteractionResult newItemsResult(Slot.RepeatedItem items) {
        InteractionResult result = new InteractionResult(Type.NEW_ITEMS);
        result.items = items;
        return result;
    }

    public static InteractionResult takeItemsResult(Slot.RepeatedItem items) {
        InteractionResult result = new InteractionResult(Type.TAKE_ITEMS);
        result.items = items;
        return result;
    }

    public static InteractionResult transitionsResult(Collection<ScriptAction.StateTransition> transitions) {
        InteractionResult result = new InteractionResult(Type.TRANSITIONS);
        result.transitions = transitions;
        return result;
    }

    public static InteractionResult journalRecordResult(String msg) {
        InteractionResult result = new InteractionResult(Type.JOURNAL_RECORD);
        result.msg = msg;
        return result;
    }

    public static InteractionResult messageResult(String msg) {
        InteractionResult result = new InteractionResult(Type.MESSAGE);
        result.msg = msg;
        return result;
    }

    public static InteractionResult newPlaceResult(int placeID) {
        InteractionResult result = new InteractionResult(Type.NEW_PLACE);
        result.entityID = placeID;
        return result;
    }

    public static InteractionResult questEndResult() {
        return new InteractionResult(Type.QUEST_END);
    }

    public static InteractionResult questLoseResult() {
        return new InteractionResult(Type.LOSE);
    }

    public static InteractionResult nextPurposeResult(String msg) {
        InteractionResult result = new InteractionResult(Type.NEXT_PURPOSE);
        result.msg = msg;
        return result;
    }

    public static InteractionResult errorResult(String msg) {
        InteractionResult result = new InteractionResult(Type.ERROR);
        result.msg = msg;
        return result;
    }

    public static InteractionResult hintResult(int hintID) {
        InteractionResult result = new InteractionResult(Type.HINT);
        result.entityID = hintID;
        return result;
    }

    Type type;
    String msg;
    Slot.RepeatedItem items;
    int entityID; // entityID has meaning only for NEW_PLACE and HINT type
    Collection<ScriptAction.StateTransition> transitions;

    private InteractionResult(Type type){this.type = type;}

    public Type getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public Slot.RepeatedItem getItems() {
        return items;
    }

    public int getEntityID() {
        return entityID;
    }

    public Collection<ScriptAction.StateTransition> getTransitions() {
        return transitions;
    }
}
