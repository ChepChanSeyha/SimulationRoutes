package com.example.simulationroute.ModelNew

import com.google.gson.annotations.SerializedName

data class EndPoint(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)