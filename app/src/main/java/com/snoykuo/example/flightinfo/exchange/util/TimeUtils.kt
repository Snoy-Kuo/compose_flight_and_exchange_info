package com.snoykuo.example.flightinfo.exchange.util

import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter

fun parseIso8601ToMillis(isoDate: String): Long {
    return try {
        Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(isoDate)).toEpochMilli()
    } catch (e: Exception) {
        0L
    }
}