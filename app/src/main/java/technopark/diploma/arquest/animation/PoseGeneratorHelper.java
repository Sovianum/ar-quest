package technopark.diploma.arquest.animation;


import com.google.ar.core.Pose;

public class PoseGeneratorHelper {
    public static PoseGenerator toPose(final Pose pose) {
        return new PoseGenerator() {
            @Override
            public Pose generate(float value) {
                return Pose.makeInterpolated(Pose.IDENTITY, pose, value);
            }
        };
    }
}
