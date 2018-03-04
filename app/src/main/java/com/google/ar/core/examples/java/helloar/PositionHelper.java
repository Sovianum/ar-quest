package com.google.ar.core.examples.java.helloar;

import android.location.Location;

public class PositionHelper {
    static void getOffsetVector(
            double startLatitude, double startLongitude,
            double endLatitude, double endLongitude,
            float[] offset
    ) {
        float xDistance = getDistance(startLatitude, startLongitude, endLatitude, startLongitude);
        float yDistance = getDistance(startLatitude, startLongitude, startLatitude, endLongitude);

        if (endLatitude < startLatitude) {
            xDistance *= -1;
        }

        if (endLongitude < startLongitude) {
            yDistance *= -1;
        }

        offset[0] = xDistance;
        offset[1] = yDistance;
    }

    static float getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        final float[] latDisplace = new float[1];
        Location.distanceBetween(
                startLatitude, startLongitude,
                endLatitude, endLongitude,
                latDisplace
        );
        return latDisplace[0];
    }
}
