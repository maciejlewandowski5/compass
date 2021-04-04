package com.example.ngcompass.mainactivity.mvp.model;

import com.example.ngcompass.mainactivity.mvp.presenter.dependency.Location;

public class PointerCompassModel extends CompassModel {


    Location targetLocation;
    Location currentPosition;


    public PointerCompassModel(Location targetLocation, Location currentPosition) {
        this.targetLocation = targetLocation;
        this.currentPosition = currentPosition;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Location getCurrentPosition() {
        return currentPosition;
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
