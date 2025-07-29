package com.snoykuo.example.flightinfo.exchange.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoykuo.example.flightinfo.common.data.DataResult
import com.snoykuo.example.flightinfo.common.util.millisToLocalDateTime
import com.snoykuo.example.flightinfo.exchange.repo.ExchangeRateRepository
import kotlinx.coroutines.Dispatchers
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 是否正在編輯匯率或基礎幣別
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _amount = MutableStateFlow("1")
    val amount: StateFlow<String> = _amount

    init {
        // 開始自動刷新，但要依 isEditing 狀態控制
        viewModelScope.launch {
            while (isActive) {
                if (!_isEditing.value) {
                    fetchRates()
                    delay(60_000) //1分一次，這頻率，免費的DEMO看來還ok
                }
            }
        }
    }

    // 變更輸入金額（字串型態方便雙向綁定輸入框）
    fun changeAmount(newAmount: String) {
        _amount.value = newAmount
    }

    fun setBaseCurrency(currency: String) {
        _baseCurrency.value = currency
        viewModelScope.launch {
            fetchRates()
        }
    }

    fun setIsEditing(editing: Boolean) {
        _isEditing.value = editing
    }

    private suspend fun fetchRates() {
        _isLoading.value = true

        val result = withContext(Dispatchers.IO) {
            repository.getRates(
                _baseCurrency.value,
                listOf("HKD", "USD", "JPY", "CNY", "KRW", "EUR")
            )
        }
        Log.d("RDTest", "result=$result")
        _lastUpdated.value = millisToLocalDateTime(repository.getLastUpdated())
        when (result) {
            is DataResult.Success -> {
                _exchangeRates.value = result.data
                _error.value = null
            }

            is DataResult.Error -> {
                result.backupData?.let {
                    _exchangeRates.value = result.backupData
                    Log.w("RDTest", " 網路問題${result.error}")
                    _error.value = "網路問題"
                } ?: run {
                    Log.w("RDTest", "資料讀取失敗${result.error}")
                    _error.value = "資料讀取失敗"
                    _exchangeRates.value = emptyMap()
                }
            }
        }
    }
}
