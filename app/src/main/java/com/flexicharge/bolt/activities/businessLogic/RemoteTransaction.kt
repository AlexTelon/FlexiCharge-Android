package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RemoteTransaction() : RemoteObject<Transaction>() {
    private var transactionId : Int = -1

    override var value =
        Transaction(-1, "invalid", 0,
            false, 0.0, 0, false,
            "", "", "", 0, transactionId, "")

    override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            val response = RetrofitInstance.flexiChargeApi.getTransaction(transactionId)
            if (!response.isSuccessful) {
                cancel("Could not fetch transaction!")
            } else {
                value = response.body() as Transaction
            }
        }
    }

    fun refresh(lifecycleScope: LifecycleCoroutineScope, transactionId: Int) : Job {
        this.transactionId = transactionId
        return refresh(lifecycleScope)
    }

}