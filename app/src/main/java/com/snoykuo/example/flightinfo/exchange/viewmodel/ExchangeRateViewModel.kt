package com.snoykuo.example.flightinfo.exchange.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snoykuo.example.flightinfo.R
import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.common.util.UiText
import com.snoykuo.example.flightinfo.common.util.getErrorMessage
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

    private val _message = MutableStateFlow<UiText?>(null)
    val message: StateFlow<UiText?> = _message

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
        _message.value = UiText.StringResource(R.string.loading)

        try {
            val result = withContext(Dispatchers.IO) {
                repository.getRates(_baseCurrency.value, TARGET_CURRENCIES)
            }

            processResult(result)

        } catch (e: Exception) {
            Log.e("ExchangeRateViewModel", "An unexpected error occurred", e)
            _message.value = UiText.StringResource(R.string.error_unknown)
            _exchangeRates.value = emptyMap()
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun processResult(result: DataResult<Map<String, Double>>) {
        when (result) {
            is DataResult.Success -> {
                _exchangeRates.value = result.data
                _message.value = null
            }

            is DataResult.Error -> {
                _exchangeRates.value = result.backupData ?: emptyMap()

                _message.value = if (result.backupData == null) {
                    UiText.StringResource(R.string.error_loading_failed)
                } else {
                    getErrorMessage(result.error)
                }
            }
        }
        _lastUpdated.value = millisToLocalDateTime(repository.getLastUpdated())
    }

    companion object {
        private val TARGET_CURRENCIES = listOf("HKD", "USD", "JPY", "CNY", "KRW", "EUR")
    }
}


class ExchangeRateViewModelFactory(private val repo: ExchangeRateRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ExchangeRateViewModel(repo) as T
    }
}
