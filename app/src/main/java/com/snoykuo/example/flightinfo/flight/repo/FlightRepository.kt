package com.snoykuo.example.flightinfo.flight.repo

import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.flight.data.FlightInfo

interface FlightRepository {
    suspend fun getFlights(): DataResult<List<FlightInfo>>
    suspend fun getLastUpdated(): Long
}