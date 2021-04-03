package com.example.ngcompass.mainactivity.model;

import android.location.Location;

public class PointerCompass extends Compass{

    Location targetLocation;
    Location currentPosition;

    public PointerCompass() {
        targetLocation = new Location("");
        targetLocation.setLongitude(0);
        targetLocation.setLatitude(0);

        currentPosition = new Location("");
        currentPosition.setLongitude(50);
        currentPosition.setLatitude(50);
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public void setCurrentPosition(Location currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    public void setCurrentAzimuth(float currentAzimuth) {

        float bearing = currentPosition.bearingTo(targetLocation);
        float targetAngle = (bearing - currentAzimuth);

        super.setCurrentAzimuth(targetAngle);
    }


}
