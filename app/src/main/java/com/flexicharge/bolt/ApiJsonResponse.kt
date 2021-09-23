package com.flexicharge.bolt

class Chargers : ArrayList<Charger>()

data class Charger(
    val chargePointID: Int,
    val chargerID: Int,
    val location: List<Double>,
    val serialNumber: String,
    val status: Int
)