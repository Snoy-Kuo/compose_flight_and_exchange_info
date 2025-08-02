package com.snoykuo.example.flightinfo.exchange.datasource

class RemoteExchangeRateDataSource(
    private val apiService: FreeCurrencyApiService,
) : ExchangeRateDataSource {

    private var lastUpdated: Long = 0L

    override suspend fun getRates(
        baseCurrency: String,
        currencies: List<String>
    ): Map<String, Double> {
        val response = apiService.getLatestRates(
            baseCurrency = baseCurrency,
            currencies = currencies.joinToString(",")
        )
        lastUpdated = System.currentTimeMillis()

        // Debug Log
        println("RemoteExchangeRateDataSource getRates: $response")

        return response.data
    }

    override suspend fun getLastUpdated(): Long = lastUpdated
}
