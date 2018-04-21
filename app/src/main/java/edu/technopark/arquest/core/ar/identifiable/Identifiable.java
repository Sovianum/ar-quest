package edu.technopark.arquest.core.ar.identifiable;

public class Identifiable {
    private String name;
    private int id;
    private int parentID;
    private int sceneID;

    public Identifiable(String name, int id, int parentID) {
        this.name = name;
        this.id = id;
        this.parentID = parentID;
    }

    public Identifiable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Identifiable() {}

    public String getName() {
        return name;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public int getId() {
        return id;
    }

    public int getSceneID() {
        return sceneID;
    }

    public void setSceneID(int sceneID) {
        this.sceneID = sceneID;
    }
}
