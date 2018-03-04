package com.google.ar.core.examples.java.helloar.scene.record;

public class ObjectRecord {
    private int id;
    private int parentId;
    private int sceneId;
    private String name;
    private String assetName;
    private PoseRecord poseRecord;

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

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public PoseRecord getPoseRecord() {
        return poseRecord;
    }

    public void setPoseRecord(PoseRecord poseRecord) {
        this.poseRecord = poseRecord;
    }
}
