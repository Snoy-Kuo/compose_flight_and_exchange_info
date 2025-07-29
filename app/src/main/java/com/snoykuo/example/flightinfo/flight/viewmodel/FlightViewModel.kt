package com.snoykuo.example.flightinfo.flight.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.common.util.millisToLocalDateTime
import com.snoykuo.example.flightinfo.flight.data.FlightInfo
import com.snoykuo.example.flightinfo.flight.repo.FlightRepository
import kotlinx.coroutines.Dispatchers
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _lastUpdated = MutableStateFlow<LocalDateTime?>(null)
    val lastUpdated: StateFlow<LocalDateTime?> = _lastUpdated

    init {
        startFlightAutoRefresh()
    }

    private fun startFlightAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                fetchFlightData()
                delay(600_000) //10分一次，這頻率，免費的還是會用完，每IP每天20次，想好好使用請申請TDX API Key囉
            }
        }
    }

    private suspend fun fetchFlightData() {
        val result = withContext(Dispatchers.IO) {
            repository.getFlights()
        }
        Log.d("RDTest", "result=$result")
        _lastUpdated.value = millisToLocalDateTime(repository.getLastUpdated())
        when (result) {
            is DataResult.Success -> {
                updateFlights(result.data)
                _error.value = null
            }

            is DataResult.Error -> {
                result.backupData?.let {
                    updateFlights(it)
                    Log.w("RDTest", " 網路問題${result.error}")
                    _error.value = "網路問題"
                } ?: run {
                    Log.w("RDTest", "資料讀取失敗${result.error}")
                    _error.value = "資料讀取失敗"
                    _arrivals.value = emptyList()
                    _departures.value = emptyList()
                }
            }
        }
    }

    private fun updateFlights(flights: List<FlightInfo>) {
        val sorted = flights.sortedByDescending { it.scheduledDateTime() }
        _arrivals.value = sorted.filter { it.type == "A" }
        _departures.value = sorted.filter { it.type == "D" }
    }

    fun retryFetch() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchFlightData()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
