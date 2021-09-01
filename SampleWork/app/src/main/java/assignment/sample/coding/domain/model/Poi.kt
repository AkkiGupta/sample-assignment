package assignment.sample.coding.domain.model

import java.io.Serializable

data class Poi(
    val coordinate: Coordinate,
    val fleetType: String,
    val heading: Double,
    val id: Int,
    var locationName: String? = null
): Serializable