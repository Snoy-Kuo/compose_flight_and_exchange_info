package com.snoykuo.example.flightinfo.exchange.datasource

import com.snoykuo.example.flightinfo.BuildConfig
import com.snoykuo.example.flightinfo.exchange.data.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeCurrencyApiService {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("apikey") apiKey: String = BuildConfig.FREE_CURRENCY_API_KEY,
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") currencies: String
    ): ExchangeRateResponse
}