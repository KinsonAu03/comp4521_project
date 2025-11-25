package com.group22.weatherForecastApp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * App-specific color definitions
 * Provides easy access to commonly used colors from the theme
 */
object AppColors {
    @Composable
    fun cardBackground() = MaterialTheme.colorScheme.surfaceVariant
    
    @Composable
    fun cardBackgroundPrimary() = MaterialTheme.colorScheme.primaryContainer
    
    @Composable
    fun cardBackgroundError() = MaterialTheme.colorScheme.errorContainer
    
    @Composable
    fun textPrimary() = MaterialTheme.colorScheme.onSurface
    
    @Composable
    fun textSecondary() = MaterialTheme.colorScheme.onSurfaceVariant
    
    @Composable
    fun textOnError() = MaterialTheme.colorScheme.onErrorContainer
    
    @Composable
    fun divider() = MaterialTheme.colorScheme.outlineVariant
}

