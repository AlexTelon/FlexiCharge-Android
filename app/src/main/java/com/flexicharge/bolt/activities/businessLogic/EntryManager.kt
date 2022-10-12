package com.flexicharge.bolt.activities.businessLogic

import com.flexicharge.bolt.helpers.StatusCode
import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.LoginResponseBody
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

class EntryManager {
    private val emptyResponseBody = LoginResponseBody("","","","")

    suspend fun singIn(
        username: String,
        password: String,
        callback: (LoginResponseBody, message: String, isOK: Boolean) -> Unit
    ) {
        try {
            val body = Credentials(username, password)
            val response = RetrofitInstance.flexiChargeApi.signIn(body)
            if (response.code() == StatusCode.ok) {
                response.body()?.let {
                    callback.invoke(it, " ", true)
                }
            } else if (response.code() == StatusCode.badRequest) {
                callback(emptyResponseBody, "Invalid username or password! Try again.", false)
            }
        } catch (e: HttpException) {
            callback(emptyResponseBody, "Internal Server Error!", false)
        } catch (e: IOException) {
            callback(emptyResponseBody, "You have no internet connection!", false)
        }
    }

    suspend fun resetPassword(
        email: String,
        callback: (message: String, isOK: Boolean) -> Unit
    ) {
        try {
            val response = RetrofitInstance.flexiChargeApi.resetPass(email)
            if (response.code() == StatusCode.ok) {
                callback.invoke("", true)
            }
        } catch (e: HttpException) {
            callback.invoke("Internal Server Error! Try later.", false)
        } catch (e: IOException) {
            callback.invoke("You have no internet connection!", false)
        }
    }
}