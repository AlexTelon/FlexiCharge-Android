package com.flexicharge.bolt.api.flexicharge

class Chargers : ArrayList<Charger>()

data class Charger(
    val chargePointID: Int,
    val chargerID: Int,
    val location: List<Double>,
    val serialNumber: String,
    val status: String
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
    val client_token: String,
    val currentChargePercentage: Int,
    val isKlarnaPayment: Boolean,
    val kwhTransfered: Double,
    val meterStart: Int,
    val paymentConfirmed: Boolean,
    val paymentID: String,
    val pricePerKwh: String,
    val session_id: String,
    val timestamp: Int,
    val transactionID: Int,
    val userID: String
)


data class TransactionSession(
    val chargerID: Int,
    val userID: String
)

data class TransactionOrder(
    val transactionID: Int,
    val authorization_token: String
)
