package com.example.simulationroute.RetrofitApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    fun getService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://dev.techostartup.center:9103/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}