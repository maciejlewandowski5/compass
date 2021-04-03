package com.example.ngcompass.mainactivity.model;

import android.hardware.SensorManager;

public class RotationModel {

    private static final float ALPHA = 0.2f;

    public float[] rotationMatrix;
    public float[] orientationAngles;
    public float screenOrientationCorrection;

    public RotationModel() {
        rotationMatrix = new float[9];
        orientationAngles = new float[3];
        screenOrientationCorrection = 0;
    }

    public void setRotationMatrixWithLowPassFilter(float[] rotationMatrix) {
        this.rotationMatrix = applyLowPassFilter(rotationMatrix,this.rotationMatrix);
    }

    public void setOrientationAnglesWithLowPassFilter(float[] orientationAngles) {
        this.orientationAngles = applyLowPassFilter(orientationAngles,this.orientationAngles);
    }

    private float[] applyLowPassFilter(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }


}
