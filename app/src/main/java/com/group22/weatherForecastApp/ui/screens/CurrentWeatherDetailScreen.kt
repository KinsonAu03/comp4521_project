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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Weather") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Weather Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        currentWeather?.let { weather ->
                            Text(
                                text = "${weather.temperature.toInt()}°C",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = weather.condition,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Details Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Feels Like", style = MaterialTheme.typography.bodySmall)
                                    Text("${weather.feelsLike.toInt()}°C", style = MaterialTheme.typography.bodyLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Humidity", style = MaterialTheme.typography.bodySmall)
                                    Text("${weather.humidity}%", style = MaterialTheme.typography.bodyLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("UV Index", style = MaterialTheme.typography.bodySmall)
                                    Text("${weather.uvIndex?.toInt() ?: 0}", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Additional Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Pressure", style = MaterialTheme.typography.bodySmall)
                                    Text("${weather.pressure?.toInt() ?: 0} hPa", style = MaterialTheme.typography.bodyLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Wind Speed", style = MaterialTheme.typography.bodySmall)
                                    Text("${weather.windSpeed.toInt()} m/s", style = MaterialTheme.typography.bodyLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Visibility", style = MaterialTheme.typography.bodySmall)
                                    Text("${(weather.visibility ?: 0.0).toInt() / 1000} km", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        } ?: run {
                            Text("Loading weather data...")
                        }
                    }
                }
            }
            
            // 24 Hour Forecast Section
            item {
                Text(
                    text = "24 Hour Forecast",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(hourlyForecast.take(24)) { hour ->
                HourlyForecastItem(hour)
            }
        }
    }
}

@Composable
fun HourlyForecastItem(weather: com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(weather.timestamp)),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = weather.condition,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${weather.temperature.toInt()}°C",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Humidity: ${weather.humidity}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Wind: ${weather.windSpeed.toInt()} m/s",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

