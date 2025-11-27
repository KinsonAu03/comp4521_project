package com.group22.weatherForecastApp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Reusable wrapper for content with pull-to-refresh functionality
 * Provides consistent pull-to-refresh behavior across screens
 * 
 * @param isRefreshing Whether the refresh operation is in progress
 * @param onRefresh Callback when pull-to-refresh is triggered
 * @param modifier Modifier for the container
 * @param content The content to display inside the pull-to-refresh container
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshableContent(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
    ) {
        content()
    }
}

