package com.snoykuo.example.flightinfo.flight.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class Airport(
    val AirportID: String,
    val AirportName: AirportName
)

@Serializable
data class AirportName(
    @SerialName("Zh_tw") val zh: String? = null,
    @SerialName("En") val en: String? = null
)

@Serializable
data class AirportListWrapper(
    val lastUpdated: String,
    val list: List<Airport>
)