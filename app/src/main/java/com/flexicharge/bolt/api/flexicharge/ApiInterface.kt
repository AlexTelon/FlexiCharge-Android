package com.flexicharge.bolt.api.flexicharge

import android.service.autofill.UserData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*



interface ApiInterface {

    @GET("chargers/{chargerId}")
    suspend fun getCharger(@Path("chargerId") chargerId: Int): Response<Charger>

    @GET("chargers")
    suspend fun getChargerList(): Response<Chargers>

    @GET("chargePoints")
    suspend fun getChargePointList(): Response<ChargePoints>

    @GET("chargers/{chargerPointId}")
    suspend fun getChargePoint(@Path("chargerPointId") chargerPointId: Int): Response<ChargePoint>

    @PUT("chargers/{chargerId}")
    suspend fun setChargerStatus(
        @Path("chargerId") chargerId: Int,
        @Body body: MutableMap<String, String>
    ): Response<Charger>

    @PUT("reservations/{chargerId}")
    suspend fun reserveCharger(
        @Path("chargerId") chargerId: Int,
        @Body body: MutableMap<String, String>
    ): Response<String>

    @GET("transactions/{transactionId}")
    suspend fun getTransaction(@Path("transactionId") transactionId: Int): Response<Transaction>

    @POST("transactions/session")
    suspend fun postTransactionSession(@Body body: TransactionSession): Response<Transaction>

    @PUT("transactions/start/{transactionId}")
    suspend fun transactionStart(
        @Path("transactionId") transactionId: Int,
        @Body body: TransactionOrder
    ): Response<TransactionList>

    @PUT("transactions/stop/{transactionId}")
    suspend fun transactionStop(@Path("transactionId") transactionId: Int): Response<TransactionList>

    @POST("/auth/sign-in")
    suspend fun signIn(@Body body: Credentials): Response<LoginBody>

    // post request to store new users' data into database
    @POST("/auth/sign-up")
    suspend fun registerNewUser (@Body body: UserDetails) : Response<Unit>


    @POST("/auth/verify")
    suspend fun verifyEmail(@Body body: VerificationDetails)


}