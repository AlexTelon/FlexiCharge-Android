package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.api.flexicharge.*
import kotlinx.coroutines.*

class RemoteTransaction(private var transactionId : Int = -1) : RemoteObject<Transaction>() {

    override var value =
        Transaction(-1, "invalid", 0,
            false, 0.0, 0, false,
            "", "", "", 0, transactionId, "")

    override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.flexiChargeApi.getTransaction(transactionId)
                if (!response.isSuccessful) {
                    cancel("Could not fetch transaction!")
                }
                else {
                    value = response.body() as Transaction
                }
            }
            catch (e: Exception) {
                cancel(CancellationException(e.message))
            }

        }
    }

    fun createSession(lifecycleScope: LifecycleCoroutineScope, transactionSession: TransactionSession): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.flexiChargeApi.postTransactionSession(transactionSession)
                if(!response.isSuccessful) {
                    cancel(response.message())
                }
                else {
                    val transactionSessionResponse = response.body() as TransactionSessionResponse
                    transactionId = transactionSessionResponse.transactionID
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

    fun stop(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.flexiChargeApi.transactionStop(value.transactionID)
                if (!response.isSuccessful) {
                    cancel(response.message())
                }
            }
            catch (e: Exception) {
                if(e.message != null) {
                    cancel(e.message!!)
                }
                else {
                    cancel("an unspecified error occurred")
                }
            }

        }
    }

    fun start(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
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

    fun refresh(lifecycleScope: LifecycleCoroutineScope, transactionId: Int) : Job {
        this.transactionId = transactionId
        return refresh(lifecycleScope)
    }

}