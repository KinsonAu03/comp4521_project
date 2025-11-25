package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group22.weatherForecastApp.ui.theme.*
import com.group22.weatherForecastApp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAlertsDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    val alerts by viewModel.alerts.collectAsState(initial = emptyList())
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Alerts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(Spacing.lg)
                                .padding(Spacing.md)
                        )
                    } else {
                        IconButton(onClick = { viewModel.refreshWeather() }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshWeather() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .screenContent()
                    .padding(Spacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.itemSpacing)
            ) {
                if (alerts.isEmpty()) {
                    item {
                        CardStyles.standardCard {
                            Column(
                                modifier = Modifier.cardContentLarge(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No Active Alerts",
                                    style = AppTextStyles.cardTitle()
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = "There are currently no weather alerts for your location.",
                                    style = AppTextStyles.cardBody()
                                )
                            }
                        }
                    }
                } else {
                    items(alerts) { alert ->
                        AlertCard(alert)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert: com.group22.weatherForecastApp.data.AlertDetail) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    CardStyles.errorCard {
        Column(
            modifier = Modifier.cardContent(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = alert.event,
                style = AppTextStyles.cardTitle(),
                color = AppColors.textOnError()
            )
            
            Text(
                text = "From: ${alert.sender_name}",
                style = AppTextStyles.cardBody(),
                color = AppColors.textOnError()
            )
            
            Text(
                text = "Start: ${dateFormat.format(Date(alert.start * 1000))}",
                style = AppTextStyles.cardBodySmall(),
                color = AppColors.textOnError()
            )
            
            Text(
                text = "End: ${dateFormat.format(Date(alert.end * 1000))}",
                style = AppTextStyles.cardBodySmall(),
                color = AppColors.textOnError()
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Divider()
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = alert.description,
                style = AppTextStyles.cardBody(),
                color = AppColors.textOnError()
            )
        }
    }
}

