package com.snoykuo.example.flightinfo.flight.datasource

import android.content.Context
import com.snoykuo.example.flightinfo.exchange.util.parseIso8601ToMillis
import com.snoykuo.example.flightinfo.flight.data.Airport
import com.snoykuo.example.flightinfo.flight.data.AirportListWrapper
import kotlinx.serialization.json.Json

class FallbackAirportDataSource(
    private val context: Context
) : AirportDataSource {
    private var cachedLastUpdated: Long = 0L
    private var cached: List<Airport>? = null

    override suspend fun getAllAirports(): List<Airport> {
        return cached ?: runCatching {
            val input = context.assets.open("default_airports.json")
            val json = input.bufferedReader().use { it.readText() }
            val wrapper: AirportListWrapper = Json.decodeFromString(json)
            cachedLastUpdated = parseIso8601ToMillis(wrapper.lastUpdated)
            wrapper.list
                .also { cached = it }
        }.getOrElse {
            it.printStackTrace()
            emptyList()
        }
    }
}
