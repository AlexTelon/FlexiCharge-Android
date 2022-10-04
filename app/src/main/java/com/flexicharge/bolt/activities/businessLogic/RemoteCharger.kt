package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.api.flexicharge.Charger
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RemoteCharger(private var id: Int = -1) : RemoteObject<Charger>() {

    override var value: Charger =
        Charger(id, -1, listOf(0.0, 0.0), "", "")

    override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            val cancelMessage = "Could not retrieve all data properly"
            try {
                val response = RetrofitInstance.flexiChargeApi.getCharger(id)
                if(response.isSuccessful) {
                    value = response.body() as Charger
                }
                else {
                    cancel(cancelMessage)
                }

            } catch (e: HttpException) {
                cancel(cancelMessage)
            } catch (e: IOException) {
                cancel(cancelMessage)
            }
        }
    }

    fun refresh(lifecycleScope: LifecycleCoroutineScope, id: Int): Job {
        this.id = id
        return refresh(lifecycleScope)
    }
}