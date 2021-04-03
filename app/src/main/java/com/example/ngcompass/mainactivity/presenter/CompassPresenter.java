package com.example.ngcompass.mainactivity.presenter;

import android.hardware.SensorManager;

import com.example.ngcompass.mainactivity.model.Compass;
import com.example.ngcompass.mainactivity.model.RotationModel;

public class CompassPresenter {

    private SensorsPresenter sensorsPresenter;
    protected Compass compass;

    public CompassPresenter(SensorsPresenter sensorsPresenter) {
        this.sensorsPresenter = sensorsPresenter;
        this.compass = new Compass();
    }

    protected CompassPresenter(SensorsPresenter sensorsPresenter, Compass compass) {
        this.sensorsPresenter = sensorsPresenter;
        this.compass = compass;
    }

    public float getLastAzimuth(){
        return compass.getLastAzimuth();
    }
    public float getCurrentAzimuth(){
        return compass.getCurrentAzimuth();
    }

    public void lockCompass(){
        compass.setLocked(true);
    }

    public void unlockCompass(){
        compass.setLocked(false);
    }

    public boolean isCompassLocked(){
        return compass.isLocked();
    }

    public void updateCompass() {
        if(!isCompassLocked()) {
            compass.setCurrentAzimuth(sensorsPresenter.calculateAzimuth());
        }else throw new IllegalStateException(
                "Compass is in locked mode, to change azimuth first unlock it");
    }

    public void onResume() {
    }

    public void onPause() {
    }
}
