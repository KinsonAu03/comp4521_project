package com.group22.weatherForecastApp.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable card styles for consistent card appearance
 */
object CardStyles {
    /**
     * Standard card with default background
     */
    @Composable
    fun standardCard(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable () -> Unit
    ) {
        if (onClick != null) {
            Card(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                content()
            }
        } else {
            Card(
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                content()
            }
        }
    }
    
    /**
     * Card with primary container background
     */
    @Composable
    fun primaryCard(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable () -> Unit
    ) {
        if (onClick != null) {
            Card(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                content()
            }
        } else {
            Card(
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                content()
            }
        }
    }
    
    /**
     * Card with error container background (for alerts)
     */
    @Composable
    fun errorCard(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable () -> Unit
    ) {
        if (onClick != null) {
            Card(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                content()
            }
        } else {
            Card(
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                content()
            }
        }
    }
    
    /**
     * Card content padding modifier
     */
    val contentPadding = Modifier.padding(Spacing.cardPadding)
    
    /**
     * Card content padding (large)
     */
    val contentPaddingLarge = Modifier.padding(Spacing.cardPaddingLarge)
}

