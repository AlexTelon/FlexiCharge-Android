package com.flexicharge.bolt.activities.businessLogic

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.StatusCode
import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.LoginBody
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class EntryManager: AppCompatActivity() {
    private val loginBody = LoginBody("","","","")

    fun singIn(
        username: String,
        password: String,
        callback: (LoginBody, message: String, isOK: Boolean) -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val body = Credentials(username, password)
                val response = RetrofitInstance.flexiChargeApi.signIn(body)
                if (response.code() == StatusCode.ok) {
                    response.body()?.let {
                        callback.invoke(it, " ", true)
                    }
                }
                else if (response.code() == StatusCode.badRequest) {
                    callback(loginBody, response.message(), false)
                }
            } catch (e: HttpException) {
                Log.d("loginChecking", "Http Error")
            } catch (e: IOException) {
                Log.d("loginChecking", "No Internet Error - ChargerList will not be initialized")
            }
        }
    }
}