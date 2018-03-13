package com.google.ar.core.examples.java.helloar.core.game;

import java.util.Collection;

public abstract class ItemlessAction implements Action {
    @Override
    public Collection<Item> getItems() {
        return null;
    }
}
