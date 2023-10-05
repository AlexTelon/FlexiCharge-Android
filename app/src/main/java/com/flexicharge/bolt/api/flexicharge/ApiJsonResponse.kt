package com.flexicharge.bolt.api.flexicharge

class Chargers : ArrayList<Charger>()

data class Credentials(
    val username: String,
    val password: String
)
data class LoginResponseBody(
    val accessToken: String,
    val email : String,
    val username: String,
    val user_id: String
)

data class Charger(
    val chargePointID: Int,
    val chargerID: Int,
    val location: List<Double>,
    val serialNumber: String,
    val status: String
)

data class ReservatioDetails(
    val chargerID: String,
    val userID: String,
    val start: Int,
    val end: Int
)

class ChargePoints : ArrayList<ChargePoint>()
data class ChargePoint(
    val chargePointID: Int,
    val klarnaReservationAmount: Int,
    val location: List<Double>,
    val name: String,
    val price: String
)

class TransactionList : ArrayList<Transaction>()

data class Transaction(
    val chargerID: Int,
    var klarna_consumer_token: String,
    var currentChargePercentage: Int,
    val isKlarnaPayment: Boolean,
    var kwhTransfered: Double,
    val meterStart: Int,
    val paymentConfirmed: Boolean,
    val paymentID: String,
    val pricePerKwh: String,
    val session_id: String,
    val timestamp: Long,
    val transactionID: Int,
    val userID: String,
    val startTimeStamp : Long ?= null
)

data class TransactionSession(
    val userID: String,
    val chargerID: Int,
    val isKlarnaPayment: Boolean,
    val pricePerKwh: Int
)

data class TransactionSessionResponse(
    val transactionID: Int
)

data class TransactionOrder(
    val authorization_token: String
)
// Every user has his own email, password
class UserDetails(
    val username : String,
    val password : String
)

class UserFullDetails(
    val firstName : String ?= null,
    val lastName : String ?= null,
    val phoneNumber : String ?= null,
    val streetAddress : String ?= null,
    val zipCode : String ?= null,
    val city : String ?= null,
    val country : String ?= null,
)

class ChargingHistoryObject(
    val location : String,
    val totalSum : String,
    val startTime : String,
    val chargeTime : String,
    val transferedKwh : String,
    val priceKwh : String
)

class VerificationDetails (
    val username : String,
    val code : String
)

class UserDetailsGotten(
    val message : String,
    val code : String,
    val statusCode : Int
)

data class ResetResponseBody(val status: String)

data class ResetRequestBody(
    val username: String,
    val password: String,
    val confirmationCode: String
)

data class InitTransactionDetails(
    val userID : String,
    val chargerID : String
)

data class InitTransactionDetailsV2(
    val userID : String,
    val chargerID : Int,
    val isKlarnaPayment: Boolean,
    val pricePerKwh: Int
)
data class TransactionId(
    val transactionID : Int
)


data class klarnaResponse (
    val payment_method_type : String,
    val status : String
        )

