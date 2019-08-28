package com.example.simulationroute.ui.home

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.simulationroute.R
import com.example.simulationroute.ui.pin.PinFragment
import kotlinx.android.synthetic.main.fragment_home.*


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

//        startSimulation.setOnClickListener {
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//        }
//

        addDestination.setOnClickListener {
            val t = this.fragmentManager!!.beginTransaction()
            val mFrag = PinFragment()
            t.replace(R.id.nav_host_fragment, mFrag)
            t.commit()
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

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) setMarker(location)
            }
    }

    @SuppressLint("MissingPermission")
    private fun setMarker(location: Location) {
        val lateLong1 = LatLng(location.latitude, location.longitude)

        if(marker == null){
            val markerOptions = MarkerOptions().position(lateLong1)
            marker = mMap.addMarker(markerOptions.title("Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lateLong1, 13f))

        }
    }

}