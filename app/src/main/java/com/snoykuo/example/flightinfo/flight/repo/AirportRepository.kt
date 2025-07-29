package com.snoykuo.example.flightinfo.flight.repo

import com.snoykuo.example.flightinfo.flight.data.AirportName

interface AirportRepository {
    suspend fun getAirportNameByCode(code: String): AirportName?
    fun getLastUpdated(): Long
    suspend fun refreshAirports()
}