package com.google.ar.core.examples.java.helloar.core.ar.record;

public class Translation {
    public static Translation Identity() {
        return new Translation(0, 0,0);
    }

    private float x;
    private float y;
    private float z;

    public Translation(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
}
