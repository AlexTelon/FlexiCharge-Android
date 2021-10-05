package com.flexicharge.bolt

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


