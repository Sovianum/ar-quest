package edu.technopark.arquest.core.game;

import java.util.Collection;

public interface Action {
    Collection<InteractionResult> act(final InteractionArgument argument);
}
