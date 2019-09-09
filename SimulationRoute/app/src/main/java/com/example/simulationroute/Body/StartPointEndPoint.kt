package com.example.simulationroute.Body

import com.google.gson.annotations.SerializedName

data class StartPointEndPoint(

	@field:SerializedName("start_point")
	val startPoint: StartPoint? = null,

	@field:SerializedName("end_point")
	val endPoint: EndPoint? = null
)