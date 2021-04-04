package com.example.ngcompass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.ngcompass.animations.CompassRotationAnimation;
import com.example.ngcompass.mainactivity.AndroidSensorEvent;
import com.example.ngcompass.mainactivity.MainActivityConstants;
import com.example.ngcompass.mainactivity.MainActivityView;
import com.example.ngcompass.mainactivity.AndroidSensorManager;
import com.example.ngcompass.mainactivity.SurfaceRotation;
import com.example.ngcompass.mainactivity.AndroidSurfaceRotation;
import com.example.ngcompass.mainactivity.model.location.AndroidLocation;
import com.example.ngcompass.mainactivity.model.location.Location;
import com.example.ngcompass.mainactivity.presenter.Presenter;

import com.example.ngcompass.mainactivity.presenter.PresenterImpBuilder;
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
    private FloatingActionButton locationPickerButton;

    private LocationManager locationManager;
    private SensorManager sensorManager;

    Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        buildPresenter();
        initializeServices();
        correctPresentersForScreenRotation();

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
        initializeLocationListener();
        startLocationUpdates();
        registerSensorsListeners();
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
        com.example.ngcompass.mainactivity.SensorEvent sensorEvent = new AndroidSensorEvent(event);
        presenter.onSensorChanged(sensorEvent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Location destination = presenter.getDestination();
        Location currentLocation = presenter.getCurrentLocation();

        Utils.saveLocationToBundle(outState, destination,
                MainActivityConstants.BUNDLE_DESTINATION_SAVE_NAME);

        Utils.saveLocationToBundle(outState, currentLocation,
                MainActivityConstants.BUNDLE_CURRENT_LOCATION_SAVE_NAME);
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

    private void initializeLocationListener() {
        pointingToLocationTitle.setText(getString(R.string.waiting_for_gsp_signal));

    }

    private void disablePointingToLocation() {
        locationPointer.setVisibility(View.INVISIBLE);
        coordinatesContainer.setVisibility(View.INVISIBLE);
        pointingToLocationTitle.setText(R.string.enable_gps_to_navigate);
        locationPickerButton.setClickable(false);
        Utils.toastMessage(this, getString(R.string.please_enable_gps));
    }

    private void enablePointingToLocation() {
        locationPointer.setVisibility(View.VISIBLE);
        coordinatesContainer.setVisibility(View.VISIBLE);
        pointingToLocationTitle.setText(getString(R.string.pointing_to_location));
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
    }


    private void startLocationUpdates() {
        presenter.updateDestination(Utils.readLocationFromStorage(this));
        presenter.updateCurrentPosition(getLastKnownLocation());

        enablePointingToLocation();
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivityConstants.REQUEST_FINE_LOCATION_CODE);

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivityConstants.REQUEST_COARSE_LOCATION_CODE);
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MainActivityConstants.MIN_TIME_LOCATION_UPDATE_MS,
                MainActivityConstants.MIN_DIST_LOCATION_UPDATE_MS,
                this);
    }

    private Location getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivityConstants.REQUEST_FINE_LOCATION_CODE);

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivityConstants.REQUEST_COARSE_LOCATION_CODE);
        }

        List<String> providers = locationManager.getProviders(true);
        android.location.Location bestLocation = null;
        for (String provider : providers) {
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
            i.putExtra("latitude", location.getLatitude());
            i.putExtra("longitude", location.getLongitude());
        }
        startActivityForResult(i, MainActivityConstants.PICK_LOCATION);

    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        presenter.updateCurrentPosition(AndroidLocation.from(location));

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        pointingToLocationTitle.setText(getString(R.string.waiting_for_gsp_signal));
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        disablePointingToLocation();
    }
}