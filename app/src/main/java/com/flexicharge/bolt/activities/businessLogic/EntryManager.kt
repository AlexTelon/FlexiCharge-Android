package com.flexicharge.bolt.activities.businessLogic

import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.LoginResponseBody
import com.flexicharge.bolt.api.flexicharge.ResetRequestBody
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.helpers.StatusCode
import java.io.IOException
import retrofit2.HttpException

class EntryManager {
    private val emptyResponseBody = LoginResponseBody("", "", "", "")

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

    suspend fun confirmResetPass(
        email: String,
        newPassword: String,
        confirmationCode: String,
        callback: (message: String, isOk: Boolean) -> Unit
    ) {
        try {
            val body = ResetRequestBody(email, newPassword, confirmationCode)
            val response = RetrofitInstance.flexiChargeApi.confReset(body)
            if (response.code() == StatusCode.ok) {
                callback.invoke("Successfully changed the password.", true)
            } else if (response.code() == StatusCode.badRequest) {
                callback("Invalid confirmation code!", false)
            }
        } catch (e: HttpException) {
            callback.invoke("Internal Server Error! Try later.", false)
        } catch (e: IOException) {
            callback.invoke("You have no internet connection!", false)
        }
    }
}