package edu.technopark.arquest.core.ar.geom;

import com.google.ar.core.Pose;

public class Translation {
    public static Translation Identity() {
        return new Translation(0, 0,0);
    }

    private float[] t;

    public Translation(float x, float y, float z) {
        t = new float[3];
        t[0] = x;
        t[1] = y;
        t[2] = z;
    }

    public Translation(Pose pose) {
        t = new float[3];
        pose.getTranslation(t, 0);
    }

    public Pose getPose() {
        return Pose.makeTranslation(t);
    }

    public void set(float x, float y, float z) {
        t[0] = x;
        t[1] = y;
        t[2] = z;
    }

    public void apply(Pose pose) {
        pose.extractTranslation().compose(Pose.makeTranslation(t)).getTranslation(t, 0);
    }

    public void applyGlobal(Pose pose) {
        pose.getTranslation(t, 0);
    }

    public float get(int coordID) {
        return t[coordID];
    }
}
