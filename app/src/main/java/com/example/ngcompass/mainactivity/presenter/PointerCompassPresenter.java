package com.example.ngcompass.mainactivity.presenter;


import com.example.ngcompass.mainactivity.model.Compass;
import com.example.ngcompass.mainactivity.model.PointerCompass;
import com.example.ngcompass.mainactivity.model.location.Location;

public class PointerCompassPresenter extends CompassPresenter {

    public PointerCompassPresenter(
            SensorsPresenter sensorsPresenter,
            Location destination,
            Location startingPosition) {

        super(
                sensorsPresenter,
                new PointerCompass(destination, startingPosition));

    }

    public void updateCurrentPosition(Location currentPosition){
        ((PointerCompass)compass).setCurrentPosition(currentPosition);
    }

    public void updateDestination(Location destination){
        ((PointerCompass)compass).setTargetLocation(destination);
    }

    public Location getDestination(){
        return ((PointerCompass)compass).getTargetLocation();
    }


}
