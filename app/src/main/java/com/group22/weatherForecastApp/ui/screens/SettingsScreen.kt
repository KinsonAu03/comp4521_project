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
import com.group22.weatherForecastApp.data.WindSpeedUnit
import com.group22.weatherForecastApp.data.ThemeMode
import com.group22.weatherForecastApp.ui.theme.*
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
    val windSpeedUnit by viewModel.windUnit.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    
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
                    .screenContent()
                    .padding(Spacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.itemSpacingLarge)
        ) {
            // Temperature Unit Selection
            CardStyles.standardCard {
                Column(
                        modifier = Modifier.cardContent()
                ) {
                    Text(
                            text = "Temperature Unit",
                            style = AppTextStyles.cardTitle()
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        FilterChip(
                                selected = temperatureUnit == TemperatureUnit.CELSIUS,
                                onClick = { viewModel.setTemperatureUnit(TemperatureUnit.CELSIUS) },
                                label = { Text("Celsius (°C)", style = AppTextStyles.cardBody()) },
                                modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                                selected = temperatureUnit == TemperatureUnit.FAHRENHEIT,
                                onClick = { viewModel.setTemperatureUnit(TemperatureUnit.FAHRENHEIT) },
                                label = { Text("Fahrenheit (°F)", style = AppTextStyles.cardBody()) },
                                modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            CardStyles.standardCard {
                Column(
                    modifier = Modifier.cardContent()
                ) {
                    Text(
                        text = "Wind Speed Unit",
                        style = AppTextStyles.cardTitle()
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        FilterChip(
                            selected = windSpeedUnit == WindSpeedUnit.KMH,
                            onClick = { viewModel.setWindSpeedUnit(WindSpeedUnit.KMH) },
                            label = { Text("KMH", style = AppTextStyles.cardBody()) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = windSpeedUnit == WindSpeedUnit.MS,
                            onClick = { viewModel.setWindSpeedUnit(WindSpeedUnit.MS) },
                            label = { Text("MS", style = AppTextStyles.cardBody()) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = windSpeedUnit == WindSpeedUnit.MPH,
                            onClick = { viewModel.setWindSpeedUnit(WindSpeedUnit.MPH) },
                            label = { Text("MPH", style = AppTextStyles.cardBody()) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            CardStyles.standardCard {
                Column(
                    modifier = Modifier.cardContent()
                ) {
                    Text(
                        text = "Theme",
                        style = AppTextStyles.cardTitle()
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        FilterChip(
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                            label = { Text("System", style = AppTextStyles.cardBody()) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK)},
                            label = { Text("Dark", style = AppTextStyles.cardBody()) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT)},
                            label = { Text("Light", style = AppTextStyles.cardBody()) },
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
                            containerColor = AppColors.cardBackground()
                    )
            ) {
                Row(
                        modifier = Modifier.cardContent(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                                text = "Location Manager",
                                style = AppTextStyles.cardTitle()
                        )
                        Text(
                                text = "Manage your locations and favorites",
                                style = AppTextStyles.cardBodySmall(),
                                color = AppColors.textSecondary()
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
