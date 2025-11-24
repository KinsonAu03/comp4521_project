package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLocationManager: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                    text = "More settings coming soon...",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
            )
        }
    }
}
