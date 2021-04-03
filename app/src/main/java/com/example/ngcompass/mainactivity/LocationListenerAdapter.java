package com.example.ngcompass.mainactivity;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class LocationListenerAdapter implements LocationListener, Serializable {

    private boolean firstRead;

    private OnFirstLocationUpdate onFirstLocationUpdate;
    private OnLocationChanged onLocationChanged;
    private OnProviderEnabled onProviderEnabled;
    private OnProviderDisabled onProviderDisabled;

    public LocationListenerAdapter() {
        this.firstRead = true;
    }

    public boolean isFirstRead() {
        return firstRead;
    }

    public void setFirstRead(boolean firstRead) {
        this.firstRead = firstRead;
    }

    public void setOnFirstLocationUpdate(OnFirstLocationUpdate onFirstLocationUpdate) {
        this.onFirstLocationUpdate = onFirstLocationUpdate;
    }

    public void setOnLocationChanged(OnLocationChanged onLocationChanged) {
        this.onLocationChanged = onLocationChanged;
    }

    public void setOnProviderEnabled(OnProviderEnabled onProviderEnabled) {
        this.onProviderEnabled = onProviderEnabled;
    }

    public void setOnProviderDisabled(OnProviderDisabled onProviderDisabled) {
        this.onProviderDisabled = onProviderDisabled;
    }

    public void removeListeners() {
        firstRead = true;
        onFirstLocationUpdate = null;
        onLocationChanged = null;
        onProviderEnabled = null;
        onProviderDisabled = null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (firstRead) {
            if (onFirstLocationUpdate != null) {
                onFirstLocationUpdate.onFirstLocationUpdate(location);
            }
            firstRead = false;
        } else {
            if (onLocationChanged != null) {
                onLocationChanged.onLocationChanged(location);
            }
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if (onProviderEnabled != null) {
            onProviderEnabled.onProviderEnabled();
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if (onProviderDisabled != null) {
            onProviderDisabled.onProviderDisabled();
        }
    }

    public interface OnFirstLocationUpdate {
        void onFirstLocationUpdate(Location location);
    }

    public interface OnLocationChanged {
        void onLocationChanged(Location location);
    }

    public interface OnProviderEnabled {
        void onProviderEnabled();
    }

    public interface OnProviderDisabled {
        void onProviderDisabled();
    }
}
