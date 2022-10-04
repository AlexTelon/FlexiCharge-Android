package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.api.flexicharge.ChargePoints
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RemoteChargePoints(override var value: ChargePoints) : RemoteObject<ChargePoints>() {
    override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.flexiChargeApi.getChargePointList()
                value = response.body() as ChargePoints

            } catch (e: HttpException) {
                Log.d("validateConnection", "Http Error")
                cancel(e.message())
            } catch (e: IOException) {
                Log.d("validateConnection", "No Internet Error - ChargePointList will not be initialized")
                cancel(e.toString())
            }
        }
    }

}