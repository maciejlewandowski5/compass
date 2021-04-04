package com.example.ngcompass.mainactivity.androidimp;

import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SensorManager;

public class AndroidSensorManager implements SensorManager {


    @Override
    public float[] getRotationMatrix(
            float[] rotationMatrixResult,
            float[] inclinationMatrix,
            float[] accelerometerReadings,
            float[] geomagneticReadings) {
        android.hardware.SensorManager.getRotationMatrix(
                rotationMatrixResult,
                inclinationMatrix,
                accelerometerReadings,
                geomagneticReadings);

        return rotationMatrixResult;
    }

    @Override
    public float[] getOrientationMatrix(float[] rotationMatrix, float[] azimuthPitchRollResults) {
        android.hardware.SensorManager.getOrientation(
                rotationMatrix,
                azimuthPitchRollResults);

        return azimuthPitchRollResults;
    }
}
