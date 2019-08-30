package com.example.myapplication.Data

import com.google.gson.annotations.SerializedName

data class RouteItem(

	@field:SerializedName("coordinates")
	val coordinates: ArrayList<ArrayList<Double>>? = null
)