package com.google.ar.core.examples.java.helloar.core.ar.collision.shape;

import com.google.ar.core.Pose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Sphere implements Shape {
    private float radius;
    private Random random;

    public Sphere(float radius, Random random) {
        this.radius = radius;
        this.random = random;
    }

    public Sphere(float radius) {
        this.radius = radius;
        this.random = new Random(System.currentTimeMillis());
    }

    @Override
    public Collection<Pose> generateBoundaryPoints(int cnt) {
        Collection<Pose> result = new ArrayList<>(cnt);

        for (int i = 0; i != cnt; ++i) {
            float phi = 2 * (float) Math.PI * random.nextFloat();
            float theta = 2 * (float) Math.PI * random.nextFloat();

            float rxy = radius * (float) Math.cos(theta);
            float z = radius * (float) Math.sin(theta);

            float x = rxy * (float) Math.cos(phi);
            float y = rxy * (float) Math.sin(phi);

            result.add(Pose.makeTranslation(x, y, z));
        }
        return result;
    }

    @Override
    public boolean contains(Pose localPose) {
        float x = localPose.tx();
        float y = localPose.ty();
        float z = localPose.tz();

        return radius * radius >= x*x + y*y + z*z;
    }

    public float getRadius() {
        return radius;
    }
}
