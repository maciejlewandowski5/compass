package com.example.ngcompass.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ngcompass.mainactivity.androidimp.AndroidLocation;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.Location;
import com.google.android.gms.maps.model.LatLng;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public static final String SHARED_PREF_LOCATION_NAME = "destination";
    public static final String SP_LATITUDE = "latitude";
    public static final String SP_LONGITUDE = "longitude";

    private static final String LAT_SUFFIX = "Lat";
    private static final String LON_SUFFIX = "Lon";


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
        outState.putDouble(saveName + LAT_SUFFIX, destination.getLatitude());
        outState.putDouble(saveName + LON_SUFFIX, destination.getLongitude());
    }

    public static Location readLocationFromBundle(Bundle savedState, String saveName) {
        Location destination = new AndroidLocation();
        destination.setLatitude(savedState.getDouble(saveName + LAT_SUFFIX));
        destination.setLongitude(savedState.getDouble(saveName + LON_SUFFIX));
        return destination;
    }

    public static Location readLocationFromStorage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREF_LOCATION_NAME,
                MODE_PRIVATE);

        Location location = new AndroidLocation();

        location.setLatitude(Double.longBitsToDouble(sp.getLong(
                SP_LATITUDE,
                Double.doubleToLongBits(0))));

        location.setLongitude(Double.longBitsToDouble(sp.getLong(
                SP_LONGITUDE,
                Double.doubleToLongBits(0))));

        return location;
    }

    public static void saveLatLangToStorage(Context context, LatLng position) {
        SharedPreferences sp =
                context.getSharedPreferences(SHARED_PREF_LOCATION_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor e = sp.edit();

        e.putLong(
                SP_LATITUDE,
                java.lang.Double.doubleToRawLongBits(position.latitude));

        e.putLong(
                SP_LONGITUDE,
                java.lang.Double.doubleToRawLongBits(position.longitude));

        e.apply();

    }

    @Nullable
    public static Location readLocationFromIntent(Intent data) {
        if (data != null) {
            Location location = new AndroidLocation();

            location.setLatitude(data.getDoubleExtra(
                    SP_LATITUDE,
                    0));

            location.setLongitude(data.getDoubleExtra(
                    SP_LONGITUDE,
                    0));

            return location;
        } else {
            return null;
        }
    }

    public static void putLocationToIntent(Intent i, Location location) {
        i.putExtra(SP_LATITUDE, location.getLatitude());
        i.putExtra(SP_LONGITUDE, location.getLongitude());
    }

    public static LatLng getLatLangFromIntent(Intent intent) {
        double longitude = intent.getDoubleExtra(SP_LONGITUDE, 0.0);
        double latitude = intent.getDoubleExtra(SP_LATITUDE, 0.0);
        return new LatLng(latitude, longitude);
    }

    public static void putLatLangToIntent(Intent i, LatLng location) {
        i.putExtra(SP_LATITUDE, location.latitude);
        i.putExtra(SP_LONGITUDE, location.longitude);
    }


}
