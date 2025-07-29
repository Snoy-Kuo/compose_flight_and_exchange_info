package com.snoykuo.example.flightinfo.flight.datasource

import android.content.Context
import androidx.core.content.edit
import com.snoykuo.example.flightinfo.flight.data.FlightInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class LocalFlightDataSource(
    private val context: Context
) : MutableFlightDataSource {

    private val fileName = "flights.json"
    private val lastUpdatedKey = "flight_last_updated"
    private val prefs = context.getSharedPreferences("flight_prefs", Context.MODE_PRIVATE)

    private var cache: List<FlightInfo>? = null

    override suspend fun getFlights(): List<FlightInfo> {
        cache?.let { return it }

        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withContext emptyList()

            val text = file.readText()
            val flights = try {
                Json.decodeFromString<List<FlightInfo>>(text)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
            cache = flights
            flights
        }
    }

    override suspend fun saveAll(flights: List<FlightInfo>) {
        withContext(Dispatchers.IO) {
            val json = Json.encodeToString(flights)
            File(context.filesDir, fileName).writeText(json)
            prefs.edit { putLong(lastUpdatedKey, System.currentTimeMillis()) }
            cache = flights
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            File(context.filesDir, fileName).delete()
            prefs.edit { remove(lastUpdatedKey) }
            cache = null
        }
    }

    override suspend fun getLastUpdated(): Long {
        return prefs.getLong(lastUpdatedKey, -1).takeIf { it > 0 } ?: 0
    }
}
