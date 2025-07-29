package com.snoykuo.example.flightinfo.exchange.repo

import com.snoykuo.example.flightinfo.common.data.DataResult

interface ExchangeRateRepository {
    suspend fun getRates(
        baseCurrency: String,
        currencies: List<String>
    ): DataResult<Map<String, Double>>

    suspend fun getLastUpdated(): Long
}