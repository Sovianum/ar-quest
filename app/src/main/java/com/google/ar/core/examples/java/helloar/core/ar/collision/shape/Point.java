package com.google.ar.core.examples.java.helloar.core.ar.collision.shape;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.game.Utils;

import java.util.Collection;

public class Point implements Shape {
    @Override
    public Collection<Pose> generateBoundaryPoints(int cnt) {
        return Utils.singleItemCollection(Pose.IDENTITY);
    }

    @Override
    public boolean contains(Pose localPose) {
        return false;
    }
}
