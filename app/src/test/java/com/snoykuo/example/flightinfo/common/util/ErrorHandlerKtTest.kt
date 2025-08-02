package com.snoykuo.example.flightinfo.common.util

import com.snoykuo.example.flightinfo.R
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorHandlerKtTest {

    private fun createHttpException(code: Int): HttpException {
        // Note: As per Retrofit's implementation, `code` must be >= 400.
        val errorResponse = Response.error<Any>(code, "".toResponseBody(null))
        return HttpException(errorResponse)
    }

    @Test
    fun `given HttpException with 404, when getErrorMessage, then returns Not Found error`() {
        val exception = createHttpException(404)
        val result = getErrorMessage(exception)
        assertEquals(UiText.StringResource(R.string.error_not_found), result)
    }

    @Test
    fun `given HttpException with 429, when getErrorMessage, then returns Rate Limit error`() {
        val exception = createHttpException(429)
        val result = getErrorMessage(exception)
        assertEquals(UiText.StringResource(R.string.error_rate_limit), result)
    }

    @Test
    fun `given HttpException with 500-599 range, when getErrorMessage, then returns Server error`() {
        // Test lower bound
        assertEquals(
            UiText.StringResource(R.string.error_server),
            getErrorMessage(createHttpException(500))
        )
        // Test upper bound
        assertEquals(
            UiText.StringResource(R.string.error_server),
            getErrorMessage(createHttpException(599))
        )
        // Test a value in between
        assertEquals(
            UiText.StringResource(R.string.error_server),
            getErrorMessage(createHttpException(550))
        )
    }

    @Test
    fun `given client HttpException with other code, when getErrorMessage, then returns FormattedString`() {
        val exception = createHttpException(400)
        val result = getErrorMessage(exception)
        assertEquals(UiText.FormattedString(R.string.error_network_with_code, 400), result)
    }

    @Test
    fun `given server HttpException outside 500-599, when getErrorMessage, then returns FormattedString`() {
        val exception = createHttpException(600)
        val result = getErrorMessage(exception)
        assertEquals(UiText.FormattedString(R.string.error_network_with_code, 600), result)
    }

    @Test
    fun `given IOException, when getErrorMessage, then returns Network Connection error`() {
        val exception = IOException("No connection")
        val result = getErrorMessage(exception)
        assertEquals(UiText.StringResource(R.string.error_network_connection), result)
    }

    @Test
    fun `given JSONException, when getErrorMessage, then returns Parsing error`() {
        val exception = JSONException("Malformed JSON")
        val result = getErrorMessage(exception)
        assertEquals(UiText.StringResource(R.string.error_parsing), result)
    }

    @Test
    fun `given an unknown Throwable, when getErrorMessage, then returns Unknown error`() {
        val exception = NullPointerException("Something was null")
        val result = getErrorMessage(exception)
        assertEquals(UiText.StringResource(R.string.error_unknown), result)
    }

    @Test
    fun `given a custom HttpException subclass, when getErrorMessage, then handles it as base HttpException`() {
        class CustomHttpException(code: Int) : HttpException(Response.error<Any>(code, "".toResponseBody(null)))
        val exception = CustomHttpException(404)
        val result = getErrorMessage(exception)
        assertEquals(UiText.StringResource(R.string.error_not_found), result)
    }

    // Tests for negative and zero error codes were removed because Response.error()
    // throws an IllegalArgumentException for codes < 400. This makes these scenarios
    // impossible to test with a real HttpException and unrealistic in a live Retrofit environment.
}
