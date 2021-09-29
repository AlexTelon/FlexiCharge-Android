package com.flexicharge.bolt

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
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