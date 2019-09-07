package com.example.simulationroute.Model

import com.example.simulationroute.ModelNew.LatLngResponse
import com.example.simulationroute.NewModel.ResponseLatLng
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/route")
    fun getRouteResponse(
        @Body latLngResponse: LatLngResponse
    ): Call<ResponseLatLng>
}
