package com.flexicharge.bolt
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.api.flexicharge.Credentials
import org.junit.Test
import org.junit.runner.RunWith
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserFullDetails
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.rules.Timeout
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TestApi{
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

    @Test
    fun testGetChargePoint() = runBlocking {
        val response = RetrofitInstance.flexiChargeApi.getChargePoint(24)
        assert(response.isSuccessful)
    }

    @Test
    fun testLogin() = runBlocking {
        val email = "didiwa6692@searpen.com"
        val pass = "Test1234!"
        val credentials = Credentials(email,pass)
        val response = RetrofitInstance.flexiChargeApi.signIn(credentials)

        assert(response.isSuccessful)
    }

    @Test
    fun getUserInfo() = runBlocking {
        val email = "donene8581@vip4e.com"
        val pass = "Test123!"
        val credentials = Credentials(email,pass)
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
        val credentials = Credentials(email,pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val token = login.body()?.accessToken
        assert(login.isSuccessful)

        val userData = mapOf(
            "accessToken" to token,
            "userId" to "HEJ",
            "username" to "HEJ"
        )


        val userInfo = UserFullDetails(
            "test33",
            "Testarsson",
            "",
            "",
            "",
            "",
            ""
        )
        val token2 = userData["accessToken"]
        val response = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token2", userInfo)
      //  val update = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token",details)
        assert(response.isSuccessful)
    }
}