package com.example.ngcompass.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ngcompass.mainactivity.MainActivityConstants;
import com.example.ngcompass.mainactivity.model.location.AndroidLocation;
import com.example.ngcompass.mainactivity.model.location.Location;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public static final String SHARED_PREF_LOCATION_NAME = "destination";
    public static final String SHARED_PREF_LOCATION_LATITUDE_PART = "latitude";
    public static final String SHARED_PREF_LOCATION_LONGITUDE_PART = "longitude";

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void toastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @SuppressLint("DefaultLocale") // formatting GPS signal
    public static String formatLocation(double latOrLang) {
        return String.format("%.6f", latOrLang);

    }

    @SuppressLint("DefaultLocale") // specific accuracy required
    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return String.format("%.2f", distanceInMeters) + " m";
        } else {
            return String.format("%.2f", distanceInMeters / 1000d) + " km";
        }

    }

    public static void saveLocationToBundle(Bundle outState, Location destination, String saveName) {
        outState.putDouble(saveName + "Lat", destination.getLatitude());
        outState.putDouble(saveName + "Lon", destination.getLongitude());
    }

    public static Location readLocationFromBundle(Bundle savedState, String saveName) {
        Location destination = new AndroidLocation();
        destination.setLatitude(savedState.getDouble(saveName + "Lat"));
        destination.setLongitude(savedState.getDouble(saveName + "Lon"));
        return destination;
    }

    public static Location readLocationFromStorage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREF_LOCATION_NAME,
                MODE_PRIVATE);

        Location location = new AndroidLocation();

        location.setLatitude(Double.longBitsToDouble(sp.getLong(
                SHARED_PREF_LOCATION_LATITUDE_PART,
                Double.doubleToLongBits(0))));

        location.setLongitude(Double.longBitsToDouble(sp.getLong(
                SHARED_PREF_LOCATION_LONGITUDE_PART,
                Double.doubleToLongBits(0))));

        return location;
    }

    @Nullable
    public static Location readLocationFromIntent(Intent data) {
        if (data != null) {
            Location location = new AndroidLocation();
            location.setLatitude(data.getDoubleExtra("latitude", 0));
            location.setLongitude(data.getDoubleExtra("longitude", 0));
            return location;
        }else {
            return null;
        }
    }

}
