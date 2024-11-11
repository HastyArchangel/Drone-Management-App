package com.example.droneapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("drones")
    fun getDrones(): Call<List<ApiResponse>>

    @PUT("drones/{id}")
    fun updateDrone(
        @Path("id") droneId: String,
        @Body drone: ApiResponse
    ): Call<ApiResponse>
}