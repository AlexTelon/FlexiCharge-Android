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
import com.flexicharge.bolt.activities.businessLogic.*
import com.flexicharge.bolt.adapters.ChargePointListAdapter
import com.flexicharge.bolt.adapters.ChargersListAdapter
import com.flexicharge.bolt.adapters.TimeCalculation
import com.flexicharge.bolt.api.flexicharge.ChargePoints
import com.flexicharge.bolt.api.flexicharge.Charger
import com.flexicharge.bolt.api.flexicharge.Transaction
import com.flexicharge.bolt.api.flexicharge.TransactionSession
import com.flexicharge.bolt.databinding.ActivityMainBinding
import com.flexicharge.bolt.foregroundServices.ChargingService
import com.flexicharge.bolt.helpers.LoginChecker
import com.flexicharge.bolt.helpers.MapHelper
import com.flexicharge.bolt.helpers.MapHelper.addNewMarkers
import com.flexicharge.bolt.helpers.MapHelper.currLocation
import com.flexicharge.bolt.helpers.MapHelper.currentLocation
import com.flexicharge.bolt.helpers.MapHelper.fetchLocation
import com.flexicharge.bolt.helpers.MapHelper.panToPos
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    ChargePointListAdapter.showChargePointInterface, ChargersListAdapter.ChangeInputInterface {
    private lateinit var binding: ActivityMainBinding       // the bindings in the Main Activity (camera, user, charger, position).
    private lateinit var chargerInputDialog: BottomSheetDialog
    private lateinit var paymentSummaryDialog: BottomSheetDialog
    private lateinit var hours: String
    private lateinit var minutes: String
    private lateinit var pinView: PinView
    private lateinit var chargerInputStatus: MaterialButton
    private lateinit var listOfChargersRecyclerView: RecyclerView
    private var isBottomSheetVisible = false
    private val timeCalculation = TimeCalculation()



    private companion object {
        const val REMOTE_CHARGERS_REFRESH_INTERVAL_MS: Long = 10000
    }

    private val remoteChargers = RemoteChargers()
    private val remoteChargePoints = RemoteChargePoints()
    private val currentRemoteTransaction = RemoteTransaction()
    private val remoteChargersRefresher =
        RemoteObjectRefresher(remoteChargers, REMOTE_CHARGERS_REFRESH_INTERVAL_MS)

    override fun onCreate(savedInstanceState: Bundle?) {
        //checkPendingTransaction()

        Log.d("LOGGIN", "main created")
        super.onCreate(savedInstanceState)
        Log.d("LOGGIN", "super main created")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("RESTART", "IT WAS ON CREATE")
        remoteChargers.setOnRefreshedCallBack {
            lifecycleScope.launch(Dispatchers.Main) {
                addNewMarkers(
                    this@MainActivity,
                    remoteChargers.value,
                    fun(charger: Charger?): Boolean {
                        if (charger != null && validateChargerId(charger.chargerID.toString())) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                setupChargerInputDialog(charger.chargerID)
                            }
                        }
                        return false
                    })
            }
        }
        remoteChargersRefresher.run(lifecycleScope)

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
            if (LoginChecker.LOGGED_IN) {
                val intent = Intent(this, ProfileMenuLoggedInActivity::class.java)
                intent.putExtra("accessToken", accessToken)
                intent.putExtra("userId", userId)
                startActivity(intent)
            } else {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {   //results of trying to connect to charger.
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12345) {
            if (resultCode == Activity.RESULT_OK) {                                      // its ok
                try {
                    setupChargerInputDialog()
                    changeInput(data?.getStringExtra("QR_SCAN_RESULT").toString())
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()  //exception handling
                }
            }
        }
    }

    override fun onResume() { //Called after onRestoreInstanceState, onRestart, or onPause
        super.onResume()
        Log.d("RESTART", "IT WAS ON RESUME")
        remoteChargersRefresher.run(lifecycleScope)
        val refreshChargers = remoteChargers.refresh(lifecycleScope)
        refreshChargers.invokeOnCompletion {
            if (refreshChargers.isCancelled) {
                return@invokeOnCompletion
            }
            val refreshChargePoints = remoteChargePoints.refresh(lifecycleScope)

            refreshChargePoints.invokeOnCompletion {
                if (refreshChargePoints.isCancelled) {
                    return@invokeOnCompletion
                }

            }
        }
        if (!isBottomSheetVisible) {
            checkPendingTransaction()
        }


        fetchLocation(this)
    }

    override fun changeInput(newInput: String) {
        pinView.setText(newInput)
    }

    override fun showChargePoint(
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
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun stopChargingProcess(
        initialPercentage: Int,
        bottomSheetDialog: BottomSheetDialog,
    ) {
        currentRemoteTransaction.refresh(lifecycleScope)
        val dateTime =
            timeCalculation.unixToDateTime(currentRemoteTransaction.value.timestamp.toString())

        try {
            val stopRemoteTransactionJob = currentRemoteTransaction.stop(lifecycleScope)
            stopRemoteTransactionJob.invokeOnCompletion {
                if (stopRemoteTransactionJob.isCancelled) {
                    return@invokeOnCompletion
                }
                lifecycleScope.launch(Dispatchers.Main) {

                    Intent(applicationContext, ChargingService::class.java).also {
                        it.action = ChargingService.Actions.STOP.toString()
                        startService(it)
                    }
                    val sharedPreferences =
                        getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().apply { putInt("TransactionId", -1) }.apply()
                    bottomSheetDialog.dismiss()
                    displayPaymentSummaryDialog(dateTime, initialPercentage)
                }
            }
        } catch (e: Exception) {
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().apply { putInt("TransactionId", -1) }.apply()
                bottomSheetDialog.dismiss()
                displayPaymentSummaryDialog(dateTime, initialPercentage)
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isPolling = false

    private fun startApiPolling() {

    }


    private fun setupChargingInProgressDialog() {
        if (this::chargerInputDialog.isInitialized) {
            Log.d("CheckTransaction", "it is initizalized")
            chargerInputDialog.dismiss()
        }

        val charger =
            remoteChargers.value.filter { it.chargerID == currentRemoteTransaction.value.chargerID }
                .getOrNull(0)
        val chargePoint =
            remoteChargePoints.value.filter { it.chargePointID == charger?.chargePointID }
                .getOrNull(0)


        if (currentRemoteTransaction.value.transactionID == -1) {
            val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val transactionId = sharedPreferences.getInt("TransactionId", -1)
            val retrieveJob =
                currentRemoteTransaction.retriveReopened(lifecycleScope, transactionId)

            try {
                retrieveJob.invokeOnCompletion {
                    if (retrieveJob.isCancelled) {
                        return@invokeOnCompletion
                    }
                }
            } catch (e: Exception) {
                println("Error when retreiving")
            }
        }


        val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_in_progress,
            findViewById<ConstraintLayout>(R.id.chargerInProgress)
        )
        val progressbarPercent =
            bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textView_progressbarPercent)
        val progressbar =
            bottomSheetView.findViewById<ProgressBar>(R.id.chargeInProgressLayout_progressBar)
        val chargingTimeStatus =
            bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textview_chargingTimeStatus)
        val chargeSpeed =
            bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textView_chargeSpeed)
        val location =
            bottomSheetView.findViewById<TextView>(R.id.chargeInProgressLayout_textView_location)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.isDraggable = false;
        bottomSheetDialog.setCancelable(false)

        val initialPercentage = currentRemoteTransaction.value.currentChargePercentage
        val dateTime =
            timeCalculation.unixToDateTime(currentRemoteTransaction.value.timestamp.toString())


        progressbar.progress = initialPercentage

        isPolling = true
        val foundCharger =
            remoteChargers.value.find { it.chargerID == currentRemoteTransaction.value.chargerID }
        val currentChargePoint =
            remoteChargePoints.value.find { it.chargePointID == foundCharger?.chargePointID }

        location.text = currentChargePoint?.name


        scope.launch {
            while (isPolling) {
                currentRemoteTransaction.retrieve(lifecycleScope)

                withContext(Dispatchers.Main) {
                    progressbar.progress = currentRemoteTransaction.value.currentChargePercentage
                    progressbarPercent.text =
                        currentRemoteTransaction.value.currentChargePercentage.toString()
                    chargeSpeed.text = currentRemoteTransaction.value.kwhTransfered.toString()

                }
                delay(3000)
            }
        }

        bottomSheetView.findViewById<MaterialButton>(R.id.chargeInProgressLayout_button_stopCharging)
            .setOnClickListener {
                isPolling = false
                stopChargingProcess(initialPercentage, bottomSheetDialog)
            }

        bottomSheetDialog.setContentView(bottomSheetView)
        isBottomSheetVisible = true
        bottomSheetDialog.show()
    }

    private fun displayPaymentSummaryDialog(dateTime: String, initialPercentage: Int) {

        // TODO Look over values before final commit

        paymentSummaryDialog = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_payment_summary,
            findViewById<ConstraintLayout>(R.id.paymentSummaryLayout)
        )
        val energyUsedTextView =
            bottomSheetView.findViewById<TextView>(R.id.paymentSummaryLayout_textView_energyUsedValue)
        val durationTextView =
            bottomSheetView.findViewById<TextView>(R.id.paymentSummaryLayout_textView_durationValue)
        val chargingStopTimeTextView =
            bottomSheetView.findViewById<TextView>(R.id.paymentSummaryLayout_textView_finishedTime)
        val totalCostTextView =
            bottomSheetView.findViewById<TextView>(R.id.paymentPriceLayout_textView_price)
        val crossButton =
            bottomSheetView.findViewById<ImageButton>(R.id.paymentSummaryLayout_button_close)

        crossButton.setOnClickListener {
            paymentSummaryDialog.dismiss()
            isBottomSheetVisible = false
        }

        val refreshJob = currentRemoteTransaction.refresh(lifecycleScope);
        refreshJob.invokeOnCompletion {
            val currentTime = System.currentTimeMillis()
            val transaction = currentRemoteTransaction.value
            val kwhTransferred = transaction.kwhTransfered
            val totalCost = (transaction.kwhTransfered.toString()
                .toDouble() * transaction.pricePerKwh.toDouble() / 100).toFloat()
            val pricePerKwh = transaction.pricePerKwh
            val duration = timeCalculation.checkDuration(currentRemoteTransaction.startTime, currentTime)

            lifecycleScope.launch(Dispatchers.Main) {

                energyUsedTextView.text =
                    kwhTransferred.toString() + " kWh @" + pricePerKwh + "kr kWh"
                durationTextView.text = duration
                chargingStopTimeTextView.text = "Charging stopped at " + dateTime
                totalCostTextView.text = totalCost.toString() + "kr"

                paymentSummaryDialog.setContentView(bottomSheetView)
                paymentSummaryDialog.show()
            }
        }
    }

    private fun setupChargerInputDialog(chargerId: Int? = null) {

        chargerInputDialog = BottomSheetDialog(
            this@MainActivity, R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_input,
            findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        )

        val arrow =
            bottomSheetView.findViewById<ImageView>(R.id.chargePointsNearMeLayout_imageView_arrow)
        arrow.setOnClickListener {
            displayChargePointList(bottomSheetView, arrow)
        }

        val backButton = bottomSheetView.findViewById<ImageButton>(R.id.checkoutLayout_button_back)
        backButton.setOnClickListener {
            showCheckout(false, -1, false, -1)
        }

        setupChargerInput(bottomSheetView, chargerId)

        chargerInputDialog.setContentView(bottomSheetView)
        chargerInputDialog.show()
    }

    private fun setupChargerInput(bottomSheetView: View, chargerId: Int? = null) {
        Log.d("displayChargerList", "displayChargerList")

        pinView =
            bottomSheetView.findViewById<PinView>(R.id.chargerInputLayout_pinView_chargerInput)
        chargerInputStatus =
            bottomSheetView.findViewById<MaterialButton>(R.id.chargerInputLayout_textView_chargerStatus)

        if (chargerId != null) {
            if (validateChargerId(chargerId.toString())) {
                pinView.setText(chargerId.toString())
                displayChargerStatus(chargerId, chargerInputStatus)
            }
        }

        pinView.doOnTextChanged { text, start, before, count ->
            if (text?.length == 6) {
                val chargerId = text.toString().toUInt().toInt()
                if (validateChargerId(text.toString())) {
                    displayChargerStatus(chargerId, chargerInputStatus)
                    hideKeyboard(bottomSheetView)
                } else {
                    setChargerButtonStatus(
                        chargerInputStatus,
                        false,
                        "ChargerId has to consist of 6 digits",
                        0
                    )
                    hideKeyboard(bottomSheetView)
                }
            }
        }
    }

    private fun displayChargerList(bottomSheetView: View, chargePointId: Int) {
        val chargerId = pinView.text.toString()
        val refreshJob = remoteChargers.refresh(lifecycleScope);
        refreshJob.invokeOnCompletion {
            if (refreshJob.isCancelled) {
                return@invokeOnCompletion
            }
            val refreshRemoteChargePointsJob = remoteChargePoints.refresh(lifecycleScope)
            refreshRemoteChargePointsJob.invokeOnCompletion {
                if (refreshRemoteChargePointsJob.isCancelled) {
                    return@invokeOnCompletion
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    listOfChargersRecyclerView =
                        bottomSheetView.findViewById(R.id.checkoutLayout_recyclerView_chargerList)
                    listOfChargersRecyclerView.layoutManager = LinearLayoutManager(
                        this@MainActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                    val chargersInCp =
                        remoteChargers.value.filter { it.chargePointID == chargePointId }
                    val chargePoint =
                        remoteChargePoints.value.filter { it.chargePointID == chargePointId }[0]
                    listOfChargersRecyclerView.adapter =
                        ChargersListAdapter(chargersInCp, chargerId, chargePoint, this@MainActivity)

                    // Only add decoration on first-time display
                    if (listOfChargersRecyclerView.itemDecorationCount == 0) {
                        listOfChargersRecyclerView.addItemDecoration(SpacesItemDecoration(15))
                    }
                }
            }

        }
    }

    private fun displayChargePointList(bottomSheetView: View, arrow: ImageView) {
        val listOfChargePointsRecyclerView =
            bottomSheetView.findViewById<RecyclerView>(R.id.chargePointsNearMeLayout_recyclerView_chargePointList)
        val chargePointsNearMe =
            bottomSheetView.findViewById<TextView>(R.id.chargePointsNearMeLayout_textView_nearMe)
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
        val refreshChargePoints = remoteChargePoints.refresh(lifecycleScope)
        refreshChargePoints.invokeOnCompletion {
            if (refreshChargePoints.isCancelled) {
                return@invokeOnCompletion
            }

            val distanceToChargePoint = mutableListOf<Float>()
            val chargerCount = mutableListOf<Int>()

            remoteChargePoints.value.forEachIndexed { index, chargePoint ->
                val dist = FloatArray(1)
                try {
                    Location.distanceBetween(
                        chargePoint.location[0],
                        chargePoint.location[1],
                        currentLocation.latitude,
                        currentLocation.longitude,
                        dist
                    )
                } catch (e: UninitializedPropertyAccessException) {
                    dist[0] = 0f
                }

                val df = DecimalFormat("#.##")
                val distanceFloat = (dist[0] / 1000)
                val roundedDistance = (distanceFloat * 100).roundToInt() / 100f

                val count =
                    remoteChargers.value.count { it.chargePointID.equals(chargePoint.chargePointID) }
                distanceToChargePoint.add(roundedDistance)
                chargerCount.add(count)
            }

            val chargePointList = ArrayList(remoteChargePoints.value)

            val indices = distanceToChargePoint.indices.sortedBy { distanceToChargePoint[it] }

            val sortedDistanceToChargePoint = indices.map { distanceToChargePoint[it] }
            val sortedChargerCount = indices.map { chargerCount[it] }.toMutableList()
            val sortedChargePoints = indices.map { chargePointList[it] }


            val sortedChargePointsList = ChargePoints()
            sortedChargePointsList.addAll(sortedChargePoints)

            val distanceAsString = mutableListOf<String>()
            distanceAsString.addAll(sortedDistanceToChargePoint.map { it.toString() })



            lifecycleScope.launch(Dispatchers.Main) {
                listOfChargePointsRecyclerView.adapter = ChargePointListAdapter(
                    sortedChargePointsList,
                    this@MainActivity,
                    distanceAsString,
                    sortedChargerCount
                )

            }


        }
    }


    private fun createKlarnaTransactionSession(userId: String, chargerId: Int) {
        val retrieveJob = currentRemoteTransaction.retrieve(lifecycleScope)

        try {
            retrieveJob.invokeOnCompletion {
                if (retrieveJob.isCancelled) {
                    return@invokeOnCompletion
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    val intent = Intent(this@MainActivity, KlarnaActivity::class.java)
                    intent.putExtra("ChargerId", chargerId)
                    intent.putExtra(
                        "klarna_consumer_token",
                        currentRemoteTransaction.value.klarna_consumer_token
                    )
                    intent.putExtra("TransactionId", currentRemoteTransaction.value.transactionID)
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            println("Error when retreiving")
        }


    }

    private fun reserveCharger(chargerId: Int, chargerInputStatus: MaterialButton) {

        val sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val tempChargerId: Int = 100000
        val transactionSession = TransactionSession(userId!!, chargerId, true, 75)

        // val transactionSession = TransactionSession(userId!!, tempChargerId, true, 75)
        val createSessionJob =
            currentRemoteTransaction.createSession(lifecycleScope, transactionSession)

        try {

            createSessionJob.invokeOnCompletion {
                if (createSessionJob.isCancelled) {
                    return@invokeOnCompletion
                }

                when (currentRemoteTransaction.status) {
                    "Accepted" -> {
                        createKlarnaTransactionSession(userId, chargerId)

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
                        setChargerButtonStatus(
                            chargerInputStatus,
                            false,
                            "Charger Unknown status",
                            2
                        )
                    }
                }

            }
        } catch (e: CancellationException) {
            lifecycleScope.launch(Dispatchers.Main) {
                setChargerButtonStatus(chargerInputStatus, false, "Could not reserve charger", 0)
            }
            Toast.makeText(
                applicationContext,
                "Could not reserve charger: " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun displayChargerStatus(chargerId: Int, chargerInputStatus: MaterialButton) {
        val remoteCharger = RemoteCharger(chargerId)
        try {
            val refreshJob = remoteCharger.refresh(lifecycleScope);
            refreshJob.invokeOnCompletion {
                if (refreshJob.isCancelled) {
                    return@invokeOnCompletion
                }

                val charger = remoteCharger.value
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
                            setChargerButtonStatus(
                                chargerInputStatus,
                                false,
                                "Charger Unavailable",
                                0
                            )
                        }

                        "Charging" -> {
                            setChargerButtonStatus(
                                chargerInputStatus,
                                false,
                                "Charger is occupied",
                                0
                            )
                        }

                        "Reserved" -> {
                            setChargerButtonStatus(
                                chargerInputStatus,
                                false,
                                "Charger is reserved",
                                0
                            )
                        }

                        else -> {
                            setChargerButtonStatus(
                                chargerInputStatus,
                                false,
                                "Charger is " + charger.status,
                                2
                            )
                        }
                    }
                }
            }
        } catch (e: CancellationException) {
            lifecycleScope.launch(Dispatchers.Main) {
                setChargerButtonStatus(chargerInputStatus, false, e.message!!, 2)
            }
        }

    }

    private fun setChargerButtonStatus(
        chargerInputStatus: MaterialButton,
        active: Boolean,
        text: String,
        color: Int
    ) {
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
            Log.d("CheckTransaction", "No active transaction")
            return
        }

        val refreshJob = currentRemoteTransaction.refresh(lifecycleScope, transactionId)
        refreshJob.invokeOnCompletion {
            if (refreshJob.isCancelled) {
                return@invokeOnCompletion
            }

            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    setupChargingInProgressDialog()
                } catch (e: Exception) {
                    Log.d("CheckTransaction", "Found an errors")
                    Toast.makeText(
                        applicationContext,
                        e.message + " : " + e.cause,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    private fun showCheckout(
        bool: Boolean,
        chargePointId: Int,
        showPayment: Boolean,
        chargerId: Int
    ) {
        val checkoutLayout =
            chargerInputDialog.findViewById<ConstraintLayout>(R.id.charger_checkout_layout)
        val chargersNearMeLayout =
            chargerInputDialog.findViewById<ConstraintLayout>(R.id.chargePoints_near_me_layout)
        val chargerInput =
            chargerInputDialog.findViewById<EditText>(R.id.chargerInputLayout_pinView_chargerInput)
        val chargerInputStatus =
            chargerInputDialog.findViewById<MaterialButton>(R.id.chargerInputLayout_textView_chargerStatus)
        val chargerInputView =
            chargerInputDialog.findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        val chargerLocationText =
            chargerInputDialog.findViewById<TextView>(R.id.checkoutLayout_textView_currentLocation)
        val paymentText =
            chargerInputDialog.findViewById<TextView>(R.id.checkoutLayout_textView_payment)
        val klarnaButton =
            chargerInputDialog.findViewById<ImageButton>(R.id.checkoutLayout_button_klarna)
        TransitionManager.beginDelayedTransition(chargerInputView as ViewGroup?, ChangeBounds())

        if (bool) {
            chargersNearMeLayout?.visibility = View.GONE
            checkoutLayout?.visibility = View.VISIBLE
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

                chargerInputStatus?.setOnClickListener {
                    reserveCharger(chargerId, chargerInputStatus!!)
                }

            } else {
                klarnaButton?.visibility = View.GONE
                paymentText?.visibility = View.GONE
                chargerInput?.isEnabled = true
            }
            try {
                val chargerWithChargePointId =
                    remoteChargePoints.value.first { it.chargePointID == chargePointId }
                chargerLocationText?.text = chargerWithChargePointId.name
            } catch (e: NoSuchElementException) {
                Log.d(
                    "remoteChargePoints",
                    "couldn't find a charger with chargePointID " + chargePointId
                )
            }

            if (chargerInputView != null) {
                displayChargerList(chargerInputView, chargePointId)
            }
        } else {
            chargerInput?.isEnabled = true
            chargersNearMeLayout?.visibility = View.VISIBLE
            checkoutLayout?.visibility = View.GONE
            chargerInput?.text?.clear()
            setChargerButtonStatus(
                chargerInputStatus!!,
                false,
                getString(R.string.charger_status_enter_code),
                3
            )
        }
    }

    override fun finish() {
        super.finish()
        remoteChargersRefresher.stop()
    }
}