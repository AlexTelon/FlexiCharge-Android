package com.flexicharge.bolt

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("charger/{chargerId}")
    suspend fun getMockCharger(@Path("chargerId") chargerId: String): Response<Charger>

    @GET("charger")
    suspend fun getMockChargerList(): Response<Chargers>
}