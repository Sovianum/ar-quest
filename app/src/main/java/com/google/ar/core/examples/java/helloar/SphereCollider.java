package com.google.ar.core.examples.java.helloar;

public class SphereCollider {
    public static int COORDINATE_CNT = 3;

    private float radius;
    private float[] position;

    public SphereCollider(float radius) {
        this.radius = radius;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public boolean Collide(SphereCollider another) {
        return distance(another) <= 2 * radius;
    }

    private float distance(SphereCollider another) {
        float result = 0;
        for (int i = 0; i !=COORDINATE_CNT; ++i) {
            float d = position[i] - another.position[i];
            result += d * d;
        }
        return result;
    }
}
