package com.snoykuo.example.flightinfo.exchange.datasource

interface ExchangeRateDataSource {
    suspend fun getRates(baseCurrency: String, currencies: List<String>): Map<String, Double>
    suspend fun getLastUpdated(): Long
}

interface MutableExchangeRateDataSource : ExchangeRateDataSource {
    suspend fun saveRates(rates: Map<String, Double>, lastUpdated: Long)
}