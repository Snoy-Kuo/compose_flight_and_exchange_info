package com.snoykuo.example.flightinfo.flight.repo

import com.snoykuo.example.flightinfo.flight.data.AirportName
import com.snoykuo.example.flightinfo.flight.datasource.AirportDataSource
import com.snoykuo.example.flightinfo.flight.datasource.MutableAirportDataSource

class DefaultAirportRepository(
    private val remote: AirportDataSource,
    private val local: MutableAirportDataSource,
    private val fallback: AirportDataSource
) : AirportRepository {

    private var isInitialized = false

    override suspend fun getAirportNameByCode(code: String): AirportName? {
        val sources = listOf(local, fallback) //remote,

        for (source in sources) {
            val name = source.getAllAirports()
                .firstOrNull { it.AirportID == code }
                ?.AirportName

            if (name != null) return name
        }

        return null
    }

    override suspend fun refreshAirports() {
        if (isInitialized) return

        val data = remote.getAllAirports()
        if (data.isNotEmpty()) {
            local.saveAll(data)
        }
        isInitialized = true
    }

    override fun getLastUpdated(): Long {
        return local.getLastUpdated()
    }
}
