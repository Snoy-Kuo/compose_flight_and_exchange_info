package com.snoykuo.example.flightinfo.exchange.util

import org.threeten.bp.OffsetDateTime

fun parseIso8601ToMillis(isoDate: String): Long {
    return try {
        OffsetDateTime.parse(isoDate).toInstant().toEpochMilli() // Changed to use OffsetDateTime
    } catch (_: Exception) {
        0L
    }
}
