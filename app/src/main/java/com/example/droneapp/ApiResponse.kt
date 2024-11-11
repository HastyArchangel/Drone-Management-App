package com.example.droneapp

import com.google.android.gms.maps.model.LatLng

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

fun ApiResponse.toDrone(): Drone {
    return Drone(
        id = this.id,
        name = this.name,
        model = this.model,
        location = this.location,
        coordinates = LatLng(this.coordinates.lat, this.coordinates.lng),
        speed = this.speed,
        destination = this.destination?.let { LatLng(it.lat, it.lng) }
    )
}

fun Drone.toApiResponse(): ApiResponse {
    return ApiResponse(
        id = this.id,
        name = this.name,
        model = this.model,
        location = this.location,
        coordinates = ApiCoordinates(this.coordinates.latitude, this.coordinates.longitude),
        speed = this.speed,
        destination = this.destination?.let { ApiCoordinates(it.latitude, it.longitude) }
    )
}
