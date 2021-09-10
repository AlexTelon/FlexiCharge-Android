package com.flexicharge.bolt

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.flexicharge.bolt.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CircleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var currentLocation : Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)

        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val chargerPos = LatLng(57.779978, 14.161790)
// color outline = 0x068268208
        // color fill = 0x12018184
       try {
           val curPos = LatLng(currentLocation.latitude, currentLocation.longitude)
           mMap.addCircle(CircleOptions().center(curPos).radius(30000.0).fillColor(0x034078105).strokeColor(
               0x096144147.toInt()
           ).strokeWidth(4f))
           mMap.addMarker(MarkerOptions().position(curPos).title("You are here"))
           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 13f))

       }catch(e : Exception){
           Log.v("MapsActivity", e.message.toString())
       }

    mMap.addMarker(MarkerOptions().position(chargerPos).title("Charger"))

    }



    private fun fetchLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)

            }

            val task = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener { location ->
                if(location != null){
                    currentLocation = location
                    val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    supportMapFragment.getMapAsync(this)

                }
            }
        }catch (e : Exception){
            Log.v("MapsActivity", e.message.toString())
        }
    }


}
