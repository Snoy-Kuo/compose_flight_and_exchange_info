package com.snoykuo.example.flightinfo.common.ui.layout

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Airlines
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snoykuo.example.flightinfo.exchange.datasource.FallbackExchangeRateDataSource
import com.snoykuo.example.flightinfo.exchange.datasource.LocalExchangeRateDataSource
import com.snoykuo.example.flightinfo.exchange.datasource.NetworkModule.exchangeApi
import com.snoykuo.example.flightinfo.exchange.datasource.RemoteExchangeRateDataSource
import com.snoykuo.example.flightinfo.exchange.repo.DefaultExchangeRateRepository
import com.snoykuo.example.flightinfo.exchange.ui.page.CalculatorBottomSheet
import com.snoykuo.example.flightinfo.exchange.ui.page.ExchangeRatePage
import com.snoykuo.example.flightinfo.exchange.viewmodel.ExchangeRateViewModel
import com.snoykuo.example.flightinfo.exchange.viewmodel.ExchangeRateViewModelFactory
import com.snoykuo.example.flightinfo.flight.datasource.FallbackAirportDataSource
import com.snoykuo.example.flightinfo.flight.datasource.FallbackFlightDataSource
import com.snoykuo.example.flightinfo.flight.datasource.LocalAirportDataSource
import com.snoykuo.example.flightinfo.flight.datasource.LocalFlightDataSource
import com.snoykuo.example.flightinfo.flight.datasource.NetworkModule
import com.snoykuo.example.flightinfo.flight.datasource.TdxAirportDataSource
import com.snoykuo.example.flightinfo.flight.datasource.TdxFlightDataSource
import com.snoykuo.example.flightinfo.flight.repo.DefaultAirportRepository
import com.snoykuo.example.flightinfo.flight.repo.DefaultFlightRepository
import com.snoykuo.example.flightinfo.flight.ui.page.FlightPage
import com.snoykuo.example.flightinfo.flight.viewmodel.FlightViewModel
import com.snoykuo.example.flightinfo.flight.viewmodel.FlightViewModelFactory

@SuppressLint("ContextCastToActivity", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainLayout() {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(LocalContext.current as Activity)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isWideScreen = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val useRail = isLandscape && isWideScreen

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    val airportRepo = DefaultAirportRepository(
        remote = TdxAirportDataSource(NetworkModule.airportApi),
        local = LocalAirportDataSource(context),
        fallback = FallbackAirportDataSource(context)
    )

    val flightRepo = remember {
        DefaultFlightRepository(
            remote = TdxFlightDataSource(NetworkModule.flightApi),
            local = LocalFlightDataSource(context),
            fallback = FallbackFlightDataSource(context),
            airportRepo = airportRepo
        )
    }
    val flightViewModel: FlightViewModel = viewModel(
        factory = FlightViewModelFactory(flightRepo)
    )

    val exchangeRepo = remember {
        DefaultExchangeRateRepository(
            remote = RemoteExchangeRateDataSource(exchangeApi),
            local = LocalExchangeRateDataSource(context),
            fallback = FallbackExchangeRateDataSource(context)
        )
    }
    val exchangeRateViewModel: ExchangeRateViewModel = viewModel(
        factory = ExchangeRateViewModelFactory(exchangeRepo)
    )

    val amountExchange by exchangeRateViewModel.amount.collectAsState()
    val showCalculator = remember { mutableStateOf(false) }


    val screens = listOf("航班", "匯率")
    val screenIcons = listOf(
        Icons.Filled.Airlines, // 航班 icon
        Icons.Filled.AttachMoney    // 匯率 icon
    )

    val isDarkTheme = isSystemInDarkTheme()
    SideEffect {
        val controller = WindowCompat.getInsetsController(
            (context as ComponentActivity).window,
            context.window.decorView
        )
        controller.isAppearanceLightStatusBars = !isDarkTheme
        controller.isAppearanceLightNavigationBars = !isDarkTheme
    }

    if (useRail) {
        // 平板或橫向使用 NavigationRail
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            Row(
                Modifier.fillMaxSize()
            ) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    screens.forEachIndexed { index, label ->
                        NavigationRailItem(
                            modifier = Modifier.padding(vertical = 10.dp),
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            icon = {
                                Icon(
                                    screenIcons[index],
                                    contentDescription = screens[index]
                                )
                            },
                            label = { Text(label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    MainContentPage(
                        selectedIndex = selectedIndex,
                        innerPadding = innerPadding,
                        onCalculateClicked = { showCalculator.value = true },
                        flightViewModel = flightViewModel,
                        exchangeRateViewModel = exchangeRateViewModel
                    )
                }
            }
            if (showCalculator.value) {
                CalculatorBottomSheet(
                    initialValue = amountExchange,
                    onSubmit = { exchangeRateViewModel.changeAmount(it) },
                    onDismiss = { showCalculator.value = false }
                )
            }
        }
    } else {
        // 手機直向用 BottomNavigationBar
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    screens.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            icon = {
                                Icon(
                                    screenIcons[index],
                                    contentDescription = screens[index]
                                )
                            },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                MainContentPage(
                    selectedIndex = selectedIndex,
                    innerPadding = paddingValues,
                    onCalculateClicked = { showCalculator.value = true },
                    flightViewModel = flightViewModel,
                    exchangeRateViewModel = exchangeRateViewModel
                )
            }
            if (showCalculator.value) {
                CalculatorBottomSheet(
                    initialValue = amountExchange,
                    onSubmit = { exchangeRateViewModel.changeAmount(it) },
                    onDismiss = { showCalculator.value = false }
                )
            }
        }
    }
}

@Composable
fun MainContentPage(
    selectedIndex: Int,
    innerPadding: PaddingValues,
    flightViewModel: FlightViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    onCalculateClicked: () -> Unit
) {
    val arrivals by flightViewModel.arrivals.collectAsState()
    val departures by flightViewModel.departures.collectAsState()
    val message by flightViewModel.message.collectAsState()
    val lastUpdated by flightViewModel.lastUpdated.collectAsState()
    val isLoading by flightViewModel.isLoading.collectAsState()

    val baseCurrency by exchangeRateViewModel.baseCurrency.collectAsState()
    val amount by exchangeRateViewModel.amount.collectAsState()
    val rates by exchangeRateViewModel.exchangeRates.collectAsState()
    val messageExchange by exchangeRateViewModel.message.collectAsState()
    val isLoadingExchange by exchangeRateViewModel.isLoading.collectAsState()
    val lastUpdatedExchange by exchangeRateViewModel.lastUpdated.collectAsState()

    when (selectedIndex) {
        0 -> {
            PageLifecycleEffect(
                selectedIndex = selectedIndex,
                pageIndex = 0,
                onStart = { flightViewModel.startFlightAutoRefresh() },
                onStop = { flightViewModel.stopFlightAutoRefresh() }
            )
            FlightPage(
                arrivals = arrivals,
                departures = departures,
                message = message,
                lastUpdated = lastUpdated,
                isLoading = isLoading,
                onRetry = { flightViewModel.retryFetch() },
            )
        }

        1 -> {
            PageLifecycleEffect(
                selectedIndex = selectedIndex,
                pageIndex = 1,
                onStart = { exchangeRateViewModel.startExchangeRateAutoRefresh() },
                onStop = { exchangeRateViewModel.stopExchangeRateAutoRefresh() }
            )
            ExchangeRatePage(
                paddings = innerPadding,
                baseCurrency = baseCurrency,
                amount = amount,
                rates = rates,
                isLoading = isLoadingExchange,
                message = messageExchange,
                lastUpdated = lastUpdatedExchange,
                onBaseCurrencyChange = { exchangeRateViewModel.setBaseCurrency(it) },
                onAmountChange = { exchangeRateViewModel.changeAmount(it) },
                onCalculateClicked = onCalculateClicked,
                onTargetCurrencyClick = { exchangeRateViewModel.setBaseCurrency(it) }
            )
        }
    }
}
