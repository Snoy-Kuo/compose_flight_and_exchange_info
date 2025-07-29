package com.snoykuo.example.flightinfo.flight.datasource

import android.content.Context
import com.snoykuo.example.flightinfo.exchange.util.parseIso8601ToMillis
import com.snoykuo.example.flightinfo.flight.data.FlightInfo
import com.snoykuo.example.flightinfo.flight.data.FlightListWrapper
import kotlinx.serialization.json.Json

class FallbackFlightDataSource(
    private val context: Context
) : FlightDataSource {
    private var cachedLastUpdated: Long = 0L
    private var cached: List<FlightInfo>? = null

    override suspend fun getFlights(): List<FlightInfo> {
        return cached ?: runCatching {
            val input = context.assets.open("default_flights.json")
            val json = input.bufferedReader().use { it.readText() }
            val wrapper: FlightListWrapper = Json.decodeFromString(json)
            cachedLastUpdated = parseIso8601ToMillis(wrapper.lastUpdated)
            wrapper.list.map { it.toFlightInfo(if (it.ScheduleArrivalTime.isNullOrEmpty()) "D" else "A") }
                .also { cached = it }
        }.getOrElse {
            it.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getLastUpdated(): Long = cachedLastUpdated
}