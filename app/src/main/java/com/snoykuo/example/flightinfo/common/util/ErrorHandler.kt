package com.snoykuo.example.flightinfo.common.util

import com.snoykuo.example.flightinfo.R
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONException

fun getErrorMessage(error: Throwable): UiText {
    return when (error) {
        is HttpException -> when (error.code()) {
            404 -> UiText.StringResource(R.string.error_not_found)
            429 -> UiText.StringResource(R.string.error_rate_limit)
            in 500..599 -> UiText.StringResource(R.string.error_server)
            else -> UiText.FormattedString(R.string.error_network_with_code, error.code())
        }
        is IOException -> UiText.StringResource(R.string.error_network_connection)
        is JSONException -> UiText.StringResource(R.string.error_parsing)
        else -> UiText.StringResource(R.string.error_unknown)
    }
}
