package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.group22.weatherForecastApp.data.AppConstants
import com.group22.weatherForecastApp.ui.components.RefreshActionButton
import com.group22.weatherForecastApp.ui.components.RefreshableContent
import com.group22.weatherForecastApp.ui.theme.*
import com.group22.weatherForecastApp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    val currentWeather by viewModel.currentWeather.collectAsState(initial = null)
    val hourlyForecast by viewModel.hourlyForecast.collectAsState(initial = emptyList())
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Weather") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    RefreshActionButton(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refreshWeather() }
                    )
                }
            )
        }
    ) { paddingValues ->
        RefreshableContent(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshWeather() },
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .screenContent()
                    .padding(Spacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.itemSpacingLarge)
            ) {
                // Current Weather Section
                item {
                    CardStyles.primaryCard {
                        Column(
                            modifier = CardStyles.contentPaddingLarge,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            currentWeather?.let { weather ->
                                // Weather Icon
                                weather.conditionIcon?.let { icon ->
                                    AsyncImage(
                                        model = "https://openweathermap.org/img/wn/$icon@2x.png",
                                        contentDescription = weather.condition,
                                        modifier = Modifier.size(96.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = "${viewModel.convertTemperature(weather.temperature).toInt()}${viewModel.getTemperatureUnitSymbol()}",
                                    style = AppTextStyles.temperatureDisplayLarge()
                                )
                                Text(
                                    text = weather.condition,
                                    style = AppTextStyles.cardTitle()
                                )
                                Spacer(modifier = Modifier.height(Spacing.md))
                                
                                // Details Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Feels Like", style = AppTextStyles.label())
                                        Text("${viewModel.convertTemperature(weather.feelsLike).toInt()}${viewModel.getTemperatureUnitSymbol()}", style = AppTextStyles.value())
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Humidity", style = AppTextStyles.label())
                                        Text("${weather.humidity}%", style = AppTextStyles.value())
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("UV Index", style = AppTextStyles.label())
                                        Text("${weather.uvIndex?.toInt() ?: 0}", style = AppTextStyles.value())
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(Spacing.md))
                                
                                // Additional Details
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Pressure", style = AppTextStyles.label())
                                        Text("${weather.pressure?.toInt() ?: 0} hPa", style = AppTextStyles.value())
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Wind Speed", style = AppTextStyles.label())
                                        Text("${viewModel.convertWindSpeed(weather.windSpeed).toInt()}${viewModel.getWindSpeedUnitSymbol()}", style = AppTextStyles.value())
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Visibility", style = AppTextStyles.label())
                                        Text("${(weather.visibility ?: 0.0).toInt() / 1000} km", style = AppTextStyles.value())
                                    }
                                }
                            } ?: run {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
                
                // Hourly Forecast Section
                item {
                    Text(
                        text = "${AppConstants.Weather.HOURLY_FORECAST_HOURS} Hour Forecast",
                        style = AppTextStyles.sectionHeader()
                    )
                }
                
                items(hourlyForecast.take(AppConstants.Weather.HOURLY_FORECAST_HOURS)) { hour ->
                    HourlyForecastItem(hour, viewModel)
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(
    weather: com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity,
    viewModel: WeatherViewModel
) {
    CardStyles.standardCard {
        Row(
            modifier = Modifier.cardContent(),
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
                        modifier = Modifier.size(48.dp)
                    )
                }
                Column {
                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(weather.timestamp)),
                        style = AppTextStyles.cardSubtitle()
                    )
                    Text(
                        text = weather.condition,
                        style = AppTextStyles.cardBodySmall()
                    )
                }
            }
            Text(
                text = "${viewModel.convertTemperature(weather.temperature).toInt()}${viewModel.getTemperatureUnitSymbol()}",
                style = AppTextStyles.cardTitle()
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Humidity: ${weather.humidity}%",
                    style = AppTextStyles.cardBodySmall()
                )
                Text(
                    text = "Wind: ${viewModel.convertWindSpeed(weather.windSpeed).toInt()}${viewModel.getWindSpeedUnitSymbol()}",
                    style = AppTextStyles.cardBodySmall()
                )
            }
        }
    }
}

