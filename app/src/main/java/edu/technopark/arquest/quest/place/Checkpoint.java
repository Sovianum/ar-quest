package edu.technopark.arquest.quest.place;

public class Checkpoint {
    private String description;
    private String title;

    public Checkpoint (String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
