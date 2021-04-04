package com.example.ngcompass.mainactivity.presenter;

import com.example.ngcompass.mainactivity.MainActivityView;
import com.example.ngcompass.mainactivity.SensorManager;
import com.example.ngcompass.mainactivity.model.location.Location;


public class PresenterImpBuilder {

    private PresenterImpBuilder(){}

    public static ViewStep newBuilder() {
        return new Steps();
    }

    public interface ViewStep{
        DestinationStep viewStep(MainActivityView mainActivityView);
    }

    public interface DestinationStep{
        CurrentLocationStep currentDestination(Location destination);
    }
    public interface  CurrentLocationStep{
        SensorManagerStep currentLocation(Location currentLocation);
    }
    public interface SensorManagerStep{
        BuildStep sensorManager(SensorManager sensorManager);
    }
    public    interface BuildStep{
        Presenter build();
    }

    private static class Steps implements
            DestinationStep,
            CurrentLocationStep,
            SensorManagerStep,
            BuildStep,
            ViewStep{

        MainActivityView mainActivityView;
        Location destination;
        Location currentLocation;
        SensorManager sensorManager;

        @Override
        public DestinationStep viewStep(MainActivityView mainActivityView) {
            this.mainActivityView = mainActivityView;
            return this;
        }


        @Override
        public CurrentLocationStep currentDestination(Location destination) {
            this.destination = destination;
            return this;
        }


        @Override
        public SensorManagerStep currentLocation(Location currentLocation) {
            this.currentLocation = currentLocation;
            return this;
        }

        @Override
        public BuildStep sensorManager(SensorManager sensorManager) {
            this.sensorManager = sensorManager;
            return this;
        }

        @Override
        public PresenterImp build() {
            return new PresenterImp(destination,currentLocation,sensorManager,mainActivityView);
        }



    }
}
