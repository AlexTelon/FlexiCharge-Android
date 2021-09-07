package com.flexicharge.bolt

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Query

interface ApiInterface {

    @GET("todos/1")
    suspend fun getFakeApiData(): Response<FakeResponse>

}