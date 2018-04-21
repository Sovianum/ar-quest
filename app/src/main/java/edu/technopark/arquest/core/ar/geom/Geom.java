package edu.technopark.arquest.core.ar.geom;

import com.google.ar.core.Pose;

public class Geom {
    public static int LINEAR_COORD_COUNT = 3;

    private Translation translation;
    private Rotation rotation;
    private float scale;

    public static float distance(Geom g1, Geom g2) {
        float result = 0;
        for(int i = 0; i != LINEAR_COORD_COUNT; ++i) {
            float delta = g1.translation.get(i) - g2.translation.get(i);
            result += delta * delta;
        }
        return (float) Math.sqrt(result);
    }

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
