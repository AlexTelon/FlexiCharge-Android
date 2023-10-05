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

    var startTime: Long = 0
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

    fun retriveReopened(lifecycleScope: LifecycleCoroutineScope, transactionId: Int) : Job{
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.getTransaction(transactionId)
                    if (!response.isSuccessful) {
                        cancel("Could not fetch transaction!")
                    }
                    else {
                        value = response.body()!!
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
                        startTime = transactionSessionResponse.timestamp
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