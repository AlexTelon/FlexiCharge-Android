package com.flexicharge.bolt

import retrofit2.Response
import retrofit2.http.*



interface ApiInterface {
    @GET("chargers/{chargerId}")
    suspend fun getMockCharger(@Path("chargerId") chargerId: String): Response<Charger>

    @GET("chargers")
    suspend fun getMockChargerList(): Response<Chargers>

    @FormUrlEncoded
    @PUT("chargers/{chargerId}")
    suspend fun setChargerStatus(
        @Path("chargerId") chargerId: String,
        @Field("status") status: String
    ): Response<Charger>
}