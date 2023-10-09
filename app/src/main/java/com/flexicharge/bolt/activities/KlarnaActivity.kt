package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.RemoteTransaction
import com.flexicharge.bolt.api.klarna.OrderClient
import com.flexicharge.bolt.foregroundServices.ChargingService
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentCategory
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError
import kotlinx.coroutines.*

class KlarnaActivity : AppCompatActivity(), KlarnaPaymentViewCallback {
    private val klarnaPaymentView by lazy {
        findViewById<KlarnaPaymentView>(
            R.id.klarnaActivity_KlarnaPaymentVie
        )
    }
    private val authorizeButton by lazy { findViewById<Button>(R.id.klarnaActivity_button_authorize) }
    private var chargerId: Int = 0
    private var klarnaConsumerToken: String = ""
    private var transactionId: Int = 0
    private var authTokenId: String = ""

    private val paymentCategory = KlarnaPaymentCategory.PAY_NOW // please update this value if needed

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klarna)

        chargerId = intent.getIntExtra("ChargerId", 0)
        klarnaConsumerToken = intent.getStringExtra("klarna_consumer_token").toString()
        transactionId = intent.getIntExtra("TransactionId", 0)
        Log.d("CLIENT_TOKEN", klarnaConsumerToken)

        initialize()

        setupButtons()
        klarnaPaymentView.category = paymentCategory
    }

    private fun initialize() {
        if (OrderClient.hasSetCredentials()) {
            job = lifecycleScope.launch {
                try {
                    runOnUiThread {
                        klarnaPaymentView.initialize(
                            klarnaConsumerToken,
                            "${getString(R.string.return_url_scheme)}://${getString(
                                R.string.return_url_host
                            )}"
                        )
                    }
                } catch (exception: Exception) {
                    showError(exception.message)
                }
            }
        } else {
            showError(getString(R.string.error_credentials))
        }
    }

    private fun setupButtons() {
        authorizeButton.setOnClickListener {
            klarnaPaymentView.authorize(true, null)
        }
    }

    private fun showError(message: String?) {
        runOnUiThread {
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setMessage(message ?: getString(R.string.error_general))
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            action.invoke()
        }
    }

    override fun onInitialized(view: KlarnaPaymentView) {
        // load the payment view after its been initialized

        view.load(null)
    }

    override fun onLoaded(view: KlarnaPaymentView) {
        // enable the authorization after the payment view is loaded
        authorizeButton.isEnabled = true
    }

    override fun onLoadPaymentReview(view: KlarnaPaymentView, showForm: Boolean) {}

    override fun onAuthorized(
        view: KlarnaPaymentView,
        approved: Boolean,
        authToken: String?,
        finalizedRequired: Boolean?
    ) {
        if (authToken != null) {
            val remoteTransaction = RemoteTransaction(transactionId)
            try {
                val startTime = System.currentTimeMillis()
                val startTransactionJob = remoteTransaction.start(lifecycleScope)
                val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                startTransactionJob.invokeOnCompletion {
                    if (!startTransactionJob.isCancelled) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            sharedPreferences.edit().apply { putInt("TransactionId", transactionId) }.apply()

                            Intent(applicationContext, ChargingService::class.java).also {
                                it.action = ChargingService.Actions.START.toString()
                                it.putExtra("startTime", startTime)
                                startService(it)
                            }

                            finish()
                        }
                    } else {
                        sharedPreferences.edit().apply { putInt("TransactionId", -1) }.apply()

                        lifecycleScope.launch(Dispatchers.Main) {
                            finish()
                        }
                    }
                }
            } catch (e: CancellationException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Transaction could not be started: " + e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        if (authToken != null) {
            authTokenId = authToken
        }
    }

    override fun onReauthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {}

    override fun onErrorOccurred(view: KlarnaPaymentView, error: KlarnaPaymentsSDKError) {
        println("An error occurred: ${error.name} - ${error.message}")
        if (error.isFatal) {
            klarnaPaymentView.visibility = View.VISIBLE
        }
    }

    override fun onFinalized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {}

    override fun onResume() {
        super.onResume()
        klarnaPaymentView.registerPaymentViewCallback(this)
    }

    override fun onPause() {
        super.onPause()
        klarnaPaymentView.unregisterPaymentViewCallback(this)
        job?.cancel()
    }
}
