package assignment.sample.coding.domain.repository

import assignment.sample.coding.data.util.Response
import assignment.sample.coding.domain.model.Vehicle

interface VehicleRepository {
    suspend fun getVehicleList(
        nelat: Double,
        nelon: Double,
        swlat: Double,
        swlon: Double
    ): Response<Vehicle>
}