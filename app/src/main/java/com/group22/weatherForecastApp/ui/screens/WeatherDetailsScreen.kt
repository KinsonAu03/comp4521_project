package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group22.weatherForecastApp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailsScreen(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Weather Details", style = AppTextStyles.cardTitle()) },
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(
                    text = "Weather Details",
                    style = AppTextStyles.sectionHeader(),
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.itemSpacingLarge))

            Text(
                    text = "In Progress",
                    style = AppTextStyles.cardBody(),
                    color = AppColors.textSecondary(),
                    textAlign = TextAlign.Center
            )
        }
    }
}
