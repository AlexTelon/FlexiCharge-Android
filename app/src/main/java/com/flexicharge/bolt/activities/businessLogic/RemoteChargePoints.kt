package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.ChargePoints
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException

class RemoteChargePoints() : RemoteObject<ChargePoints>() {
    override var value = ChargePoints()

    override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
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

}