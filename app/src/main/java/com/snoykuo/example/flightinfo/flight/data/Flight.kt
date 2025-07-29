package com.snoykuo.example.flightinfo.flight.data

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class Flight(
    val FlightNumber: String,
    val AirlineID: String,
    val DepartureAirportID: String,
    val ArrivalAirportID: String,
    val ScheduleArrivalTime: String? = null,
    val ActualArrivalTime: String? = null,
    val EstimatedArrivalTime: String? = null,
    val ScheduleDepartureTime: String? = null,
    val ActualDepartureTime: String? = null,
    val EstimatedDepartureTime: String? = null,
    val ArrivalRemark: String? = null,
    val DepartureRemark: String? = null,
    val Terminal: String? = null,
    val Gate: String? = null,
    val IsCargo: Boolean = false,
    val BaggageClaim: String? = null,
    val AcType: String? = null,
    val CheckCounter: String? = null,
    val UpdateTime: String? = null
)

@Serializable
data class FlightListWrapper(
    val lastUpdated: String,
    val list: List<Flight>
)