package com.google.ar.core.examples.java.helloar.core.ar.geom;

import com.google.ar.core.Pose;

public class Geom {
    private Translation translation;
    private Rotation rotation;
    private float scale;

    public Geom() {
        translation = Translation.Identity();
        rotation = Rotation.Identity();
        scale = 1;
    }

    public Geom(Pose pose) {
        translation = new Translation(pose);
        rotation = new Rotation(pose);
        scale = 1;
    }

    public Geom apply(Pose pose) {
        translation.apply(pose);
        rotation.apply(pose);
        return this;
    }

    public Geom applyGlobal(Pose pose) {
        translation.applyGlobal(pose);
        rotation.applyGlobal(pose);
        return this;
    }

    public Pose getPose() {
        Pose result = Pose.IDENTITY;
        if (translation != null) {
            result = result.compose(translation.getPose());
        }
        if (rotation != null) {
            result = result.compose(rotation.getPose());
        }
        return result;
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

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
