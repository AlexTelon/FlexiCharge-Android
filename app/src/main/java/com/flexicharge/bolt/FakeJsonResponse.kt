package com.flexicharge.bolt

class Chargers : ArrayList<Charger>()
data class Charger(
    val chargePointId: Int,
    val chargePointAddress: String,
    val numberOfChargers: Int,
    val id: Int,
    val location: Location,
    val status: Int
)
data class Location(
    val latitude: Double,
    val longitude: Double
)