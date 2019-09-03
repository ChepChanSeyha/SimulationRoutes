package com.example.myapplication.Data

import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun getRouteResponse(
        @Url url: String
    ): retrofit2.Call<LineResponse>
}
