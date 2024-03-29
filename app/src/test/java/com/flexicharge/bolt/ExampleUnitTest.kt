package com.flexicharge.bolt

import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
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
        val email = "green.sock5070+native@fastmail.com"
        val pass = "aB3${'$'}hejhejhejhej"
        val credentials = Credentials(email, pass)
        val logIn = RetrofitInstance.flexiChargeApi.signIn(credentials)
        assert(logIn.isSuccessful)
        val accessToken = logIn.body()?.accessToken
        val response = RetrofitInstance.flexiChargeApi.getTransaction("Bearer $accessToken", 1)
        assert(response.isSuccessful)
    }
}