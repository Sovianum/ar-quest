package com.google.ar.core.examples.java.helloar.core.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InteractionArgument {
    public static InteractionArgument itemArg(Collection<Slot.RepeatedItem> items) {
        return new InteractionArgument(new ArrayList<String>(), items);
    }

    public static InteractionArgument stringArg(List<String> strings) {
        return new InteractionArgument(strings, new ArrayList<Slot.RepeatedItem>());
    }

    List<String> strings;
    Collection<Slot.RepeatedItem> items;

    public InteractionArgument(List<String> strings, Collection<Slot.RepeatedItem> items) {
        this.strings = strings;
        this.items = items;
    }

    public InteractionArgument() {
        strings = new ArrayList<>();
        items = new ArrayList<>();
    }
}
