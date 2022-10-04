package com.flexicharge.bolt.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaos.view.PinView
import com.flexicharge.bolt.R
import com.flexicharge.bolt.SpacesItemDecoration
import com.flexicharge.bolt.activities.businessLogic.RemoteChargePoints
import com.flexicharge.bolt.activities.businessLogic.RemoteChargers
import com.flexicharge.bolt.activities.businessLogic.RemoteTransaction
import com.flexicharge.bolt.helpers.MapHelper.addNewMarkers
import com.flexicharge.bolt.helpers.MapHelper.currLocation
import com.flexicharge.bolt.helpers.MapHelper.currentLocation
import com.flexicharge.bolt.helpers.MapHelper.fetchLocation
import com.flexicharge.bolt.helpers.MapHelper.panToPos
import com.flexicharge.bolt.adapters.ChargePointListAdapter
import com.flexicharge.bolt.adapters.ChargersListAdapter
import com.flexicharge.bolt.databinding.ActivityMainBinding
import com.flexicharge.bolt.helpers.MapHelper
import com.flexicharge.bolt.api.flexicharge.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), OnMapReadyCallback, ChargePointListAdapter.showChargePointInterface, ChargersListAdapter.ChangeInputInterface {
    private lateinit var binding: ActivityMainBinding       // the bindings in the Main Activity (camera, user, charger, position).
    private lateinit var chargerInputDialog: BottomSheetDialog
    private lateinit var paymentSummaryDialog: BottomSheetDialog
    private lateinit var hours : String
    private lateinit var minutes : String
    private lateinit var pinView: PinView
    private lateinit var chargerInputStatus: MaterialButton
    private lateinit var listOfChargersRecyclerView: RecyclerView

    private val remoteChargers = RemoteChargers()
    private val remoteChargePoints = RemoteChargePoints()
    private val currentRemoteTransaction = RemoteTransaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val isGuest = sharedPreferences.getBoolean("isGuest", true) //Set to true to enable registration

        if (!isGuest) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        remoteChargers.setOnRefreshedCallBack {
            lifecycleScope.launch(Dispatchers.Main) {
                addNewMarkers(this@MainActivity, remoteChargers.value, fun (charger: Charger?) : Boolean {
                    if(charger != null && validateChargerId(charger.chargerID.toString())) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            setupChargerInputDialog()
                            changeInput(charger.chargerID.toString())
                        }
                    }
                    return false;
                })
            }
        }
        remoteChargers.refresh(lifecycleScope)

        binding.mainActivityButtonPinPosition.setOnClickListener {
            currLocation(this)
        }

        binding.mainActivityButtonCamera.setOnClickListener {
            val intent = Intent(this, QrActivity::class.java)
            startActivityForResult(intent, 12345)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mainActivity_fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.mainActivityButtonIdentifyCharger.setOnClickListener {
            setupChargerInputDialog()
        }

        val loginSharedPref = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val accessToken = loginSharedPref.getString("accessToken", Context.MODE_PRIVATE.toString())
        val userId = loginSharedPref.getString("userId", Context.MODE_PRIVATE.toString())
        val isLoggedIn = loginSharedPref.getString("loggedIn", Context.MODE_PRIVATE.toString())

        binding.mainActivityButtonUser.setOnClickListener {
            if (isLoggedIn == "true") {
                val intent = Intent(this, ProfileMenuLoggedInActivity::class.java)
                intent.putExtra("accessToken", accessToken)
                intent.putExtra("userId", userId)
                startActivity(intent)
            } else {
                startActivity(Intent(this, ProfileMenuLoggedOutActivity::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {   //results of trying to connect to charger.
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 12345){
            if (resultCode == Activity.RESULT_OK){                                      // its ok
                try {
                    setupChargerInputDialog()
                    changeInput(data?.getStringExtra("QR_SCAN_RESULT").toString())
                }catch (e: Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()  //exception handling
                }
            }
        }
    }

    override fun onResume() { //Called after onRestoreInstanceState, onRestart, or onPause
        super.onResume()       //, for your activity to start interacting with the user.
        remoteChargers.refresh(lifecycleScope).invokeOnCompletion {
            remoteChargePoints.refresh(lifecycleScope).invokeOnCompletion {
                checkPendingTransaction()
            }
        }

        fetchLocation(this)
    }

    override fun changeInput(newInput: String){
        pinView.setText(newInput)
    }

    override fun showChargePoint (
        latitude: Double,
        longitude: Double,
        chargePointID: Int
    ) {
        panToPos(latitude, longitude)
        showCheckout(true, chargePointID, false, -1)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        MapHelper.onMapReady(this, googleMap)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public fun unixToDateTime(unixTime: String) : String {
        val sdf = SimpleDateFormat("MM/dd/HH:mm")
        val GMTOffset = TimeZone.getTimeZone("Europe/Stockholm")
        val netDate = Date(unixTime.toLong() * 1000)
        return sdf.format(netDate)
    }

    private fun stopChargingProcess(
        initialPercentage: Int,
        bottomSheetDialog: BottomSheetDialog,
    ) {
        val dateTime = unixToDateTime(currentRemoteTransaction.value.timestamp.toString())
        //hours = Calendar.getInstance().time.hours.toString()
        //minutes = Calendar.getInstance().time.minutes.toString()
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val response = RetrofitInstance.flexiChargeApi.transactionStop(currentRemoteTransaction.value.transactionID)
                if (response.isSuccessful) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().apply { putInt("TransactionId", -1) }.apply()
                        bottomSheetDialog.dismiss()
                        displayPaymentSummaryDialog(dateTime, initialPercentage)
                    }
                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().apply { putInt("TransactionId", -1) }.apply()
                        bottomSheetDialog.dismiss()
                        displayPaymentSummaryDialog(dateTime, initialPercentage)
                    }
                }
            }
        }
        catch (e: IOException) {
            lifecycleScope.launch(Dispatchers.Main) {
                val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().apply { putInt("TransactionId", -1) }.apply()
                bottomSheetDialog.dismiss()
                displayPaymentSummaryDialog(dateTime, initialPercentage)
            }
        }
    }


    private suspend fun setupChargingInProgressDialog() {
        if (this::chargerInputDialog.isInitialized) {
            chargerInputDialog.dismiss()
        }

        val charger = remoteChargers.value.filter { it.chargerID == currentRemoteTransaction.value.chargerID }.getOrNull(0)
        val chargePoint = remoteChargePoints.value.filter { it.chargePointID == charger?.chargePointID }.getOrNull(0)

        if(charger == null || chargePoint == null) {
            throw Exception("Tried to display charging information for a charger that doesn't exist.")
        }

        val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_in_progress,
            findViewById<ConstraintLayout>(R.id.chargerInProgress)
        )
        val progressbarPercent = bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textView_progressbarPercent)
        val progressbar = bottomSheetView.findViewById<ProgressBar>(R.id.chargeInProgressLayout_progressBar)
        val chargingTimeStatus = bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textview_chargingTimeStatus)
        val chargingLocation = bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textView_location)
        val chargeSpeed = bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textView_chargeSpeed)

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.isDraggable = false;
        bottomSheetDialog.setCancelable(false)

        var continueLooping = true;

        val initialPercentage = currentRemoteTransaction.value.currentChargePercentage

        bottomSheetView.findViewById<MaterialButton>(R.id.chargeInProgressLayout_button_stopCharging).setOnClickListener {
            continueLooping = false
            stopChargingProcess(initialPercentage, bottomSheetDialog)
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        if (!currentRemoteTransaction.value.paymentConfirmed) {
            val df = DecimalFormat("#.##")
            var percent = initialPercentage
            val delayBetweenUpdatesMs : Long = 2000

            chargingLocation.text = chargePoint.name
            chargeSpeed.text = (currentRemoteTransaction.value.kwhTransfered/100).toString() + " kWh transferred"
            GlobalScope.launch {
                while (percent < 100 && continueLooping) {

                    val refreshCurrentTransaction = currentRemoteTransaction.refresh(lifecycleScope)
                    refreshCurrentTransaction.invokeOnCompletion {
                        lifecycleScope.launch(Dispatchers.Main) {
                            percent = currentRemoteTransaction.value.currentChargePercentage
                            progressbar.progress = percent
                            progressbarPercent.text = percent.toString()
                            chargeSpeed.text = currentRemoteTransaction.value.kwhTransfered.toString() + " kWh"

                            val minutesLeft = (100 - percent) / 60
                            val secondsLeft = (100 - percent) % 60
                            val timeString = String.format("%02d:%02d", minutesLeft, secondsLeft)
                            chargingTimeStatus.text = timeString + " until fully charged"
                        }
                    }
                    if(refreshCurrentTransaction.isCancelled) {
                        val couldNotRetrieve = "Could not get data from server"
                        chargingTimeStatus.text = couldNotRetrieve
                        chargeSpeed.text = couldNotRetrieve
                        delay(delayBetweenUpdatesMs)
                        continue
                    }

                    Log.d("tag", percent.toString())
                    if (percent == 100) {
                        stopChargingProcess(initialPercentage, bottomSheetDialog)
                    }
                    else {
                        delay(delayBetweenUpdatesMs)
                    }
                }
            }
        }
        else {
            continueLooping = false
            stopChargingProcess(initialPercentage, bottomSheetDialog)
        }
    }

    private fun displayPaymentSummaryDialog(dateTime: String, initialPercentage: Int) {

        // TODO Look over values before final commit

        paymentSummaryDialog = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.layout_payment_summary, findViewById<ConstraintLayout>(R.id.paymentSummaryLayout))
        val energyUsedTextView = bottomSheetView.findViewById<TextView>(R.id.paymentSummaryLayout_textView_energyUsedValue)
        val durationTextView = bottomSheetView.findViewById<TextView>(R.id.paymentSummaryLayout_textView_durationValue)
        val chargingStopTimeTextView = bottomSheetView.findViewById<TextView>(R.id.paymentSummaryLayout_textView_finishedTime)
        val totalCostTextView = bottomSheetView.findViewById<TextView>(R.id.paymentPriceLayout_textView_price)
        val crossButton = bottomSheetView.findViewById<ImageButton>(R.id.paymentSummaryLayout_button_close)

        crossButton.setOnClickListener {
            paymentSummaryDialog.dismiss()
        }

        currentRemoteTransaction.refresh(lifecycleScope).invokeOnCompletion {
            val transaction = currentRemoteTransaction.value
            val kwhTransferred = transaction.kwhTransfered
            val totalCost = (transaction.kwhTransfered.toString().toDouble() * transaction.pricePerKwh.toDouble()/100).toFloat()
            val pricePerKwh = transaction.pricePerKwh
            val duration = transaction.currentChargePercentage - initialPercentage

            energyUsedTextView.text = kwhTransferred.toString() + " kWh @" + pricePerKwh + "kr kWh"
            durationTextView.text = duration.toString() + " Seconds"
            chargingStopTimeTextView.text = "Charging stopped at " + dateTime
            totalCostTextView.text = totalCost.toString() + "kr"

            paymentSummaryDialog.setContentView(bottomSheetView)
            paymentSummaryDialog.show()
        }
    }

    private fun setupChargerInputDialog() {

        chargerInputDialog = BottomSheetDialog(
            this@MainActivity, R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_input,
            findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        )

        val arrow = bottomSheetView.findViewById<ImageView>(R.id.chargePointsNearMeLayout_imageView_arrow)
        arrow.setOnClickListener {
            displayChargePointList(bottomSheetView,arrow)
        }

        val backButton = bottomSheetView.findViewById<ImageButton>(R.id.checkoutLayout_button_back)
        backButton.setOnClickListener {
            showCheckout(false, -1, false, -1)
        }

        setupChargerInput(bottomSheetView)

        chargerInputDialog.setContentView(bottomSheetView)
        chargerInputDialog.show()
    }

    private fun setupChargerInput(bottomSheetView: View) {

        pinView = bottomSheetView.findViewById<PinView>(R.id.chargerInputLayout_pinView_chargerInput)
        chargerInputStatus = bottomSheetView.findViewById<MaterialButton>(R.id.chargerInputLayout_textView_chargerStatus)
        pinView.doOnTextChanged { text, start, before, count ->
            if (text?.length == 6) {
                val chargerId = text.toString().toUInt().toInt()
                if (validateChargerId(text.toString())) {
                    displayChargerStatus(chargerId,chargerInputStatus)
                    hideKeyboard(bottomSheetView)
                } else {
                    setChargerButtonStatus(chargerInputStatus, false, "ChargerId has to consist of 6 digits", 0)
                    hideKeyboard(bottomSheetView)
                }
            }
        }

    }

    private fun displayChargerList(bottomSheetView: View, chargePointId: Int){
        val chargerId = pinView.text.toString()

        remoteChargers.refresh(lifecycleScope).invokeOnCompletion {
            lifecycleScope.launch(Dispatchers.Main) {
                listOfChargersRecyclerView = bottomSheetView.findViewById(R.id.checkoutLayout_recyclerView_chargerList)
                listOfChargersRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

                val chargersInCp = remoteChargers.value.filter {it.chargePointID == chargePointId}
                val chargePoint = remoteChargePoints.value.filter { it.chargePointID == chargePointId }[0]
                listOfChargersRecyclerView.adapter = ChargersListAdapter(chargersInCp, chargerId, chargePoint,  this@MainActivity)

                // Only add decoration on first-time display
                if( listOfChargersRecyclerView.itemDecorationCount == 0) {
                    listOfChargersRecyclerView.addItemDecoration(SpacesItemDecoration(15))
                }
            }
        }
    }

    private fun displayChargePointList(bottomSheetView: View, arrow: ImageView) {       // display the chargers near u
        val listOfChargePointsRecyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.chargePointsNearMeLayout_recyclerView_chargePointList)
        val chargePointsNearMe = bottomSheetView.findViewById<TextView>(R.id.chargePointsNearMeLayout_textView_nearMe)
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
        listOfChargePointsRecyclerView.layoutManager = LinearLayoutManager(this)
        remoteChargePoints.refresh(lifecycleScope).invokeOnCompletion {
            val distanceToChargePoint = mutableListOf<String>()
            val chargerCount = mutableListOf<Int>()

            remoteChargePoints.value.forEachIndexed { index, chargePoint ->
                val dist = FloatArray(1)
                var couldGetLocation = true
                try {
                    Location.distanceBetween(chargePoint.location[0], chargePoint.location[1], currentLocation.latitude, currentLocation.longitude, dist)
                }
                catch (e: UninitializedPropertyAccessException) {
                    dist[0] = 0f
                    couldGetLocation = false
                }

                val df = DecimalFormat("#.##")
                val distanceStr = if(couldGetLocation) { df.format(dist[0] / 1000).toString() } else {
                    "?"
                }

                val count = remoteChargers.value.count { it.chargePointID.equals(chargePoint.chargePointID) }
                distanceToChargePoint.add(distanceStr)
                chargerCount.add(count)
            }
            lifecycleScope.launch(Dispatchers.Main) {
                listOfChargePointsRecyclerView.adapter = ChargePointListAdapter(remoteChargePoints.value, this@MainActivity, distanceToChargePoint, chargerCount)
            }

        }
        //listOfChargePointsRecyclerView.adapter = ChargePointListAdapter(chargePoints.map { it.chargePointAddress }, chargePoints.map {it.chargePointId}, chargePoints.map { it.chargePointId})
    }


    private fun createKlarnaTransactionSession(userId: String, chargerId: Int) {

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestBody = TransactionSession(chargerId, userId) // post request is stored in HTTP body.
                val response = RetrofitInstance.flexiChargeApi.postTransactionSession(requestBody)
                if (response.isSuccessful) {
                    //TODO Backend Klarna/Order/Session Request if successful
                    val transaction = response.body() as Transaction
                    lifecycleScope.launch(Dispatchers.Main) {
                        val intent = Intent(this@MainActivity, KlarnaActivity::class.java)
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

    private fun reserveCharger(chargerId: Int, chargerInputStatus: MaterialButton) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestParams: MutableMap<String, String> = HashMap()
                requestParams.put("chargerId", chargerId.toString())
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

    private fun displayChargerStatus(chargerId: Int, chargerInputStatus: MaterialButton) {
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
                                setChargerButtonStatus(chargerInputStatus, true, "Continue", 2)
                            }
                            "Faulted" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Faulted", 0)
                            }
                            "Occupied" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Occupied", 0)
                            }
                            "Rejected" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Rejected", 0)
                            }
                            "Unavailable" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger Unavailable", 0)
                            }
                            "Charging" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger is occupied", 0)
                            }
                            "Reserved" -> {
                                setChargerButtonStatus(chargerInputStatus, false, "Charger is reserved", 0)
                            }
                            else -> { setChargerButtonStatus(chargerInputStatus, false, "Charger is " + charger.status, 2) }
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

    private fun setChargerButtonStatus(chargerInputStatus: MaterialButton, active: Boolean, text: String, color: Int) {
        chargerInputStatus.isClickable = active
        chargerInputStatus.text = text
        when (color) {
            0 -> {
                chargerInputStatus.setBackgroundColor(getColor(R.color.red))
                chargerInputStatus.setTextColor(getColor(R.color.white))
            }
            1 -> {
                chargerInputStatus.setBackgroundColor(getColor(R.color.green))
                chargerInputStatus.setTextColor(getColor(R.color.white))
            }
            2 -> {
                chargerInputStatus.setBackgroundColor(getColor(R.color.light_grey))
                chargerInputStatus.setTextColor(getColor(R.color.black))
            }
            3 -> {
                chargerInputStatus.setBackgroundColor(getColor(R.color.dark_grey))
                chargerInputStatus.setTextColor(getColor(R.color.white))
            }
        }
    }

    public fun validateChargerId(chargerId: String): Boolean {
        if (chargerId.length != 6) {
            return false
        }
        if (chargerId.count { it.isDigit() } != 6) {
            return false
        }
        return true
    }

    private fun checkPendingTransaction() {
        //TODO Fix fetching transaction ID smartly
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val transactionId = sharedPreferences.getInt("TransactionId", -1)

        if (transactionId == -1) {
            return
        }

        currentRemoteTransaction.refresh(lifecycleScope, transactionId).invokeOnCompletion {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    setupChargingInProgressDialog()
                }
                catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message + " : " + e.cause, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun showCheckout(bool: Boolean, chargePointId: Int, showPayment: Boolean, chargerId: Int){
        val checkoutLayout = chargerInputDialog.findViewById<ConstraintLayout>(R.id.charger_checkout_layout)
        val chargersNearMeLayout = chargerInputDialog.findViewById<ConstraintLayout>(R.id.chargePoints_near_me_layout)
        val chargerInput = chargerInputDialog.findViewById<EditText>(R.id.chargerInputLayout_pinView_chargerInput)
        val chargerInputStatus = chargerInputDialog.findViewById<MaterialButton>(R.id.chargerInputLayout_textView_chargerStatus)
        val chargerInputView = chargerInputDialog.findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        val chargerLocationText = chargerInputDialog.findViewById<TextView>(R.id.checkoutLayout_textView_currentLocation)
        val paymentText = chargerInputDialog.findViewById<TextView>(R.id.checkoutLayout_textView_payment)
        val klarnaButton = chargerInputDialog.findViewById<ImageButton>(R.id.checkoutLayout_button_klarna)
        TransitionManager.beginDelayedTransition(chargerInputView as ViewGroup?, ChangeBounds())

        if(bool) {
            chargersNearMeLayout?.visibility = View.GONE
            checkoutLayout?.visibility =View.VISIBLE
            if (showPayment) {
                klarnaButton?.background = getDrawable(R.drawable.rounded_background)
                klarnaButton?.visibility = View.VISIBLE
                paymentText?.visibility = View.VISIBLE
                chargerInput?.isEnabled = false
                chargerInputStatus?.isEnabled = false

                klarnaButton?.setOnClickListener {
                    klarnaButton?.background = getDrawable(R.drawable.rounded_background_selected)
                    chargerInputStatus?.isEnabled = true
                    if (chargerInputStatus != null) {
                        setChargerButtonStatus(chargerInputStatus, true, "Continue", 1)
                    }
                    //reserveCharger(chargerId, chargerInputStatus!!)
                }

                chargerInputStatus?.setOnClickListener{
                    reserveCharger(chargerId, chargerInputStatus!!)
                }

            }
            else {
                klarnaButton?.visibility = View.GONE
                paymentText?.visibility = View.GONE
                chargerInput?.isEnabled = true
            }
            chargerLocationText?.text = remoteChargePoints.value.filter { it.chargePointID == chargePointId }[0].name
            if (chargerInputView != null) {
                displayChargerList(chargerInputView, chargePointId)
            }
        }
        else {
            chargerInput?.isEnabled = true
            chargersNearMeLayout?.visibility = View.VISIBLE
            checkoutLayout?.visibility = View.GONE
            chargerInput?.text?.clear()
            setChargerButtonStatus(chargerInputStatus!!, false, getString(R.string.charger_status_enter_code), 3)
        }
    }
}
