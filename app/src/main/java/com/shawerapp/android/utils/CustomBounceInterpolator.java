package com.shawerapp.android.utils;

/**
 * Created by john.ernest on 2/15/18.
 */

public class CustomBounceInterpolator implements android.view.animation.Interpolator {
    double mAmplitude = 1;

    double mFrequency = 10;

    public CustomBounceInterpolator(double amp, double freq) {
        mAmplitude = amp;
        mFrequency = freq;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) * Math.cos(mFrequency * time) + 1);
    }
}
