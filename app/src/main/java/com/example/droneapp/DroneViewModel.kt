package com.example.droneapp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

data class Drone(
    val id: String,
    val name: String,
    val model: String,
    val location: String,
    val coordinates: LatLng,
    val speed: Float,
    val destination: LatLng? = null
)

class DroneViewModel : ViewModel() {

    private val _drones = mutableStateOf<List<Drone>>(listOf(
        Drone(id = "1", name = "Drone 1", model = "Model A", location = "București", coordinates = LatLng(44.4268, 26.1025), speed = 10f),
        Drone(id = "2", name = "Drone 2", model = "Model B", location = "Cluj-Napoca", coordinates = LatLng(46.7712, 23.6236), speed = 12f),
        Drone(id = "3", name = "Drone 3", model = "Model C", location = "Timișoara", coordinates = LatLng(45.7489, 21.2087), speed = 8f)
    ))

    val drones: State<List<Drone>> = _drones

    fun updateDronePosition(updatedDrone: Drone) {
        _drones.value = _drones.value.map {
            if (it.id == updatedDrone.id) updatedDrone else it
        }
    }

    fun setDestination(drone: Drone, destination: LatLng) {
        println("so.")
        _drones.value = _drones.value.map {
            if (it.id == drone.id) it.copy(destination = destination) else it
        }
    }

    private fun moveDrone(drone: Drone): Drone {
        val destination = drone.destination ?: return drone

        val currentPos = drone.coordinates
        val distance = FloatArray(1)
        android.location.Location.distanceBetween(
            currentPos.latitude, currentPos.longitude,
            destination.latitude, destination.longitude,
            distance
        )

        if (distance[0] < 10) {
            return drone.copy(destination = null)
        } else {

            val moveRatio = drone.speed / distance[0]
            val newLat = currentPos.latitude + (destination.latitude - currentPos.latitude) * moveRatio
            val newLng = currentPos.longitude + (destination.longitude - currentPos.longitude) * moveRatio
            return drone.copy(coordinates = LatLng(newLat, newLng))
        }
    }

    fun updateAllDrones() {
        _drones.value = _drones.value.map { drone ->
            moveDrone(drone)
        }
    }
}

