package com.flexicharge.bolt.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.flexicharge.bolt.R
import com.flexicharge.bolt.api.klarna.OrderClient
import com.flexicharge.bolt.api.klarna.OrderPayload
import com.klarna.mobile.sdk.api.KlarnaMobileSDKCommon
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentCategory
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileMenuLoggedOutActivity : AppCompatActivity(), KlarnaPaymentViewCallback {

    private val klarnaPaymentView by lazy { findViewById<KlarnaPaymentView>(R.id.klarnaActivity_KlarnaPaymentVie) }
    //private val authorizeButton by lazy { findViewById<Button>(R.id.authorizeButton) }
    //private val finalizeButton by lazy { findViewById<Button>(R.id.finalizeButton) }
    //private val orderButton by lazy { findViewById<Button>(R.id.orderButton) }
    private var job: Job? = null
    private val paymentCategory = KlarnaPaymentCategory.PAY_NOW
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_menu_logged_out)
        val deviceIdentifier: String = KlarnaMobileSDKCommon.deviceIdentifier()
        initialize()
        klarnaPaymentView.category = paymentCategory }

    override fun onPause() {
        super.onPause()
        klarnaPaymentView.unregisterPaymentViewCallback(this)
        job?.cancel()
    }

    private fun initialize() {
        if (OrderClient.hasSetCredentials()) {
            job = GlobalScope.launch {

                // create a session and then initialize the payment view with the client token received in the response
                val sessionCall = OrderClient.instance.createCreditSession(OrderPayload.defaultPayload)
                try {
                    val resp = sessionCall.execute()
                    resp.body()?.let { session ->
                        runOnUiThread {
                            klarnaPaymentView.initialize(
                                session.client_token,
                                "${getString(R.string.return_url_scheme)}://${getString(R.string.return_url_host)}"
                            )
                        }
                    } ?: showError(getString(R.string.error_server, resp.code()))
                } catch (exception: Exception) {
                    showError(exception.message)
                }
            }
        } else {
            showError(getString(R.string.error_credentials))
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


    override fun onInitialized(view: KlarnaPaymentView) {

        // load the payment view after its been initialized
        view.load(null)
    }

    override fun onLoaded(view: KlarnaPaymentView) {

        // enable the authorization after the payment view is loaded
        //authorizeButton.isEnabled = true
    }

    override fun onLoadPaymentReview(view: KlarnaPaymentView, showForm: Boolean) {}

    override fun onAuthorized(
        view: KlarnaPaymentView,
        approved: Boolean,
        authToken: String?,
        finalizedRequired: Boolean?
    ) {
        //finalizeButton.isEnabled = finalizedRequired ?: false
        //orderButton.isEnabled = approved && !(finalizedRequired ?: false)
        //orderButton.tag = authToken
    }

    override fun onReauthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {}

    override fun onErrorOccurred(view: KlarnaPaymentView, error: KlarnaPaymentsSDKError) {
        println("An error occurred: ${error.name} - ${error.message}")
        if (error.isFatal) {
            klarnaPaymentView.visibility = View.INVISIBLE
        }
    }

    override fun onFinalized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {
        //orderButton.isEnabled = approved
        //orderButton.tag = authToken
    }

    fun aboutClick(view: View) {}
    fun loginClick(view: View) {
        klarnaPaymentView.authorize(true, null)
    }
}