package com.snoykuo.example.flightinfo.exchange.data

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    val data: Map<String, Double>,
)

@Serializable
data class ExchangeRateResponseWrapper(
    val lastUpdated: String,
    val value: ExchangeRateResponse
)