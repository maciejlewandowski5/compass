package com.example.ngcompass.mainactivity.model.location;

import java.io.Serializable;

public class AndroidLocation implements Location, Serializable {

    android.location.Location mLocation;


    public AndroidLocation() {
        mLocation = new android.location.Location("");
    }

    public static AndroidLocation from(android.location.Location location){
        AndroidLocation result = new AndroidLocation();
        result.mLocation = location;
        return result;
    }

    @Override
    public void setLongitude(double longitude) {
        mLocation.setLongitude(longitude);
    }

    @Override
    public void setLatitude(double latitude) {
        mLocation.setLatitude(latitude);
    }

    @Override
    public float bearingTo(Location location) {
        android.location.Location bearingArgumentLocation = prepareOriginalAndroidLocation(location);

        return this.mLocation.bearingTo(bearingArgumentLocation);


    }

    private android.location.Location prepareOriginalAndroidLocation(Location location) {
        android.location.Location bearingArgumentLocation = new android.location.Location("");

        bearingArgumentLocation.setLatitude(location.getLatitude());
        bearingArgumentLocation.setLongitude(location.getLongitude());
        return bearingArgumentLocation;
    }

    @Override
    public float distanceTo(Location location) {
        android.location.Location distanceArgumentLocation = prepareOriginalAndroidLocation(location);
        return mLocation.distanceTo(distanceArgumentLocation);
    }

    @Override
    public double getLatitude() {
        return mLocation.getLatitude();
    }

    @Override
    public double getLongitude() {
        return mLocation.getLongitude();
    }
}
