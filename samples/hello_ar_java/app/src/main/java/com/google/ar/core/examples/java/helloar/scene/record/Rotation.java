package com.google.ar.core.examples.java.helloar.scene.record;

public class Rotation {
    public static Rotation Identity() {
        return new Rotation(0, 1, 0, 0);
    }

    private float x;
    private float y;
    private float z;
    private float angle;

    public Rotation(float x, float y, float z, float angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
