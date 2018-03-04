package com.google.ar.core.examples.java.helloar.scene.record;

public class ObjectRecord {
    private int id;
    private int parentId;
    private int sceneId;
    private String name;
    private String modelName;
    private String textureName;
    private PoseRecord poseRecord;
    private float scale;

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public PoseRecord getPoseRecord() {
        return poseRecord;
    }

    public void setPoseRecord(PoseRecord poseRecord) {
        this.poseRecord = poseRecord;
    }
}
