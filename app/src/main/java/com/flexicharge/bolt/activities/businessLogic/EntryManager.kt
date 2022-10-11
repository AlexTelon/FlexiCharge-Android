package com.flexicharge.bolt.activities.businessLogic

import com.flexicharge.bolt.helpers.StatusCode
import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.LoginBody
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

class EntryManager {
    private val loginBody = LoginBody("","","","")

    suspend fun singIn(
        username: String,
        password: String,
        callback: (LoginBody, message: String, isOK: Boolean) -> Unit
    ) {
        try {
            val body = Credentials(username, password)
            val response = RetrofitInstance.flexiChargeApi.signIn(body)
            if (response.code() == StatusCode.ok) {
                response.body()?.let {
                    callback.invoke(it, " ", true)
                }
            } else if (response.code() == StatusCode.badRequest) {
                callback(loginBody, "Invalid username or password! Try again.", false)
            }
        } catch (e: HttpException) {
            callback(loginBody, "Internal Server Error!", false)
        } catch (e: IOException) {
            callback(loginBody, "You have no internet connection!", false)
        }
    }
}