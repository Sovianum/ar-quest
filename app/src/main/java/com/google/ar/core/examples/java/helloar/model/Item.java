package com.google.ar.core.examples.java.helloar.model;

public class Item {
    private String name;
    private String description;
    private String imgPath;

    public Item(String name, String description, String imgPath) {
        this.name = name;
        this.description = description;
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImgPath() {
        return imgPath;
    }
}
