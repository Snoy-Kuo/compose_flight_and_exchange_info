package com.snoykuo.example.flightinfo.flight.data

import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter


@Serializable
data class FlightInfo(
    val terminal: String,
    val type: String,
    val airlineCode: String,
    val airlineName: String,
    val flightNumber: String,
    val gate: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val estimatedDate: String,
    val estimatedTime: String,
    val destinationCode: String,
    val destinationEn: String,
    val destinationZh: String,
    val status: String,
    val aircraftType: String
) {
    fun scheduledDateTime(): LocalDateTime {
        val withSeconds = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val withoutSeconds = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
        val withoutTime = DateTimeFormatter.ofPattern("yyyy/MM/dd")
//        Log.d("RDTest", "scheduledDate=$scheduledDate, scheduledTime=$scheduledTime")
        return when {
            (scheduledTime.length == 8) -> {
                LocalDateTime.parse("$scheduledDate $scheduledTime", withSeconds)
            }

            (scheduledTime.length == 5) -> {
                LocalDateTime.parse("$scheduledDate $scheduledTime", withoutSeconds)
            }

            else -> {
                LocalDateTime.parse("$scheduledDate $scheduledTime", withoutTime)
            }
        }
    }
}
