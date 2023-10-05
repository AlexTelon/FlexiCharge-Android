package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.InitTransaction
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.Transaction
import com.flexicharge.bolt.api.flexicharge.TransactionSession
import kotlinx.coroutines.*

class RemoteTransaction( var transactionId : Int = -1) : RemoteObject<Transaction>() {

    var status: String = ""
        private set
    var klarnaConsumerToken: String = ""
        private set

    override var value =
        Transaction(null,null,
            null,null,null,null,
            null,null,null)

     public override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.getTransaction(transactionId)
                    if (!response.isSuccessful) {
                        cancel("Could not fetch transaction!")
                    }
                    else {
                        value = response.body() as Transaction
                      //  value.kwhTransferred = response.body()!!.kwhTransferred
                       // value.currentChargePercentage = response.body()!!.currentChargePercentage

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
                        Log.d("CurrentTest", " response init fail")
                        cancel(response.message())
                    }
                    else {
                        val transactionSessionResponse = response.body() as InitTransaction
                        Log.d("CurrentTest", "${response.body()}")
                        transactionId = transactionSessionResponse.transactionId.toInt()
                        klarnaConsumerToken = transactionSessionResponse.klarnaConsumerToken
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
                    val response = RetrofitInstance.flexiChargeApi.transactionStop(transactionId)
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
                    val response = RetrofitInstance.flexiChargeApi.transactionStart(transactionId)
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