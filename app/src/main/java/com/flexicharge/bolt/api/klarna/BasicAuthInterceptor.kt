package com.flexicharge.bolt.api.klarna

import java.io.IOException
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor(user: String, password: String) : Interceptor {

    private val credentials: String = Credentials.basic(user, password)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder().header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}