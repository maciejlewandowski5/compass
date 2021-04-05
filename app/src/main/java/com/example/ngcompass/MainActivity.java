package com.example.ngcompass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ngcompass.utils.CompassRotationAnimation;
import com.example.ngcompass.mainactivity.androidimp.AndroidSensorEvent;
import com.example.ngcompass.utils.MainActivityConstants;
import com.example.ngcompass.mainactivity.mvp.MainActivityView;
import com.example.ngcompass.mainactivity.androidimp.AndroidSensorManager;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SurfaceRotation;
import com.example.ngcompass.mainactivity.androidimp.AndroidSurfaceRotation;
import com.example.ngcompass.mainactivity.androidimp.AndroidLocation;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.Location;
import com.example.ngcompass.mainactivity.mvp.presenter.Presenter;

import com.example.ngcompass.mainactivity.mvp.presenter.PresenterImpBuilder;
import com.example.ngcompass.utils.PermissionHelper;
import com.example.ngcompass.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainActivity extends AppCompatActivity implements MainActivityView, SensorEventListener, LocationListener {

    private ImageView compassFace;
    private ImageView locationPointer;
    private TextView pointingToLocationTitle;
    private TextView longitudeTextView;
    private TextView latitudeTextView;
    private TextView distanceTextView;
    private LinearLayout coordinatesContainer;
    private LinearLayout distanceContainer;
    private FloatingActionButton locationPickerButton;

    private PermissionHelper permissionHelper;

    private LocationManager locationManager;
    private SensorManager sensorManager;

    private Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeViews();
        checkLocationPermission();
        buildPresenter();
        initializeServices();
        correctPresentersForScreenRotation();


    }

    private void checkLocationPermission() {
        permissionHelper = new PermissionHelper();
        permissionHelper.checkLocationPermission(this);
    }

    private void buildPresenter() {
        presenter = PresenterImpBuilder.
                newBuilder().
                viewStep(this).
                currentDestination(new AndroidLocation(0, 0)).
                currentLocation(new AndroidLocation(50, 50)).
                sensorManager(new AndroidSensorManager()).
                build();
    }

    private void correctPresentersForScreenRotation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        SurfaceRotation surfaceRotation = new AndroidSurfaceRotation();
        presenter.setScreenOrientationCorrection(surfaceRotation.getRotation(rotation));

    }

    private void initializeServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }


    @Override
    protected void onResume() {
        presenter.onResume();
        registerSensorsListeners();
        if (permissionHelper.isLocationAccess()) {
            startLocationUpdates();
        } else {
            disablePointingToLocation();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {

        presenter.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivityConstants.PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Location location = Utils.readLocationFromIntent(data);
                    presenter.updateDestination(location);
                }
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        com.example.ngcompass.mainactivity.mvp.presenter.dependency.SensorEvent sensorEvent =
                new AndroidSensorEvent(event);
        presenter.onSensorChanged(sensorEvent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Location destination = presenter.getDestination();
        Location currentLocation = presenter.getCurrentLocation();

        if (currentLocation != null && destination != null) {
            Utils.saveLocationToBundle(outState, destination,
                    MainActivityConstants.BUNDLE_DESTINATION_SAVE_NAME);

            Utils.saveLocationToBundle(outState, currentLocation,
                    MainActivityConstants.BUNDLE_CURRENT_LOCATION_SAVE_NAME);
        }
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Location currentLocation =
                Utils.readLocationFromBundle(savedInstanceState,
                        MainActivityConstants.BUNDLE_CURRENT_LOCATION_SAVE_NAME);

        Location destination =
                Utils.readLocationFromBundle(savedInstanceState,
                        MainActivityConstants.BUNDLE_DESTINATION_SAVE_NAME);

        presenter.updateCurrentPosition(currentLocation);
        presenter.updateDestination(destination);

        enablePointingToLocation();
    }


    private void disablePointingToLocation() {
        coordinatesContainer.setVisibility(View.INVISIBLE);
        distanceContainer.setVisibility(View.INVISIBLE);
        presenter.lockGPSCompass();
        locationPointer.setVisibility(View.INVISIBLE);
        locationPointer.setImageResource(R.drawable.compass_pointer_disabled);
        locationPickerButton.setClickable(false);
        locationPickerButton.setEnabled(false);
        if (permissionHelper.isLocationAccess()) {
            pointingToLocationTitle.setText(R.string.enable_gps_to_navigate);
            Utils.toastMessage(this, getString(R.string.please_enable_gps));
        } else {
            pointingToLocationTitle.setText(R.string.no_location_access);
            Utils.toastMessage(this, getString(R.string.requre_permisssion));
        }
    }

    private void enablePointingToLocation() {
        coordinatesContainer.setVisibility(View.VISIBLE);
        pointingToLocationTitle.setText(getString(R.string.pointing_to_location));
        presenter.unlockGPSCompass();
        distanceContainer.setVisibility(View.VISIBLE);
        locationPointer.setVisibility(View.VISIBLE);
        locationPointer.setImageResource(R.drawable.comapss_location_pointer);
        locationPickerButton.setEnabled(true);
        locationPickerButton.setClickable(true);
    }

    private void updateLocationTextViews() {

        Location destination = presenter.getDestination();
        latitudeTextView.setText(Utils.formatLocation(destination.getLatitude()));
        longitudeTextView.setText(Utils.formatLocation(destination.getLongitude()));
        distanceTextView.setText(Utils.formatDistance(presenter.getDistance()));
    }

    private void registerSensorsListeners() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        startSensorListener(accelerometer);
        startSensorListener(magnetometer);

    }

    private void startSensorListener(Sensor sensor) {
        if (sensor != null) {
            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Utils.toastMessage(this, getString(R.string.sensor_not_supported));
        }
    }


    private void initializeViews() {
        compassFace = findViewById(R.id.compass_front_face);
        locationPointer = findViewById(R.id.location_pointer);
        pointingToLocationTitle = findViewById(R.id.pointing_to_location_title);
        longitudeTextView = findViewById(R.id.longitude);
        latitudeTextView = findViewById(R.id.latitude);
        coordinatesContainer = findViewById(R.id.gps_coordinates_container);
        locationPickerButton = findViewById(R.id.location_picker);
        distanceTextView = findViewById(R.id.distance);
        distanceContainer = findViewById(R.id.distance_container);
    }


    private void startLocationUpdates() {
        presenter.updateDestination(Utils.readLocationFromStorage(this));
        presenter.updateCurrentPosition(getLastKnownLocation());

        requestLocationUpdates();
        enablePointingToLocation();

    }


    @SuppressLint("MissingPermission") //  // Checked by PermisssionHelper
    private void requestLocationUpdates() {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MainActivityConstants.MIN_TIME_LOCATION_UPDATE_MS,
                MainActivityConstants.MIN_DIST_LOCATION_UPDATE_MS,
                this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (MainActivityConstants.REQUEST_FINE_COARSE_LOCATION_CODE == requestCode) {
            permissionHelper.setLocationAccess(
                    grantResults.length > 0
                            &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    private Location getLastKnownLocation() {

        List<String> providers = locationManager.getProviders(true);
        android.location.Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") // Checked by PermisssionHelper
                    android.location.Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {

                bestLocation = l;
            }
        }

        return AndroidLocation.from(bestLocation);
    }


    @Override
    public void prepareAndStartGPSCompassAnimation(double lastAzimuth, float currentAzimuth) {
        CompassRotationAnimation animation = new
                CompassRotationAnimation(
                lastAzimuth,
                currentAzimuth,
                locationPointer);

        animation.setOnAnimationStart(() -> presenter.lockGPSCompass());
        animation.setOnAnimationEnd(() -> presenter.unlockGPSCompass());
        animation.startAnimation();
        updateLocationTextViews();
    }

    @Override
    public void prepareAndStartRegularCompassAnimation(double lastAzimuth, float currentAzimuth) {
        CompassRotationAnimation animation = new
                CompassRotationAnimation(
                lastAzimuth,
                currentAzimuth,
                compassFace);

        animation.setOnAnimationStart(presenter::lockRegularCompass);
        animation.setOnAnimationEnd(presenter::unlockRegularCompass);
        animation.startAnimation();
        updateLocationTextViews();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startLocationPickActivity(View view) {
        Intent i = new Intent(MainActivity.this, LocationPicker.class);
        Location location = presenter.getDestination();
        if (location != null) {
            Utils.putLocationToIntent(i, location);
        }
        startActivityForResult(i, MainActivityConstants.PICK_LOCATION);

    }


    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        presenter.updateCurrentPosition(AndroidLocation.from(location));

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        enablePointingToLocation();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        disablePointingToLocation();
    }
}