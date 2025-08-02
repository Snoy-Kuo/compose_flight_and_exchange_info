package com.snoykuo.example.flightinfo.flight.repo

import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.flight.data.FlightInfo
import com.snoykuo.example.flightinfo.flight.datasource.FlightDataSource
import com.snoykuo.example.flightinfo.flight.datasource.MutableFlightDataSource
import retrofit2.HttpException
import java.io.IOException

class DefaultFlightRepository(
    private val remote: FlightDataSource,
    private val local: MutableFlightDataSource,
    private val fallback: FlightDataSource,
    private val airportRepo: AirportRepository?
) : FlightRepository {

    override suspend fun getFlights(): DataResult<List<FlightInfo>> {

        // 1. 先嘗試 remote
        val remoteError = try {
            airportRepo?.refreshAirports()
            val flights = remote.getFlights()
            local.saveAll(flights)
            val enriched = enrichFlights(flights)
            return DataResult.Success(enriched)
        } catch (e: Exception) {
            e
        }

        // 2. Remote 失敗，  試 local 快取
        val cached = local.getFlights()
        if (cached.isNotEmpty()) {
            val enriched = enrichFlights(cached)
            return DataResult.Error(error = remoteError, backupData = enriched)
        }

        // 3. Local 也沒資料， fallback
        val fallbackData = fallback.getFlights()
        if (fallbackData.isNotEmpty()) {
            val enriched = enrichFlights(fallbackData)
            return DataResult.Error(error = remoteError, backupData = enriched)
        }

        // 4. 全部失敗
        return DataResult.Error(error = remoteError)
    }

    override suspend fun getLastUpdated(): Long {
        val localUpdated = local.getLastUpdated()
        if (localUpdated > 0) return localUpdated
        return fallback.getLastUpdated()
    }


    private suspend fun enrichFlights(flights: List<FlightInfo>): List<FlightInfo> = flights.map {
        enrichFlight(it)
    }

    private suspend fun enrichFlight(flight: FlightInfo): FlightInfo {
        val name = airportRepo?.getAirportNameByCode(flight.destinationCode)
        return flight.copy(
            destinationEn = name?.en ?: flight.destinationCode,
            destinationZh = name?.zh ?: flight.destinationCode
        )
    }
}
