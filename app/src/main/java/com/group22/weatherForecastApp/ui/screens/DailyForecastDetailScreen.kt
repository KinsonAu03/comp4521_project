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
                                .size(24.dp)
                                .padding(12.dp)
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
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateFormat.format(Date(weather.timestamp)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text(
                    text = "${viewModel.convertTemperature(weather.temperature).toInt()}${viewModel.getTemperatureUnitSymbol()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = weather.condition,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Humidity", style = MaterialTheme.typography.bodySmall)
                    Text("${weather.humidity}%", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("UV Index", style = MaterialTheme.typography.bodySmall)
                    Text("${weather.uvIndex?.toInt() ?: 0}", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Wind Speed", style = MaterialTheme.typography.bodySmall)
                    Text("${weather.windSpeed.toInt()} m/s", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pressure", style = MaterialTheme.typography.bodySmall)
                    Text("${weather.pressure?.toInt() ?: 0} hPa", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

