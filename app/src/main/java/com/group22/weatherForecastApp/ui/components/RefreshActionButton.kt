package com.group22.weatherForecastApp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.group22.weatherForecastApp.ui.theme.Spacing

/**
 * Reusable refresh action button for TopAppBar
 * Shows a loading indicator when refreshing, otherwise shows a refresh icon button
 * 
 * @param isRefreshing Whether the refresh operation is in progress
 * @param onRefresh Callback when refresh button is clicked
 * @param modifier Modifier for the button
 */
@Composable
fun RefreshActionButton(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isRefreshing) {
        CircularProgressIndicator(
            modifier = modifier
                .size(Spacing.lg)
                .padding(Spacing.md)
        )
    } else {
        IconButton(
            onClick = onRefresh,
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh"
            )
        }
    }
}

