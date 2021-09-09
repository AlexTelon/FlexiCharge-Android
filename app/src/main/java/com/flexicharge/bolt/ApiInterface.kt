package com.flexicharge.bolt

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("charger/{chargerId}")
    suspend fun getMockApiData(@Path("chargerId") chargerId: String): Response<FakeJsonResponse>
    @GET("todos/1")
    suspend fun getFakeApiData(): Response<FakeResponse>
}