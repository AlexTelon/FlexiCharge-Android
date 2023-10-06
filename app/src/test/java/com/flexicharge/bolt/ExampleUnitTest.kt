package com.flexicharge.bolt

import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserFullDetails
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    @Test
    fun testGetChargerSuccessful() = runBlocking {
        val response = RetrofitInstance.flexiChargeApi.getCharger(100009)
        assert(response.isSuccessful)
    }

    @Test
    fun testGetChargerListSuccessful() = runBlocking {
        val response = RetrofitInstance.flexiChargeApi.getChargerList()
        assert(response.isSuccessful)
    }

    @Test
    fun testGetChargePointListSuccessful() = runBlocking {
        val response = RetrofitInstance.flexiChargeApi.getChargePointList()
        assert(response.isSuccessful)
    }

    @Test
    fun testGetTransactionSuccessful() = runBlocking {
        val response = RetrofitInstance.flexiChargeApi.getTransaction(1)
        assert(response.isSuccessful)
    }

    @Test
    fun testGetChargePoint() = runBlocking {
        val response = RetrofitInstance.flexiChargeApi.getChargePoint(100029)
        assert(response.isSuccessful)
    }

    @Test
    fun testLogin() = runBlocking {
        val email = "didiwa6692@searpen.com"
        val pass = "Test1234!"
        val credentials = Credentials(email, pass)
        val response = RetrofitInstance.flexiChargeApi.signIn(credentials)

        assert(response.isSuccessful)
    }

    @Test
    fun getUserInfo() = runBlocking {
        val email = "donene8581@vip4e.com"
        val pass = "Test123!"
        val credentials = Credentials(email, pass)
        val response = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val token = response.body()?.accessToken
        assert(response.isSuccessful)

        val info = RetrofitInstance.flexiChargeApi.getUserInfo("Bearer $token")
        println(info)
        assert(info.isSuccessful)
    }

    @Test
    fun updateUserInfo() = runBlocking {
        val email = "powamat696@vip4e.com"
        val pass = "Test123!"
        val credentials = Credentials(email, pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val token = login.body()?.accessToken
        assert(login.isSuccessful)

        val userInfo = UserFullDetails(
            "test33",
            "Testarsson",
            "",
            "",
            "",
            "",
            ""
        )
        val update = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token", userInfo)
        assert(update.isSuccessful)
    }
}