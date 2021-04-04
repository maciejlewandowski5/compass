package com.example.ngcompass.mainactivity.presenter.logic;



import com.example.ngcompass.mainactivity.model.PointerCompassModel;
import com.example.ngcompass.mainactivity.model.location.Location;

public class PointerCompassOperator extends CompassOperator {

    public PointerCompassOperator(
            RotationOrientationMatrixCalculator rotationOrientationMatrixCalculator,
            Location destination,
            Location startingPosition) {

        super(
                rotationOrientationMatrixCalculator,
                new PointerCompassModel(destination, startingPosition));

    }

    public void updateCurrentPosition(Location currentPosition){
        ((PointerCompassModel) compassModel).setCurrentPosition(currentPosition);
    }

    public void updateDestination(Location destination){
        ((PointerCompassModel) compassModel).setTargetLocation(destination);
    }

    public Location getDestination(){
        return ((PointerCompassModel) compassModel).getTargetLocation();
    }

    public Location getCurrentLocation(){return ((PointerCompassModel) compassModel).getCurrentPosition();}

}
