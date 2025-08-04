package com.snoykuo.example.flightinfo.exchange.ui.page

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalculatorLogicTest {

    @Test
    fun `calculate - addition`() {
        val result = calculate("10", "5.5", '+')
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        assertThat((result as CalculationResult.Success).value).isEqualTo(15.5)
    }

    @Test
    fun `calculate - subtraction`() {
        val result = calculate("10", "5", '-')
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        assertThat((result as CalculationResult.Success).value).isEqualTo(5.0)
    }

    @Test
    fun `calculate - multiplication`() {
        val result = calculate("10", "5", '×')
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        assertThat((result as CalculationResult.Success).value).isEqualTo(50.0)
    }

    @Test
    fun `calculate - division`() {
        val result = calculate("10", "4", '÷')
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        assertThat((result as CalculationResult.Success).value).isEqualTo(2.5)
    }

    @Test
    fun `calculate - division by zero`() {
        val result = calculate("10", "0", '÷')
        assertThat(result).isEqualTo(CalculationResult.DivisionByZeroError)
    }

    @Test
    fun `calculate - invalid number input`() {
        val result = calculate("abc", "5", '+')
        assertThat(result).isEqualTo(CalculationResult.GenericError)
    }

    @Test
    fun `formatForDisplay - integer with thousand separator`() {
        val formatted = formatForDisplay("1234567")
        assertThat(formatted).isEqualTo("1,234,567")
    }

    @Test
    fun `formatForDisplay - decimal with thousand separator`() {
        val formatted = formatForDisplay("12345.6789")
        assertThat(formatted).isEqualTo("12,345.6789")
    }

    @Test
    fun `formatForDisplay - short number`() {
        val formatted = formatForDisplay("123")
        assertThat(formatted).isEqualTo("123")
    }

    @Test
    fun `formatForDisplay - empty string`() {
        val formatted = formatForDisplay("")
        assertThat(formatted).isEqualTo("0")
    }

    @Test
    fun `formatForDisplay - with trailing dot`() {
        val formatted = formatForDisplay("1234.")
        assertThat(formatted).isEqualTo("1,234.")
    }

    @Test
    fun `stripTrailingDot - with dot`() {
        val stripped = stripTrailingDot("123.")
        assertThat(stripped).isEqualTo("123")
    }

    @Test
    fun `stripTrailingDot - without dot`() {
        val stripped = stripTrailingDot("123")
        assertThat(stripped).isEqualTo("123")
    }
}
