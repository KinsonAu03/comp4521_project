package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.group22.weatherForecastApp.data.AppConstants
import com.group22.weatherForecastApp.ui.components.ErrorDialog
import com.group22.weatherForecastApp.ui.theme.*
import com.group22.weatherForecastApp.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCurrentWeather: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToDailyForecast: () -> Unit,
    viewModel: WeatherViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentWeather by viewModel.currentWeather.collectAsState(initial = null)
    val alerts by viewModel.alerts.collectAsState(initial = emptyList())
    val dailyForecast by viewModel.dailyForecast.collectAsState(initial = emptyList())
    val currentLocation by viewModel.currentLocation.collectAsState(initial = null)
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { 
                    Column {
                        Text("Weather Forecast")
                        currentLocation?.let { location ->
                            Text(
                                text = location.name,
                                style = AppTextStyles.cardBodySmall(),
                                color = AppColors.textSecondary()
                            )
                        }
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
        },
        modifier = modifier
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshWeather() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .screenContent()
                    .padding(Spacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.itemSpacingLarge)
            ) {
                // Current Weather Card
                WeatherCard(
                    title = "Current Weather",
                    weather = currentWeather,
                    temperatureUnit = temperatureUnit,
                    viewModel = viewModel,
                    onClick = {
                        onNavigateToCurrentWeather()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Weather Alerts Card
                WeatherCard(
                    title = "Weather Alerts",
                    alertsCount = alerts.size,
                    hasAlerts = alerts.isNotEmpty(),
                    onClick = {
                        onNavigateToAlerts()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Daily Forecast Card
                WeatherCard(
                    title = "${AppConstants.Weather.DAILY_FORECAST_DAYS}-Day Forecast",
                    forecastCount = dailyForecast.size,
                    onClick = {
                        onNavigateToDailyForecast()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Error Dialog
        ErrorDialog(
            error = errorState,
            onDismiss = { viewModel.clearError() },
            onRetry = { viewModel.refreshWeather() },
            onContactSupport = {
                // TODO: Implement contact support
                viewModel.clearError()
            }
        )
    }
}

@Composable
fun WeatherCard(
    title: String,
    weather: com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity? = null,
    alertsCount: Int = 0,
    hasAlerts: Boolean = false,
    forecastCount: Int = 0,
    temperatureUnit: com.group22.weatherForecastApp.data.TemperatureUnit = com.group22.weatherForecastApp.data.TemperatureUnit.CELSIUS,
    viewModel: WeatherViewModel? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (hasAlerts) {
                AppColors.cardBackgroundError()
            } else {
                AppColors.cardBackground()
            }
        )
    ) {
        Column(
            modifier = Modifier.cardContentLarge(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = AppTextStyles.cardTitle()
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            when {
                weather != null -> {
                    // Show weather icon (only if available)
                    weather.conditionIcon?.let { icon ->
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/$icon@2x.png",
                            contentDescription = weather.condition,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    // Safe temperature conversion with fallback
                    val convertedTemp = viewModel?.convertTemperature(weather.temperature)?.toInt() 
                        ?: weather.temperature.toInt()
                    val unitSymbol = viewModel?.getTemperatureUnitSymbol() ?: "°C"
                    Text(
                        text = "$convertedTemp$unitSymbol",
                        style = AppTextStyles.temperatureDisplay()
                    )
                    Text(
                        text = weather.condition.ifEmpty { "Unknown" },
                        style = AppTextStyles.cardSubtitle()
                    )
                }
                hasAlerts -> {
                    Text(
                        text = "$alertsCount Active Alert${if (alertsCount > 1) "s" else ""}",
                        style = AppTextStyles.cardSubtitle(),
                        color = AppColors.textOnError()
                    )
                }
                alertsCount == 0 && title == "Weather Alerts" -> {
                    // Show "No alerts" message for alerts card when there are no alerts
                    Text(
                        text = "No alerts currently",
                        style = AppTextStyles.cardSubtitle(),
                        color = AppColors.textSecondary()
                    )
                }
                forecastCount > 0 -> {
                    Text(
                        text = "$forecastCount Days Available",
                        style = AppTextStyles.cardSubtitle()
                    )
                }
                else -> {
                    CircularProgressIndicator(modifier = Modifier.size(Spacing.lg))
                }
            }
        }
    }
}
