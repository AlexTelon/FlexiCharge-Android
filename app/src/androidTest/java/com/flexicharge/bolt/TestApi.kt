package com.flexicharge.bolt
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.api.flexicharge.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith

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
        println(response)
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
    fun testLogin() = runBlocking {
        val email = "didiwa6692@searpen.com"
        val pass = "Test1234!"
        val credentials = Credentials(email, pass)
        val response = RetrofitInstance.flexiChargeApi.signIn(credentials)

        assert(response.isSuccessful)
    }

    @Test
    fun getUserInfo() = runBlocking {
        val email = "kofap47986@viicard.com"
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
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email, pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val token = login.body()?.accessToken
        assert(login.isSuccessful)

        val userInfo = UserFullDetails(
            "test22",
            "TESTARSSON",
            "+46712345678",
            "",
            "",
            "",
            ""
        )
        val update = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token", userInfo)
        assert(update.isSuccessful)
    }

    @Test
    fun getUserTransactions() = runBlocking {
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email, pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val userID = login.body()?.user_id.toString()
        val chargerID = "100030"
        assert(login.isSuccessful)
        val transactions = RetrofitInstance.flexiChargeApi.transactionsByUserID(userID)
        println(transactions)
        assert(transactions.isSuccessful)
    }

    @Test
    fun startTransaction() = runBlocking {
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email, pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val userID = login.body()?.user_id.toString()
        val chargerID = "100000"
        assert(login.isSuccessful)
/*
        val transactions = RetrofitInstance.flexiChargeApi.transactionsByUserID(userID)

        val transactionId = transactions.body()?.get(0)?.transactionID
        println(transactionId)

 */ val id: Int = 9999

        val startTransaction = RetrofitInstance.flexiChargeApi.startTransaction(id)
        println(startTransaction)
        assert(startTransaction.isSuccessful)
    }
}
