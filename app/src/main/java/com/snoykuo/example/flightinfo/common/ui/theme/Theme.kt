package com.snoykuo.example.flightinfo.common.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 明亮模式
val LightColors = lightColorScheme(
    primary = Color(0xFFA2820B),         // 金黃
    onPrimary = Color(0xFFF5F5F5),       // 柔和白字
    secondary = Color(0xFF616161),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E0E0), // 比 secondary 更淺一點
    onSecondaryContainer = Color(0xFF212121),

    tertiary = Color(0xFF424242),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFBDBDBD),
    onTertiaryContainer = Color.Black,

    background = Color(0xFFFFFBF3),
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = Color(0xFF777777),
)

// 暗黑模式
val DarkColors = darkColorScheme(
    primary = Color(0xFFFFDC7C),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF37474F), // 深灰藍容器
    onSecondaryContainer = Color(0xFFECEFF1),

    tertiary = Color(0xFFBDBDBD),
    onTertiary = Color(0xFF212121),
    tertiaryContainer = Color(0xFF424242),
    onTertiaryContainer = Color.White,

    background = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF2C2C2C),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFAAAAAA),
)

@Composable
fun FlightInfoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}