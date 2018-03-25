package com.google.ar.core.examples.java.helloar.core.game;

import java.util.Collection;

public interface Action {
    Collection<InteractionResult> act(final InteractionArgument argument);
}
