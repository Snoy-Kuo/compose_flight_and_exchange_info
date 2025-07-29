package com.snoykuo.example.flightinfo.flight.datasource

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.snoykuo.example.flightinfo.flight.data.FlightInfo
import java.net.URL

object FlightCsvParser {

    private const val CSV_URL =
        "https://www.taoyuan-airport.com/uploads/govdata/FidsPassenger.csv"

    fun fetchFlights(): List<FlightInfo> {
        val inputStream = URL(CSV_URL).openStream()
        val csv: CsvReader = csvReader()

        return csv.readAllWithHeader(inputStream).map { row ->
            FlightInfo(
                terminal = row["航廈"] ?: "",
                type = row["種類"] ?: "",
                airlineCode = row["航空公司代碼"] ?: "",
                airlineName = row["航空公司中文"]?.trim() ?: "",
                flightNumber = row["班次"]?.trim() ?: "",
                gate = row["登機門/機坪"]?.trim() ?: "",
                scheduledDate = row["表訂日期"] ?: "",
                scheduledTime = row["表訂時間"] ?: "",
                estimatedDate = row["預計日期"] ?: "",
                estimatedTime = row["預計時間"] ?: "",
                destinationCode = row["往來地點"] ?: "",
                destinationEn = row["往來地點英文"] ?: "",
                destinationZh = row["往來地點中文"] ?: "",
                status = row["航班狀態"] ?: "",
                aircraftType = row["機型"] ?: ""
            )
        }
    }
}