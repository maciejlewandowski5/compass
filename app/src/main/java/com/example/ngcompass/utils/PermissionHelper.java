package com.example.ngcompass.utils;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PermissionHelper {

    private boolean locationAccess;


    public PermissionHelper() {
        this.locationAccess = false;
    }

    public void checkLocationPermission(AppCompatActivity activity) {
        if (
                ActivityCompat.checkSelfPermission(
                        activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(
                                activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {

            locationAccess = true;

        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivityConstants.REQUEST_FINE_COARSE_LOCATION_CODE);

            locationAccess = false;
        }
    }

    public void setLocationAccess(boolean locationAccess) {
        this.locationAccess = locationAccess;
    }

    public boolean isLocationAccess() {
        return locationAccess;
    }
}
