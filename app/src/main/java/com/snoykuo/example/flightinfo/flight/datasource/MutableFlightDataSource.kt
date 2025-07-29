package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.FlightInfo

interface MutableFlightDataSource : FlightDataSource {
    suspend fun saveAll(flights: List<FlightInfo>)
    suspend fun clear()
    override suspend fun getLastUpdated(): Long
}