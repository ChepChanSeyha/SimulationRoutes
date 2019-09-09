package com.example.simulationroute.NewModel

import com.example.simulationroute.ModelNew.StartPointEndPoint
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/route")
    fun getRouteResponse(
        @Body latLngResponse: StartPointEndPoint
    ): Call<ResponseLatLng>
}
