package com.flexicharge.bolt

import android.content.Context
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.util.Log
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.chaos.view.PinView
import com.flexicharge.bolt.adapters.ChargerListAdapter
import com.flexicharge.bolt.AccountActivities.RegisterActivity
import com.flexicharge.bolt.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), OnMapReadyCallback, ChargerListAdapter.panToMarkerInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var chargers: Chargers
    private lateinit var chargerInputDialog: BottomSheetDialog
    private lateinit var codeScanner : CodeScanner

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {

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
            if (this::currentLocation.isInitialized) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 13f))
            } else {
                Toast.makeText(this, "Location permissions are required for this feature.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cameraButton.setOnClickListener {
            binding.scannerView.visibility = View.VISIBLE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
            }else {
                scanQR()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.identifyChargerButton.setOnClickListener {
            setupChargerDialog()
        }
        binding.userButton.setOnClickListener {
            if (isGuest) {
                startActivity(Intent(this, ProfileMenuLoggedOutActivity::class.java))
            }
            else {
                startActivity(Intent(this, ProfileMenuLoggedInActivity::class.java))
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        updateChargerList()
    }

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE ->
            if (grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED) {
            getLocationAccess()
        }
        }
    }

    private fun setCurrentLocation() {
        try {
            val curPos = LatLng(currentLocation.latitude, currentLocation.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 13f))

        } catch (e: Exception) {
            Log.v("MapsActivity", e.message.toString())
            // TODO ERROR HANDLING
        }

    }

    private fun addNewMarkers(chargers: Chargers){
        val blackIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_black_marker)?.toBitmap())
        val greenIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_green_marker)?.toBitmap())
        val redIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_red_marker)?.toBitmap())
        chargers.forEach {
            val marker = mMap.addMarker(MarkerOptions().position(LatLng(it.location[0], it.location[1])).title(it.chargerID.toString()))
            if(it.status == 1) marker.setIcon(greenIcon)
            else marker.setIcon(redIcon)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.flexicharge_map_style) )
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CODE)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val supportMapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                supportMapFragment.getMapAsync(this)
                getLocationAccess()
                setCurrentLocation()
            }
        }
    }

    override fun panToMarker (latitude: Double, longitude: Double, title: String, status: Int) {
//        val icon : BitmapDescriptor
//        if (status == 0) {
//            icon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_red_marker)?.toBitmap())
//        }
//        else if (status == 1) {
//            icon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_green_marker)?.toBitmap())
//        }
//        else {
//            icon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_black_marker)?.toBitmap())
//        }
//        mMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title(title)).setIcon(icon)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 13f))
    }

    private fun setupChargerInProgress(charger: Charger) {

        val bottomSheetDialog = BottomSheetDialog(
            this@MainActivity
        )
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.peekHeight = 140
        bottomSheetDialog.setCancelable(false)

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_in_progress,
            findViewById<ConstraintLayout>(R.id.chargerInProgress)
        )
        val progressbar = bottomSheetView.findViewById<ProgressBar>(R.id.progressbar)
        val progressbarPercent = bottomSheetView.findViewById<TextView>(R.id.progressbarPercent)

        var progress = 67;

        bottomSheetView.findViewById<MaterialButton>(R.id.stopCharging).setOnClickListener {
            setChargerStatus(charger.chargerID,1)
            bottomSheetDialog.dismiss()
        }

        progressbar.progress = progress;
        progressbarPercent.text = progress.toString();

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }


    private fun setupChargerDialog() {
        chargerInputDialog = BottomSheetDialog(
            this@MainActivity
        )

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_input,
            findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        )

        val arrow = bottomSheetView.findViewById<ImageView>(R.id.arrow)
        arrow.setOnClickListener {
            displayChargerList(bottomSheetView,arrow)
        }

        val klarna = bottomSheetView.findViewById<ImageButton>(R.id.Klarna_Button)
        klarna.setOnClickListener {
            Toast.makeText(this, "You have chosen Klarna as your payment service", Toast.LENGTH_SHORT).show()
        }

        setupChargerInput(bottomSheetView)

        chargerInputDialog.setContentView(bottomSheetView)
        chargerInputDialog.show()
        //getAllChargersFromDataApi()
    }
    private fun setupChargerInput(bottomSheetView: View) {
        val pinView = bottomSheetView.findViewById<PinView>(R.id.charger_input_pinview)
        val chargerInputStatus = bottomSheetView.findViewById<TextView>(R.id.charger_input_status)

        pinView.doOnTextChanged { text, start, before, count ->
            if (text?.length == 6) {
                val chargerId = text.toString().toUInt().toInt()

                if (validateChargerId(text.toString())) validateConnectionToDataApi(
                    chargerId,
                    chargerInputStatus
                )
                else {
                    chargerInputStatus.text = "ChargerId has to consist of 6 digits"
                    chargerInputStatus.setBackgroundResource(R.color.red)
                    chargerInputStatus.isClickable = false
                }
            }
        }
    }


    private fun displayChargerList(bottomSheetView: View, arrow: ImageView){
        var distanceToCharger = mutableListOf<String>()
        chargers.forEach {
            var dist = FloatArray(1)
            Location.distanceBetween(it.location[0], it.location[1], currentLocation.latitude, currentLocation.longitude, dist)
            val df = DecimalFormat("#.##")
            val distanceStr = df.format(dist[0] / 1000).toString()
            distanceToCharger.add(distanceStr)
        }

        val listOfChargersRecyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.charger_input_list_recyclerview)
        listOfChargersRecyclerView.layoutManager = LinearLayoutManager(this)
        if (this::chargers.isInitialized)
            listOfChargersRecyclerView.adapter = ChargerListAdapter(chargers, this, distanceToCharger)
        //listOfChargersRecyclerView.adapter = ChargerListAdapter(chargers.map { it.chargePointAddress }, chargers.map {it.chargePointId}, chargers.map { it.chargePointId})
        val chargersNearMe = bottomSheetView.findViewById<TextView>(R.id.chargers_near_me)

        TransitionManager.beginDelayedTransition(bottomSheetView as ViewGroup?, Fade())

        if (listOfChargersRecyclerView.visibility == View.GONE) {
            arrow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_reverse));
            listOfChargersRecyclerView.visibility = View.VISIBLE
            chargersNearMe.visibility = View.GONE
            bottomSheetView.findViewById<ImageButton>(R.id.Klarna_Button).visibility = View.INVISIBLE
        } else {
            arrow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
            listOfChargersRecyclerView.visibility = View.GONE
            chargersNearMe.visibility = View.VISIBLE
            bottomSheetView.findViewById<ImageButton>(R.id.Klarna_Button).visibility = View.VISIBLE
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
                        lifecycleScope.launch(Dispatchers.Main) {
                            addNewMarkers(chargers)
                        }
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

    private fun scanQR(){
        val scannerView : CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.startPreview()

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.text))
                startActivity(browserIntent)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error : ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        scannerView.setOnClickListener{
            codeScanner.startPreview()
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
                        panToMarker(charger.location[0], charger.location[1], charger.chargePointID.toString(), charger.status)
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
                                    chargerInputDialog.dismiss()
                                    setupChargerInProgress(charger)
                                }
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
}
