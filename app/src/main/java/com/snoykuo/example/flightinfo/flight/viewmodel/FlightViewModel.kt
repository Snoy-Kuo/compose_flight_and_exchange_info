package com.snoykuo.example.flightinfo.flight.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snoykuo.example.flightinfo.R
import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.common.util.UiText
import com.snoykuo.example.flightinfo.common.util.getErrorMessage
import com.snoykuo.example.flightinfo.common.util.millisToLocalDateTime
import com.snoykuo.example.flightinfo.flight.data.FlightInfo
import com.snoykuo.example.flightinfo.flight.repo.FlightRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime

class FlightViewModel(private val repository: FlightRepository) : ViewModel() {

    private val _arrivals = MutableStateFlow<List<FlightInfo>>(emptyList())
    val arrivals: StateFlow<List<FlightInfo>> = _arrivals

    private val _departures = MutableStateFlow<List<FlightInfo>>(emptyList())
    val departures: StateFlow<List<FlightInfo>> = _departures

    private val _message = MutableStateFlow<UiText?>(null)
    val message: StateFlow<UiText?> = _message

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _lastUpdated = MutableStateFlow<LocalDateTime?>(null)
    val lastUpdated: StateFlow<LocalDateTime?> = _lastUpdated

    private var refreshJob: Job? = null

    fun startFlightAutoRefresh() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            while (isActive) {
                fetchFlightData()
                delay(600_000) //10分一次，這頻率，免費的還是會用完，每IP每天20次，想好好使用請申請TDX API Key囉
            }
        }
    }

    fun stopFlightAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    private suspend fun fetchFlightData() {
        _isLoading.value = true
        _message.value = UiText.StringResource(R.string.loading)
        val startTime = System.currentTimeMillis()

        try {
            val result = withContext(Dispatchers.IO) {
                repository.getFlights()
            }
            processResult(result)
        } catch (e: Exception) {
            Log.e("FlightViewModel", "An unexpected error occurred", e)
            _message.value = UiText.StringResource(R.string.error_unknown)
            _arrivals.value = emptyList()
            _departures.value = emptyList()
        } finally {
            val elapsed = System.currentTimeMillis() - startTime
            val remaining = 1000L - elapsed
            if (remaining > 0) {
                delay(remaining)
            }
            _isLoading.value = false
        }
    }

    private suspend fun processResult(result: DataResult<List<FlightInfo>>) {
        when (result) {
            is DataResult.Success -> {
                updateFlights(result.data)
                _message.value = null
            }

            is DataResult.Error -> {
                result.backupData?.let { updateFlights(it) } ?: run {
                    _arrivals.value = emptyList()
                    _departures.value = emptyList()
                }

                _message.value = if (result.backupData == null) {
                    UiText.StringResource(R.string.error_loading_failed)
                } else {
                    getErrorMessage(result.error)
                }
            }
        }
        _lastUpdated.value = millisToLocalDateTime(repository.getLastUpdated())
    }

    private fun updateFlights(flights: List<FlightInfo>) {
        val sorted = flights.sortedByDescending { it.scheduledDateTime() }
        _arrivals.value = sorted.filter { it.type == "A" }
        _departures.value = sorted.filter { it.type == "D" }
    }

    fun retryFetch() {
        viewModelScope.launch {
            fetchFlightData()
        }
    }
}

class FlightViewModelFactory(private val repo: FlightRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FlightViewModel(repo) as T
    }
}
