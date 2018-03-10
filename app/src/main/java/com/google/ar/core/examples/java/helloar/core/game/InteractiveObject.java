package com.google.ar.core.examples.java.helloar.core.game;


import com.google.ar.core.examples.java.helloar.core.ar.SceneObject;
import com.google.ar.core.examples.java.helloar.core.ar.identifiable.Identifiable;

import java.util.Collection;

public class InteractiveObject extends SceneObject {
    private final int id;
    private final String name;
    private final String description;
    private Action action;

    public InteractiveObject(int id, String name, String description, boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.description = description;

        setIdentifiable(new Identifiable(name, id));
        setEnabled(isEnabled);
    }

    public Collection<InteractionResult> interact(final InteractionArgument argument) {
        if (!isEnabled()) {
            return Utils.singleItemCollection(InteractionResult.ERR);
        }
        return action.act(argument);
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
}
