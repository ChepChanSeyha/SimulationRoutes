package com.example.simulationroute.RetrofitApi

import com.example.simulationroute.Body.StartPointEndPoint
import com.example.simulationroute.Response.ResponseLatLng
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/v2/route")
    fun getRouteResponse(
        @Body latLngResponse: StartPointEndPoint
    ): Call<ResponseLatLng>
}
