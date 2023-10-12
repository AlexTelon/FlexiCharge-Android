package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.InitTransaction
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.Transaction
import com.flexicharge.bolt.api.flexicharge.TransactionSession
import kotlinx.coroutines.*

class RemoteTransaction(var transactionId: Int = -1) : RemoteObject<Transaction>() {

    var status: String = ""
        private set
    var klarnaConsumerToken: String = ""
        private set

    var localAccessToken: String = ""

    override var value =
        Transaction(
            null, null,
            null, null, null, null,
            null, null, null
        )

    public override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.getTransaction(
                        "Bearer $localAccessToken",
                        transactionId
                    )
                    Log.d("StartVerify", response.toString())
                    Log.d("StartVerify", "values : ${response.body()}")
                    if (!response.isSuccessful) {
                        Log.d("StartVerify", response.toString())
                        cancel("Could not fetch transaction!")
                    } else {
                        value = response.body() as Transaction

                        //  value.kwhTransferred = response.body()!!.kwhTransferred
                        // value.currentChargePercentage = response.body()!!.currentChargePercentage
                    }
                } catch (e: Exception) {
                    Log.d("StartVerify", e.message!!)

                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun createSession(
        lifecycleScope: LifecycleCoroutineScope,
        transactionSession: TransactionSession,
        accessToken: String
    ): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    localAccessToken = accessToken
                    val response = RetrofitInstance.flexiChargeApi.initTransaction(
                        "Bearer $accessToken",
                        transactionSession
                    )
                    if (!response.isSuccessful) {
                        Log.d("CurrentTest", " response init fail")
                        cancel(response.message())
                    } else {
                        val transactionSessionResponse = response.body() as InitTransaction
                        Log.d("CurrentTest", "init: ${response.body()}")
                        transactionId = transactionSessionResponse.transactionID.toInt()
                        klarnaConsumerToken = transactionSessionResponse.klarnaClientToken

                        status = "Accepted"
                        try {
                            val refreshJob = refresh(lifecycleScope)
                            refreshJob.join()
                        } catch (e: CancellationException) {
                            cancel(e)
                        }
                    }
                } catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun stop(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.transactionStop(
                        "Bearer $localAccessToken",
                        transactionId
                    )
                    Log.d("EndVerify", response.toString())
                    Log.d("EndVerify", response.body().toString())

                    if (!response.isSuccessful) {
                        cancel(response.message())
                    } else {
                        value.endTimestamp = response.body()?.endTimestamp
                        value.startTimestamp = response.body()?.startTimestamp
                        value.price = response.body()?.price
                        value.kwhTransferred = response.body()?.kwhTransferred
                        value.discount = response.body()?.discount
                    }
                } catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun start(lifecycleScope: LifecycleCoroutineScope, accessToken: String): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.transactionStart(
                        "Bearer $accessToken",
                        transactionId
                    )
                    if (!response.isSuccessful) {
                        cancel(response.message())
                    }
                } catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }
}