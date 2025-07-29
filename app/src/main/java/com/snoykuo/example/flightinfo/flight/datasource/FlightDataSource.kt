package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.FlightInfo

interface FlightDataSource {
    suspend fun getFlights(): List<FlightInfo>
    suspend fun getLastUpdated(): Long
}