package com.flexicharge.bolt

import retrofit2.Response
import retrofit2.http.*



interface ApiInterface {

    @GET("chargers/{chargerId}")
    suspend fun getCharger(@Path("chargerId") chargerId: Int): Response<Charger>

    @GET("chargers")
    suspend fun getChargerList(): Response<Chargers>

    @PUT("chargers/{chargerId}")
    suspend fun setChargerStatus(
        @Path("chargerId") chargerId: Int,
        @Body body: MutableMap<String, Int>
    ): Response<Charger>

}