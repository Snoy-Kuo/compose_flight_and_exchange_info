package com.snoykuo.example.flightinfo.flight.datasource

import com.snoykuo.example.flightinfo.flight.data.Airport
import retrofit2.http.GET
import retrofit2.http.Query

interface AirportApiService {
    @GET("Air/Airport")
    suspend fun getAirports(
        @Query("\$format") format: String = "JSON"
    ): List<Airport>
}