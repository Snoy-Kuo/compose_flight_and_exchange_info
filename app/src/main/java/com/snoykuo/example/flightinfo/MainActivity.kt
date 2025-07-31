package com.snoykuo.example.flightinfo

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.snoykuo.example.flightinfo.common.ui.layout.MainLayout
import com.snoykuo.example.flightinfo.common.ui.theme.FlightInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
//        window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        enableEdgeToEdge()
        setContent {
            FlightInfoTheme {
                val winBgColor = MaterialTheme.colorScheme.background
                LaunchedEffect(Unit) {
                    window.setBackgroundDrawable(winBgColor.toArgb().toDrawable())
                }
                MainLayout()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setSystemBarsVisibility(show = false)
        } else {
            setSystemBarsVisibility(show = true)
        }
    }
}

fun Activity.setSystemBarsVisibility(show: Boolean) {
    val window = this.window
    val controller = WindowCompat.getInsetsController(window, window.decorView)

    WindowCompat.setDecorFitsSystemWindows(window, !show)

    if (show) {
        controller.show(WindowInsetsCompat.Type.systemBars())
    } else {
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
