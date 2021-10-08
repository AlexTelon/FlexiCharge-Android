package com.flexicharge.bolt

import okhttp3.RequestBody
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

    @POST("transactions/order")
    suspend fun postTransactionOrder(@Body body: TransactionOrder): Response<TransactionList>
}