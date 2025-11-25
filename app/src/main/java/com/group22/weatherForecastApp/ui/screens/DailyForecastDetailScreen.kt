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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.group22.weatherForecastApp.ui.theme.*
import com.group22.weatherForecastApp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyForecastDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    val dailyForecast by viewModel.dailyForecast.collectAsState(initial = emptyList())
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("7-Day Forecast") },
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
                items(dailyForecast.take(7)) { day ->
                    DailyForecastItem(day, viewModel)
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(
    weather: com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity,
    viewModel: WeatherViewModel
) {
    val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(weather.timestamp))
    
    CardStyles.standardCard {
        Column(
            modifier = Modifier.cardContent(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    weather.conditionIcon?.let { icon ->
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/$icon@2x.png",
                            contentDescription = weather.condition,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Column {
                        Text(
                            text = dayName,
                            style = AppTextStyles.cardTitle()
                        )
                        Text(
                            text = dateFormat.format(Date(weather.timestamp)),
                            style = AppTextStyles.cardBodySmall()
                        )
                    }
                }
                Text(
                    text = "${viewModel.convertTemperature(weather.temperature).toInt()}${viewModel.getTemperatureUnitSymbol()}",
                    style = AppTextStyles.temperatureDisplay()
                )
            }
            
            Text(
                text = weather.condition,
                style = AppTextStyles.cardBody()
            )
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Humidity", style = AppTextStyles.label())
                    Text("${weather.humidity}%", style = AppTextStyles.value())
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("UV Index", style = AppTextStyles.label())
                    Text("${weather.uvIndex?.toInt() ?: 0}", style = AppTextStyles.value())
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Wind Speed", style = AppTextStyles.label())
                    Text("${weather.windSpeed.toInt()} m/s", style = AppTextStyles.value())
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pressure", style = AppTextStyles.label())
                    Text("${weather.pressure?.toInt() ?: 0} hPa", style = AppTextStyles.value())
                }
            }
        }
    }
}

