package com.example.simulationroute.Response

import com.google.gson.annotations.SerializedName

data class ResponseLatLng(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("start_point")
	val startPoint: List<Double>? = null,

	@field:SerializedName("end_point")
	val endPoint: List<Double>? = null,

	@field:SerializedName("geometries")
	val geometries: Geometries? = null
)