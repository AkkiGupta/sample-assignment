package assignment.sample.coding.data.repository

import assignment.sample.coding.data.source.remote.VehicleApi
import assignment.sample.coding.data.util.Response
import assignment.sample.coding.domain.model.Vehicle
import assignment.sample.coding.domain.repository.VehicleRepository
import assignment.sample.coding.util.Constants.NETWORK_ERROR
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * class: VehicleRepositoryImpl
 * @param vehicleApi the Network API provider for vehicles list
 * Desc: implementation of [VehicleRepository]. It is responsible for all the data collection and
 * providing it to the next part of the architecture i.e. view model in this case
 */
@ActivityScoped
class VehicleRepositoryImpl @Inject constructor(
    private val vehicleApi: VehicleApi
) : VehicleRepository {

    /**
     * Get the vehicle list from API providing valid parameters
     * @param nelat NorthEast latitude
     * @param nelon NorthEast longitude
     * @param swlat SouthWest latitude
     * @param swlon SouthWest longitude
     *
     * @return [Response<Vehicle>] with data if success else errormessage
     *
     * HERE WE SHALL RETURN LIVE DATA ACTUALLY, BUT I haven't implemented that flow to save some of my time
     */
    override suspend fun getVehicleList(
        nelat: Double,
        nelon: Double,
        swlat: Double,
        swlon: Double
    ): Response<Vehicle> {
        return try {
            val response = vehicleApi.getVehicleList(nelat, nelon, swlat, swlon)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Response.Success(it)
                } ?: Response.Error("An error Occurred.", null)
            } else {
                Response.Error("An error Occurred.", null)
            }
        } catch (e: Exception) {
            return Response.Error(
                NETWORK_ERROR,
                null
            )
        }
    }
}