package com.example.myapplication.Data

import retrofit2.http.GET

interface ApiService {

    @GET("api/route?start_lng=104.8912&start_lat=11.5684&end_lng=104.8834&end_lat=11.5040&route=osrm&scan=false")
    fun getRouteResponse(): retrofit2.Call<LineResponse>
}
