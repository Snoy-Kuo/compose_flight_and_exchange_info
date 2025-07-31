package com.snoykuo.example.flightinfo.flight.ui.page

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun RotatingRefreshIcon(isLoading: Boolean) {
    val transition = rememberInfiniteTransition(label = "refresh_anim")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotate"
    )

    Icon(
        Icons.Default.Refresh,
        contentDescription = if (isLoading) "Loading" else "Retry",
        modifier = if (isLoading) Modifier.rotate(rotation) else Modifier,
        tint = MaterialTheme.colorScheme.onBackground
    )
}