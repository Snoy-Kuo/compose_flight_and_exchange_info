package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.Flight
import com.snoykuo.example.flightinfo.flight.data.FlightInfo

class TdxFlightDataSource(
    private val api: FlightApiService
) : FlightDataSource {
    private var lastUpdated: Long = 0L
    override suspend fun getFlights(): List<FlightInfo> {
        val arrivals = api.getArrivals().map { it.toFlightInfo("A") }
        val departures = api.getDepartures().map { it.toFlightInfo("D") }
        lastUpdated = System.currentTimeMillis()
        return arrivals + departures
    }

    override suspend fun getLastUpdated(): Long = lastUpdated
}

fun Flight.toFlightInfo(type: String): FlightInfo {
    val scheduled = when (type) {
        "A" -> this.ScheduleArrivalTime ?: ""
        "D" -> this.ScheduleDepartureTime ?: ""
        else -> ""
    }

    val estimated = when (type) {
        "A" -> this.EstimatedArrivalTime ?: ""
        "D" -> this.EstimatedDepartureTime ?: ""
        else -> ""
    }
//    Log.d("RDTest", "type=$type")
//    Log.d("RDTest", "ScheduleArrivalTime=$ScheduleArrivalTime, ScheduleDepartureTime=$ScheduleDepartureTime")
//    Log.d("RDTest", "EstimatedArrivalTime=$EstimatedArrivalTime, EstimatedDepartureTime=$EstimatedDepartureTime")
//    Log.d("RDTest", "scheduled=$scheduled, estimated=$estimated")
    val data = FlightInfo(
        airlineName = this.AirlineID, // 可以另外對照轉換中文
        terminal = this.Terminal ?: "",
        type = type,
        airlineCode = this.AirlineID,
        flightNumber = this.FlightNumber,
        gate = this.Gate ?: "",
        scheduledDate = scheduled.take(10).replace("-", "/"),
        scheduledTime = if (scheduled.length >= 16) scheduled.substring(11, 16) else "",
        estimatedDate = estimated.take(10).replace("-", "/"),
        estimatedTime = if (estimated.length >= 16) estimated.substring(11, 16) else "",
        destinationCode = if (type == "A") this.DepartureAirportID else this.ArrivalAirportID,
        destinationEn = "",
        destinationZh = "",
        status = this.ArrivalRemark ?: this.DepartureRemark ?: "",
        aircraftType = this.AcType ?: "",
    )
//    Log.d("RDTest", "data=$data")
    return data
}


