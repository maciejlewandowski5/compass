package com.example.ngcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.ngcompass.mainactivity.MainActivityView;
import com.example.ngcompass.mainactivity.model.Compass;
import com.example.ngcompass.mainactivity.model.PointerCompass;
import com.example.ngcompass.mainactivity.presenter.CompassPresenter;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    private ImageView compassFace;
    private ImageView locationPointer;

    private LocationManager locationManager;

    private CompassPresenter regularCompassPresenter;
    private CompassPresenter gpsCompassPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassFace = findViewById(R.id.compass_front_face);
        locationPointer = findViewById(R.id.location_pointer);

        regularCompassPresenter = new CompassPresenter(new Compass());
        gpsCompassPresenter = new CompassPresenter(new PointerCompass());



    }
}