package com.snoykuo.example.flightinfo.exchange.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snoykuo.example.flightinfo.common.ui.theme.FlightInfoTheme
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExchangeRatePage(
    paddings: PaddingValues,
    baseCurrency: String = "USD",
    amount: String = "1",
    rates: Map<String, Double> = emptyMap(),
    error: String? = null,
    lastUpdated: LocalDateTime? = null,
    onBaseCurrencyChange: (String) -> Unit = {},
    onAmountChange: (String) -> Unit = {},
    onCalculateClicked: () -> Unit = {},
    onTargetCurrencyClick: (String) -> Unit = { _ -> }//(currency)
) {
    val currencyList = listOf("USD", "JPY", "KRW", "CNY", "HKD", "EUR")
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    val formattedTime = lastUpdated?.format(formatter) ?: "--"
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(paddings)
            .padding(horizontal = 16.dp)
    ) {

        Row {
            Icon(
                imageVector = Icons.Default.CurrencyExchange,
                contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .padding(horizontal = 5.dp)
            )
            Text(text = "匯率換算", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    // 限制最大長度（避免溢位）
                    if (it.length <= 10) onAmountChange(it)
                },
                label = { Text("金額") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 幣別下拉選單
            var expanded by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(baseCurrency)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Change base currency")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    currencyList.forEach { currency ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = currency,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onBaseCurrencyChange(currency)
                                expanded = false
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onCalculateClicked) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = "Calculator",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val baseAmount = amount.toDoubleOrNull() ?: 1.0

        // 顯示兌換匯率
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 4
            maximumFractionDigits = 4
        }
        currencyList.forEach { currency ->
            if (currency != baseCurrency) {
                val rate = rates[currency]
                val converted = if (rate != null) rate * baseAmount else null
                val displayValue = converted?.let { formatter.format(it) } ?: "N/A"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTargetCurrencyClick(currency) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "=",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Box(
                        modifier = Modifier.weight(2.5f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(text = displayValue)
                    }
                    Text(
                        text = currency,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${if (error == null) "" else "$error;\t"} 資料更新時間：$formattedTime",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true, name = "ExchangeRatePage Preview")
@Composable
fun ExchangeRatePagePreview() {
    val sampleRates = mapOf(
        "USD" to 1.0,
        "JPY" to 147.7809850776,
        "CNY" to 7.1619907837,
        "KRW" to 1381.7567788568,
        "EUR" to 0.8504401663,
        "HKD" to 7.8471813998
    )

    FlightInfoTheme {
        ExchangeRatePage(
            paddings = PaddingValues(all = 16.dp),
            baseCurrency = "USD",
            amount = "1",
            rates = sampleRates,
            lastUpdated = null,
            onBaseCurrencyChange = {},
            onAmountChange = {},
            onCalculateClicked = {},
            onTargetCurrencyClick = { _ -> }
        )
    }
}