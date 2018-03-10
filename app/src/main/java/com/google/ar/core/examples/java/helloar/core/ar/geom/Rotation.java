package com.google.ar.core.examples.java.helloar.core.ar.geom;

import com.google.ar.core.Pose;

public class Rotation {
    public static Rotation Identity() {
        return new Rotation(0, 1, 0, 0);
    }

    private float[] q;

    public Rotation(float x, float y, float z, float angle) {
        q = new float[4];
        set(x, y, z, angle);
    }

    public Rotation(Pose pose) {
        q = new float[4];
        pose.getRotationQuaternion(q, 0);
    }

    public Pose getPose() {
        return Pose.makeRotation(q);
    }

    public void set(float x, float y, float z, float angle) {
        q[0] = x * (float) Math.sin(angle);
        q[1] = y * (float) Math.sin(angle);
        q[2] = z * (float) Math.sin(angle);
        q[3] = (float) Math.cos(angle);
    }

    public void apply(Pose pose) {
        pose.extractRotation().compose(Pose.makeRotation(q)).getRotationQuaternion(q, 0);
    }
}
