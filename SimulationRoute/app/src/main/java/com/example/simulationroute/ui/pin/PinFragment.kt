package com.example.simulationroute.ui.pin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.simulationroute.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class PinFragment : Fragment(),
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener,
    GoogleMap.OnCameraIdleListener,
    OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var textResult: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_pin, container, false)

        textResult = view.findViewById(R.id.currentPlaceTV)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapPin) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {

        mMap = map

        mMap!!.setOnCameraIdleListener(this)
        mMap!!.setOnCameraMoveStartedListener(this)
        mMap!!.setOnCameraMoveListener(this)
        mMap!!.setOnCameraMoveCanceledListener(this)
    }

    override fun onCameraMoveStarted(reason: Int) {
        when (reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> Toast.makeText(context, "The user gestured on the map.", Toast.LENGTH_SHORT).show()
            GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION -> Toast.makeText(context, "The user tapped something on the map.", Toast.LENGTH_SHORT).show()
            GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> Toast.makeText(context, "The app moved the camera.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCameraMove() {
        Toast.makeText(context, "The camera is moving.", Toast.LENGTH_SHORT).show()
    }

    override fun onCameraMoveCanceled() {
        Toast.makeText(context, "Camera movement canceled.", Toast.LENGTH_SHORT).show()
    }

    override fun onCameraIdle() {
        val latLng = mMap!!.cameraPosition.target
        textResult?.text  = latLng.toString()
    }

}
