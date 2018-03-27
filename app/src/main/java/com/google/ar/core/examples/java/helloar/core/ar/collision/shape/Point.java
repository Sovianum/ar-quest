package com.google.ar.core.examples.java.helloar.core.ar.collision.shape;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.common.CollectionUtils;

import java.util.Collection;

public class Point implements Shape {
    @Override
    public Collection<Pose> generateBoundaryPoints(int cnt) {
        return CollectionUtils.singleItemList(Pose.IDENTITY);
    }

    @Override
    public boolean contains(Pose localPose) {
        return false;
    }
}
