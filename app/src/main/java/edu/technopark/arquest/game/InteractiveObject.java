package edu.technopark.arquest.game;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.technopark.arquest.common.CollectionUtils;
import edu.technopark.arquest.game.script.ObjectState;
import edu.technopark.arquest.game.script.ScriptAction;
import edu.technopark.arquest.model.VisualResource;

public class InteractiveObject extends Identifiable3D {
    private final String description;
    private Action action;
    private Collection<Item> items;

    private int currentStateID;
    private Map<Integer, ObjectState> states;

    public InteractiveObject(int id, String name, String description) {
        this(id, name, description, new ArrayList<Item>());
    }

    public InteractiveObject(int id, String name, String description, Collection<Item> items) {
        super(id, name, true);
        this.description = description;
        this.items = items;
    }

    public Collection<InteractionResult> interact(final InteractionArgument argument) {
        if (!isEnabled()) {
            return CollectionUtils.singleItemList(InteractionResult.errorResult(""));
        }
        return action.act(argument);
    }

    public Collection<Item> getItems() {
        return items;
    }

    public String getDescription() {
        return description;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getActionFromStates() {
        return new Action() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                ObjectState currState = states.get(currentStateID);
                ScriptAction matchingAction = currState.getMatchingAction(argument.getItems(), argument.getStrings());
                if (matchingAction != null) {
                    return matchingAction.getInteractionResults();
                }
                return new ArrayList<>();
            }
        };
    }

    public int getCurrentStateID() {
        return currentStateID;
    }

    public void setCurrentStateID(int currentStateID) {
        this.currentStateID = currentStateID;
        updateState(states.get(currentStateID));
    }

    public Map<Integer, ObjectState> getStates() {
        return states;
    }

    public void setStates(Collection<ObjectState> states) {
        this.states = new HashMap<>();

        boolean foundDefault = false;
        for (ObjectState state : states) {
            if (state.isDefault()) {
                if (!foundDefault) {
                    updateState(state);
                    foundDefault = true;
                } else {
                    throw new RuntimeException("found multiple default states");
                }
            }
            this.states.put(state.getId(), state);
        }
        if (!foundDefault) {
            throw new RuntimeException("not found default state");
        }
    }

    private void updateState(ObjectState state) {
        currentStateID = state.getId();
        setEnabled(state.isEnabled());
    }
}
