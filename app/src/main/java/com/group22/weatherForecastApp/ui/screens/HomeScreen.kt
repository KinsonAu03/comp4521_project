package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToWeatherDetails: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(topBar = { TopAppBar(title = { Text("Weather Forecast") }) }, modifier = modifier) {
            paddingValues ->
        Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(
                    text = "Home Screen",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToWeatherDetails) { Text("View Weather Details") }
        }
    }
}
