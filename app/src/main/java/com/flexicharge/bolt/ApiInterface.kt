package com.flexicharge.bolt

import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("charger/123456")
    suspend fun getMockApiData(): Response<FakeJsonResponse>
    @GET("todos/1")
    suspend fun getFakeApiData(): Response<FakeResponse>

}