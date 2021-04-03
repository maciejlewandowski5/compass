package com.example.ngcompass.mainactivity.model;

import com.example.ngcompass.mainactivity.model.location.Location;

public class PointerCompass extends Compass{


    Location targetLocation;
    Location currentPosition;


    public PointerCompass(Location targetLocation, Location currentPosition) {
        this.targetLocation = targetLocation;
        this.currentPosition = currentPosition;
    }

    public Location getTargetLocation() {
        return targetLocation;
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
