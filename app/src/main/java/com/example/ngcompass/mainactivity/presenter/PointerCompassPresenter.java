package com.example.ngcompass.mainactivity.presenter;


import com.example.ngcompass.mainactivity.model.Compass;
import com.example.ngcompass.mainactivity.model.PointerCompass;
import com.example.ngcompass.mainactivity.model.location.Location;

public class PointerCompassPresenter extends CompassPresenter {

    public PointerCompassPresenter(PointerCompass compass) {
        super(compass);
    }

    public void updateCurrentPosition(Location currentPosition){
        ((PointerCompass)compass).setCurrentPosition(currentPosition);
    }

    public void updateDestination(Location destination){
        ((PointerCompass)compass).setTargetLocation(destination);
    }


}
