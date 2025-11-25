package com.group22.weatherForecastApp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier

/**
 * Extension functions for common modifier patterns
 * Provides reusable modifier combinations
 */

/**
 * Standard card content modifier with padding
 */
fun Modifier.cardContent() = this
    .fillMaxWidth()
    .padding(Spacing.cardPadding)

/**
 * Large card content modifier with more padding
 */
fun Modifier.cardContentLarge() = this
    .fillMaxWidth()
    .padding(Spacing.cardPaddingLarge)

/**
 * Screen content modifier with standard screen padding
 */
fun Modifier.screenContent() = this
    .fillMaxSize()
    .padding(Spacing.screenPadding)

/**
 * Standard spacing between items
 */
fun Modifier.itemSpacing() = this.padding(vertical = Spacing.itemSpacing)

/**
 * Large spacing between items
 */
fun Modifier.itemSpacingLarge() = this.padding(vertical = Spacing.itemSpacingLarge)

