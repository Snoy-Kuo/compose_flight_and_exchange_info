package com.snoykuo.example.flightinfo.exchange.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.common.util.millisToLocalDateTime
import com.snoykuo.example.flightinfo.exchange.repo.ExchangeRateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime

class ExchangeRateViewModel(
    private val repository: ExchangeRateRepository
) : ViewModel() {

    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val exchangeRates: StateFlow<Map<String, Double>> = _exchangeRates

    private val _baseCurrency = MutableStateFlow("USD")
    val baseCurrency: StateFlow<String> = _baseCurrency

    private val _lastUpdated = MutableStateFlow<LocalDateTime?>(null)
    val lastUpdated: StateFlow<LocalDateTime?> = _lastUpdated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val isEditing = MutableStateFlow(false)

    private val _amount = MutableStateFlow("1")
    val amount: StateFlow<String> = _amount

    private var refreshJob: Job? = null

    fun startExchangeRateAutoRefresh() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            while (isActive) {
                if (!isEditing.value) {
                    fetchRates()
                }
                delay(60_000)
            }
        }
    }

    fun stopExchangeRateAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    private var editingJob: Job? = null

    fun changeAmount(newAmount: String) {
        _amount.value = newAmount
        isEditing.value = true

        editingJob?.cancel()
        editingJob = viewModelScope.launch {
            delay(3000)
            isEditing.value = false
        }
    }

    fun setBaseCurrency(currency: String) {
        _baseCurrency.value = currency
        viewModelScope.launch {
            fetchRates()
        }
    }

    private suspend fun fetchRates() {
        _isLoading.value = true
        _message.value = "載入中..."

        val result = withContext(Dispatchers.IO) {
            repository.getRates(
                _baseCurrency.value,
                listOf("HKD", "USD", "JPY", "CNY", "KRW", "EUR")
            )
        }
        _lastUpdated.value = millisToLocalDateTime(repository.getLastUpdated())

        when (result) {
            is DataResult.Success -> {
                _exchangeRates.value = result.data
                _message.value = null
            }

            is DataResult.Error -> {
                result.backupData?.let {
                    _exchangeRates.value = it
                    _message.value = "網路問題"
                } ?: run {
                    _exchangeRates.value = emptyMap()
                    _message.value = "資料讀取失敗"
                }
            }
        }

        _isLoading.value = false
    }
}


class ExchangeRateViewModelFactory(private val repo: ExchangeRateRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ExchangeRateViewModel(repo) as T
    }
}
