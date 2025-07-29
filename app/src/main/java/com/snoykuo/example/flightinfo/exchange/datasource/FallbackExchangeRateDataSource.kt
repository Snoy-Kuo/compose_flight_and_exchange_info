package com.snoykuo.example.flightinfo.exchange.datasource

import android.content.Context
import com.snoykuo.example.flightinfo.exchange.data.ExchangeRateResponseWrapper
import com.snoykuo.example.flightinfo.exchange.util.parseIso8601ToMillis
import kotlinx.serialization.json.Json

class FallbackExchangeRateDataSource(private val context: Context) : ExchangeRateDataSource {
    private var cachedRates: Map<String, Double> = emptyMap()
    private var cachedLastUpdated: Long = 0L

    init {
        loadFromAssets()
    }

    private fun loadFromAssets() {
        val json = context.assets.open("default_exchange_rates.json").bufferedReader()
            .use { it.readText() }
        val wrapper: ExchangeRateResponseWrapper = Json.decodeFromString(json)
        cachedLastUpdated = parseIso8601ToMillis(wrapper.lastUpdated)
        cachedRates = wrapper.value.data
    }

    override suspend fun getRates(
        baseCurrency: String,
        currencies: List<String>
    ): Map<String, Double> {
        return cachedRates.filterKeys { it in currencies }
    }

    override suspend fun getLastUpdated(): Long = cachedLastUpdated
}
