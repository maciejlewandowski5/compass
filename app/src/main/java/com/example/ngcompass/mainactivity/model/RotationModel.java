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





}
