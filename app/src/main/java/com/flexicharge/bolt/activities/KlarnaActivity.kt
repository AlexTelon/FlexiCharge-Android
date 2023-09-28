package com.flexicharge.bolt.activities

import android.content.Context
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
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentCategory
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError
import kotlinx.coroutines.*

class KlarnaActivity : AppCompatActivity(), KlarnaPaymentViewCallback {
    private val klarnaPaymentView by lazy { findViewById<KlarnaPaymentView>(R.id.klarnaActivity_KlarnaPaymentVie) }
    private val authorizeButton by lazy { findViewById<Button>(R.id.klarnaActivity_button_authorize) }
    private var chargerId : Int = 0
    private var clientToken : String = ""
    private var transactionId : Int = 0
    private var authTokenId : String = ""

    private val paymentCategory = KlarnaPaymentCategory.PAY_NOW // please update this value if needed

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klarna)

        chargerId = intent.getIntExtra("ChargerId", 0)
        clientToken = intent.getStringExtra("ClientToken").toString()
        transactionId = intent.getIntExtra("TransactionId", 0)
        Log.d("CLIENTTOKEN", clientToken)

        println("--------------------------------------")
        println(chargerId)
        println(transactionId)

        println("--------------------------------------")

        if (clientToken == "null")
            clientToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyMzA1ZWJjLWI4MTEtMzYzNy1hYTRjLTY2ZWNhMTg3NGYzZCJ9.eyJzZXNzaW9uX2lkIjoiNjRiY2VhZGEtODZmZC01ODkxLTg3ZTYtNDAxYWY2YWJhODNjIiwiYmFzZV91cmwiOiJodHRwczovL2pzLnBsYXlncm91bmQua2xhcm5hLmNvbS9ldS9rcCIsImRlc2lnbiI6ImtsYXJuYSIsImxhbmd1YWdlIjoic3YiLCJwdXJjaGFzZV9jb3VudHJ5IjoiU0UiLCJlbnZpcm9ubWVudCI6InBsYXlncm91bmQiLCJtZXJjaGFudF9uYW1lIjoiWW91ciBidXNpbmVzcyBuYW1lIiwic2Vzc2lvbl90eXBlIjoiUEFZTUVOVFMiLCJjbGllbnRfZXZlbnRfYmFzZV91cmwiOiJodHRwczovL2V1LnBsYXlncm91bmQua2xhcm5hZXZ0LmNvbSIsInNjaGVtZSI6dHJ1ZSwiZXhwZXJpbWVudHMiOlt7Im5hbWUiOiJrcGMtMWstc2VydmljZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3BjLVBTRUwtMzA5OSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXBvcHVwLXJldHJpYWJsZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXN0YXRpYy13aWRnZXQiLCJ2YXJpYXRlIjoiaW5kZXgiLCJwYXJhbWV0ZXJzIjp7ImR5bmFtaWMiOiJ0cnVlIn19LHsibmFtZSI6ImtwLWNsaWVudC1vbmUtcHVyY2hhc2UtZmxvdyIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJpbi1hcHAtc2RrLW5ldy1pbnRlcm5hbC1icm93c2VyIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoibmV3LWludGVybmFsLWJyb3dzZXItZW5hYmxlIn19LHsibmFtZSI6ImtwLWNsaWVudC11dG9waWEtc2RrLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3AtY2xpZW50LXV0b3BpYS13ZWJ2aWV3LWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoiaW4tYXBwLXNkay1jYXJkLXNjYW5uaW5nIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoiY2FyZC1zY2FubmluZy1lbmFibGUifX1dLCJyZWdpb24iOiJldSIsIm9yZGVyX2Ftb3VudCI6NTAwMDAsIm9mZmVyaW5nX29wdHMiOjIsIm9vIjoiYmEiLCJ2ZXJzaW9uIjoidjEuMTAuMC0xNTkwLWczZWJjMzkwNyJ9.hXs1xp8yXOZNQnA9HTMYKuhGZXqsf4Vv9I5VRu-t6vQeJPxyVDBw-yqQ8cPq_lsDEMEZK5yuqRsm2CdttsM5iwF5Yea9IO5MevFUm-ryrr27zk1dJEaJfHAKQZ04VCsGp2ZeIqASsEr1mAUAOnaWuD-XZgy9D01DveMP1gS2lnYNlGfT7IpUs96RvG_PJyFfUn8EzSGQiIiIpeyjpZsC9fGxiY80ekoZgEML_Vsn1_jLWk-bHxi5KPlTblR_-5ys-_AUOeD9nPMT7bjrSUMZrXx3Md_EMOEMJwKZA7C25erPLr-P7k8iz9YNvtFE58bSwojDnUKBTMSsPD2CUGIk6Q"

        println(clientToken)
        initialize()

        setupButtons()
        klarnaPaymentView.category = paymentCategory
    }

    private fun initialize() {
        if (OrderClient.hasSetCredentials()) {
            job = GlobalScope.launch {
            try {
                runOnUiThread {
                    klarnaPaymentView.initialize(
                        clientToken,
                        "${getString(R.string.return_url_scheme)}://${getString(R.string.return_url_host)}"
                    )
                }
            }
            catch (exception: Exception) {
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
        GlobalScope.launch(Dispatchers.Main) {
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
            try{
                val startTransactionJob = remoteTransaction.start(lifecycleScope)
                val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                startTransactionJob.invokeOnCompletion {
                    if(!startTransactionJob.isCancelled) {
                        sharedPreferences.edit().apply { putInt("TransactionId", transactionId) }.apply()
                    }
                    else {

                        sharedPreferences.edit().apply { putInt("TransactionId", -1) }.apply()
                    }

                    lifecycleScope.launch(Dispatchers.Main) {

                        finish()
                    }
                }

            }
            catch (e: CancellationException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Transaction could not be started: " + e.message, Toast.LENGTH_LONG).show()
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
            klarnaPaymentView.visibility = View.INVISIBLE
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
