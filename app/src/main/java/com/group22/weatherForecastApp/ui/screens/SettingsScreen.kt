package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group22.weatherForecastApp.data.TemperatureUnit
import com.group22.weatherForecastApp.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLocationManager: () -> Unit = {},
    viewModel: WeatherViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    
    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Settings") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                )
                            }
                        }
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
            // Temperature Unit Selection
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                ) {
                    Text(
                            text = "Temperature Unit",
                            style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                                selected = temperatureUnit == TemperatureUnit.CELSIUS,
                                onClick = { viewModel.setTemperatureUnit(TemperatureUnit.CELSIUS) },
                                label = { Text("Celsius (°C)") },
                                modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                                selected = temperatureUnit == TemperatureUnit.FAHRENHEIT,
                                onClick = { viewModel.setTemperatureUnit(TemperatureUnit.FAHRENHEIT) },
                                label = { Text("Fahrenheit (°F)") },
                                modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Location Manager button
            Card(
                    modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToLocationManager),
                    colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
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
                                text = "Location Manager",
                                style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                                text = "Manage your locations and favorites",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Navigate to Location Manager"
                    )
                }
            }
        }
    }
}
