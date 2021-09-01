package assignment.sample.coding.data.repository

import assignment.sample.coding.data.util.Response
import assignment.sample.coding.domain.model.Vehicle
import assignment.sample.coding.domain.repository.VehicleRepository
import assignment.sample.coding.util.Constants.NETWORK_ERROR

class FakeVehicleRepositoryImpl: VehicleRepository {

    companion object {
        private var shouldReturnNetworkError = false

        fun setShouldReturnNetworkError(value: Boolean) {
            shouldReturnNetworkError = value
        }
    }

    override suspend fun getVehicleList(
        nelat: Double,
        nelon: Double,
        swlat: Double,
        swlon: Double
    ): Response<Vehicle> {
        return if(shouldReturnNetworkError) {
            Response.Error(NETWORK_ERROR, null)
        } else {
            Response.Success(Vehicle(listOf()))
        }
    }

}