package com.flexicharge.bolt

import android.content.Context
import android.content.Intent
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaos.view.PinView
import com.flexicharge.bolt.AccountActivities.ProfileMenuLoggedInActivity
import com.flexicharge.bolt.AccountActivities.ProfileMenuLoggedOutActivity
import com.flexicharge.bolt.adapters.ChargePointListAdapter
import com.flexicharge.bolt.AccountActivities.RegisterActivity
import com.flexicharge.bolt.adapters.ChargersListAdapter
import com.flexicharge.bolt.databinding.ActivityMainBinding
import com.flexicharge.bolt.payment.KlarnaActivity
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
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), OnMapReadyCallback, ChargePointListAdapter.showChargePointInterface, ChargersListAdapter.ChangeInputInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var chargers: Chargers
    private lateinit var chargePoints: ChargePoints
    private lateinit var chargerInputDialog: BottomSheetDialog
    private lateinit var paymentSummaryDialog: BottomSheetDialog
    private lateinit var hours : String
    private lateinit var minutes : String
    private lateinit var pinView: PinView
    private lateinit var chargerInputStatus: TextView
    private lateinit var listOfChargersRecyclerView: RecyclerView
    private lateinit var currentTransaction: Transaction

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
                fetchLocation()
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 13f))
            } else {
                Toast.makeText(this, "Location permissions are required for this feature.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.identifyChargerButton.setOnClickListener {
            if (this::chargePoints.isInitialized) {
                setupChargerInputDialog()
            }
            else {
                updateChargerList()
                updateChargePointList()
                setupChargerInputDialog()
            }
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
        updateChargePointList()
    }

    override fun onResume() {
        super.onResume()
        checkCharging()
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAccess()
            }
        }
    }

    private fun setCurrentLocation() {
        try {
            val curPos = LatLng(currentLocation.latitude, currentLocation.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 13f))
        } catch (e: Exception) {
            Toast.makeText(this,"Location permissions are required for MyLocation.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNewMarkers(chargers: Chargers){
        val blackIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_black_marker)?.toBitmap())
        val greenIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_green_marker)?.toBitmap())
        val redIcon = BitmapDescriptorFactory.fromBitmap(this.getDrawable(R.drawable.ic_red_marker)?.toBitmap())
        chargers.forEach {
            val marker = mMap.addMarker(MarkerOptions().position(LatLng(it.location[0], it.location[1])).title(it.chargerID.toString()))
            if(it.status == "Available") marker.setIcon(greenIcon)
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

    override fun showChargePoint (
        latitude: Double,
        longitude: Double,
        chargePointID: Int
    ) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 13f))
        showCheckout(true, chargePointID, false, -1)
    }

    private fun setupChargingInProgressDialog(transaction: Transaction) {
        //TODO Populate and update frequently from transaction
        val bottomSheetDialog = BottomSheetDialog(this@MainActivity)

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.isDraggable = false;
        bottomSheetDialog.setCancelable(false)

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_in_progress,
            findViewById<ConstraintLayout>(R.id.chargerInProgress)
        )
        val progressbarPercent = bottomSheetView.findViewById<TextView>(R.id.progressbarPercent)
        val progressbar = bottomSheetView.findViewById<ProgressBar>(R.id.progressbar)
        var progress = 67;

        bottomSheetView.findViewById<MaterialButton>(R.id.stopCharging).setOnClickListener {
            //setChargerStatus(charger.chargerID,"Available")
            hours = Calendar.getInstance().time.hours.toString()
            minutes = Calendar.getInstance().time.minutes.toString()
            bottomSheetDialog.dismiss()
            displayPaymentSummaryDialog()
        }

        progressbar.progress = progress;
        progressbarPercent.text = progress.toString();

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun displayPaymentSummaryDialog(){
        paymentSummaryDialog = BottomSheetDialog(
            this@MainActivity, R.style.BottomSheetDialogTheme
        )
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_payment_summary,
            findViewById<ConstraintLayout>(R.id.paymentSummaryLayout)
        )
        val energyUsed = bottomSheetView.findViewById<TextView>(R.id.energy_used_value)
        val duration = bottomSheetView.findViewById<TextView>(R.id.duration_value)
        val chargingStopTime = bottomSheetView.findViewById<TextView>(R.id.charging_stop_time)
        //energyUsed.text = ""
        //duration.text = ""
        chargingStopTime.text = "Charging stopped at " + hours + ":" + minutes

        val cross = bottomSheetView.findViewById<ImageButton>(R.id.close_button)
        cross.setOnClickListener {
            paymentSummaryDialog.dismiss()
        }

        paymentSummaryDialog.setContentView(bottomSheetView)
        paymentSummaryDialog.show()

    }

    private fun setupChargerInputDialog() {
        chargerInputDialog = BottomSheetDialog(
            this@MainActivity, R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_input,
            findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        )

        val arrow = bottomSheetView.findViewById<ImageView>(R.id.arrow)
        arrow.setOnClickListener {
            displayChargePointList(bottomSheetView,arrow)
        }

        val backButton = bottomSheetView.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            showCheckout(false, -1, false, -1)
        }

        setupChargerInput(bottomSheetView)

        chargerInputDialog.setContentView(bottomSheetView)
        chargerInputDialog.show()
    }

    private fun setupChargerInput(bottomSheetView: View) {
        pinView = bottomSheetView.findViewById<PinView>(R.id.charger_input_pinview)
        chargerInputStatus = bottomSheetView.findViewById<TextView>(R.id.charger_input_status)
        pinView.doOnTextChanged { text, start, before, count ->
            if (text?.length == 6) {
                val chargerId = text.toString().toUInt().toInt()

                if (validateChargerId(text.toString())) {
                    validateChargerConnection(chargerId,chargerInputStatus)
                    hideKeyboard(bottomSheetView)
                } else {
                    setChargerButtonStatus(chargerInputStatus, false, "ChargerId has to consist of 6 digits", 0)
                    hideKeyboard(bottomSheetView)
                }
            }
        }
    }

    override fun changeInput(newInput: String){
        pinView.setText(newInput)
    }

    private fun displayChargerList(bottomSheetView: View, chargePointId: Int){
        val chargerId = pinView.text.toString()

        listOfChargersRecyclerView = bottomSheetView.findViewById(R.id.chargerListRecyclerView)
        listOfChargersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        if (this::chargePoints.isInitialized) {
            var chargersInCp = chargers.filter {it.chargePointID == chargePointId}
            var chargePoint = chargePoints.filter { it.chargePointID == chargePointId }[0]
            listOfChargersRecyclerView.adapter = ChargersListAdapter(chargersInCp, chargerId, chargePoint,  this)
        }



        // Only add decoration on first-time display
        if( listOfChargersRecyclerView.itemDecorationCount == 0) {
            listOfChargersRecyclerView.addItemDecoration(SpacesItemDecoration(15))
        }
    }

    private fun displayChargePointList(bottomSheetView: View, arrow: ImageView) {
        val listOfChargePointsRecyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.charger_input_list_recyclerview)
        listOfChargePointsRecyclerView.layoutManager = LinearLayoutManager(this)
        if (this::chargePoints.isInitialized) {
            var distanceToChargePoint = mutableListOf<String>()
            var chargerCount = mutableListOf<Int>()

            chargePoints.forEachIndexed { index, chargePoint ->
                var dist = FloatArray(1)
                Location.distanceBetween(chargePoint.location[0], chargePoint.location[1], currentLocation.latitude, currentLocation.longitude, dist)
                val df = DecimalFormat("#.##")
                val distanceStr = df.format(dist[0] / 1000).toString()
                val count = chargers.count { it.chargePointID.equals(chargePoint.chargePointID) }
                distanceToChargePoint.add(distanceStr)
                chargerCount.add(count)
            }
            listOfChargePointsRecyclerView.adapter = ChargePointListAdapter(chargePoints, this, distanceToChargePoint, chargerCount)
        }
        //listOfChargePointsRecyclerView.adapter = ChargePointListAdapter(chargePoints.map { it.chargePointAddress }, chargePoints.map {it.chargePointId}, chargePoints.map { it.chargePointId})
        val chargePointsNearMe = bottomSheetView.findViewById<TextView>(R.id.chargepoints_near_me)
        TransitionManager.beginDelayedTransition(bottomSheetView as ViewGroup?, Fade())
        if (listOfChargePointsRecyclerView.visibility == View.GONE) {
            arrow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_reverse));
            listOfChargePointsRecyclerView.visibility = View.VISIBLE
            chargePointsNearMe.visibility = View.GONE
        } else {
            arrow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
            listOfChargePointsRecyclerView.visibility = View.GONE
            chargePointsNearMe.visibility = View.VISIBLE
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

    private fun updateChargePointList() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.flexiChargeApi.getChargePointList()
                if (response.isSuccessful) {
                    val chargePoints = response.body() as ChargePoints
                    if (!chargePoints.isEmpty()) {
                        this@MainActivity.chargePoints = response.body() as ChargePoints
                    }
                    else {
                        this@MainActivity.chargePoints = ChargePoints()
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Http Error")
            } catch (e: IOException) {
                Log.d("validateConnection", "No Internet Error - ChargePointList will not be initialized")
            }
        }
    }

    private fun updateChargerList() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.flexiChargeApi.getChargerList()
                if (response.isSuccessful) {
                    val chargers = response.body() as Chargers
                    if (!chargers.isEmpty()) {
                        this@MainActivity.chargers = response.body() as Chargers
                        lifecycleScope.launch(Dispatchers.Main) {
                            addNewMarkers(chargers)
                        }
                    }
                    else {
                        this@MainActivity.chargers = Chargers()
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Http Error")
            } catch (e: IOException) {
                Log.d("validateConnection", "No Internet Error - ChargerList will not be initialized")
            }
        }
    }

    private fun createKlarnaTransactionSession(userId: String, chargerId: Int) {

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestBody = TransactionSession(chargerId, userId)
                val response = RetrofitInstance.flexiChargeApi.postTransactionSession(requestBody)
                if (response.isSuccessful) {
                    //TODO Backend Klarna/Order/Session Request if successful
                    val transaction = response.body() as Transaction
                    lifecycleScope.launch(Dispatchers.Main) {
                        val intent = Intent(this@MainActivity,KlarnaActivity::class.java)
                        intent.putExtra("ChargerId", chargerId)
                        intent.putExtra("ClientToken", transaction.client_token)
                        intent.putExtra("TransactionId", transaction.transactionID)
                        startActivity(intent)
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        // TODO Dont fake that it was successful
                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    setChargerButtonStatus(
                        chargerInputStatus,
                        false,
                        "Could not get all data correctly",
                        0
                    )
                }
            } catch (e: IOException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    setChargerButtonStatus(
                        chargerInputStatus,
                        false,
                        "Unable to establish connection",
                        0
                    )
                }
            }
        }
    }

    private fun reserveCharger(chargerId: Int, chargerInputStatus: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestParams: MutableMap<String, String> = HashMap()
                requestParams.put("connectorId", "1")
                requestParams.put("idTag", "1")
                requestParams.put("reservationId", "1")
                requestParams.put("parentIdTag", "1")
                val response = RetrofitInstance.flexiChargeApi.reserveCharger(chargerId, requestParams)
                if (response.isSuccessful) {

                    //TODO Backend Klarna/Order/Session Request if successful

                    val status = response.body() as String
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (status) {
                            "Accepted" -> {
                                createKlarnaTransactionSession("BoltGuest", chargerId)
                            }
                            "Faulted" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Faulted", 2)
                            }
                            "Occupied" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Occupied", 2)
                            }
                            "Rejected" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Rejected", 2)
                            }
                            "Unavailable" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Unavailable", 2)
                            }
                            else -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Unknown status", 2)
                            }
                        }
                    }
                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        // TODO Dont fake that it was successful
                        createKlarnaTransactionSession("BoltGuest", chargerId)
                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    setChargerButtonStatus(chargerInputStatus, false, "Could not get all data correctly", 0)
                }
            } catch (e: IOException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    setChargerButtonStatus(chargerInputStatus, false, "Unable to establish connection", 0)
                }
            }
        }
    }

    private fun validateChargerConnection(chargerId: Int, chargerInputStatus: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.flexiChargeApi.getCharger(chargerId)
                if (response.isSuccessful) {
                    val charger = response.body() as Charger

                    Log.d("validateConnection", "Connected to charger " + charger.chargerID)
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (charger.status) {
                            "Available" -> {
                                showCheckout(true, charger.chargePointID, true, chargerId)
                                setChargerButtonStatus(chargerInputStatus, false, "Pay to start charging", 2)
                            }
                            "Faulted" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Faulted", 2)
                            }
                            "Occupied" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Occupied", 2)
                            }
                            "Rejected" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Rejected", 2)
                            }
                            "Unavailable" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Unavailable", 2)
                            }
                            "Charging" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger is occupied", 2)
                            }

                            else -> { setChargerButtonStatus(chargerInputStatus, false, "Charger Out of Order", 2) }
                        }
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        setChargerButtonStatus(chargerInputStatus, false, "Charger not identified", 2)
                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    setChargerButtonStatus(chargerInputStatus, false, "Could not get all data correctly", 2)
                }
            } catch (e: IOException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    setChargerButtonStatus(chargerInputStatus, false, "Unable to establish connection", 2)
                }
            }
        }
    }

    private fun setChargerButtonStatus(chargerInputStatus: TextView, active: Boolean, text: String, color: Int) {
        chargerInputStatus.isClickable = active
        chargerInputStatus.text = text
        when (color) {
            0 -> { chargerInputStatus.setBackgroundResource(R.color.red)}
            1 -> { chargerInputStatus.setBackgroundResource(R.color.green)}
            2 -> { chargerInputStatus.setBackgroundResource(R.color.dark_grey)}
            3 -> { chargerInputStatus.setBackgroundResource(R.color.yellow)}
        }
    }

    private fun checkCharging() {

        //TODO Fix fetching transaction ID smartly

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val transactionId = sharedPreferences.getInt("TransactionId", -1)

        if (transactionId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.getTransaction(transactionId)
                    if (response.isSuccessful) {
                        //TODO Backend Klarna/Order/Session Request if successful
                        val transaction = response.body() as Transaction
                        lifecycleScope.launch(Dispatchers.Main) {
                            chargerInputDialog.dismiss()
                            setupChargingInProgressDialog(transaction)
                        }
                    }
                    else {

                    }
                } catch (e: HttpException) {

                } catch (e: IOException) {

                }
            }


        }
    }
    private fun showCheckout(bool: Boolean, chargePointId: Int, showPayment: Boolean, chargerId: Int){
        val checkoutLayout = chargerInputDialog.findViewById<ConstraintLayout>(R.id.charger_checkout_layout)
        val chargersNearMeLayout = chargerInputDialog.findViewById<ConstraintLayout>(R.id.chargers_near_me_layout)
        val chargerInput = chargerInputDialog.findViewById<EditText>(R.id.charger_input_pinview)
        val chargerInputStatus = chargerInputDialog.findViewById<TextView>(R.id.charger_input_status)
        val chargerInputView = chargerInputDialog.findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        val chargerLocationText = chargerInputDialog.findViewById<TextView>(R.id.currentLocation)
        val paymentText = chargerInputDialog.findViewById<TextView>(R.id.paymentText)
        val klarnaButton = chargerInputDialog.findViewById<ImageButton>(R.id.klarnaButton)
        TransitionManager.beginDelayedTransition(chargerInputView as ViewGroup?, ChangeBounds())

        if(bool) {
            chargersNearMeLayout?.visibility = View.GONE
            checkoutLayout?.visibility =View.VISIBLE
            if (showPayment) {
                klarnaButton?.visibility = View.VISIBLE
                paymentText?.visibility = View.VISIBLE
                chargerInput?.isEnabled = false
                klarnaButton?.setOnClickListener {
                    reserveCharger(chargerId, chargerInputStatus!!)
                }
            }
            else {
                klarnaButton?.visibility = View.GONE
                paymentText?.visibility = View.GONE
                chargerInput?.isEnabled = true
            }
            chargerLocationText?.text = chargePoints.filter { it.chargePointID == chargePointId }[0].name
            if (chargerInputView != null) {
                displayChargerList(chargerInputView, chargePointId)
            }
        }
        else {
            chargerInput?.isEnabled = true
            chargersNearMeLayout?.visibility = View.VISIBLE
            checkoutLayout?.visibility = View.GONE
            chargerInput?.text?.clear()
            setChargerButtonStatus(chargerInputStatus!!, false, getString(R.string.charger_status_enter_code), 2)
        }
    }
}
