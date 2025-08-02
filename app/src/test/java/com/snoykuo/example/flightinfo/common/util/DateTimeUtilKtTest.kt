package com.snoykuo.example.flightinfo.common.util

import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import org.threeten.bp.zone.TzdbZoneRulesProvider
import org.threeten.bp.zone.ZoneRulesProvider
import java.util.TimeZone

class DateTimeUtilKtTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun initZoneRules() {
            try {
                ZoneRulesProvider.getAvailableZoneIds()
            } catch (_: Exception) {
                ZoneRulesProvider.registerProvider(TzdbZoneRulesProvider())
            }
        }
    }

    @Test
    fun `millisToLocalDateTime with zero milliseconds`() {
        val millis = 0L
        val expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        assertEquals(expected, millisToLocalDateTime(millis))
    }

    @Test
    fun `millisToLocalDateTime with positive milliseconds`() {
        val millis = 1678886400000L
        val expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        assertEquals(expected, millisToLocalDateTime(millis))
    }

    @Test
    fun `millisToLocalDateTime with negative milliseconds`() {
        val millis = -1L
        val expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        assertEquals(expected, millisToLocalDateTime(millis))
    }

    @Test
    fun `millisToLocalDateTime with Long MAX VALUE`() {
        val millis = Long.MAX_VALUE
        val expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        assertEquals(expected, millisToLocalDateTime(millis))
    }

    @Test
    fun `millisToLocalDateTime with Long MIN VALUE`() {
        val millis = Long.MIN_VALUE
        val expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        assertEquals(expected, millisToLocalDateTime(millis))
    }

    @Test
    fun `millisToLocalDateTime at midnight`() {
        val specificDateAtMidnight = LocalDateTime.of(2023, Month.OCTOBER, 27, 0, 0, 0)
        val millis =
            specificDateAtMidnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals(specificDateAtMidnight, millisToLocalDateTime(millis))
    }

    @Test
    fun `millisToLocalDateTime across daylight saving time changes`() {
        val testZone = ZoneId.of("America/New_York")
        val originalDefaultTimeZone = TimeZone.getDefault()

        try {
            TimeZone.setDefault(TimeZone.getTimeZone(testZone.id))

            val winterDateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0)
            val summerDateTime = LocalDateTime.of(2024, Month.JULY, 1, 10, 0)

            val winterMillis = winterDateTime.atZone(testZone).toInstant().toEpochMilli()
            val summerMillis = summerDateTime.atZone(testZone).toInstant().toEpochMilli()

            assertEquals(winterDateTime, millisToLocalDateTime(winterMillis))
            assertEquals(summerDateTime, millisToLocalDateTime(summerMillis))

        } finally {
            TimeZone.setDefault(originalDefaultTimeZone)
        }
    }


    @Test
    fun `millisToLocalDateTime at a leap second`() {
        val millisNearLeap = Instant.parse("2016-12-31T23:59:59Z").toEpochMilli()
        val expected =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(millisNearLeap), ZoneId.systemDefault())
        assertEquals(expected, millisToLocalDateTime(millisNearLeap))
    }

    @Test
    fun `millisToLocalDateTime with different system default time zones`() {
        val originalDefaultTimeZone = TimeZone.getDefault()
        try {
            val millis = 1678886400000L // A fixed point in time: 2023-03-15 13:20:00 UTC

            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
            val expectedInUtc = LocalDateTime.of(2023, Month.MARCH, 15, 13, 20, 0)
            assertEquals(expectedInUtc, millisToLocalDateTime(millis))

            TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))
            val expectedInNewYork = LocalDateTime.of(2023, Month.MARCH, 15, 9, 20, 0)
            assertEquals(expectedInNewYork, millisToLocalDateTime(millis))

        } finally {
            TimeZone.setDefault(originalDefaultTimeZone)
        }
    }
}
