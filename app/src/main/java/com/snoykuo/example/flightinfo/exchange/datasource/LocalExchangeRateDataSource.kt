package com.snoykuo.example.flightinfo.exchange.datasource

import android.content.Context
import kotlinx.serialization.json.Json

class LocalExchangeRateDataSource(context: Context) : MutableExchangeRateDataSource {
    private val prefs = context.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)

    override suspend fun getRates(
        baseCurrency: String,
        currencies: List<String>
    ): Map<String, Double> {
        val json = prefs.getString("rates_json", null)
        return if (json != null) {
            Json.decodeFromString<Map<String, Double>>(json)
        } else {
            emptyMap()
        }
    }

    override suspend fun getLastUpdated(): Long {
        return prefs.getLong("last_updated", 0L)
    }

    override suspend fun saveRates(rates: Map<String, Double>, lastUpdated: Long) {
        prefs.edit().apply {
            putString("rates_json", Json.encodeToString(rates))
            putLong("last_updated", lastUpdated)
            apply()
        }
    }
}
