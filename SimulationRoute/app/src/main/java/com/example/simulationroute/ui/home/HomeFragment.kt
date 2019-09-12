package com.example.simulationroute.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.simulationroute.Body.EndPoint
import com.example.simulationroute.Body.StartPointEndPoint
import com.example.simulationroute.Body.StartPoint
import com.example.simulationroute.RetrofitApi.RetrofitClient
import com.example.simulationroute.Response.ResponseLatLng
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.simulationroute.R
import com.example.simulationroute.PinActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var marker1: Marker? = null
    private var marker2: Marker? = null
    private var newLat: Double? = null
    private var newLng: Double? = null
    private var startLat: Double? = null
    private var startLng: Double? = null
    private var varLat: Double? = null
    private var varLng: Double? = null
    private var titleMarker: Int = 1
    private var listMarker = ArrayList<LatLng>()
    private var isMarkerRotating: Boolean = false
    private var locationUpdateState = false

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

        createLocationRequest()

//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//        fusedLocationClient.removeLocationUpdates(locationCallback)

        startSimulation.setOnClickListener {
            val bearing = bearingBetweenLocations(LatLng(startLat!!, startLng!!), LatLng(newLat!!, newLng!!))
            rotateMarker(marker1!!, bearing.toFloat())
        }

        addDestination.setOnClickListener {
            val intent = Intent(context, PinActivity::class.java)
            startActivityForResult(intent, 123)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
            val currentLocation = LatLng(location.latitude, location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))

            startLat = location.latitude
            startLng = location.longitude


            marker1 = mMap.addMarker(MarkerOptions().position(currentLocation).title("Me").icon(
                R.drawable.ic_no_box.bitmapDescriptorFromVector(
                    context!!
                )
            ))

        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 168)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val lastLocation = locationResult.lastLocation
                val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                marker1?.position = latLng
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {

                newLat = data.getDoubleExtra("lat", 0.0)
                newLng = data.getDoubleExtra("lng", 0.0)

                // Set marker
                marker2 = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(newLat!!, newLng!!))
                        .title("Destination $titleMarker")
                )

                // add each LatLng to array
                listMarker.add(LatLng(newLat!!, newLng!!))

                if (newLat != null && newLng != null) {
                    if (varLat != null && varLng != null) {
                        startLng = varLng
                        startLat = varLat
                        drawRoute(startLat!!, startLng!!)
                    } else {
                        drawRoute(startLat!!, startLng!!)
                    }
                }

                titleMarker += 1
            }
        }
    }

    // 3
    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun drawRoute(lat: Double, lng: Double) {
        varLat = newLat
        varLng = newLng

        val client = RetrofitClient()
        val latLngResponse = StartPointEndPoint(StartPoint(lng, lat), EndPoint(newLng, newLat))
        val call = client.getService().getRouteResponse(latLngResponse)

        call.enqueue(object : Callback<ResponseLatLng> {
            override fun onFailure(call: Call<ResponseLatLng>, t: Throwable) {
                Toast.makeText(context, "Get Status error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResponseLatLng>, myResponse: Response<ResponseLatLng>) {
                // Declare polyline object and set up color and width
                val polylineOptions = PolylineOptions()
                polylineOptions.color(Color.BLUE)
                polylineOptions.width(7f)

                val routeObj = myResponse.body()?.geometries?.route

                if (routeObj != null) {
                    for (i in routeObj.indices) {
                        val startToEndLatLng = LatLng(routeObj[i][0], routeObj[i][1])
                        polylineOptions.add(startToEndLatLng)
                    }
                }

                mMap.addPolyline(polylineOptions)

            }
        })
    }

    // function to custom marker to motor
    private fun Int.bitmapDescriptorFromVector(context: Context): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, this)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {

        val pi = 3.14159

        val lat1 = latLng1.latitude * pi / 180
        val long1 = latLng1.longitude * pi / 180
        val lat2 = latLng2.latitude * pi / 180
        val long2 = latLng2.longitude * pi / 180

        val dLon = long2 - long1

        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - (sin(lat1) * cos(lat2) * cos(dLon))

        var bearing = atan2(y, x)

        bearing = toDegrees(bearing)
        bearing = (bearing + 360) % 360

        return bearing
    }

    private fun rotateMarker(marker: Marker, toRotation: Float) {
        if (!isMarkerRotating) {
            val handler = Handler()
            val start = SystemClock.uptimeMillis()
            val startRotation = marker.rotation
            val duration: Long = 2000

            val interpolator = LinearInterpolator()

            handler.post(object : Runnable {
                override fun run() {
                    isMarkerRotating = true

                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)

                    val rot = t * toRotation + (1 - t) * startRotation

                    val bearing = if (-rot > 180) rot / 2 else rot

                    marker.rotation = bearing

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    } else {
                        isMarkerRotating = false
                    }
                }
            })
        }
    }

}