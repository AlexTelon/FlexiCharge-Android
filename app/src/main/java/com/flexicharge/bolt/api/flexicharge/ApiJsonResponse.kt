package com.flexicharge.bolt.api.flexicharge

class Chargers : ArrayList<Charger>()

data class Credentials(
    val username: String,
    val password: String
)

data class LoginResponseBody(
    val accessToken: String,
    val email: String,
    val username: String,
    val user_id: String
)

data class Charger(
    val chargePointID: Int,
    val connectorID: Int,
    val location: List<Double>,
    val serialNumber: String,
    val status: String
)

class ChargePoints : ArrayList<ChargePoint>()
data class ChargePoint(
    val chargePointID: Int,
    val location: List<Double>,
    val name: String,
    val address: String
)

data class InitTransaction(
    val klarnaClientToken: String,
    val klarnaSessionID: String,
    val transactionID: Double
)

data class Transaction(
    var transactionID: Double ? = null,
    var connectorID: Int ? = null,
    var currentChargePercentage: Int ? = null,
    var kwhTransferred: Double ? = null,
    val pricePerKwh: String ? = null,
    var price: Int? = null,
    var discount: String ? = null,
    var startTimestamp: Long? = null,
    var endTimestamp: Long? = null
)

data class TransactionSession(
    val connectorID: Int,
    val paymentType: String

)

// Every user has his own email, password
class UserDetails(
    val username: String,
    val password: String
)

class UserFullDetails(
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val streetAddress: String? = null,
    val zipCode: String? = null,
    val city: String? = null,
    val country: String? = null
)

class ChargingHistoryObject(
    val location: String,
    val totalSum: String,
    val startTime: String,
    val chargeTime: String,
    val transferedKwh: String,
    val priceKwh: String
)

class VerificationDetails(
    val username: String,
    val code: String
)

data class ResetResponseBody(val status: String)

data class ResetRequestBody(
    val username: String,
    val password: String,
    val confirmationCode: String
)
