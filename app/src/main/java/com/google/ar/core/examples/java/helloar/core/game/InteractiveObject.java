package com.google.ar.core.examples.java.helloar.core.game;


import java.util.Collection;

public class InteractiveObject {
    private final int id;
    private final String name;
    private final String description;
    private boolean isAccessible;
    private Action action;

    public InteractiveObject(int id, String name, String description, boolean isAccessible) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAccessible = isAccessible;
    }

    public Collection<InteractionResult> interact(final InteractionArgument argument) {
        if (!isAccessible) {
            return Utils.singleItemCollection(InteractionResult.ERR);
        }
        return action.act(argument);
    }

    public int getId() {
        return id;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public void setAccessible(boolean isAccessible) {
        this.isAccessible = isAccessible;
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
