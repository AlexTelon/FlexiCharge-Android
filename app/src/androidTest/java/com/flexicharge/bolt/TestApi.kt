package com.flexicharge.bolt
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.rules.Timeout
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TestApi {
    @get:Rule public var timeout = Timeout(10, TimeUnit.SECONDS)

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