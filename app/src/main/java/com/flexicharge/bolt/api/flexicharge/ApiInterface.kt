package com.flexicharge.bolt.api.flexicharge

import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {

    @POST("/reservations")
    suspend fun makeReservation(@Body reservation: ReservatioDetails) : Response<ReservatioDetails>

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
    suspend fun postTransactionSession(@Body body: TransactionSession): Response<TransactionSessionResponse>

    @PUT("transactions/start/{transactionId}")
    suspend fun transactionStart(
        @Path("transactionId") transactionId: Int,
    ): Response<Unit>

    @PUT("transactions/stop/{transactionId}")
    suspend fun transactionStop(@Path("transactionId") transactionId: Int): Response<TransactionList>


    @POST("/auth/sign-in")
    suspend fun signIn(@Body body: Credentials): Response<LoginResponseBody>

    // post request to store new users' data into database
    @POST("/auth/sign-up")
    suspend fun registerNewUser (@Body body: UserDetails) : Response<Unit>


    @POST("/auth/verify")
    suspend fun verifyEmail(@Body body: VerificationDetails) : Response<Unit>


    @POST("/auth/forgot-password/{username}")
    suspend fun resetPass(@Path("username") username: String): Response<ResetResponseBody>

    @POST("/auth/confirm-forgot-password")
    suspend fun confReset(@Body body: ResetRequestBody): Response<ResetResponseBody>

    @PUT("/auth/user-information")
    suspend fun updateUserInfo(@Header("Authorization") authorizationHeader : String, @Body body : UserFullDetails) : Response<UserFullDetails>

    @GET("/auth/user-information")
    suspend fun  getUserInfo(@Header("Authorization") authorizationHeader : String) : Response<UserFullDetails>
}