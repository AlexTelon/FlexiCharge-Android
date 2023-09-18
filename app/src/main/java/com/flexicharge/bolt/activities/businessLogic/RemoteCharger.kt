package com.flexicharge.bolt.activities.businessLogic

import androidx.lifecycle.LifecycleCoroutineScope
import com.flexicharge.bolt.api.flexicharge.Charger
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException

class RemoteCharger(private var id: Int = -1) : RemoteObject<Charger>() {

    override var value: Charger =
        Charger(0, id, listOf(0.0, 0.0), "", "")

    var status: String = ""
        private set

    override fun retrieve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                val cancelMessage = "Could not retrieve all data properly"
                try {
                    val response = RetrofitInstance.flexiChargeApi.getCharger(id)
                    if(response.isSuccessful) {
                        value = response.body() as Charger
                    }
                    else {
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

    fun reserve(lifecycleScope: LifecycleCoroutineScope): Job {
        return lifecycleScope.launch (Dispatchers.IO){
            withTimeout(REMOTE_OBJECT_TIMEOUT_MILLISECONDS) {
                try {
                    val requestParams: MutableMap<String, String> = HashMap()
                    requestParams["chargerId"] = value.chargerID.toString()
                    requestParams["connectorId"] = "1"
                    requestParams["idTag"] = "1"
                    requestParams["reservationId"] = "1"
                    requestParams["parentIdTag"] = "1"
                    val response = RetrofitInstance.flexiChargeApi.reserveCharger(value.chargerID, requestParams)
                    if(!response.isSuccessful) {
                        cancel(response.message())
                    }

                    status = response.body() as String
                }
                catch (e: Exception) {
                    cancel(CancellationException(e.message))
                }
            }
        }
    }

    fun refresh(lifecycleScope: LifecycleCoroutineScope, id: Int): Job {
        this.id = id
        return refresh(lifecycleScope)
    }
}