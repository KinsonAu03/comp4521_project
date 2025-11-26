package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group22.weatherForecastApp.ui.theme.*

@Composable
fun LoadingScreen(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.itemSpacingLarge)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(Spacing.xxl),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = AppTextStyles.cardBody(),
                color = AppColors.textPrimary(),
                textAlign = TextAlign.Center
            )
        }
    }
}

