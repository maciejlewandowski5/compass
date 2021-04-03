package com.example.ngcompass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ngcompass.animations.CompassRotationAnimation;
import com.example.ngcompass.mainactivity.LocationListenerAdapter;
import com.example.ngcompass.mainactivity.MainActivityView;
import com.example.ngcompass.mainactivity.model.location.AndroidLocation;
import com.example.ngcompass.mainactivity.model.location.Location;
import com.example.ngcompass.mainactivity.presenter.CompassPresenter;
import com.example.ngcompass.mainactivity.presenter.PointerCompassPresenter;
import com.example.ngcompass.mainactivity.presenter.SensorsPresenter;
import com.example.ngcompass.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements MainActivityView, SensorEventListener {

    private static final int MIN_TIME_LOCATION_UPDATE_MS = 0;
    private static final int MIN_DIST_LOCATION_UPDATE_MS = 0;
    private static final int REQUEST_FINE_LOCATION_CODE = 1;
    private static final int REQUEST_COARSE_LOCATION_CODE = 2;
    private final int PICK_LOCATION = 12;

    private ImageView compassFace;
    private ImageView locationPointer;
    private TextView pointingToLocationTitle;
    private TextView longitudeTextView;
    private TextView latitudeTextView;
    private LinearLayout coordinatesContainer;
    private FloatingActionButton locationPickerButton;

    private LocationManager locationManager;
    private SensorManager sensorManager;
    private LocationListenerAdapter locationListener;

    private CompassPresenter regularCompassPresenter;
    private PointerCompassPresenter gpsCompassPresenter;
    private SensorsPresenter sensorsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializePresenters();
        initializeServices();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            regularCompassPresenter.setScreenLandscapeMode();
            gpsCompassPresenter.setScreenLandscapeMode();
        } else {
            regularCompassPresenter.setScreenPortraitMode();
            gpsCompassPresenter.setScreenPortraitMode();
        }
    }

    private void initializeServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        locationListener = new LocationListenerAdapter();
    }

    private void initializePresenters() {
        sensorsPresenter = new SensorsPresenter();
        regularCompassPresenter = new CompassPresenter(sensorsPresenter);

        AndroidLocation destination = new AndroidLocation();
        AndroidLocation currentLocation = new AndroidLocation();
        destination.setLatitude(0);
        destination.setLongitude(0);
        currentLocation.setLongitude(50);
        currentLocation.setLatitude(50);

        gpsCompassPresenter = new PointerCompassPresenter(
                sensorsPresenter,
                destination,
                currentLocation);
    }

    @Override
    protected void onResume() {
        regularCompassPresenter.onResume();
        gpsCompassPresenter.onResume();

        initializeLocationListener();

        requestLocationUpdates();
        registerSensorsListeners();
        super.onResume();
    }

    @Override
    protected void onPause() {
        regularCompassPresenter.onPause();
        gpsCompassPresenter.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListener);
        locationListener.removeListeners();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOCATION) {
            if (resultCode == RESULT_OK) {

                Location location = new AndroidLocation();
                location.setLatitude(data.getDoubleExtra("latitude", 0));
                location.setLongitude(data.getDoubleExtra("longitude", 0));
                gpsCompassPresenter.updateDestination(location);
                if (!regularCompassPresenter.isCompassLocked()) {
                    regularCompassPresenter.updateCompass();
                    prepareAndStartCompassAnimation(regularCompassPresenter, compassFace);
                }
                ;

            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        updateAccMagReadings(event, event.sensor.getType());
        if (isAnyCompassPresenterUnlocked()) {
            sensorsPresenter.updateRotationModel();
        }
        if (!regularCompassPresenter.isCompassLocked()) {
            regularCompassPresenter.updateCompass();
            prepareAndStartCompassAnimation(regularCompassPresenter, compassFace);
        }
        if (!gpsCompassPresenter.isCompassLocked() && !locationListener.isFirstRead()) {
            gpsCompassPresenter.updateCompass();
            prepareAndStartCompassAnimation(gpsCompassPresenter, locationPointer);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Location destination = gpsCompassPresenter.getDestination();

        outState.putDouble("longitude",destination.getLongitude());
        outState.putDouble("latitude",destination.getLatitude());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Location destination
    }

    private void initializeLocationListener() {
        pointingToLocationTitle.setText(getString(R.string.waiting_for_gsp_signal));

        locationListener.setOnLocationChanged(location -> {
            gpsCompassPresenter.updateCurrentPosition(AndroidLocation.from(location));
            updateLocationTextViews();

        });
        locationListener.setOnProviderDisabled(this::disablePointingToLocation);

        locationListener.setOnProviderEnabled(() -> {
            pointingToLocationTitle.setText(getString(R.string.waiting_for_gsp_signal));
        });

        locationListener.setOnFirstLocationUpdate(location -> {
            enablePointingToLocation();

            gpsCompassPresenter.updateCurrentPosition(AndroidLocation.from(location));
            updateLocationTextViews();
        });
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
        Location destination = gpsCompassPresenter.getDestination();

        latitudeTextView.setText(Utils.formatLocation(destination.getLatitude()));
        longitudeTextView.setText(Utils.formatLocation(destination.getLongitude()));
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
    }

    @SuppressLint("MissingPermission")
    //permission check is in methods:
    // isEachPermissionGranted()
    // and requestLocationUpdates()
    private void requestLocationUpdates() {
        if (isEachPermissionGranted()) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION_CODE);

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION_CODE);
        }

        System.out.println("Regesterd");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_LOCATION_UPDATE_MS,
                MIN_DIST_LOCATION_UPDATE_MS,
                locationListener);
    }

    private boolean isEachPermissionGranted() {
        return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED;
    }


    private void prepareAndStartCompassAnimation(CompassPresenter gpsCompassPresenter, ImageView locationPointer) {
        CompassRotationAnimation animation = new
                CompassRotationAnimation(
                gpsCompassPresenter,
                locationPointer);

        animation.setOnAnimationStart(gpsCompassPresenter::lockCompass);
        animation.setOnAnimationEnd(gpsCompassPresenter::unlockCompass);
        animation.startAnimation();
        updateLocationTextViews();
    }

    private boolean isAnyCompassPresenterUnlocked() {
        return !regularCompassPresenter.isCompassLocked() || !gpsCompassPresenter.isCompassLocked();
    }

    private void updateAccMagReadings(SensorEvent event, int sensorType) {
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            sensorsPresenter.updateAccelerometerReadings(event.values.clone());
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            sensorsPresenter.updateMagnetometerReadings(event.values.clone());
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startLocationPickActivity(View view) {
        Intent i = new Intent(MainActivity.this, LocationPicker.class);
        Location location = gpsCompassPresenter.getDestination();
        if (location != null) {
            i.putExtra("latitude", location.getLatitude());
            i.putExtra("longitude", location.getLongitude());
        }
        startActivityForResult(i, PICK_LOCATION);

    }

}