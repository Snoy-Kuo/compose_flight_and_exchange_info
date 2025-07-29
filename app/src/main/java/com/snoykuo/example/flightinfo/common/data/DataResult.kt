package com.snoykuo.example.flightinfo.common.data

sealed interface DataResult<T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error<T>(val error: Throwable, val backupData: T? = null) : DataResult<T>
}
