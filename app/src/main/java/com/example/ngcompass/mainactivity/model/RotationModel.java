package com.example.ngcompass.mainactivity.model;

import android.hardware.SensorManager;

public class RotationModel {


    public float[] rotationMatrix;
    public float[] orientationAngles;


    public RotationModel() {
        rotationMatrix = new float[9];
        orientationAngles = new float[3];

    }





}
