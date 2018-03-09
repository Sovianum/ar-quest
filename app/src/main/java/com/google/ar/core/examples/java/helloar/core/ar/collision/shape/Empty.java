package com.google.ar.core.examples.java.helloar.core.ar.collision.shape;

import com.google.ar.core.Pose;

import java.util.Collection;

public class Empty implements Shape {
    @Override
    public Collection<Pose> generateBoundaryPoints(int cnt) {
        return null;
    }

    @Override
    public boolean contains(Pose localPose) {
        return false;
    }
}
