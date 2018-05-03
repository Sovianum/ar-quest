package edu.technopark.arquest.game;

import edu.technopark.arquest.model.VisualResource;

public class Item extends Identifiable3D {
    private final String description;
    private final VisualResource visualResource;
    private final String avatar;

    public Item(int id, String name, String description, VisualResource visualResource) {
        super(id, name, false);
        this.description = description;
        this.visualResource = visualResource;
        this.avatar = "banana.jpg";//stubs!!!
        setVisible(false);
    }

    public String getDescription() {
        return description;
    }

    public VisualResource getVisualResource() {
        return visualResource;
    }

    public String getAvatar() {
        return avatar;
    }
}