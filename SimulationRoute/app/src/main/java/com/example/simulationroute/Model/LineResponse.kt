package com.example.myapplication.Data

import com.google.gson.annotations.SerializedName

data class LineResponse(

	@field:SerializedName("route")
	val route: ArrayList<RouteItem>? = null
)