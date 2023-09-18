package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.Chargers
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException

class RemoteChargers() : RemoteObject<Chargers>() {

    override var value = Chargers()

    override fun retrieve(lifecycleScope: LifecycleCoroutineScope) : Job {

        val refreshJob = lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.getChargerList() // Retrofit is a REST Client, Retrieve and upoad JSON
                    if (!response.isSuccessful) {
                        cancel("Failed retrieving chargers")
                    }

                    value = response.body() as Chargers


                } catch (e: HttpException) {
                    Log.d("validateConnection", "Http Error")
                    cancel(e.message())
                } catch (e: IOException) {
                    Log.d("validateConnection", "No Internet Error - ChargerList will not be initialized")
                    cancel(e.toString())
                }

            }
        }

        return refreshJob
    }
}