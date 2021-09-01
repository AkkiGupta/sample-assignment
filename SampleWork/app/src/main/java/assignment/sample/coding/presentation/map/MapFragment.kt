package assignment.sample.coding.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import assignment.sample.coding.R
import assignment.sample.coding.data.util.Response
import assignment.sample.coding.domain.model.Poi
import assignment.sample.coding.util.Constants.HAMBURG_NE_LAT
import assignment.sample.coding.util.Constants.HAMBURG_NE_LON
import assignment.sample.coding.util.Constants.HAMBURG_SW_LAT
import assignment.sample.coding.util.Constants.HAMBURG_SW_LON
import assignment.sample.coding.util.Constants.MAP_ZOOM_LEVEL
import assignment.sample.coding.util.Constants.REQUEST_CODE_LOCATION_PERMISSION
import assignment.sample.coding.util.PermissionUtility
import assignment.sample.coding.util.carMarker
import assignment.sample.coding.util.fleetIconResource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.pbLoadingState
import kotlinx.android.synthetic.main.fragment_map.tvErrorMessage
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

/**
 * MapFragment to represent map view with
 * 1. Selected Taxi from Vehicle List view to locate on Map
 * 2. Given Location Permissions and enabled device location to locate current user and find
 * vehicles in current map bound and present on map
 * 3. Denied Location Permissions to located Hamburg bound vehicles available on MAP
 */
@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), EasyPermissions.PermissionCallbacks {

    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private val args: MapFragmentArgs by navArgs()
    private var currentUserLocation: LatLng? = null

    private var latLngList = mutableListOf<LatLng>()
    private val latLngBoundBuilder: LatLngBounds.Builder = LatLngBounds.builder()
    private var currentLocationBoundLoaded: MutableLiveData<Boolean> = MutableLiveData(false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        requestLocationPermissions()

        val vehicle = args.let {
            Timber.d("arguments not null")
            it.vehicle
        }

        mapView.getMapAsync {
            googleMap = it
            setupAndClearMap()
            setupMapView(vehicle)
        }
    }

    private fun setupAndClearMap() {
        googleMap?.clear()
        setupMapListener()
    }

    @SuppressLint("MissingPermission")
    private fun setupMapView(vehicle: Poi?) {
        vehicle?.let { vehicle_it ->
            prepareMapViewForSelectedTaxi(vehicle_it)
        } ?: if (PermissionUtility.hasLocationPermission(requireContext())) {
            googleMap?.isMyLocationEnabled = true
            prepareMapViewForCurrentLocation()
        } else {
            prepareMapViewForHamburgLocation()
        }
    }

    private fun prepareMapViewForSelectedTaxi(vehicle: Poi) {
        val latLng = LatLng(
            vehicle.coordinate.latitude,
            vehicle.coordinate.longitude
        )

        showSelectedVehicleMarker(latLng, vehicle.fleetIconResource(), vehicle.heading)
        moveCamera(latLng)
        animateCamera(latLng)
    }

    private fun prepareMapViewForCurrentLocation() {
        drawUserLocationIfAccessible()
        observeVehicleListResponse()
    }

    private fun setupMapListener() {
        googleMap?.setOnCameraIdleListener {
            if (currentLocationBoundLoaded.value == true) {
                currentLocationBoundLoaded.value = false
                getBoundsAndRequestForVehicle()
            }
        }
    }

    private fun getBoundsAndRequestForVehicle() {
        val currentViewBounds =
            googleMap?.projection?.visibleRegion?.latLngBounds ?: LatLngBounds(
                LatLng(
                    HAMBURG_SW_LAT,
                    HAMBURG_SW_LON
                ),
                LatLng(
                    HAMBURG_NE_LAT,
                    HAMBURG_NE_LON
                )
            )
        getVehiclesInCurrentMapBounds(currentViewBounds)
    }

    private fun getVehiclesInCurrentMapBounds(latLngBounds: LatLngBounds) {
        Timber.d("Akash ${latLngBounds.northeast}, ${latLngBounds.southwest}")
        val ne = latLngBounds.northeast
        val sw = latLngBounds.southwest
        viewModel.listVehiclesInGivenBound(ne.latitude, ne.longitude, sw.latitude, sw.longitude)
    }

    private fun prepareMapViewForHamburgLocation() {
        observeVehicleListResponse()
        getVehicleListInHamburg()
    }

    private fun getVehicleListInHamburg() {
        viewModel.listVehiclesInGivenBound(
            HAMBURG_NE_LAT,
            HAMBURG_NE_LON,
            HAMBURG_SW_LAT,
            HAMBURG_SW_LON,
        )
    }

    private fun drawUserLocationIfAccessible() {
        if (PermissionUtility.hasLocationPermission(requireContext())) {
            viewModel.getUserLocation().observe(viewLifecycleOwner, {
                currentUserLocation = LatLng(
                    it.latitude,
                    it.longitude
                )
                showCurrentLocationOnMap(currentUserLocation!!)
                currentLocationBoundLoaded.value = true
            })
        }
    }

    private fun showCurrentLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)
    }

    private fun moveCamera(latLng: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng) {
        val camera = CameraPosition.Builder()
            .target(latLng)
            .zoom(MAP_ZOOM_LEVEL)
            .build()
        googleMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(camera)
        )
    }

    private fun animateCameraToLatLngBound(bounds: LatLngBounds, padding: Int = 0) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, padding)
        )
    }

    private fun showSelectedVehicleMarker(latLng: LatLng, resId: Int, rotation: Double): Marker? {
        val bitmapDescriptor = requireContext().carMarker(resId)
        return googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .flat(true)
                .icon(bitmapDescriptor)
                .anchor(0.5f, 0.5f)
                .rotation(rotation.toFloat())
        )
    }

    private fun observeVehicleListResponse() {
        viewModel.observeVehicleListChange().observe(viewLifecycleOwner, { response ->
            when (val responseCaptured = response.getContentIfNotAlreadyHandled()) {
                is Response.Success -> {
                    hideProgressBar()
                    responseCaptured.data?.let { vehicleResponse ->
                        Timber.d("Vehicle data loaded successfully ${vehicleResponse.poiList}")
                        addCarsToMap(vehicleResponse.poiList)
                    }
                }
                is Response.Error -> {
                    hideProgressBar()
                    responseCaptured.message?.let { message ->
                        Timber.e("An error occurred $message")
                        showError(message)
                    }
                }
                is Response.Loading -> {
                    Timber.d("Vehicle data is loading")
                    showProgressBar()
                }
            }
        })
    }

    private fun addCarsToMap(vehicles: List<Poi>) {
        googleMap?.clear()
        vehicles.forEach { vehicle ->
            val latlng = LatLng(vehicle.coordinate.latitude, vehicle.coordinate.longitude).also {
                latLngList.add(it)
            }
            latLngBoundBuilder.apply {
                include(latlng)
            }
            val bitmapDescriptor = requireContext().carMarker(vehicle.fleetIconResource())
            googleMap?.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .flat(true)
                    .icon(bitmapDescriptor)
                    .anchor(0.5f, 0.5f)
                    .rotation(vehicle.heading.toFloat())
            )
        }
        focusLatLngBoundMap()
    }

    private fun focusLatLngBoundMap() {
        animateCameraToLatLngBound(
            latLngBoundBuilder.build()
        )
    }

    private fun showError(message: String) {
        tvErrorMessage.visibility = View.VISIBLE
        tvErrorMessage.text = message
    }

    private fun showProgressBar() {
        tvErrorMessage.visibility = View.INVISIBLE
        pbLoadingState.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        pbLoadingState.visibility = View.INVISIBLE
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun requestLocationPermissions() {
        if (PermissionUtility.hasLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EasyPermissions.requestPermissions(
                this,
                "Please accept location permissions to use the Map view.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (!EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            requestLocationPermissions()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            val len = permissions.size
            for (i in 0 until len) {
                val permission = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    val showRationale = shouldShowRequestPermissionRationale(permission)
                    if (showRationale) {
                        AppSettingsDialog.Builder(this).build().show()
                        break
                    }
                }
            }
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}