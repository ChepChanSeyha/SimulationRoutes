package com.example.myapplication.Data

import com.example.simulationroute.NewModel.GeometriesResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/v1/route")
    fun getRouteResponse(
        @Query("route") route: String
    ): retrofit2.Call<GeometriesResponse>
}
