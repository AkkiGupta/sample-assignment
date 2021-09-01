package assignment.sample.coding.util

object Constants {
    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val HAMBURG_NE_LAT = 53.694865
    const val HAMBURG_NE_LON = 9.757589
    const val HAMBURG_SW_LAT = 53.394655
    const val HAMBURG_SW_LON = 10.099891

    const val LATITUDE_MAX: Double = 90.0
    const val LATITUDE_MIN: Double = -90.0
    const val LONGITUDE_MAX: Double = 180.0
    const val LONGITUDE_MIN: Double = -180.0

    const val LOCATION_UPDATE_INTERVAL = 5 * 60 * 1000L // 5 Minute
    const val FASTEST_LOCATION_UPDATE_INTERVAL =
        1 * 60 * 1000L // Not required location in 1 Minute

    const val MAP_ZOOM_LEVEL = 15.5f

    const val MAP_VEHICLE_WIDTH = 50
    const val MAP_VEHICLE_HEIGHT = 100

    //Extras
    const val EXTRAS_SELECTED_VEHICLE = "SELECTED_VEHICLE"

    //Error Message
    const val INVALID_LATITUDE = "Latitude can't be out of range (-90,90)."
    const val INVALID_LONGITUDE = "Longitude can't be out of range (-180,180)."
    const val NETWORK_ERROR = "Please check your internet connection."
}