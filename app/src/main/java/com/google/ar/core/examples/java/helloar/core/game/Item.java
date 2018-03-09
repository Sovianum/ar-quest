package com.google.ar.core.examples.java.helloar.core.game;

public class Item {
    private final int id;
    private final String name;
    private final String description;
    private final String modelName;
    private final String textureName;

    public Item(int id, String name, String description, String modelName, String textureName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modelName = modelName;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModelName() {
        return modelName;
    }

    public String getTextureName() {
        return textureName;
    }
}