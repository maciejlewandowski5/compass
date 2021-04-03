package com.example.ngcompass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ngcompass.mainactivity.MainActivityView;
import com.example.ngcompass.mainactivity.model.Compass;
import com.example.ngcompass.mainactivity.model.PointerCompass;
import com.example.ngcompass.mainactivity.model.location.AndroidLocation;
import com.example.ngcompass.mainactivity.presenter.CompassPresenter;
import com.example.ngcompass.mainactivity.presenter.PointerCompassPresenter;
import com.example.ngcompass.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements MainActivityView, LocationListener, SensorEventListener {

    private static final int MIN_TIME_LOCATION_UPDATE_MS = 10000;
    private static final int MIN_DIST_LOCATION_UPDATE_MS = 0;

    private ImageView compassFace;
    private ImageView locationPointer;
    private TextView pointingToLocationTitle;
    private TextView longitudeTextView;
    private TextView latitudeTextView;
    private LinearLayout coordinatesContainer;
    private FloatingActionButton locationPickerButton;

    private LocationManager locationManager;
    private SensorManager sensorManager;

    private CompassPresenter regularCompassPresenter;
    private PointerCompassPresenter gpsCompassPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeCompassPresenters();
        initializeServices();
        requestLocationUpdates();
    }

    private void initializeServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    private void initializeCompassPresenters() {
        regularCompassPresenter = new CompassPresenter(new Compass());
        AndroidLocation destination = new AndroidLocation();
        AndroidLocation currentLocation = new AndroidLocation();
        destination.setLatitude(0);
        destination.setLongitude(0);
        currentLocation.setLongitude(50);
        currentLocation.setLatitude(50);
        gpsCompassPresenter = new PointerCompassPresenter(new PointerCompass(destination,currentLocation));
    }

    @Override
    protected void onResume() {
        regularCompassPresenter.onResume();
        gpsCompassPresenter.onResume();
        registerSensorsListeners();
        super.onResume();
    }

    private void registerSensorsListeners() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        startSensorListener(accelerometer);
        startSensorListener(magnetometer);

    }

    private void startSensorListener(Sensor sensor) {
        if(sensor!=null){
            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Utils.toastMessage(this, getString(R.string.sensor_not_supported));
        }
    }

    @Override
    protected void onPause() {
        regularCompassPresenter.onPause();
        gpsCompassPresenter.onPause();
        sensorManager.unregisterListener(this);
        super.onPause();
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

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    2);
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_LOCATION_UPDATE_MS,
                MIN_DIST_LOCATION_UPDATE_MS,this);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        gpsCompassPresenter.updateCurrentPosition(AndroidLocation.from(location));
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        locationPointer.setVisibility(View.VISIBLE);
        coordinatesContainer.setVisibility(View.VISIBLE);
        pointingToLocationTitle.setVisibility(View.VISIBLE);
        locationPickerButton.setClickable(true);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        locationPointer.setVisibility(View.INVISIBLE);
        coordinatesContainer.setVisibility(View.INVISIBLE);
        pointingToLocationTitle.setVisibility(View.INVISIBLE);
        locationPickerButton.setClickable(false);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}