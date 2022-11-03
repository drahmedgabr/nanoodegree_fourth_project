package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.*
import kotlinx.android.synthetic.main.fragment_select_location.*
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var latLng: LatLng = LatLng(30.044022, 31.230202)
    private var poiName = ""
    private val zoomLevel = 15f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveLocationButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        var marker: Marker? = null
        map = googleMap
        map.isMyLocationEnabled = true
        styleMap()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

        map.setOnPoiClickListener {
            if (marker != null) marker?.remove()
            latLng = it.latLng
            poiName = it.name
            val title = String.format(
                Locale.getDefault(),
                "Lat: %1$.6f, Long: %2$.6f",
                latLng.latitude,
                latLng.longitude
            )
            marker =
                map.addMarker(MarkerOptions().position(latLng).title("Dropped Pin").snippet(title))
        }

        map.setOnMapClickListener {
            if (marker != null) marker?.remove()
            latLng = it
            poiName = getString(R.string.custom_location)
            val title = String.format(
                Locale.getDefault(),
                "Lat: %1$.6f, Long: %2$.6f",
                latLng.latitude,
                latLng.longitude
            )
            marker =
                map.addMarker(MarkerOptions().position(latLng).title("Dropped Pin").snippet(title))
        }
    }

    private fun styleMap() {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity().applicationContext,
                    R.raw.style_json
                )
            )
            if (!success) Log.e("MapsActivity", "Style parsing failed.")
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Style parsing failed. Reason: ", e)
        }
    }

    private fun onLocationSelected() {
        _viewModel.latitude.value = latLng.latitude
        _viewModel.longitude.value = latLng.longitude
        _viewModel.reminderSelectedLocationStr.value = poiName
        _viewModel.navigationCommand.value = NavigationCommand.Back
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
