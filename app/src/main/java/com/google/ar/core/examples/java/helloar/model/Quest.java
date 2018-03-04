package com.google.ar.core.examples.java.helloar.model;

public class Quest {
    private String title;
    private String description;
    private float rating;

    public Quest(String title, String description, float rating) {
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public float getRating() {
        return rating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
