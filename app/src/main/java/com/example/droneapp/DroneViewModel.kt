package com.example.droneapp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers

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

    private val _drones = mutableStateOf<List<Drone>>(emptyList())
    val drones: State<List<Drone>> = _drones

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.141:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    fun fetchDrones() {
        println("fetch")
        viewModelScope.launch(Dispatchers.IO) {
            api.getDrones().enqueue(object : Callback<List<ApiResponse>> {
                override fun onResponse(call: Call<List<ApiResponse>>, response: Response<List<ApiResponse>>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body() ?: emptyList()
                        _drones.value = apiResponse.map { apiDrone ->
                            Drone(
                                id = apiDrone.id,
                                name = apiDrone.name,
                                model = apiDrone.model,
                                location = apiDrone.location,
                                coordinates = LatLng(apiDrone.coordinates.lat, apiDrone.coordinates.lng),
                                speed = apiDrone.speed,
                                destination = apiDrone.destination?.let { LatLng(it.lat, it.lng) }
                            )
                        }
                    } else {
                        println("Failed to fetch drones: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<ApiResponse>>, t: Throwable) {
                    println("API call failed: ${t.message}")
                }
            })
        }
    }


    fun setDestination(drone: Drone, destination: LatLng) {
        val updatedDrone = drone.copy(destination = destination)

        api.updateDrone(updatedDrone.id, updatedDrone).enqueue(object : Callback<Drone> {
            override fun onResponse(call: Call<Drone>, response: Response<Drone>) {
                if (response.isSuccessful) {
                    println("Drone updated successfully: ${response.body()}")
                } else {
                    println("Failed to update drone: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Drone>, t: Throwable) {
                println("Error updating drone: ${t.message}")
            }
        })
    }

}

