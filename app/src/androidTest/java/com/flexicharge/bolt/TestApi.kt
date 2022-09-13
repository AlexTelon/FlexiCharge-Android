package com.flexicharge.bolt
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class TestApi {

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
}