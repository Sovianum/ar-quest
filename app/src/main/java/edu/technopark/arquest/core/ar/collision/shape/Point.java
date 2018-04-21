package edu.technopark.arquest.core.ar.collision.shape;

import com.google.ar.core.Pose;
import edu.technopark.arquest.common.CollectionUtils;

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
