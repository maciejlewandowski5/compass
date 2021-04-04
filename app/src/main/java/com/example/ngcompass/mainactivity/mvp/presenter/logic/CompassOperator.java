package com.example.ngcompass.mainactivity.mvp.presenter.logic;

import com.example.ngcompass.mainactivity.mvp.model.CompassModel;

public class CompassOperator {

    private RotationOrientationMatrixCalculator rotationOrientationMatrixCalculator;
    protected CompassModel compassModel;
    public float screenOrientationCorrection;

    public CompassOperator(RotationOrientationMatrixCalculator rotationOrientationMatrixCalculator) {
        this.rotationOrientationMatrixCalculator = rotationOrientationMatrixCalculator;
        this.compassModel = new CompassModel();
        screenOrientationCorrection = 0;
    }

    protected CompassOperator(RotationOrientationMatrixCalculator rotationOrientationMatrixCalculator, CompassModel compassModel) {
        this.rotationOrientationMatrixCalculator = rotationOrientationMatrixCalculator;
        this.compassModel = compassModel;
        screenOrientationCorrection =0;
    }

    public float getLastAzimuth() {
        return compassModel.getLastAzimuth() + screenOrientationCorrection;
    }

    public float getCurrentAzimuth() {
        return compassModel.getCurrentAzimuth() + screenOrientationCorrection;
    }

    public void updateRotationModel() {
    rotationOrientationMatrixCalculator.updateRotationModel();
    }

    public void updateAccelerometerReadings(float[] reading){
        rotationOrientationMatrixCalculator.updateAccelerometerReadings(reading);
    }

    public void updateMagnetometerReadings(float[] reading){
        rotationOrientationMatrixCalculator.updateMagnetometerReadings(reading);
    }

    public void lockCompass() {
        compassModel.setLocked(true);
    }

    public void unlockCompass() {
        compassModel.setLocked(false);
    }

    public boolean isCompassLocked() {
        return compassModel.isLocked();
    }

    public void updateCompass() {
        if (!isCompassLocked()) {
            compassModel.setCurrentAzimuth(rotationOrientationMatrixCalculator.calculateAzimuth());
        } else throw new IllegalStateException(
                "CompassModel is in locked mode, to change azimuth first unlock it");
    }

    public void onResume() {
        rotationOrientationMatrixCalculator.onResume();
    }

    public void onPause() {
        rotationOrientationMatrixCalculator.onPause();
    }

    public void setScreenOrientationCorrection(int correctionAngle) {
        screenOrientationCorrection = correctionAngle;
    }

    public void setScreenPortraitMode() {
        screenOrientationCorrection = 0;
    }
}
