package com.google.ar.core.examples.java.helloar.core.game;

import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;

public class InteractionResult {
    public static InteractionResult ERR = new InteractionResult(Type.ERROR, "");

    public enum Type {
        NEW_ITEMS,
        JOURNAL_RECORD,
        NEW_PLACE,
        QUEST_END,
        MESSAGE,
        ERROR,
    }

    Type type;
    String msg;
    Slot.RepeatedItem items;

    public InteractionResult(Type type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public InteractionResult(Type type, String msg, Slot.RepeatedItem items) {
        this.type = type;
        this.msg = msg;
        this.items = items;
    }

    public InteractionResult(Type type, Slot.RepeatedItem items) {
        this.type = type;
        this.items = items;
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
}
