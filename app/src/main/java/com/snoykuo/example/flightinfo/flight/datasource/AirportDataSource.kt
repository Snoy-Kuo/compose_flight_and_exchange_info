package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.Airport

interface AirportDataSource {
    suspend fun getAllAirports(): List<Airport>
}