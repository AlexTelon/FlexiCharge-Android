package com.flexicharge.bolt
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.api.flexicharge.*
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
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
        val email = "kofap47986@viicard.com"
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
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email,pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val token = login.body()?.accessToken
        assert(login.isSuccessful)

        val userInfo = UserFullDetails(
            "test22",
            "TESTARSSON",
            "",
            "",
            "",
            "",
            ""
        )
        val update = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token",userInfo)
        assert(update.isSuccessful)
    }

    @Test
    fun makeReservation() = runBlocking {
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email,pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val token = login.body()?.accessToken
        val id = login.body()?.user_id
        assert(login.isSuccessful)
        val reservatioDetails = ReservatioDetails(
            chargerID = "100030",
            userID = id!!,
            start = 1695201434,
            end = 1695201834
        )

        val reservation = RetrofitInstance.flexiChargeApi.makeReservation(reservatioDetails)
        println(reservation)
        assert(reservation.isSuccessful)
    }

    @Test
    fun initTransactionForUser() = runBlocking {
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email,pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val userID = login.body()?.user_id.toString()
        val chargerID = "100030"
        assert(login.isSuccessful)

        val details = InitTransactionDetails(userID,chargerID)
      /*
        val initTrans = RetrofitInstance.flexiChargeApi.initTransaction(details)
        assert(initTrans.isSuccessful)
        println(initTrans)

       */
    }


    @Test
    fun initTransactionForUserV2() = runBlocking {
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email,pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val userID = login.body()?.user_id.toString()
        val chargerID = "100028"
        assert(login.isSuccessful)

        val details = InitTransactionDetails(userID,chargerID)
        val initTrans = RetrofitInstance.flexiChargeApi.initTransaction2(details)
        assert(initTrans.isSuccessful)
        println(initTrans)

        val firstToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyMzA1ZWJjLWI4MTEtMzYzNy1hYTRjLTY2ZWNhMTg3NGYzZCJ9.eyJzZXNzaW9uX2lkIjoiNjRiY2VhZGEtODZmZC01ODkxLTg3ZTYtNDAxYWY2YWJhODNjIiwiYmFzZV91cmwiOiJodHRwczovL2pzLnBsYXlncm91bmQua2xhcm5hLmNvbS9ldS9rcCIsImRlc2lnbiI6ImtsYXJuYSIsImxhbmd1YWdlIjoic3YiLCJwdXJjaGFzZV9jb3VudHJ5IjoiU0UiLCJlbnZpcm9ubWVudCI6InBsYXlncm91bmQiLCJtZXJjaGFudF9uYW1lIjoiWW91ciBidXNpbmVzcyBuYW1lIiwic2Vzc2lvbl90eXBlIjoiUEFZTUVOVFMiLCJjbGllbnRfZXZlbnRfYmFzZV91cmwiOiJodHRwczovL2V1LnBsYXlncm91bmQua2xhcm5hZXZ0LmNvbSIsInNjaGVtZSI6dHJ1ZSwiZXhwZXJpbWVudHMiOlt7Im5hbWUiOiJrcGMtMWstc2VydmljZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3BjLVBTRUwtMzA5OSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXBvcHVwLXJldHJpYWJsZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXN0YXRpYy13aWRnZXQiLCJ2YXJpYXRlIjoiaW5kZXgiLCJwYXJhbWV0ZXJzIjp7ImR5bmFtaWMiOiJ0cnVlIn19LHsibmFtZSI6ImtwLWNsaWVudC1vbmUtcHVyY2hhc2UtZmxvdyIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJpbi1hcHAtc2RrLW5ldy1pbnRlcm5hbC1icm93c2VyIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoibmV3LWludGVybmFsLWJyb3dzZXItZW5hYmxlIn19LHsibmFtZSI6ImtwLWNsaWVudC11dG9waWEtc2RrLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3AtY2xpZW50LXV0b3BpYS13ZWJ2aWV3LWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoiaW4tYXBwLXNkay1jYXJkLXNjYW5uaW5nIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoiY2FyZC1zY2FubmluZy1lbmFibGUifX1dLCJyZWdpb24iOiJldSIsIm9yZGVyX2Ftb3VudCI6NTAwMDAsIm9mZmVyaW5nX29wdHMiOjIsIm9vIjoiYmEiLCJ2ZXJzaW9uIjoidjEuMTAuMC0xNTkwLWczZWJjMzkwNyJ9.hXs1xp8yXOZNQnA9HTMYKuhGZXqsf4Vv9I5VRu-t6vQeJPxyVDBw-yqQ8cPq_lsDEMEZK5yuqRsm2CdttsM5iwF5Yea9IO5MevFUm-ryrr27zk1dJEaJfHAKQZ04VCsGp2ZeIqASsEr1mAUAOnaWuD-XZgy9D01DveMP1gS2lnYNlGfT7IpUs96RvG_PJyFfUn8EzSGQiIiIpeyjpZsC9fGxiY80ekoZgEML_Vsn1_jLWk-bHxi5KPlTblR_-5ys-_AUOeD9nPMT7bjrSUMZrXx3Md_EMOEMJwKZA7C25erPLr-P7k8iz9YNvtFE58bSwojDnUKBTMSsPD2CUGIk6Q"
        val secondToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyMzA1ZWJjLWI4MTEtMzYzNy1hYTRjLTY2ZWNhMTg3NGYzZCJ9.eyJzZXNzaW9uX2lkIjoiNjRiY2VhZGEtODZmZC01ODkxLTg3ZTYtNDAxYWY2YWJhODNjIiwiYmFzZV91cmwiOiJodHRwczovL2pzLnBsYXlncm91bmQua2xhcm5hLmNvbS9ldS9rcCIsImRlc2lnbiI6ImtsYXJuYSIsImxhbmd1YWdlIjoic3YiLCJwdXJjaGFzZV9jb3VudHJ5IjoiU0UiLCJlbnZpcm9ubWVudCI6InBsYXlncm91bmQiLCJtZXJjaGFudF9uYW1lIjoiWW91ciBidXNpbmVzcyBuYW1lIiwic2Vzc2lvbl90eXBlIjoiUEFZTUVOVFMiLCJjbGllbnRfZXZlbnRfYmFzZV91cmwiOiJodHRwczovL2V1LnBsYXlncm91bmQua2xhcm5hZXZ0LmNvbSIsInNjaGVtZSI6dHJ1ZSwiZXhwZXJpbWVudHMiOlt7Im5hbWUiOiJrcGMtMWstc2VydmljZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3BjLVBTRUwtMzA5OSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXBvcHVwLXJldHJpYWJsZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXN0YXRpYy13aWRnZXQiLCJ2YXJpYXRlIjoiaW5kZXgiLCJwYXJhbWV0ZXJzIjp7ImR5bmFtaWMiOiJ0cnVlIn19LHsibmFtZSI6ImtwLWNsaWVudC1vbmUtcHVyY2hhc2UtZmxvdyIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJpbi1hcHAtc2RrLW5ldy1pbnRlcm5hbC1icm93c2VyIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoibmV3LWludGVybmFsLWJyb3dzZXItZW5hYmxlIn19LHsibmFtZSI6ImtwLWNsaWVudC11dG9waWEtc2RrLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3AtY2xpZW50LXV0b3BpYS13ZWJ2aWV3LWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoiaW4tYXBwLXNkay1jYXJkLXNjYW5uaW5nIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoiY2FyZC1zY2FubmluZy1lbmFibGUifX1dLCJyZWdpb24iOiJldSIsIm9yZGVyX2Ftb3VudCI6NTAwMDAsIm9mZmVyaW5nX29wdHMiOjIsIm9vIjoiYmEiLCJ2ZXJzaW9uIjoidjEuMTAuMC0xNTkwLWczZWJjMzkwNyJ9.hXs1xp8yXOZNQnA9HTMYKuhGZXqsf4Vv9I5VRu-t6vQeJPxyVDBw-yqQ8cPq_lsDEMEZK5yuqRsm2CdttsM5iwF5Yea9IO5MevFUm-ryrr27zk1dJEaJfHAKQZ04VCsGp2ZeIqASsEr1mAUAOnaWuD-XZgy9D01DveMP1gS2lnYNlGfT7IpUs96RvG_PJyFfUn8EzSGQiIiIpeyjpZsC9fGxiY80ekoZgEML_Vsn1_jLWk-bHxi5KPlTblR_-5ys-_AUOeD9nPMT7bjrSUMZrXx3Md_EMOEMJwKZA7C25erPLr-P7k8iz9YNvtFE58bSwojDnUKBTMSsPD2CUGIk6Q"
        assert(firstToken != secondToken)

    }

    @Test
    fun getUserTransactions() = runBlocking {
        val email = "kofap47986@viicard.com"
        val pass = "Test123!"
        val credentials = Credentials(email,pass)
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
        val credentials = Credentials(email,pass)
        val login = RetrofitInstance.flexiChargeApi.signIn(credentials)
        val userID = login.body()?.user_id.toString()
        val chargerID = "100000"
        assert(login.isSuccessful)
/*
        val transactions = RetrofitInstance.flexiChargeApi.transactionsByUserID(userID)

        val transactionId = transactions.body()?.get(0)?.transactionID
        println(transactionId)

 */     val id : Int = 9999

        val startTransaction = RetrofitInstance.flexiChargeApi.startTransaction(id)
        println(startTransaction)
        assert(startTransaction.isSuccessful)

    }

    @Test
    fun same(){
        val token1 = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyMzA1ZWJjLWI4MTEtMzYzNy1hYTRjLTY2ZWNhMTg3NGYzZCJ9.eyJzZXNzaW9uX2lkIjoiNjRiY2VhZGEtODZmZC01ODkxLTg3ZTYtNDAxYWY2YWJhODNjIiwiYmFzZV91cmwiOiJodHRwczovL2pzLnBsYXlncm91bmQua2xhcm5hLmNvbS9ldS9rcCIsImRlc2lnbiI6ImtsYXJuYSIsImxhbmd1YWdlIjoic3YiLCJwdXJjaGFzZV9jb3VudHJ5IjoiU0UiLCJlbnZpcm9ubWVudCI6InBsYXlncm91bmQiLCJtZXJjaGFudF9uYW1lIjoiWW91ciBidXNpbmVzcyBuYW1lIiwic2Vzc2lvbl90eXBlIjoiUEFZTUVOVFMiLCJjbGllbnRfZXZlbnRfYmFzZV91cmwiOiJodHRwczovL2V1LnBsYXlncm91bmQua2xhcm5hZXZ0LmNvbSIsInNjaGVtZSI6dHJ1ZSwiZXhwZXJpbWVudHMiOlt7Im5hbWUiOiJrcGMtMWstc2VydmljZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3BjLVBTRUwtMzA5OSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXBvcHVwLXJldHJpYWJsZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXN0YXRpYy13aWRnZXQiLCJ2YXJpYXRlIjoiaW5kZXgiLCJwYXJhbWV0ZXJzIjp7ImR5bmFtaWMiOiJ0cnVlIn19LHsibmFtZSI6ImtwLWNsaWVudC1vbmUtcHVyY2hhc2UtZmxvdyIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJpbi1hcHAtc2RrLW5ldy1pbnRlcm5hbC1icm93c2VyIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoibmV3LWludGVybmFsLWJyb3dzZXItZW5hYmxlIn19LHsibmFtZSI6ImtwLWNsaWVudC11dG9waWEtc2RrLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3AtY2xpZW50LXV0b3BpYS13ZWJ2aWV3LWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoiaW4tYXBwLXNkay1jYXJkLXNjYW5uaW5nIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoiY2FyZC1zY2FubmluZy1lbmFibGUifX1dLCJyZWdpb24iOiJldSIsIm9yZGVyX2Ftb3VudCI6NTAwMDAsIm9mZmVyaW5nX29wdHMiOjIsIm9vIjoiYmEiLCJ2ZXJzaW9uIjoidjEuMTAuMC0xNTkwLWczZWJjMzkwNyJ9.hXs1xp8yXOZNQnA9HTMYKuhGZXqsf4Vv9I5VRu-t6vQeJPxyVDBw-yqQ8cPq_lsDEMEZK5yuqRsm2CdttsM5iwF5Yea9IO5MevFUm-ryrr27zk1dJEaJfHAKQZ04VCsGp2ZeIqASsEr1mAUAOnaWuD-XZgy9D01DveMP1gS2lnYNlGfT7IpUs96RvG_PJyFfUn8EzSGQiIiIpeyjpZsC9fGxiY80ekoZgEML_Vsn1_jLWk-bHxi5KPlTblR_-5ys-_AUOeD9nPMT7bjrSUMZrXx3Md_EMOEMJwKZA7C25erPLr-P7k8iz9YNvtFE58bSwojDnUKBTMSsPD2CUGIk6Q"
        val token2 = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyMzA1ZWJjLWI4MTEtMzYzNy1hYTRjLTY2ZWNhMTg3NGYzZCJ9.eyJzZXNzaW9uX2lkIjoiNjRiY2VhZGEtODZmZC01ODkxLTg3ZTYtNDAxYWY2YWJhODNjIiwiYmFzZV91cmwiOiJodHRwczovL2pzLnBsYXlncm91bmQua2xhcm5hLmNvbS9ldS9rcCIsImRlc2lnbiI6ImtsYXJuYSIsImxhbmd1YWdlIjoic3YiLCJwdXJjaGFzZV9jb3VudHJ5IjoiU0UiLCJlbnZpcm9ubWVudCI6InBsYXlncm91bmQiLCJtZXJjaGFudF9uYW1lIjoiWW91ciBidXNpbmVzcyBuYW1lIiwic2Vzc2lvbl90eXBlIjoiUEFZTUVOVFMiLCJjbGllbnRfZXZlbnRfYmFzZV91cmwiOiJodHRwczovL2V1LnBsYXlncm91bmQua2xhcm5hZXZ0LmNvbSIsInNjaGVtZSI6dHJ1ZSwiZXhwZXJpbWVudHMiOlt7Im5hbWUiOiJrcGMtMWstc2VydmljZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3BjLVBTRUwtMzA5OSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXBvcHVwLXJldHJpYWJsZSIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJrcC1jbGllbnQtdXRvcGlhLXN0YXRpYy13aWRnZXQiLCJ2YXJpYXRlIjoiaW5kZXgiLCJwYXJhbWV0ZXJzIjp7ImR5bmFtaWMiOiJ0cnVlIn19LHsibmFtZSI6ImtwLWNsaWVudC1vbmUtcHVyY2hhc2UtZmxvdyIsInZhcmlhdGUiOiJ2YXJpYXRlLTEifSx7Im5hbWUiOiJpbi1hcHAtc2RrLW5ldy1pbnRlcm5hbC1icm93c2VyIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoibmV3LWludGVybmFsLWJyb3dzZXItZW5hYmxlIn19LHsibmFtZSI6ImtwLWNsaWVudC11dG9waWEtc2RrLWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoia3AtY2xpZW50LXV0b3BpYS13ZWJ2aWV3LWZsb3ciLCJ2YXJpYXRlIjoidmFyaWF0ZS0xIn0seyJuYW1lIjoiaW4tYXBwLXNkay1jYXJkLXNjYW5uaW5nIiwicGFyYW1ldGVycyI6eyJ2YXJpYXRlX2lkIjoiY2FyZC1zY2FubmluZy1lbmFibGUifX1dLCJyZWdpb24iOiJldSIsIm9yZGVyX2Ftb3VudCI6NTAwMDAsIm9mZmVyaW5nX29wdHMiOjIsIm9vIjoiYmEiLCJ2ZXJzaW9uIjoidjEuMTAuMC0xNTkwLWczZWJjMzkwNyJ9.hXs1xp8yXOZNQnA9HTMYKuhGZXqsf4Vv9I5VRu-t6vQeJPxyVDBw-yqQ8cPq_lsDEMEZK5yuqRsm2CdttsM5iwF5Yea9IO5MevFUm-ryrr27zk1dJEaJfHAKQZ04VCsGp2ZeIqASsEr1mAUAOnaWuD-XZgy9D01DveMP1gS2lnYNlGfT7IpUs96RvG_PJyFfUn8EzSGQiIiIpeyjpZsC9fGxiY80ekoZgEML_Vsn1_jLWk-bHxi5KPlTblR_-5ys-_AUOeD9nPMT7bjrSUMZrXx3Md_EMOEMJwKZA7C25erPLr-P7k8iz9YNvtFE58bSwojDnUKBTMSsPD2CUGIk6Q"
        assert(token1 == token2)
    }


}