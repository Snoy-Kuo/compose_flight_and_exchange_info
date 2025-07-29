package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.Airport

class TdxAirportDataSource(
    private val api: AirportApiService
) : AirportDataSource {
    override suspend fun getAllAirports(): List<Airport> {
        return try {
            api.getAirports()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}