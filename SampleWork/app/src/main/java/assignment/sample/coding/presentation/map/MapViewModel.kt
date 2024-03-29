package assignment.sample.coding.presentation.map

import android.app.Application
import androidx.lifecycle.*
import assignment.sample.coding.data.util.Event
import assignment.sample.coding.data.util.Response
import assignment.sample.coding.domain.model.Coordinate
import assignment.sample.coding.domain.model.Vehicle
import assignment.sample.coding.domain.repository.VehicleRepository
import assignment.sample.coding.domain.util.CurrentLocationLiveData
import assignment.sample.coding.util.Constants.INVALID_LATITUDE
import assignment.sample.coding.util.Constants.INVALID_LONGITUDE
import assignment.sample.coding.util.Constants.LATITUDE_MAX
import assignment.sample.coding.util.Constants.LATITUDE_MIN
import assignment.sample.coding.util.Constants.LONGITUDE_MAX
import assignment.sample.coding.util.Constants.LONGITUDE_MIN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MapViewModel is a bridge between UI and [VehicleRepository] Repository.
 *
 * It refrains UI to act on business logic and performs all computations in it.
 * It is running in ViewModel scope so no chance of memory leaks if activity or fragment
 * associated gets killed.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    application: Application,
    private val repository: VehicleRepository
) : AndroidViewModel(application) {

    private val _vehicleList = MutableLiveData<Event<Response<Vehicle>>>()
    private val vehicleList: LiveData<Event<Response<Vehicle>>> = _vehicleList

    private val currentLocation = CurrentLocationLiveData(getApplication())

    /**
     * Observe [getUserLocation] to get the current user location updates.
     *
     * Make sure the user has given permissions
     * [android.Manifest.permission.ACCESS_COARSE_LOCATION]
     * [android.Manifest.permission.ACCESS_FINE_LOCATION]
     *
     * and location is enabled in device
     *
     * @return [LiveData]<[Coordinate]>
     */
    fun getUserLocation() = currentLocation

    /**
     * list all the vehicles available in given NE, SW latitude and longitude bounds
     *
     * @param nelat NorthEast Latitude within range 90 to -90
     * @param neLon NorthEast Longitude within range 90 to -90
     * @param swLat SouthWest Latitude within range 180 to -180
     * @param swLon SouthWest Longitude within range 180 to -180
     *
     * This method doesn't return anything but to get results,
     * you shall observe [observeVehicleListChange].
     */
    fun listVehiclesInGivenBound(
        nelat: Double, neLon: Double,
        swLat: Double, swLon: Double
    ) {
        if (nelat !in LATITUDE_MIN..LATITUDE_MAX || swLat !in LATITUDE_MIN..LATITUDE_MAX) {
            _vehicleList.postValue(
                Event(Response.Error(INVALID_LATITUDE))
            )
            return
        }

        if (neLon !in LONGITUDE_MIN..LONGITUDE_MAX || swLon !in LONGITUDE_MIN..LONGITUDE_MAX) {
            _vehicleList.postValue(
                Event(Response.Error(INVALID_LONGITUDE))
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            postUpdateOnMainThread(_vehicleList, (Event(Response.Loading())))

            val response = repository.getVehicleList(nelat, neLon, swLat, swLon)
            postUpdateOnMainThread(_vehicleList, (Event(handleVehicleListResponse(response))))
        }
    }

    /**
     * Observer for vehicle list method, this should be registered
     * before you make [listVehiclesInGivenBound] method call to avoid any response miss.
     *
     * @return [LiveData]<[Event]<[Response]<[Vehicle]>>>
     */
    fun observeVehicleListChange() = vehicleList

    private fun handleVehicleListResponse(response: Response<Vehicle>): Response<Vehicle> {
        when (response) {
            is Response.Success -> {
                response.data?.let { result ->
                    return Response.Success(result)
                }
            }
            is Response.Error -> {
                return Response.Error(response.message ?: "Unknown Error occurred")
            }
            is Response.Loading -> {
                return Response.Loading()
            }
        }
        return Response.Error(response.message ?: "Unknown Error occurred")
    }

    private fun postUpdateOnMainThread(
        liveData: MutableLiveData<Event<Response<Vehicle>>>,
        data: Event<Response<Vehicle>>
    ) {
        MainScope().launch {
            liveData.value = data
        }
    }
}