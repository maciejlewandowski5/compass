package com.example.ngcompass;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationPicker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker marker;
    LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        double longitude =intent.getDoubleExtra("longitude",0);
        double latitude = intent.getDoubleExtra("latitude",0);

        location = new LatLng(latitude,longitude);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        marker = mMap.addMarker(
                new MarkerOptions()
                        .position(location)
                        .title(createTitle(location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        mMap.setOnMapClickListener(this::setMarker);
    }

    private void setMarker(LatLng latLng) {
        if(marker!=null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions().
                alpha(1).
                draggable(true).
                position(latLng).
                title(createTitle(latLng));

        marker = mMap.addMarker(markerOptions);
    }

    private String createTitle(LatLng location) {
        return "Lat.: " + location.latitude + " Long.: " + location.longitude;
    }

    public void locationPickConfirmationClicked(View view) {
        Intent data = new Intent();
        data.putExtra("latitude",marker.getPosition().latitude);
        data.putExtra("longitude",marker.getPosition().longitude);
        setResult(RESULT_OK, data);
        onBackPressed();
    }
}