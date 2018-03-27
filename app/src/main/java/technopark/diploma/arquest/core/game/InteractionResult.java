package technopark.diploma.arquest.core.game;

import technopark.diploma.arquest.core.game.script.ScriptAction;
import technopark.diploma.arquest.core.game.slot.Slot;

import java.util.Collection;

public class InteractionResult {
    public enum Type {
        NEW_ITEMS,
        TAKE_ITEMS,
        TRANSITIONS,
        JOURNAL_RECORD,
        NEW_PLACE,
        QUEST_END,
        MESSAGE,
        ERROR,
    }

    public static InteractionResult newItemsResult(Slot.RepeatedItem items) {
        return new InteractionResult(Type.NEW_ITEMS, "", items, 0, null);
    }

    public static InteractionResult takeItemsResult(Slot.RepeatedItem items) {
        return new InteractionResult(Type.TAKE_ITEMS, "", items, 0, null);
    }

    public static InteractionResult transitionsResult(Collection<ScriptAction.StateTransition> transitions) {
        return new InteractionResult(Type.TRANSITIONS, "", null, 0, transitions);
    }

    public static InteractionResult journalRecordResult(String msg) {
        return new InteractionResult(Type.JOURNAL_RECORD, msg, null, 0, null);
    }

    public static InteractionResult messageResult(String msg) {
        return new InteractionResult(Type.MESSAGE, msg, null, 0, null);
    }

    public static InteractionResult newPlaceResult(int placeID) {
        return new InteractionResult(Type.NEW_PLACE, "", null, placeID, null);
    }

    public static InteractionResult questEndResult() {
        return new InteractionResult(Type.QUEST_END, "", null, 0, null);
    }

    public static InteractionResult errorResult(String msg) {
        return new InteractionResult(Type.ERROR, msg, null, 0, null);
    }

    Type type;
    String msg;
    Slot.RepeatedItem items;
    int id; // id has meaning only for NEW_PLACE type
    Collection<ScriptAction.StateTransition> transitions;

    private InteractionResult(Type type, String msg, Slot.RepeatedItem items, int id, Collection<ScriptAction.StateTransition> transitions) {
        this.type = type;
        this.msg = msg;
        this.items = items;
        this.id = id;
        this.transitions = transitions;
    }

    public Type getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public Slot.RepeatedItem getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

    public Collection<ScriptAction.StateTransition> getTransitions() {
        return transitions;
    }
}
