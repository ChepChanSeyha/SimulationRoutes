package com.example.simulationroute.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.Data.LineResponse
import com.example.myapplication.Data.RetrofitClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.simulationroute.R
import com.example.simulationroute.PinActivity
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var marker: Marker? = null

    private var newLat: Double? = null
    private var newLng: Double? = null
    private var startLat: Double? = null
    private var startLng: Double? = null
    private var varLat: Double? = null
    private var varLng: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        buildLocationCallback()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        fusedLocationClient.removeLocationUpdates(locationCallback)

        addDestination.setOnClickListener {
            val intent = Intent(context, PinActivity::class.java)
            startActivityForResult(intent, 123)
        }

    }

    private fun buildLocationCallback() {
        locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isCompassEnabled = true
        mMap.isMyLocationEnabled = true

        getLastKnownLocation()
    }

    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                marker = mMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))
            }

            startLat = location!!.latitude
            startLng = location.longitude

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {

                newLat = data.getDoubleExtra("lat", 0.0)
                newLng = data.getDoubleExtra("lng", 0.0)

                marker = mMap.addMarker(MarkerOptions().position(LatLng(newLat!!, newLng!!)).title("Destination"))

                if (newLat != null && newLng != null) {
                    if (varLat != null && varLng != null) {
                        startLng = varLng
                        startLat = varLat
                        drawRoute(startLat!!, startLng!!)
                    }
                    else{
                        drawRoute(startLat!!, startLng!!)
                    }
                }
            }
        }
    }

    private fun drawRoute(lat: Double, lng: Double) {
        val client = RetrofitClient()
        val call = client.getService().getRouteResponse(
            "api/route?start_lng=$lng&start_lat=$lat&end_lng=$newLng&end_lat=$newLat&route=osrm"
        )
        varLat = newLat!!
        varLng = newLng!!

        call.enqueue(object : Callback<LineResponse> {
            override fun onFailure(call: Call<LineResponse>, t: Throwable) {
                Toast.makeText(context, "Get Status error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<LineResponse>, myResponse: Response<LineResponse>) {
                myResponse.body()?.let {
//                    Toast.makeText(context, "Get Status Success", Toast.LENGTH_LONG).show()
                }

                // Declare polyline object and set up color and width
                val polylineOptions = PolylineOptions()
                polylineOptions.color(Color.RED)
                polylineOptions.width(10f)

                val gg = myResponse.body()?.route!![0].coordinates

                if (gg != null)
                    for (i in 0 until gg.size) {
                        polylineOptions.add(LatLng(gg[i][0], gg[i][1]))
                    }

                mMap.addPolyline(polylineOptions)

            }
        })
    }

}