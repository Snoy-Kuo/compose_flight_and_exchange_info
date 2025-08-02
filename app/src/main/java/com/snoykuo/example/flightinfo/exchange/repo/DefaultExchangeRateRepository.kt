package com.snoykuo.example.flightinfo.exchange.repo

import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.exchange.datasource.ExchangeRateDataSource
import com.snoykuo.example.flightinfo.exchange.datasource.MutableExchangeRateDataSource

class DefaultExchangeRateRepository(
    private val remote: ExchangeRateDataSource,
    private val local: MutableExchangeRateDataSource,
    private val fallback: ExchangeRateDataSource
) : ExchangeRateRepository {
    override suspend fun getRates(
        baseCurrency: String,
        currencies: List<String>
    ): DataResult<Map<String, Double>> {

        // 1. 先嘗試 remote
        val remoteError = try {
            val rates = remote.getRates(baseCurrency, currencies)
            local.saveRates(rates, remote.getLastUpdated())
            return DataResult.Success(rates)
        } catch (e: Exception) {
            e
        }

        // 2. Remote 失敗，  試 local 快取
        val cached = local.getRates(baseCurrency, currencies)
        if (cached.isNotEmpty()) {
            return DataResult.Error(error = remoteError, backupData = cached)
        }

        // 3. Local 也沒資料， fallback
        val fallbackData = fallback.getRates(baseCurrency, currencies)
        if (fallbackData.isNotEmpty()) {
            return DataResult.Error(error = remoteError, backupData = fallbackData)
        }

        // 4. 全部失敗
        return DataResult.Error(error = remoteError)
    }

    override suspend fun getLastUpdated(): Long {
        val localUpdated = local.getLastUpdated()
        if (localUpdated > 0) return localUpdated
        return fallback.getLastUpdated()
    }
}
