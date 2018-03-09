package com.google.ar.core.examples.java.helloar.core.ar.collision.shape;

import com.google.ar.core.Pose;

import java.util.Collection;

public interface Shape {
    Collection<Pose> generateBoundaryPoints(int cnt);
    boolean contains(Pose localPose);
}
