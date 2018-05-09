package edu.technopark.arquest.game;

import java.util.List;

public interface Action {
    List<InteractionResult> act(final InteractionArgument argument);
}
