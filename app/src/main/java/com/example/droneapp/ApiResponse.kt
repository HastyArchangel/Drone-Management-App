package com.example.droneapp

data class ApiResponse(
    val id: String,
    val name: String,
    val model: String,
    val location: String,
    val coordinates: ApiCoordinates,
    val speed: Float,
    val destination: ApiCoordinates?
)

data class ApiCoordinates(
    val lat: Double,
    val lng: Double
)