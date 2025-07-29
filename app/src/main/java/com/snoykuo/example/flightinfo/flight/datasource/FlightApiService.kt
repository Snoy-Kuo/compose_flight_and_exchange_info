package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.Flight
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightApiService {

    @GET("Air/FIDS/Airport/Arrival/TPE")
    suspend fun getArrivals(
        @Query("\$format") format: String = "JSON"
    ): List<Flight>

    @GET("Air/FIDS/Airport/Departure/TPE")
    suspend fun getDepartures(
        @Query("\$format") format: String = "JSON"
    ): List<Flight>
}