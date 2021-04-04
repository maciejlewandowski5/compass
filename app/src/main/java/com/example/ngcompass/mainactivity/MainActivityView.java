package com.example.ngcompass.mainactivity;

import android.widget.ImageView;

import com.example.ngcompass.mainactivity.presenter.logic.CompassOperator;

public interface  MainActivityView {

    public void prepareAndStartGPSCompassAnimation(double lastAzimuth, float currentAzimuth);
    public void prepareAndStartRegularCompassAnimation(double lastAzimuth, float currentAzimuth);
}
