package technopark.diploma.arquest.animation;


import android.animation.ObjectAnimator;
import android.util.Log;
import android.util.Property;

import com.google.ar.core.Pose;
import technopark.diploma.arquest.core.ar.Scene;

public class Animator {
    public static class AnchorPoseInterpProperty extends Property<Integer, Float> {
        private float prevValue;
        private float currValue;
        private int id;
        private final Scene scene;
        private PoseGenerator poseGenerator;

        AnchorPoseInterpProperty(Scene scene, int id, PoseGenerator poseGenerator) {
            super(Float.class, "currValue");
            this.scene = scene;
            this.id = id;
            this.poseGenerator = poseGenerator;
        }

        @Override
        public Float get(Integer object) {
            return currValue;
        }

        @Override
        public void set(Integer _, Float value) {
            prevValue = currValue;
            currValue = value;
        }

        public void actualize() {
            if (currValue >= 1) {
                return;
            }

            Pose prevTransform = poseGenerator.generate(prevValue);
            Pose currTransform = poseGenerator.generate(currValue);
            Pose transform = currTransform.compose(prevTransform.inverse());
            Log.e("INFO", transform.toString());
            scene.apply(this.id, transform);
        }

        public int getId() {
            return id;
        }
    }

    public static AnchorPoseInterpProperty createProperty(Scene scene, int id, PoseGenerator poseGenerator) {
        return new AnchorPoseInterpProperty(scene, id, poseGenerator);
    }

    public static ObjectAnimator createAnimator(AnchorPoseInterpProperty property) {
        return ObjectAnimator.ofFloat(property.getId(), property, 0, 1);
    }
}
