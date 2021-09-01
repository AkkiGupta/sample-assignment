package assignment.sample.coding.data.source.remote

import assignment.sample.coding.domain.model.Vehicle
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for VehicleAPI
 *
 * Lists out all the Get/Post methods to be used by Retrofit
 */
interface VehicleApi {

    @GET("/")
    suspend fun getVehicleList(
        @Query("p1Lat") nelat: Double,
        @Query("p1Lon") nelon: Double,
        @Query("p2Lat") swlat: Double,
        @Query("p2Lon") swlon: Double
    ): Response<Vehicle>

}