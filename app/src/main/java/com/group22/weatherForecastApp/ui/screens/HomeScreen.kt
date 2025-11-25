package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Weather Forecast") }
            ) 
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Weather Card
            Card(
                onClick = onNavigateToCurrentWeather,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current Weather",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    currentWeather?.let { weather ->
                        Text(
                            text = "${weather.temperature.toInt()}°C",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = weather.condition,
                            style = MaterialTheme.typography.titleMedium
                        )
                    } ?: run {
                        Text("Loading...")
                    }
                }
            }
            
            // Weather Alerts Card
            Card(
                onClick = onNavigateToAlerts,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (alerts.isNotEmpty()) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Weather Alerts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (alerts.isNotEmpty()) {
                        Text(
                            text = "${alerts.size} Active Alert${if (alerts.size > 1) "s" else ""}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    } else {
                        Text(
                            text = "No Active Alerts",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            
            // Daily Forecast Card
            Card(
                onClick = onNavigateToDailyForecast,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "7-Day Forecast",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (dailyForecast.isNotEmpty()) {
                        Text(
                            text = "${dailyForecast.size} Days Available",
                            style = MaterialTheme.typography.titleMedium
                        )
                    } else {
                        Text("Loading...")
                    }
                }
            }
        }
    }
}
