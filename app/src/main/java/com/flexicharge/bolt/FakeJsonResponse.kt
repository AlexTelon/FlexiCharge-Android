package com.flexicharge.bolt

data class FakeJsonResponse(
    val id: Int,
    val location: Location,
    val chargePointId: Int,
    val status: Int
)

data class Location(
    val latitude: Double,
    val longitude: Double
)