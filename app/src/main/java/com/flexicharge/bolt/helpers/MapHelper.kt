package com.flexicharge.bolt.helpers

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.MainActivity
import com.flexicharge.bolt.api.flexicharge.Charger
import com.flexicharge.bolt.api.flexicharge.Chargers
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

object MapHelper {
    lateinit var currentLocation: Location
    private lateinit var mMap: GoogleMap

    private var markerToChargerMap = mutableMapOf<Marker, Charger>()
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    private const val PERMISSION_CODE = 101
    private const val LOCATION_UPDATE_INTERVAL_MS = 5000L

    fun currLocation(activity: MainActivity) {
        if (MapHelper::currentLocation.isInitialized) {
            fetchLocation(activity)
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLocation.latitude, currentLocation.longitude),
                    13f
                )
            )
        } else {
            Toast.makeText(
                activity,
                "Location permissions are required for this feature.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun fetchLocation(activity: MainActivity) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_CODE
            )
            return
        }

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval = LOCATION_UPDATE_INTERVAL_MS
        }

        val supportMapFragment =
            activity.supportFragmentManager.findFragmentById(R.id.mainActivity_fragment_map) as SupportMapFragment
        supportMapFragment.getMapAsync(activity)

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                val fragment = activity.supportFragmentManager.findFragmentById(
                    R.id.mainActivity_fragment_map
                )
                if (fragment is SupportMapFragment) {
                    fragment.getMapAsync(activity)
                    currentLocation = result.lastLocation
                } else {
                    // Handle the case when the fragment is not found or is not a SupportMapFragment.
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
       /* val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val supportMapFragment =
                    activity.supportFragmentManager.findFragmentById(R.id.mainActivity_fragment_map) as SupportMapFragment
                supportMapFragment.getMapAsync(activity)
                getLocationAccess(activity)
                setCurrentLocation(activity)
            }
        }*/
    }

    private fun getLocationAccess(activity: MainActivity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun addNewMarkers(
        activity: MainActivity,
        chargers: Chargers,
        onTapMarker: (Charger?) -> Boolean
    ) {
        val blackIcon = BitmapDescriptorFactory.fromBitmap(
            AppCompatResources.getDrawable(activity, R.drawable.ic_black_marker)?.toBitmap()
        )
        val greenIcon = BitmapDescriptorFactory.fromBitmap(
            AppCompatResources.getDrawable(activity, R.drawable.ic_green_marker)?.toBitmap()
        )
        val redIcon = BitmapDescriptorFactory.fromBitmap(
            AppCompatResources.getDrawable(activity, R.drawable.ic_red_marker)?.toBitmap()
        )

        markerToChargerMap.keys.forEach {
            it.remove()
        }

        chargers.forEach { charger ->
            val marker = mMap.addMarker(
                MarkerOptions().position(LatLng(charger.location[0], charger.location[1])).title(
                    charger.chargerID.toString()
                )
            )
            markerToChargerMap[marker] = charger
            when (charger.status) {
                "Available" -> marker.setIcon(greenIcon)
                "Faulted" -> marker.setIcon(blackIcon)
                else -> marker.setIcon(redIcon)
            }
        }

        mMap.setOnMarkerClickListener {
            Log.d("mapHelper", "Marker clicked: " + it.id)
            onTapMarker(markerToChargerMap[it])
        }
    }

    fun panToPos(
        latitude: Double,
        longitude: Double
    ) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 13f))
    }

    fun onMapReady(activity: MainActivity, googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(activity, R.raw.flexicharge_map_style)
        )
        getLocationAccess(activity)
    }
}