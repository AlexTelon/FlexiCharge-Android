package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.api.flexicharge.Charger
import com.flexicharge.bolt.api.flexicharge.Chargers
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.helpers.MapHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel
import retrofit2.HttpException
import java.io.IOException

class RemoteChargerList(override var value: Chargers) : RemoteObject<Chargers>() {


    override fun retrieve(lifecycleScope: LifecycleCoroutineScope) : Job {

        val refreshJob = lifecycleScope.launch(Dispatchers.IO) {
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

        return refreshJob
    }
}