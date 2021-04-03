package com.example.ngcompass.mainactivity.presenter;

import android.hardware.SensorManager;

import com.example.ngcompass.mainactivity.model.Compass;
import com.example.ngcompass.mainactivity.model.RotationModel;

public class CompassPresenter {

    private SensorsPresenter sensorsPresenter;
    protected Compass compass;
    public float screenOrientationCorrection;

    public CompassPresenter(SensorsPresenter sensorsPresenter) {
        this.sensorsPresenter = sensorsPresenter;
        this.compass = new Compass();
        screenOrientationCorrection = 0;
    }

    protected CompassPresenter(SensorsPresenter sensorsPresenter, Compass compass) {
        this.sensorsPresenter = sensorsPresenter;
        this.compass = compass;
        screenOrientationCorrection =0;
    }

    public float getLastAzimuth() {
        return compass.getLastAzimuth() + screenOrientationCorrection;
    }

    public float getCurrentAzimuth() {
        return compass.getCurrentAzimuth() + screenOrientationCorrection;
    }

    public void lockCompass() {
        compass.setLocked(true);
    }

    public void unlockCompass() {
        compass.setLocked(false);
    }

    public boolean isCompassLocked() {
        return compass.isLocked();
    }

    public void updateCompass() {
        if (!isCompassLocked()) {
            compass.setCurrentAzimuth(sensorsPresenter.calculateAzimuth());
        } else throw new IllegalStateException(
                "Compass is in locked mode, to change azimuth first unlock it");
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void setScreenLandscapeMode() {
        screenOrientationCorrection = -90;
    }

    public void setScreenPortraitMode() {
        screenOrientationCorrection = 0;
    }
}
