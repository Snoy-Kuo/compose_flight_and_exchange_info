package com.snoykuo.example.flightinfo.flight.datasource

import android.content.Context
import com.snoykuo.example.flightinfo.flight.data.Airport
import kotlinx.serialization.json.Json
import java.io.File

class LocalAirportDataSource(
    context: Context
) : MutableAirportDataSource {

    private var memoryCache: List<Airport>? = null
    private var lastUpdated: Long = 0

    private val file = File(context.filesDir, "airports.json")

    override suspend fun getAllAirports(): List<Airport> {
        memoryCache?.let { return it }

        return runCatching {
            val json = file.readText()
            val data = Json.decodeFromString<List<Airport>>(json)
            memoryCache = data
            data
        }.getOrElse {
            emptyList()
        }
    }

    override suspend fun saveAll(airports: List<Airport>) {
        memoryCache = airports
        lastUpdated = System.currentTimeMillis()
        file.writeText(Json.encodeToString(airports))
    }

    override fun getLastUpdated(): Long = lastUpdated
}
