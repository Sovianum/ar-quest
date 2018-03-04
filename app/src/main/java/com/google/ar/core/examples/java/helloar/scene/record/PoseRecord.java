package com.google.ar.core.examples.java.helloar.scene.record;

import com.google.ar.core.Pose;

public class PoseRecord {
    private Translation translation;
    private Rotation rotation;

    public PoseRecord(Translation translation, Rotation rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    public Pose buildPose() {
        float sin = (float) Math.sin(rotation.getAngle() / 2);
        float cos = (float) Math.cos(rotation.getAngle() / 2);
        return new Pose(
                new float[]{translation.getX(), translation.getY(), translation.getZ()},
                new float[]{
                        rotation.getX() * sin,
                        rotation.getY() * sin,
                        rotation.getZ() * sin,
                        cos
                }
        );
    }

    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }
}
