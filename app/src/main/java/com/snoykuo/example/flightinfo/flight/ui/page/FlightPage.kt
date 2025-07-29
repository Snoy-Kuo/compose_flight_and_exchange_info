package com.snoykuo.example.flightinfo.flight.ui.page

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.snoykuo.example.flightinfo.common.ui.theme.FlightInfoTheme
import com.snoykuo.example.flightinfo.flight.data.FlightInfo
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightPage(
    arrivals: List<FlightInfo>,
    departures: List<FlightInfo>,
    error: String? = null,
    lastUpdated: LocalDateTime? = null,
    isLoading: Boolean = false,
    onRetry: () -> Unit
) {
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val topBarHeight = if (isLandscape) 50.dp else 70.dp // 自訂高度

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(topBarHeight),
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Flight,
                            contentDescription = null,
                            modifier = Modifier
                                .height(30.dp)
                                .padding(horizontal = 5.dp)
                        )
                        Text(text = "航班資訊", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onRetry,
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Loading",
                                modifier = Modifier.rotate(rotation),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh, contentDescription = "Retry",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            val formattedTime = lastUpdated?.format(formatter) ?: "--"
            Text(
                text = "${if (error == null) "" else "$error;\t"} 資料更新時間：$formattedTime",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    ) { paddingValues ->
        ResponsiveTabPager(
            tabTitles = listOf("抵達", "起飛"),
            arrivals = arrivals,
            departures = departures,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topBarHeight,
                    start = paddingValues.calculateLeftPadding(
                        LayoutDirection.Ltr
                    ),
                    end = paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding()
                )
        )
    }
}

@Composable
fun ResponsiveTabPager(
    modifier: Modifier = Modifier,
    tabTitles: List<String>,
    arrivals: List<FlightInfo>,
    departures: List<FlightInfo>,
    pagerState: PagerState = rememberPagerState { 2 }
) {
    val scope = rememberCoroutineScope()
    val isWideScreen = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val backgroundColor = MaterialTheme.colorScheme.surface
    val backgroundColorLand = MaterialTheme.colorScheme.background

    if (isWideScreen) {
        Row(
            modifier = modifier
                .padding(horizontal = 0.dp)
        )
        {
            NavigationRail(
                containerColor = backgroundColorLand
            ) {
                tabTitles.forEachIndexed { index, title ->
                    NavigationRailItem(
                        modifier = Modifier.padding(vertical = 10.dp),
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.FlightLand else Icons.Default.FlightTakeoff,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = selectedColor,
                            unselectedIconColor = unselectedColor,
                            selectedTextColor = selectedColor.copy(alpha = 0.5f),
                            unselectedTextColor = unselectedColor,
                            indicatorColor = selectedColor.copy(alpha = 0.1f)
                        )
                    )
                }
            }

            FlightPagerContent(
                pagerState = pagerState,
                arrivals = arrivals,
                departures = departures,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = backgroundColor,
                contentColor = selectedColor,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .height(3.dp),
                        color = selectedColor.copy(alpha = 1f)
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selectedContentColor = selectedColor,
                        unselectedContentColor = unselectedColor,
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (index == pagerState.currentPage) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            FlightPagerContent(
                pagerState = pagerState,
                arrivals = arrivals,
                departures = departures,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun FlightPagerContent(
    pagerState: PagerState,
    arrivals: List<FlightInfo>,
    departures: List<FlightInfo>,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        when (page) {
            0 -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(arrivals) { flight -> FlightRow(flight) }
            }

            1 -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(departures) { flight -> FlightRow(flight) }
            }
        }
    }
}


@Preview(showBackground = true, name = "FlightPage Preview")
@Composable
fun FlightPagePreview() {
    val arrivals = emptyList<FlightInfo>()
    val departures = emptyList<FlightInfo>()
    FlightInfoTheme {
        FlightPage(
            arrivals = arrivals,
            departures = departures,
            error = null,
            lastUpdated = null,
            isLoading = false,
            onRetry = {}
        )
    }
}



