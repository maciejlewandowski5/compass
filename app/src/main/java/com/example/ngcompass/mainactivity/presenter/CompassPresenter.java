package com.example.ngcompass.mainactivity.presenter;

import android.hardware.SensorManager;

import com.example.ngcompass.mainactivity.model.Compass;
import com.example.ngcompass.mainactivity.model.RotationModel;

public class CompassPresenter {

    private RotationModel rotationModel;
    protected Compass compass;

    public CompassPresenter(Compass compass) {
        rotationModel = new RotationModel();

    }

    public void lockCompass(){
        compass.setLocked(true);
    }

    public boolean isCompassLocked(){
        return compass.isLocked();
    }

    public void updateCompass() {
        if(!isCompassLocked()) {

            float azimuth = calculateAzimuth();
            compass.setCurrentAzimuth(azimuth);

        }else throw new IllegalStateException(
                "Compass is in locked mode, to change azimuth first unlock it");
    }

    private float calculateAzimuth() {
        return -(float)
                Math.toDegrees(rotationModel.orientationAngles[0])
                + rotationModel.screenOrientationCorrection;
    }

    public void updateRotationModel(float[] accelerometerReading, float[] magnetometerReading) {
        SensorManager.getRotationMatrix(
                rotationModel.rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading);

        SensorManager.getOrientation(
                rotationModel.rotationMatrix,
                rotationModel.orientationAngles);
    }

    public void onResume() {
    }

    public void onPause() {
    }
}
