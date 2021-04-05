package com.example.ngcompass

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class LocationPicker : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null;
    private lateinit var location: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val intent = intent
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val latitude = intent.getDoubleExtra("latitude", 0.0)

        location = LatLng(latitude, longitude);
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        marker = mMap.addMarker(
                MarkerOptions()
                        .position(location)
                        .title(createTitle(location)))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))

        mMap.setOnMapClickListener { latLng: LatLng? ->

            if (latLng != null) {
                this.setMarker(latLng)
            }

        }

    }

    private fun setMarker(latLng: LatLng) {
        marker?.remove()
        val markerOptions = MarkerOptions().alpha(1f).draggable(true).position(latLng).title(createTitle(latLng))
        marker = mMap.addMarker(markerOptions)
    }

    private fun createTitle(location: LatLng): String {
        return "Lat.: " + location.latitude + " Long.: " + location.longitude
    }

    fun locationPickConfirmationClicked(view: View?) {
        saveToIntent()
        saveToStorage()
        onBackPressed()
    }

    private fun saveToIntent() {
        val data = Intent()
        if (marker != null) {
            data.putExtra("latitude", marker!!.position.latitude)
            data.putExtra("longitude", marker!!.position.longitude)
            setResult(Activity.RESULT_OK, data)
        }
    }

    private fun saveToStorage() {
        val sp = getSharedPreferences("destination", Context.MODE_PRIVATE)
        val e = sp.edit()
        if (marker != null) {
            e.putLong("latitude", java.lang.Double.doubleToRawLongBits(marker!!.position.latitude))
            e.putLong("longitude", java.lang.Double.doubleToRawLongBits(marker!!.position.longitude))
            e.apply()
        }
    }
}