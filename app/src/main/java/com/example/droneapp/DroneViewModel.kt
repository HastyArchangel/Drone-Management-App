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
        println("Fetching drones...")
        viewModelScope.launch(Dispatchers.IO) {
            api.getDrones().enqueue(object : Callback<List<ApiResponse>> {
                override fun onResponse(call: Call<List<ApiResponse>>, response: Response<List<ApiResponse>>) {
                    if (response.isSuccessful) {
                        _drones.value = response.body()?.map { apiResponse ->
                            apiResponse.toDrone()  // or mapApiResponseToDrone(apiResponse)
                        } ?: emptyList()
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

        val apiResponseDrone = updatedDrone.toApiResponse()

        api.updateDrone(updatedDrone.id, apiResponseDrone).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {

                    val updatedDroneFromApi = response.body()?.toDrone()
                    println("Drone updated successfully: $updatedDroneFromApi")
                } else {
                    println("Failed to update drone: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                println("Error updating drone: ${t.message}")
            }
        })
    }

}

