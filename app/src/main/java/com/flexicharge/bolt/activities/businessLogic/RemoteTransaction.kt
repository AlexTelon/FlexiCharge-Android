package com.flexicharge.bolt.activities.businessLogic

import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.Transaction
import com.flexicharge.bolt.api.flexicharge.TransactionSession
import com.flexicharge.bolt.api.flexicharge.TransactionSessionResponse
import kotlinx.coroutines.*

class RemoteTransaction(private var transactionId : Int = -1) : RemoteObject<Transaction>() {

    var status: String = ""
        private set

    override var value =
        Transaction(-1, "invalid", 0,
            false, 0.0, 0, false,
            "", "", "", 0, transactionId, "")

     public override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.getTransaction(transactionId)
                    println("-----------------")
                    println(response.body())
                    println("-----------------")
                    if (!response.isSuccessful) {
                        cancel("Could not fetch transaction!")
                    }
                    else {
                       // value = response.body() as Transaction
                        value.kwhTransfered = response.body()!!.kwhTransfered
                        value.currentChargePercentage = response.body()!!.currentChargePercentage

                    }
                }
                catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun createSession(lifecycleScope: LifecycleCoroutineScope, transactionSession: TransactionSession): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {

                    val response = RetrofitInstance.flexiChargeApi.initTransaction(transactionSession)
                    if(!response.isSuccessful) {

                        cancel(response.message())
                    }
                    else {
                        val transactionSessionResponse = response.body() as Transaction
                        value = transactionSessionResponse
                        transactionId = transactionSessionResponse.transactionID
                       // value.klarna_consumer_token = transactionSessionResponse.klarna_consumer_token
                        status = "Accepted"
                        try {
                            val refreshJob = refresh(lifecycleScope)
                            refreshJob.join()
                        }
                        catch (e: CancellationException) {
                            cancel(e)
                        }
                    }
                }
                catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun stop(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.transactionStop(value.transactionID)
                    if (!response.isSuccessful) {
                        println("----------------------------------------")
                        println(response)
                        println("RESPONSE NOT SUCCESS")
                        println("----------------------------------------")
                        cancel(response.message())
                    }
                }
                catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun start(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    println("--------------------------------------")
                    println(value.transactionID)
                    println("--------------------------------------")
                    val response = RetrofitInstance.flexiChargeApi.transactionStart(value.transactionID)
                    if(!response.isSuccessful) {
                        cancel(response.message())
                    }
                }
                catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun refresh(lifecycleScope: LifecycleCoroutineScope, transactionId: Int) : Job {
        this.transactionId = transactionId
        return refresh(lifecycleScope)
    }

}