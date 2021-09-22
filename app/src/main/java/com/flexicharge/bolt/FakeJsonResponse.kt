package com.flexicharge.bolt

class Chargerss : ArrayList<Chargerr>()
data class Chargerr(
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