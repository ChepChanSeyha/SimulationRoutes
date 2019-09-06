package com.example.simulationroute.NewModel

import com.google.gson.annotations.SerializedName

data class Geometries(

	@field:SerializedName("duration")
	val duration: Double? = null,

	@field:SerializedName("route")
	val route: ArrayList<ArrayList<Double>>? = null,

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("blocks_scan")
	val blocksScan: ArrayList<Any?>? = null,

	@field:SerializedName("options")
	val options: ArrayList<Any?>? = null,

	@field:SerializedName("weather")
	val weather: String? = null
)