package com.group22.weatherForecastApp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Extended text styles for consistent typography throughout the app
 * Extends Material3 Typography with app-specific styles
 */
object AppTextStyles {
    @Composable
    fun cardTitle(): TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold
    )
    
    @Composable
    fun cardSubtitle(): TextStyle = MaterialTheme.typography.titleMedium
    
    @Composable
    fun cardBody(): TextStyle = MaterialTheme.typography.bodyMedium
    
    @Composable
    fun cardBodySmall(): TextStyle = MaterialTheme.typography.bodySmall
    
    @Composable
    fun temperatureDisplay(): TextStyle = MaterialTheme.typography.displayMedium
    
    @Composable
    fun temperatureDisplayLarge(): TextStyle = MaterialTheme.typography.displayLarge
    
    @Composable
    fun sectionHeader(): TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold
    )
    
    @Composable
    fun label(): TextStyle = MaterialTheme.typography.bodySmall
    
    @Composable
    fun value(): TextStyle = MaterialTheme.typography.bodyMedium
}

