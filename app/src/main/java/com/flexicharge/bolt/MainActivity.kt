package com.flexicharge.bolt

import android.content.Context
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.util.Log
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.adapters.ChargerListAdapter
import com.flexicharge.bolt.AccountActivities.RegisterActivity
import com.flexicharge.bolt.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin



class MainActivity : AppCompatActivity(), OnMapReadyCallback, ChargerListAdapter.addAndPanToMarkerInterface {

    private var chargerAddressList = mutableListOf<String>()
    private var chargerDistanceList = mutableListOf<Int>()
    private var numberOfChargers = mutableListOf<Int>()


    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var mockChargers: Chargers

    override fun onCreate(savedInstanceState: Bundle?) {

        updateMockChargerList()
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val isGuest = sharedPreferences.getBoolean("isGuest", false)
        if (!isGuest) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.positionPinButton.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 13f))
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.identifyChargerButton.setOnClickListener {
            setupChargerInput()
        }
        binding.positionPinButton.setOnClickListener {
            moveCameraToCurrentPosition()
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val isGuest = sharedPreferences.getBoolean("isGuest", false)
        if (!isGuest) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.flexicharge_map_style)
        )
        try {
            val curPos = LatLng(currentLocation.latitude, currentLocation.longitude)
            /*
            mMap.addCircle(
                CircleOptions().center(curPos).radius(13000.0).fillColor(0x034078105).strokeColor(
                    0x096144147.toInt()
                ).strokeWidth(4f)
            )
            addNewMarkers(mockChargers)
            mMap.addMarker(MarkerOptions().position(curPos).title("You are here"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 13f))

        } catch (e: Exception) {
            Log.v("MapsActivity", e.message.toString())
            // TODO ERROR HANDLING
        }

        mMap.setOnMarkerClickListener {  marker ->
            val distance = BigDecimal(distanceToMarkerInKm(marker.position.latitude, marker.position.longitude)).setScale(1, RoundingMode.HALF_EVEN)
            var numberOfChargers : Int = -1
            mockChargers.forEach {
                if(it.id.toString() == marker.title.toString()){
                    numberOfChargers = it.numberOfChargers
                }
            }
            Toast.makeText(this,"This charger is " + distance.toString() + " km away", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Number of chargers: " + numberOfChargers.toString(), Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun addNewMarkers(chargers: Chargers){
        val blackIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_black_marker)?.toBitmap())
        val greenIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_green_marker)?.toBitmap())
        val redIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_red_marker)?.toBitmap())
        chargers.forEach {
            val marker = mMap.addMarker(MarkerOptions().position(LatLng(it.location.latitude, it.location.longitude)).title(it.id.toString()))
            if(it.numberOfChargers > 0) marker.setIcon(greenIcon)
            else marker.setIcon(redIcon)
        }
    }

    private fun moveCameraToCurrentPosition(){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 13f))
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
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )

            }
            val task = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    val supportMapFragment =
                        supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    supportMapFragment.getMapAsync(this)
                }
            }
        } catch (e: Exception) {
            Log.v("MapsActivity", e.message.toString())
            // TODO ERROR HANDLING
        }
    }

    override fun addAndPanToMarker (latitude: Double, longitude: Double, title: String) {
        mMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title(title))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 13f))
    }

    private fun setupChargerInput() {

        val bottomSheetDialog = BottomSheetDialog(
            this@MainActivity, R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_input,
            findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        )

        val arrow = bottomSheetView.findViewById<ImageView>(R.id.arrow)
        arrow.setOnClickListener {
            displayChargerList(bottomSheetView,arrow)
        }
        setupChargerInputFocus(bottomSheetView)
        setupChargerInputCompletion(bottomSheetView)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        //getAllChargersFromDataApi()
    }

    private fun displayChargerList(bottomSheetView: View, arrow: ImageView){

        val listOfChargersRecyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.charger_input_list_recyclerview)
        listOfChargersRecyclerView.layoutManager = LinearLayoutManager(this)
        if (this::chargers.isInitialized)
            listOfChargersRecyclerView.adapter = ChargerListAdapter(chargers, this)
        //listOfChargersRecyclerView.adapter = ChargerListAdapter(chargers.map { it.chargePointAddress }, chargers.map {it.chargePointId}, chargers.map { it.chargePointId})
        val chargersNearMe = bottomSheetView.findViewById<TextView>(R.id.chargers_near_me)

        TransitionManager.beginDelayedTransition(bottomSheetView as ViewGroup?, Fade())

        if (listOfChargersRecyclerView.visibility == View.GONE) {
            arrow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_reverse));
            listOfChargersRecyclerView.visibility = View.VISIBLE
            chargersNearMe.visibility = View.GONE
        } else {
            arrow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
            listOfChargersRecyclerView.visibility = View.GONE
            chargersNearMe.visibility = View.VISIBLE
        }
    }

    private fun setupChargerInputFocus(view: View) {
        val editTextInput1 = view.findViewById<EditText>(R.id.charger_input_edit_text_1)
        val editTextInput2 = view.findViewById<EditText>(R.id.charger_input_edit_text_2)
        val editTextInput3 = view.findViewById<EditText>(R.id.charger_input_edit_text_3)
        val editTextInput4 = view.findViewById<EditText>(R.id.charger_input_edit_text_4)
        val editTextInput5 = view.findViewById<EditText>(R.id.charger_input_edit_text_5)
        val editTextInput6 = view.findViewById<EditText>(R.id.charger_input_edit_text_6)

        editTextInput1.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput2.requestFocus()
        }
        editTextInput2.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput3.requestFocus()
        }
        editTextInput3.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput4.requestFocus()
        }
        editTextInput4.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput5.requestFocus()
        }
        editTextInput5.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput6.requestFocus()
        }
    }

    private fun setupChargerInputCompletion(view: View) {
        val editTextInput1 = view.findViewById<EditText>(R.id.charger_input_edit_text_1)
        val editTextInput2 = view.findViewById<EditText>(R.id.charger_input_edit_text_2)
        val editTextInput3 = view.findViewById<EditText>(R.id.charger_input_edit_text_3)
        val editTextInput4 = view.findViewById<EditText>(R.id.charger_input_edit_text_4)
        val editTextInput5 = view.findViewById<EditText>(R.id.charger_input_edit_text_5)
        val editTextInput6 = view.findViewById<EditText>(R.id.charger_input_edit_text_6)
        val chargerInputStatus = view.findViewById<TextView>(R.id.charger_input_status)
        editTextInput6.doOnTextChanged { _, _, _, _ ->
            val chargerId = (editTextInput1.text.toString() +
                    editTextInput2.text.toString() +
                    editTextInput3.text.toString() +
                    editTextInput4.text.toString() +
                    editTextInput5.text.toString() +
                    editTextInput6.text.toString())
            if (validateChargerId(chargerId)) validateConnectionToDataApi(
                chargerId.toInt(),
                chargerInputStatus
            )
            else {
                chargerInputStatus.text = "ChargerId has to consist of 6 digits"
                chargerInputStatus.setBackgroundResource(R.color.red)
                chargerInputStatus.isClickable = false
            }
        }
    }

    private fun validateChargerId(chargerId: String): Boolean {
        if (chargerId.length != 6) {
            return false
        }
        if (chargerId.count { it.isDigit() } != 6) {
            return false
        }
        return true
    }

    private fun updateChargerList() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getChargerList()
                if (response.isSuccessful) {
                    val chargers = response.body() as Chargers
                    Log.d("validateConnection", "Connected to charger ")
                    if (!chargers.isEmpty()) {
                        this@MainActivity.chargers = response.body() as Chargers
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger")
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
            }
        }

    }

    private fun setChargerStatus(chargerId: Int, status: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.setChargerStatus(chargerId, status)
                if (response.isSuccessful) {
                    val charger = response.body() as Charger
                    Log.d("validateConnection", "Charger:" + charger.chargerID +  " status set to" + status)
                    //if (!chargers.isEmpty()) {
                    //    chargers = response.body() as Chargers
                    //}
                } else {
                    Log.d("validateConnection", "Could not change status")
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
            }
        }
    }

    private fun validateConnectionToDataApi(chargerId: Int, chargerInputStatus: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getCharger(chargerId)
                if (response.isSuccessful) {
                    val charger = response.body() as Charger

                    Log.d("validateConnection", "Connected to charger " + charger.chargerID)
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (charger.status) {
                            0 -> {
                                chargerInputStatus.text = "Charger Occupied"
                                chargerInputStatus.setBackgroundResource(R.color.red)
                            }
                            1 -> {
                                chargerInputStatus.text = "Begin Charging"
                                chargerInputStatus.isClickable=true
                                chargerInputStatus.setBackgroundResource(R.color.green)
                                chargerInputStatus.setOnClickListener {
                                    setChargerStatus(charger.chargerID,0)
                                    chargerInputStatus.isClickable=true
                                    chargerInputStatus.setBackgroundResource(R.color.yellow)
                                    chargerInputStatus.text = "Tap to disconnect"
                                    chargerInputStatus.setOnClickListener {
                                        setChargerStatus(charger.chargerID,1)
                                        chargerInputStatus.text = "You disconnected from charger " + charger.chargerID + ". Have a nice day!"
                                        chargerInputStatus.setBackgroundResource(R.color.green)
                                        chargerInputStatus.isClickable=false
                                    }
                                }
                                addAndPanToMarker(charger.location[0], charger.location[1], charger.chargePointID.toString())
                                //chargerInputStatus.setBackgroundResource(R.color.green)
                            }
                            2 -> {
                                chargerInputStatus.text = "Charger Out of Order"
                                chargerInputStatus.setBackgroundResource(R.color.red)
                            }
                        }
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger" + chargerId)
                    lifecycleScope.launch(Dispatchers.Main) {
                        chargerInputStatus.text = "Charger Not Identified"
                        chargerInputStatus.setBackgroundResource(R.color.red)
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
                lifecycleScope.launch(Dispatchers.Main) {
                    chargerInputStatus.text = "Unable to establish connection"
                    chargerInputStatus.setBackgroundResource(R.color.red)
                }
            }
        }
    }

    private fun distanceToMarkerInKm(markerLat : Double, markerLon : Double): Double{
        val curPosLat : Double = currentLocation.latitude
        val curPosLon : Double = currentLocation.longitude
        val theta = curPosLon - markerLon
        var distance = sin(deg2rad(curPosLat)) * sin(deg2rad(markerLat)) + cos(deg2rad(curPosLat)) * cos(deg2rad(markerLat)) * cos(deg2rad(theta))
        distance = acos(distance)
        distance = rad2deg(distance)
        distance *= 60 * 1.1515
        distance *= 1.609344
        return distance
    }

    private fun deg2rad(deg : Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad : Double): Double {
        return rad * 180 / Math.PI
    }
}

