package com.example.ngcompass.mainactivity.model.location;

import java.io.Serializable;

public interface Location extends Serializable {

    void setLongitude(double longitude);
    void setLatitude(double latitude);
    float bearingTo(Location location);
    double getLatitude();
    double getLongitude();

}

