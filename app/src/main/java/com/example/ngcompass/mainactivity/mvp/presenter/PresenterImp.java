package com.example.ngcompass.mainactivity.mvp.presenter;


import com.example.ngcompass.mainactivity.mvp.MainActivityView;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SensorEvent;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SensorManager;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SurfaceRotation;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.Location;
import com.example.ngcompass.mainactivity.mvp.presenter.logic.CompassOperator;
import com.example.ngcompass.mainactivity.mvp.presenter.logic.PointerCompassOperator;
import com.example.ngcompass.mainactivity.mvp.presenter.logic.RotationOrientationMatrixCalculator;

public class PresenterImp implements Presenter {


    private final CompassOperator regularCompassOperator;
    private final PointerCompassOperator gpsCompassOperator;
    private final RotationOrientationMatrixCalculator rotationOrientationMatrixCalculator;
    private final MainActivityView mainActivityView;

    PresenterImp(
            Location destination,
            Location currentLocation,
            SensorManager sensorManager,
            MainActivityView mainActivityView) {

        rotationOrientationMatrixCalculator = new RotationOrientationMatrixCalculator(sensorManager);
        regularCompassOperator = new CompassOperator(rotationOrientationMatrixCalculator);

        gpsCompassOperator = new PointerCompassOperator(
                rotationOrientationMatrixCalculator,
                destination,
                currentLocation);

        this.mainActivityView = mainActivityView;

    }

    public void setScreenOrientationCorrection(SurfaceRotation.ScreenRotation screenRotation) {
        int angle;
        int gpsCoercion;

        if (screenRotation == SurfaceRotation.ScreenRotation.ROTATION_90) {
            angle = -90;
            gpsCoercion = 90;
        } else if (screenRotation == SurfaceRotation.ScreenRotation.ROTATION_180) {
            angle = 180;
            gpsCoercion = 180;
        } else if (screenRotation == SurfaceRotation.ScreenRotation.ROTATION_270) {
            angle = 90;
            gpsCoercion = 270;
        } else if (screenRotation == SurfaceRotation.ScreenRotation.ROTATION_0) {
            angle = 0;
            gpsCoercion = 0;
        } else {
            angle = 0;
            gpsCoercion = 0;
        }

        regularCompassOperator.setScreenOrientationCorrection(angle);
        gpsCompassOperator.setScreenOrientationCorrection(gpsCoercion);

    }

    @Override
    public void onResume() {
        regularCompassOperator.onResume();
        gpsCompassOperator.onResume();
        rotationOrientationMatrixCalculator.onResume();
    }

    @Override
    public void onPause() {
        regularCompassOperator.onPause();
        gpsCompassOperator.onPause();
        rotationOrientationMatrixCalculator.onPause();
    }

    @Override
    public void updateDestination(Location destination) {
        gpsCompassOperator.updateDestination(destination);
    }
    @Override
    public void updateCurrentPosition(Location destination) {
        gpsCompassOperator.updateCurrentPosition(destination);
    }

    @Override
    public float getDistance() {
        return  getCurrentLocation().distanceTo(getDestination());
    }

    @Override
    public void lockGPSCompass() {
        gpsCompassOperator.lockCompass();
    }

    @Override
    public void unlockGPSCompass() {
        gpsCompassOperator.unlockCompass();
    }

    @Override
    public void lockRegularCompass() {
        regularCompassOperator.lockCompass();
    }

    @Override
    public void unlockRegularCompass() {
        regularCompassOperator.unlockCompass();
    }

    private boolean isAnyCompassPresenterUnlocked() {
        return !regularCompassOperator.isCompassLocked() || !gpsCompassOperator.isCompassLocked();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        updateAccMagReadings(sensorEvent);
        if (isAnyCompassPresenterUnlocked()) {
            regularCompassOperator.updateRotationModel();
            gpsCompassOperator.updateRotationModel();
        }
        if (!regularCompassOperator.isCompassLocked()) {
            regularCompassOperator.updateCompass();
            mainActivityView.prepareAndStartRegularCompassAnimation(
                    regularCompassOperator.getLastAzimuth(),
                    regularCompassOperator.getCurrentAzimuth());
        }
        if (!gpsCompassOperator.isCompassLocked()) {
            gpsCompassOperator.updateCompass();
            mainActivityView.prepareAndStartGPSCompassAnimation(
                    gpsCompassOperator.getLastAzimuth(),
                    gpsCompassOperator.getCurrentAzimuth()
            );
        }
    }

    public void updateAccMagReadings(SensorEvent sensorEvent) {
        if (sensorEvent.getSensorType() == SensorEvent.SensorType.TYPE_ACCELEROMETER) {
            regularCompassOperator.updateAccelerometerReadings(sensorEvent.getValues().clone());
            gpsCompassOperator.updateAccelerometerReadings(sensorEvent.getValues().clone());
        } else if (sensorEvent.getSensorType() == SensorEvent.SensorType.TYPE_MAGNETIC_FIELD) {
            regularCompassOperator.updateMagnetometerReadings(sensorEvent.getValues().clone());
            gpsCompassOperator.updateMagnetometerReadings(sensorEvent.getValues().clone());
        }

    }


    public Location getDestination() {
        return gpsCompassOperator.getDestination();
    }

    public Location getCurrentLocation() {
        return gpsCompassOperator.getCurrentLocation();
    }

}
