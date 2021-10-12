package com.flexicharge.bolt.api.flexicharge

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .connectTimeout(360, TimeUnit.SECONDS)
                .readTimeout(360, TimeUnit.SECONDS)
                .writeTimeout(360, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl("http://54.220.194.65:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val flexiChargeApi by lazy {
            retrofit.create(ApiInterface::class.java)
        }



    }

}