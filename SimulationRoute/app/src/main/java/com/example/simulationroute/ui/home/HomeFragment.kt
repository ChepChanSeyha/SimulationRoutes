package com.example.simulationroute.ui.home

import android.annotation.SuppressLint
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
import com.example.simulationroute.MainActivity
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
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

//        startSimulation.setOnClickListener {
//        }

        addDestination.setOnClickListener {
            val intent = Intent(context, PinActivity::class.java)
            startActivity(intent)
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
//                for (location in locationResult.locations){
                // Update UI with location data
                // ...
                setMarker(locationResult.lastLocation)
//                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLastKnownLocation()

        mMap.uiSettings.isCompassEnabled = true
        mMap.isMyLocationEnabled = true

    }

    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    setMarker(location)
                }
            }
    }

    private fun setMarker(location: Location) {
        val lateLong1 = LatLng(location.latitude, location.longitude)

        if(marker == null){
            val markerOptions = MarkerOptions().position(lateLong1)
            marker = mMap.addMarker(markerOptions.title("Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lateLong1, 13f))

        }
    }

}