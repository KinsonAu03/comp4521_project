package com.group22.weatherForecastApp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

/**
 * Navigation animation configurations for consistent screen transitions
 * Using simple fade in/out animations
 */
object NavAnimations {
    /**
     * Complete animation set for a composable destination
     */
    data class AnimationSet(
        val enter: EnterTransition,
        val exit: ExitTransition,
        val popEnter: EnterTransition,
        val popExit: ExitTransition
    )

    /**
     * Default fade animations for all screens
     * Simple fade in/out transitions (300ms duration)
     */
    fun defaultAnimations(): AnimationSet {
        return AnimationSet(
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            popEnter = fadeIn(animationSpec = tween(300)),
            popExit = fadeOut(animationSpec = tween(300))
        )
    }
}

