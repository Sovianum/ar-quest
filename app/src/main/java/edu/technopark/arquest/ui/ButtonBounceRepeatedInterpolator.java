package edu.technopark.arquest.ui;

public class ButtonBounceRepeatedInterpolator implements android.view.animation.Interpolator {
    private double mAmplitude = 1;
    private double mFrequency = 10;

    public ButtonBounceRepeatedInterpolator(double mAmplitude, double mFrequency) {
        this.mAmplitude = mAmplitude;
        this.mFrequency = mFrequency;
    }


    @Override
    public float getInterpolation(float time) {
        return (float) (mAmplitude * Math.sin(Math.PI * mFrequency * time) + 1);
    }
}
