package com.example.simulationroute.Body

import com.google.gson.annotations.SerializedName

data class StartPoint(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)