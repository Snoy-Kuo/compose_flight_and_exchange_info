package com.snoykuo.example.flightinfo.exchange.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime

class TimeUtilsTest {

    @Test
    fun `parseIso8601ToMillis with valid ISO 8601 date-time string and offset`() {
        val isoDate = "2023-10-27T10:15:30+01:00"
        // Changed to use OffsetDateTime for calculating expectedMillis
        val expectedMillis = OffsetDateTime.parse(isoDate).toInstant().toEpochMilli()
        assertEquals(expectedMillis, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with valid ISO 8601 date-time string with Z (UTC)`() {
        val isoDate = "2023-10-27T10:15:30Z"
        // Instant.parse should be fine for 'Z' (UTC) format
        val expectedMillis = Instant.parse(isoDate).toEpochMilli()
        assertEquals(expectedMillis, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with valid ISO 8601 date-time string with milliseconds`() {
        val isoDate = "2023-10-27T10:15:30.123Z"
        val expectedMillis = Instant.parse(isoDate).toEpochMilli()
        assertEquals(expectedMillis, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with invalid date-time format`() {
        val isoDate = "2023/10/27 10:15:30"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with another invalid date-time format`() {
        val isoDate = "invalid-date"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with empty string input`() {
        val isoDate = ""
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with date-time string with only date part`() {
        val isoDate = "2023-10-27"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with invalid month component`() {
        val isoDate = "2023-13-01T10:00:00Z"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with invalid day component`() {
        val isoDate = "2023-10-32T10:00:00Z"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with invalid hour component`() {
        val isoDate = "2023-10-27T25:00:00Z"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with invalid minute component`() {
        val isoDate = "2023-10-27T10:60:00Z"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with invalid timezone offset`() {
        val isoDate = "2023-10-27T10:15:30+25:00" // Invalid hour offset
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with very old date`() {
        val isoDate = "0001-01-01T00:00:00Z"
        // Instant.parse should be fine for 'Z' format
        val expectedMillis = Instant.parse(isoDate).toEpochMilli()
        assertEquals(expectedMillis, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with very future date`() {
        val isoDate = "9999-12-31T23:59:59Z"
        val expectedMillis = Instant.parse(isoDate).toEpochMilli()
        assertEquals(expectedMillis, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with leap year date February 29th`() {
        val isoDate = "2024-02-29T12:00:00Z"
        val expectedMillis = Instant.parse(isoDate).toEpochMilli()
        assertEquals(expectedMillis, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with non-leap year February 29th (invalid date)`() {
        val isoDate = "2023-02-29T12:00:00Z"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with string with leading whitespace`() {
        val isoDate = " 2023-10-27T10:15:30Z"
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }

    @Test
    fun `parseIso8601ToMillis with string with trailing whitespace`() {
        val isoDate = "2023-10-27T10:15:30Z "
        assertEquals(0L, parseIso8601ToMillis(isoDate))
    }
}
