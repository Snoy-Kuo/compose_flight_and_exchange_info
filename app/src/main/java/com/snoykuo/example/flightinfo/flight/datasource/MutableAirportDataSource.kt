package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.Airport

interface MutableAirportDataSource : AirportDataSource {
    suspend fun saveAll(airports: List<Airport>)
    fun getLastUpdated(): Long
}