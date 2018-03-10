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

    public InteractionResult(Type type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    Type type;
    String msg;
    Slot.RepeatedItem items;
}
