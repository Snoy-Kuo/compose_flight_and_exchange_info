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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snoykuo.example.flightinfo.R
import com.snoykuo.example.flightinfo.common.ui.theme.FlightInfoTheme
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

// A standard formatter for numbers that fit on the screen.
private val standardFormatter: NumberFormat = NumberFormat.getInstance(Locale.US).apply {
    isGroupingUsed = true
    maximumFractionDigits = 16 // Allow ample precision
}

// A dedicated formatter that ALWAYS uses scientific notation (E-notation).
private val scientificFormatter: NumberFormat =
    DecimalFormat("0.##############E0", DecimalFormatSymbols(Locale.US)).apply {
        isGroupingUsed = false
        maximumFractionDigits = 14 // Adjust precision for scientific display
    }

// A threshold to decide when to switch to scientific notation.
// 10^16 is a 17-digit number, which is a good point to switch.
private const val SCIENTIFIC_NOTATION_THRESHOLD = 1e16

// A sealed class to represent the result of a calculation, enabling specific error handling.
private sealed class CalculationResult {
    data class Success(val value: Double) : CalculationResult()
    data object DivisionByZeroError : CalculationResult()
    data object OverflowError : CalculationResult() // For Infinity, -Infinity
    data object GenericError : CalculationResult()
}

/**
 * A pure function to perform a calculation.
 * It expects clean, unformatted numbers.
 * Returns a [CalculationResult] to distinguish between success and specific errors.
 */
private fun calculate(a: String, b: String, op: Char): CalculationResult {
    val num1 = a.toDoubleOrNull()
    val num2 = b.toDoubleOrNull()

    // Generic error if numbers are not valid (should not happen with current UI logic)
    if (num1 == null || num2 == null) return CalculationResult.GenericError

    val result = when (op) {
        '+' -> num1 + num2
        '-' -> num1 - num2
        '×' -> num1 * num2
        '÷' -> if (num2 != 0.0) num1 / num2 else return CalculationResult.DivisionByZeroError
        else -> Double.NaN // Should not be reached
    }

    return if (result.isFinite()) {
        CalculationResult.Success(result)
    } else {
        CalculationResult.OverflowError // Catches Infinity, -Infinity
    }
}

/**
 * Formats a final calculation result.
 * It intelligently chooses between standard and scientific notation based on the number's magnitude.
 */
private fun formatResult(value: Double): String {
    // Explicitly choose the formatter based on the number's magnitude.
    return if (abs(value) >= SCIENTIFIC_NOTATION_THRESHOLD && value.isFinite()) {
        scientificFormatter.format(value)
    } else {
        standardFormatter.format(value)
    }
}

/**
 * Formats a raw number string for real-time display, adding thousand separators.
 * Example: "12345.67" -> "12,345.67"
 */
private fun formatForDisplay(number: String): String {
    if (number.isEmpty()) return "0"
    if (number.contains('E', ignoreCase = true)) return number // Don't format scientific notation

    val parts = number.split('.')
    val integerPart = parts.getOrNull(0) ?: ""
    val fractionalPart = parts.getOrNull(1)


    // Format the integer part, using BigDecimal to handle large numbers safely.
    val formattedInteger = if (integerPart.isNotEmpty()) {
        try {
            standardFormatter.format(BigDecimal(integerPart))
        } catch (_: NumberFormatException) {
            integerPart // Fallback if something goes wrong
        }
    } else {
        "" // Handle cases like ".5"
    }

    return when {
        fractionalPart != null -> "$formattedInteger.$fractionalPart"
        number.endsWith('.') -> "$formattedInteger."
        else -> formattedInteger
    }
}


/**
 * Removes a trailing dot from a string, if it exists.
 */
private fun stripTrailingDot(input: String): String {
    return input.removeSuffix(".")
}

private const val MAX_INPUT_DIGITS = 17
//private const val DISMISS_LABEL = "▼"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorBottomSheet(
    initialValue: String,
    onSubmit: (String) -> Unit,      // Submit the result
    onDismiss: () -> Unit            // Dismiss the sheet
) {
    val modalState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- Internal State ---
    // These values are always pure, raw numbers without formatting.
    var firstNumber by remember { mutableStateOf(initialValue.replace(",", "")) }
    var operator by remember { mutableStateOf<Char?>(null) }
    var secondNumber by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // Get strings from resources for error messages and buttons
    val strDone = stringResource(id = R.string.calculator_done)
    val strErrorDivisionByZero = stringResource(id = R.string.calculator_error_division_by_zero)
    val strErrorOverflow = stringResource(id = R.string.calculator_error_overflow)
    val strErrorGeneric = stringResource(id = R.string.calculator_error_generic)


    // --- Display Logic ---
    // Creates the text to be displayed by formatting the raw internal state.
    val primaryDisplayText = error ?: if (operator == null) {
        formatForDisplay(firstNumber)
    } else {
        formatForDisplay(secondNumber)
    }

    val secondaryDisplayText = if (error != null) {
        ""
    } else {
        operator?.let { "${formatForDisplay(firstNumber)} $it" } ?: ""
    }


    LaunchedEffect(Unit) {
        modalState.show()
    }

    fun onButtonClick(label: String) {
        // Any button press after an error clears the state and starts fresh.
        if (error != null) {
            firstNumber = if (label in "0".."9") label else ""
            operator = null
            secondNumber = ""
            error = null
            if (label !in "0".."9") return
        }

        when (label) {
            in "0".."9" -> {
                if (operator == null) {
                    if (firstNumber.length >= MAX_INPUT_DIGITS) return
                    if (label == "0" && firstNumber == "0") return
                    if (label in "1".."9" && firstNumber == "0") {
                        firstNumber = label
                    } else {
                        firstNumber += label
                    }
                } else {
                    if (secondNumber.length >= MAX_INPUT_DIGITS) return
                    if (label == "0" && secondNumber == "0") return
                    if (label in "1".."9" && secondNumber == "0") {
                        secondNumber = label
                    } else {
                        secondNumber += label
                    }
                }
            }

            "." -> {
                if (operator == null) {
                    if (firstNumber.contains(".")) return
                    if (firstNumber.length >= MAX_INPUT_DIGITS) return
                    firstNumber = if (firstNumber.isEmpty()) "0." else "$firstNumber."
                } else {
                    if (secondNumber.contains(".")) return
                    if (secondNumber.length >= MAX_INPUT_DIGITS) return
                    secondNumber = if (secondNumber.isEmpty()) "0." else "$secondNumber."
                }
            }

            in listOf("+", "-", "×", "÷") -> {
                if (firstNumber.isNotEmpty() && firstNumber != ".") {
                    // If a full expression exists, calculate it before setting the new operator (chaining).
                    val currentOperator = operator
                    if (secondNumber.isNotEmpty() && currentOperator != null) {
                        val num1 = stripTrailingDot(firstNumber)
                        val num2 = stripTrailingDot(secondNumber)
                        when (val result = calculate(num1, num2, currentOperator)) {
                            is CalculationResult.Success -> {
                                val formattedValue = formatResult(result.value)
                                // Keep internal state clean by removing formatting before storing.
                                firstNumber = formattedValue.replace(",", "")
                                secondNumber = ""
                            }

                            is CalculationResult.DivisionByZeroError -> {
                                error = strErrorDivisionByZero; return
                            }

                            is CalculationResult.OverflowError -> {
                                error = strErrorOverflow; return
                            }

                            is CalculationResult.GenericError -> {
                                error = strErrorGeneric; return
                            }
                        }
                    }
                    operator = label[0]
                }
            }

            "=" -> {
                val currentOperator = operator
                if (firstNumber.isNotEmpty() && secondNumber.isNotEmpty() && currentOperator != null) {
                    val num1 = stripTrailingDot(firstNumber)
                    val num2 = stripTrailingDot(secondNumber)
                    when (val result = calculate(num1, num2, currentOperator)) {
                        is CalculationResult.Success -> {
                            val formattedValue = formatResult(result.value)
                            firstNumber = formattedValue.replace(",", "")
                            operator = null
                            secondNumber = ""
                        }

                        is CalculationResult.DivisionByZeroError -> error = strErrorDivisionByZero
                        is CalculationResult.OverflowError -> error = strErrorOverflow
                        is CalculationResult.GenericError -> error = strErrorGeneric
                    }
                }
            }

            "C" -> {
                firstNumber = ""
                operator = null
                secondNumber = ""
                error = null
            }

            strDone -> {
                // If there's a pending calculation, perform it.
                val currentOperator = operator
                if (firstNumber.isNotEmpty() && secondNumber.isNotEmpty() && currentOperator != null) {
                    val num1 = stripTrailingDot(firstNumber)
                    val num2 = stripTrailingDot(secondNumber)
                    when (val result = calculate(num1, num2, currentOperator)) {
                        is CalculationResult.Success -> {
                            val formattedValue = formatResult(result.value)
                            firstNumber = formattedValue.replace(",", "")
                            operator = null
                            secondNumber = ""
                        }

                        is CalculationResult.DivisionByZeroError -> error = strErrorDivisionByZero
                        is CalculationResult.OverflowError -> error = strErrorOverflow
                        is CalculationResult.GenericError -> error = strErrorGeneric
                    }
                }

                // Only submit and dismiss if there is no error.
                if (error == null) {
                    // Submit the raw, clean number.
                    onSubmit(stripTrailingDot(firstNumber))
                    onDismiss()
                }
            }

//            DISMISS_LABEL -> {
//                onDismiss()
//            }
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val sheetModifier = if (isLandscape) {
        Modifier.width(450.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = sheetModifier
    ) {
        CalculatorSheetContent(
            primaryDisplayText = primaryDisplayText,
            secondaryDisplayText = secondaryDisplayText,
            isLandscape = isLandscape,
            onButtonClick = ::onButtonClick
        )
    }
}

/**
 * The stateless content of the calculator sheet.
 * This is stable for previews.
 */
@Composable
internal fun CalculatorSheetContent(
    primaryDisplayText: String,
    secondaryDisplayText: String,
    isLandscape: Boolean,
    onButtonClick: (String) -> Unit
) {
    val contentPadding = PaddingValues(16.dp)

    val strDone = stringResource(id = R.string.calculator_done)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Secondary display (for the expression)
            Text(
                text = secondaryDisplayText,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Primary display (for the current number or result)
            Text(
                text = primaryDisplayText,
                fontSize = 32.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            val buttonsPortrait = listOf(
                listOf("7", "8", "9", "÷"),
                listOf("4", "5", "6", "×"),
                listOf("1", "2", "3", "-"),
                listOf("0", ".", "C", "+"),
                listOf("=", strDone)//, DISMISS_LABEL)
            )
            val buttonsLandscape = listOf(
                listOf("7", "8", "9", "÷", "×"),
                listOf("4", "5", "6", "-", "+"),
                listOf("1", "2", "3", ".", "C"),
                listOf("0", "=", strDone)//, DISMISS_LABEL)
            )
            val buttons = if (isLandscape) buttonsLandscape else buttonsPortrait

            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { label ->
                        val containerColor = when (label) {
                            in "0".."9", "." -> MaterialTheme.colorScheme.secondaryContainer
                            in listOf(
                                "+",
                                "-",
                                "×",
                                "÷",
                                "=",
                                "C"
                            ) -> MaterialTheme.colorScheme.tertiaryContainer

                            strDone -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.primary // Fallback
                        }
                        val textColor = when (label) {
                            in "0".."9", "." -> MaterialTheme.colorScheme.onSecondaryContainer
                            in listOf(
                                "+",
                                "-",
                                "×",
                                "÷",
                                "=",
                                "C"
                            ) -> MaterialTheme.colorScheme.onTertiaryContainer

                            strDone -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onPrimary // Fallback
                        }

                        Button(
                            onClick = { onButtonClick(label) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = containerColor
                            )
                        ) {
                            Text(text = label, fontSize = 20.sp, color = textColor)
                        }
                    }
                }
            }
        }
    }
}


// --- Previews for different configurations ---

@Preview(
    name = "Light Theme - Portrait",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=360dp,height=640dp"
)
@Preview(
    name = "Light Theme - Landscape",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=360dp"
)
@Preview(
    name = "Dark Theme - Portrait",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=360dp,height=640dp"
)
@Preview(
    name = "Dark Theme - Landscape",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=360dp"
)
annotation class DevicePreviews

@DevicePreviews
@Composable
fun CalculatorBottomSheetPreview() {
    FlightInfoTheme {
        // We need to get the orientation from the preview's configuration
        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        // In Preview, apply the same width logic to the wrapping Surface
        val previewModifier = if (isLandscape) {
            Modifier.width(450.dp)
        } else {
            Modifier
        }

        // Preview the stable, stateless content directly
        Surface(modifier = previewModifier) {
            CalculatorSheetContent(
                primaryDisplayText = "12,345.67",
                secondaryDisplayText = "987,654,321 +",
                isLandscape = isLandscape,
                onButtonClick = {}
            )
        }
    }
}
