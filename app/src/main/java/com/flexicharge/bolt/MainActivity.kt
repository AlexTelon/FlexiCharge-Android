package com.flexicharge.bolt

import android.content.Context
import android.content.Intent
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.AccountActivities.RegisterActivity
import com.flexicharge.bolt.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.button.setOnClickListener {
            setupChargerInput()
        }
        
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val isGuest = sharedPreferences.getBoolean("isGuest", false)
        if (!isGuest) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }


    private fun setupChargerInput() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.button.setOnClickListener {
            setupChargerInput()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val chargerPos = LatLng(57.779978, 14.161790)
        try {
            val curPos = LatLng(currentLocation.latitude, currentLocation.longitude)
            mMap.addCircle(
                CircleOptions().center(curPos).radius(30000.0).fillColor(0x034078105).strokeColor(
                    0x096144147.toInt()
                ).strokeWidth(4f)
            )
            mMap.addMarker(MarkerOptions().position(curPos).title("You are here"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 13f))

        } catch (e: Exception) {
            Log.v("MapsActivity", e.message.toString())
        }

      //  mMap.addMarker(MarkerOptions().position(chargerPos).title("Charger"))
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
        }
    }

    fun setupChargerInput() {
      
        val bottomSheetDialog = BottomSheetDialog(
            this@MainActivity, R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_input,
            findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        )

        setupChargerInputFocus(bottomSheetView)
        setupChargerInputCompletion(bottomSheetView)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
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
            var chargerId = (editTextInput1.text.toString() +
                    editTextInput2.text.toString() +
                    editTextInput3.text.toString() +
                    editTextInput4.text.toString() +
                    editTextInput5.text.toString() +
                    editTextInput6.text.toString())
            if (validateChargerId(chargerId)) validateConnectionToMockDataApi(
                chargerId,
                chargerInputStatus
            )
            else {
                chargerInputStatus.text = "ChargerId has to consist of 6 digits"
                chargerInputStatus.setBackgroundResource(R.color.red)
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

    private fun validateConnectionToMockDataApi(chargerId: String, chargerInputStatus: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getMockApiData(chargerId)
                if (response.isSuccessful) {
                    val charger = response.body() as FakeJsonResponse
                    Log.d("validateConnection", "Connected to charger " + charger.id)
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (charger.status == 1) {
                            chargerInputStatus.text =
                                "Connected to charger " + charger.id + "\n located at Long:" + charger.location.longitude + " Lat:" + charger.location.latitude
                            mMap.addMarker(MarkerOptions().position(LatLng(charger.location.latitude, charger.location.longitude)).title("Charger " + chargerId))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(charger.location.latitude, charger.location.longitude), 13f))

                            chargerInputStatus.setBackgroundResource(R.color.green)
                        } else {
                            chargerInputStatus.text = "Charger " + charger.id + " is busy"
                            chargerInputStatus.setBackgroundResource(R.color.red)
                        }
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger" + chargerId)
                    lifecycleScope.launch(Dispatchers.Main) {
                        chargerInputStatus.text = "Charger " + chargerId + " does not exist"
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