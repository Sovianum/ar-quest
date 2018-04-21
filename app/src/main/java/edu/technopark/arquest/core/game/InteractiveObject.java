package edu.technopark.arquest.core.game;


import edu.technopark.arquest.common.CollectionUtils;
import edu.technopark.arquest.core.ar.SceneObject;
import edu.technopark.arquest.core.ar.collision.Collider;
import edu.technopark.arquest.core.ar.drawable.IDrawable;
import edu.technopark.arquest.core.ar.identifiable.Identifiable;
import edu.technopark.arquest.core.game.script.ObjectState;
import edu.technopark.arquest.core.game.script.ScriptAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InteractiveObject extends SceneObject {
    private final int id;
    private final String name;
    private final String description;
    private Action action;
    private Collection<Item> items;

    private int currentStateID;
    private Map<Integer, ObjectState> states;

    public InteractiveObject(int id, String name, String description) {
        this(id, name, description, new ArrayList<Item>());
    }

    public InteractiveObject(int id, String name, String description, Collection<Item> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = items;

        setIdentifiable(new Identifiable(name, id));
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

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
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
        IDrawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setEnabled(state.isVisible());
        }
        Collider collider = getCollider();
        if (collider != null) {
            collider.setEnabled(state.isCollidable());
        }
        setEnabled(state.isEnabled());

    }
}
