package com.example.ngcompass.mainactivity.model.location;

public interface Location {

    void setLongitude(double longitude);
    void setLatitude(double latitude);
    float bearingTo(Location location);
    double getLatitude();
    double getLongitude();

}

