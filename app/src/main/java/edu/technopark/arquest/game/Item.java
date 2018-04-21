package edu.technopark.arquest.game;

import edu.technopark.arquest.model.VisualResource;

public class Item extends Identifiable3D {
    private final String description;
    private final VisualResource visualResource;

    public Item(int id, String name, String description, VisualResource visualResource) {
        super(id, name, false);
        this.description = description;
        this.visualResource = visualResource;
    }

    public String getDescription() {
        return description;
    }

    public VisualResource getVisualResource() {
        return visualResource;
    }
}