package com.example.ngcompass.mainactivity.mvp.presenter;



import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SensorEvent;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SurfaceRotation;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.Location;

public interface Presenter {

    void setScreenOrientationCorrection(SurfaceRotation.ScreenRotation screenRotation);

    void onResume();

    void onPause();

    void updateDestination(Location destination);

    void onSensorChanged(SensorEvent sensorEvent);

    Location getDestination();

    Location getCurrentLocation();

    void updateCurrentPosition(Location destination);

    float getDistance();

    void lockGPSCompass();

    void unlockGPSCompass();

    void lockRegularCompass();

    void unlockRegularCompass();
}
