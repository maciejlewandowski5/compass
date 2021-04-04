package com.example.ngcompass.mainactivity.mvp;

public interface  MainActivityView {

    public void prepareAndStartGPSCompassAnimation(double lastAzimuth, float currentAzimuth);
    public void prepareAndStartRegularCompassAnimation(double lastAzimuth, float currentAzimuth);
}
