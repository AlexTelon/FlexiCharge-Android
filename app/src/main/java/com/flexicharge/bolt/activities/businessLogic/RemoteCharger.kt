package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.Charger
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import java.io.IOException
import kotlinx.coroutines.*
import retrofit2.HttpException

class RemoteCharger(private var id: Int = -1) : RemoteObject<Charger>() {

    override var value: Charger =
        Charger(0, id, listOf(0.0, 0.0), "", "")

    override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                val cancelMessage = "Could not retrieve all data properly"
                try {
                    val response = RetrofitInstance.flexiChargeApi.getCharger(id)
                    if (response.isSuccessful) {
                        value = response.body() as Charger
                    } else {
                        cancel(response.message())
                    }
                } catch (e: HttpException) {
                    cancel(cancelMessage)
                } catch (e: IOException) {
                    cancel(cancelMessage)
                }
            }
        }
    }
}