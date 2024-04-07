package com.hassanpial.uber

// RetrofitService.kt

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("vehicle/position")
    fun getVehiclePosition(): Call<VehiclePositionResponse>
}
