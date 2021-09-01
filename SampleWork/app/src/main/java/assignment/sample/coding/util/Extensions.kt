package assignment.sample.coding.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import assignment.sample.coding.R
import assignment.sample.coding.domain.model.Poi
import assignment.sample.coding.util.Constants.MAP_VEHICLE_HEIGHT
import assignment.sample.coding.util.Constants.MAP_VEHICLE_WIDTH
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.IOException
import java.util.*

fun Context.carMarker(resId: Int): BitmapDescriptor {
    return BitmapDescriptorFactory.fromBitmap(
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources,
                resId
            ),
            MAP_VEHICLE_WIDTH,
            MAP_VEHICLE_HEIGHT,
            false
        )
    )
}

fun Poi.fleetIconResource(): Int {
    return when (fleetType.lowercase()) {
        "POOLING".lowercase() ->
            R.drawable.ic_vehicle_pool
        "TAXI".lowercase() ->
            R.drawable.ic_vehicle_taxi
        else -> R.drawable.ic_vehicle_taxi
    }
}

fun Poi.fleetString(): String {
    return when (fleetType.lowercase()) {
        "POOLING".lowercase() ->
            "Enjoy with new fellas with safety!"
        "TAXI".lowercase() ->
            "Your own private vehicle!"
        else -> "Your own private vehicle!"
    }
}

fun Poi.getAddress(context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses: List<Address> =
            geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
        val obj: Address = addresses[0]
        "${obj.locality}, ${obj.countryCode}"
    } catch (e: IOException) {
        e.printStackTrace()
        "N/A"
    }
}