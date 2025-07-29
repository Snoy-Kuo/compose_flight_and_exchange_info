package com.snoykuo.example.flightinfo.exchange.ui.page

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorBottomSheet(
    initialValue: String,
    onSubmit: (String) -> Unit,      // 按「算完」送出結果
    onDismiss: () -> Unit            // 按「▼」或外部點擊關閉
) {
    val modalState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var firstNumber by remember { mutableStateOf(initialValue) }
    var operator by remember { mutableStateOf<Char?>(null) }
    var secondNumber by remember { mutableStateOf("") }

    var displayText by remember {
        mutableStateOf(initialValue)
    }

    LaunchedEffect(Unit) {
        modalState.show()
    }

    fun calculate(a: String, b: String, op: Char): Double {
        val num1 = a.toDoubleOrNull() ?: 0.0
        val num2 = b.toDoubleOrNull() ?: 0.0
        return when (op) {
            '+' -> num1 + num2
            '-' -> num1 - num2
            '*' -> num1 * num2
            '/' -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> 0.0
        }
    }

    fun updateDisplay() {
        displayText = buildString {
            append(firstNumber)
            if (operator != null) append(operator)
            append(secondNumber)
        }
    }

    fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            value.toString()
        }
    }

    fun stripTrailingDot(input: String): String {
        return if (input.endsWith(".")) input.dropLast(1) else input
    }

    fun onButtonClick(label: String) {
        // 防止 NaN 後繼續輸入
        if (firstNumber == "NaN") {
            firstNumber = ""
            operator = null
            secondNumber = ""
            displayText = ""
        }

        when (label) {
            in "0".."9", "." -> {
                if (operator == null) {
                    if (label == "." && firstNumber.contains(".")) return
                    if (label == "0" && firstNumber == "0") return
                    if (label in "1".."9" && firstNumber == "0") {
                        firstNumber = label
                    } else {
                        firstNumber += label
                    }
                } else {
                    if (label == "." && secondNumber.contains(".")) return
                    if (label == "0" && secondNumber == "0") return
                    if (label in "1".."9" && secondNumber == "0") {
                        secondNumber = label
                    } else {
                        secondNumber += label
                    }
                }
                updateDisplay()
            }

            in listOf("+", "-", "*", "/") -> {
                if (firstNumber.isNotEmpty()) {
                    if (secondNumber.isNotEmpty() && operator != null) {
                        firstNumber = stripTrailingDot(firstNumber)
                        secondNumber = stripTrailingDot(secondNumber)
                        val result = calculate(firstNumber, secondNumber, operator!!)
                        firstNumber = formatResult(result)
                        secondNumber = ""
                    }
                    operator = label[0]
                    updateDisplay()
                }
            }

            "=" -> {
                if (firstNumber.isNotEmpty() && secondNumber.isNotEmpty() && operator != null) {
                    firstNumber = stripTrailingDot(firstNumber)
                    secondNumber = stripTrailingDot(secondNumber)
                    val result = calculate(firstNumber, secondNumber, operator!!)
                    firstNumber = formatResult(result)
                    operator = null
                    secondNumber = ""
                    updateDisplay()
                }
            }

            "C" -> {
                firstNumber = ""
                operator = null
                secondNumber = ""
                displayText = ""
            }

            "算完" -> {
                firstNumber = stripTrailingDot(firstNumber)
                secondNumber = stripTrailingDot(secondNumber)

                if (firstNumber.isNotEmpty() && secondNumber.isNotEmpty() && operator != null) {
                    val result = calculate(firstNumber, secondNumber, operator!!)
                    firstNumber = formatResult(result)
                    operator = null
                    secondNumber = ""
                    updateDisplay()
                }
                onSubmit(firstNumber)
                onDismiss()
            }

            "▼" -> {
                onDismiss()
            }
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

// Landscape 時兩邊各縮 32.dp，portrait 則正常 padding
    val contentPadding = if (isLandscape) PaddingValues(horizontal = 48.dp, vertical = 16.dp)
    else PaddingValues(16.dp)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // 顯示當前輸入內容或結果
                Text(
                    text = displayText,
                    fontSize = 32.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // 鍵盤配置依方向不同
                val buttonsPortrait = listOf(
                    listOf("7", "8", "9", "/"),
                    listOf("4", "5", "6", "*"),
                    listOf("1", "2", "3", "-"),
                    listOf("0", ".", "C", "+"),
                    listOf("=", "算完", "▼")
                )

                val buttonsLandscape = listOf(
                    listOf("7", "8", "9", "/", "*"),
                    listOf("4", "5", "6", "-", "+"),
                    listOf("1", "2", "3", ".", "C"),
                    listOf("0", "=", "算完", "▼")
                )

                val buttons = if (isLandscape) buttonsLandscape else buttonsPortrait

                // 鍵盤渲染
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { label ->
                            Button(
                                onClick = { onButtonClick(label) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .height(48.dp)
                            ) {
                                Text(label, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun CalculatorBottomSheetPreview() {
    CalculatorBottomSheet(
        initialValue = "123.45",
        onSubmit = {},
        onDismiss = {}
    )
}