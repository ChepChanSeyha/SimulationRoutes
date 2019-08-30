package com.example.myapplication.Data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    fun getService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.10.40.29:8093/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}