package assignment.sample.coding.domain.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import assignment.sample.coding.domain.model.Coordinate
import assignment.sample.coding.util.Constants
import com.google.android.gms.location.*

/**
 * Utility Live Data to listen for current user location and post updates to all the observers.
 * It starts listening when a observer is attached and stops when all observers are removed.
 *
 * @param context [Context] of the application to register Location services
 *
 * @return [LiveData<Coordinate>] observable
 */
class CurrentLocationLiveData(context: Context) : LiveData<Coordinate>() {

    var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.lastLocation?.let { location ->
                setCurrentLocationData(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient.requestLocationUpdates(
            requestLocation,
            locationCallback,
            null
        )
    }

    private fun setCurrentLocationData(location: Location) {
        value = Coordinate(
            longitude = location.longitude,
            latitude = location.latitude
        )
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.also {
                    setCurrentLocationData(it)
                }
            }
        getCurrentLocation()
    }

    companion object {
        val requestLocation: LocationRequest = LocationRequest.create().apply {
            interval = Constants.LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fastestInterval = Constants.FASTEST_LOCATION_UPDATE_INTERVAL
        }
    }
}