package com.example.simulationroute

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_pin.*


class PinActivity : AppCompatActivity(),
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener,
    GoogleMap.OnCameraIdleListener,
    OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var textResult: TextView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var marker: Marker? = null
    private lateinit var latLng: LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapPin) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        buildLocationCallback()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        fusedLocationClient.removeLocationUpdates(locationCallback)


    }

    private fun buildLocationCallback() {
        locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                setMarker(locationResult.lastLocation)

            }
        }
    }

    override fun onMapReady(map: GoogleMap) {

        mMap = map

        getLastKnownLocation()

        mMap!!.uiSettings.isCompassEnabled = true
        mMap!!.isMyLocationEnabled = true

        mMap!!.setOnCameraIdleListener(this)
        mMap!!.setOnCameraMoveStartedListener(this)
        mMap!!.setOnCameraMoveListener(this)
        mMap!!.setOnCameraMoveCanceledListener(this)
    }

    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) setMarker(location)
            }
    }

    private fun setMarker(location: Location) {
        val lateLong1 = LatLng(location.latitude, location.longitude)

        if(marker == null){
//            val markerOptions = MarkerOptions().position(lateLong1)
//            marker = mMap?.addMarker(markerOptions.title("Current Location"))
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(lateLong1, 13f))

        }
    }

    override fun onCameraMoveStarted(reason: Int) {
        when (reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE ->
                Toast.makeText(this, "The user gestured on the map.", Toast.LENGTH_SHORT).show()
            GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION ->
                Toast.makeText(this, "The user tapped something on the map.", Toast.LENGTH_SHORT).show()
            GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION ->
                Toast.makeText(this, "The app moved the camera.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCameraMove() {
        Toast.makeText(this, "The camera is moving.", Toast.LENGTH_SHORT).show()
    }

    override fun onCameraMoveCanceled() {
        Toast.makeText(this, "Camera movement canceled.", Toast.LENGTH_SHORT).show()
    }

    override fun onCameraIdle() {
        textResult = findViewById(R.id.currentPlaceTV)
        latLng = mMap!!.cameraPosition.target
        textResult?.text  = latLng.toString()

        add.setOnClickListener {
            val intent = Intent()
            intent.putExtra("lat", latLng.latitude)
            intent.putExtra("lng", latLng.longitude)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}
