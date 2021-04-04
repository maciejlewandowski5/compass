package com.example.ngcompass.mainactivity.presenter.logic;



import com.example.ngcompass.mainactivity.SensorManager;
import com.example.ngcompass.mainactivity.model.RotationModel;

public class RotationOrientationMatrixCalculator {

    private static final float ALPHA = 0.5f;

    private float[] accelerometerReading;
    private float[] magnetometerReading;

    private RotationModel rotationModel;

    private SensorManager sensorManager;

    public RotationOrientationMatrixCalculator(SensorManager sensorManager) {
        rotationModel = new RotationModel();
        accelerometerReading = new float[3];
        magnetometerReading = new float[3];
        this.sensorManager  = sensorManager;
    }

    public float calculateAzimuth() {
        return -(float)
                Math.toDegrees(rotationModel.orientationAngles[0]);
    }

    public void updateRotationModel() {
        sensorManager.getRotationMatrix(
                rotationModel.rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading);

        sensorManager.getOrientationMatrix(
                rotationModel.rotationMatrix,
                rotationModel.orientationAngles);

    }



    private float[] applyLowPassFilter(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    public void updateAccelerometerReadings(float[] reading){
        accelerometerReading = applyLowPassFilter(reading, accelerometerReading);
    }

    public void updateMagnetometerReadings(float[] reading){
        magnetometerReading = applyLowPassFilter(reading, magnetometerReading);
    }

    public void onResume() {
    }

    public void onPause() {
    }



}
