package com.example.ngcompass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ngcompass.animations.CompassRotationAnimation;
import com.example.ngcompass.mainactivity.MainActivityView;
import com.example.ngcompass.mainactivity.model.location.AndroidLocation;
import com.example.ngcompass.mainactivity.model.location.Location;
import com.example.ngcompass.mainactivity.presenter.CompassPresenter;
import com.example.ngcompass.mainactivity.presenter.PointerCompassPresenter;
import com.example.ngcompass.mainactivity.presenter.SensorsPresenter;
import com.example.ngcompass.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityView, SensorEventListener, LocationListener {

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
    private TextView distanceTextView;
    private LinearLayout coordinatesContainer;
    private FloatingActionButton locationPickerButton;

    private LocationManager locationManager;
    private SensorManager sensorManager;

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
        correctPresentersForScreenRotation();


    }

    private Location retrieveSavedDestination() {
        SharedPreferences sp = getSharedPreferences("destination",MODE_PRIVATE);


        Location location = new AndroidLocation();
        location.setLatitude(Double.longBitsToDouble(sp.getLong("latitude",Double.doubleToLongBits(0))));
        location.setLongitude(Double.longBitsToDouble(sp.getLong("longitude",Double.doubleToLongBits(0))));
        return location;
    }

    private void correctPresentersForScreenRotation() {
        int rotation =  getWindowManager().getDefaultDisplay().getRotation();

        int angle = 0;
        int gpsCoercion = 0;
        switch (rotation) {
            case Surface.ROTATION_90:
                angle = -90;
                gpsCoercion = 90;
                break;
            case Surface.ROTATION_180:
                angle = 180;
                gpsCoercion = 180;
                break;
            case Surface.ROTATION_270:
                angle = 90;
                gpsCoercion = 270;
                break;
            default:
                angle = 0;
                gpsCoercion = 0;
                break;
            case Surface.ROTATION_0:
                break;
        }

        regularCompassPresenter.setScrennOrientationCorrection(angle);
        gpsCompassPresenter.setScrennOrientationCorrection(gpsCoercion);
    }

    private void initializeServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


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
        locationManager.removeUpdates(this);
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
        if (!gpsCompassPresenter.isCompassLocked() ) {
            gpsCompassPresenter.updateCompass();
            prepareAndStartCompassAnimation(gpsCompassPresenter, locationPointer);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Location destination = gpsCompassPresenter.getDestination();
        Location currentLocation = gpsCompassPresenter.getCurrentLocation();
        outState.putDouble("DestinationLat", destination.getLatitude());
        outState.putDouble("DestinationLon", destination.getLongitude());
        outState.putSerializable("CurrentLocationLat", currentLocation.getLatitude());
        outState.putSerializable("CurrentLocationLon", currentLocation.getLongitude());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Location destination = new AndroidLocation();
        Location currentLocation = new AndroidLocation();
        destination.setLatitude(savedInstanceState.getDouble("DestinationLat"));
        destination.setLongitude(savedInstanceState.getDouble("DestinationLon"));
        currentLocation.setLongitude(savedInstanceState.getDouble("CurrentLocationLon"));
        currentLocation.setLatitude(savedInstanceState.getDouble("CurrentLocationLat"));

        gpsCompassPresenter.updateCurrentPosition(currentLocation);
        gpsCompassPresenter.updateDestination(destination);
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
        Location destination = gpsCompassPresenter.getDestination();
        Location currentLocation = gpsCompassPresenter.getCurrentLocation();
        float distance = currentLocation.distanceTo(destination);

        latitudeTextView.setText(Utils.formatLocation(destination.getLatitude()));
        longitudeTextView.setText(Utils.formatLocation(destination.getLongitude()));
        
        distanceTextView.setText(Utils.formatDistance(distance));
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


        Location destination = retrieveSavedDestination();
        gpsCompassPresenter.updateDestination(destination);

        Location currentPosition = getLastKnownLocation();
        gpsCompassPresenter.updateCurrentPosition(currentPosition);
        enablePointingToLocation();


        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_LOCATION_UPDATE_MS,
                MIN_DIST_LOCATION_UPDATE_MS,
                this);
    }

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
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

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        gpsCompassPresenter.updateCurrentPosition(AndroidLocation.from(location));

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