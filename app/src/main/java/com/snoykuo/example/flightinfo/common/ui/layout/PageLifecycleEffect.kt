package com.snoykuo.example.flightinfo.common.ui.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun PageLifecycleEffect(
    selectedIndex: Int,
    pageIndex: Int,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // 當頁面 tab 被選到時啟動，否則停止
    DisposableEffect(selectedIndex) {
        if (selectedIndex == pageIndex) onStart()
        else onStop()

        onDispose {
            if (selectedIndex == pageIndex) onStop()
        }
    }

    // App 回到前景時，若該頁仍顯示中，就重啟
    DisposableEffect(lifecycleOwner, selectedIndex) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> if (selectedIndex == pageIndex) onStart()
                Lifecycle.Event.ON_PAUSE -> if (selectedIndex == pageIndex) onStop()
                else -> {}
            }
        }

        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
