package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.FlightInfo

@Suppress("unused")
class CsvFlightDataSource() : FlightDataSource {
    private var lastUpdated: Long = 0L
    override suspend fun getFlights(): List<FlightInfo> {
        return FlightCsvParser.fetchFlights().also {
            lastUpdated = System.currentTimeMillis()
        }
    }

    override suspend fun getLastUpdated(): Long = lastUpdated
}


