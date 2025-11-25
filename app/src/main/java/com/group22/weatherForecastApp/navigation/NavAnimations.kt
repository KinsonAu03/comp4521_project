package com.group22.weatherForecastApp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

/**
 * Navigation animation configurations for consistent screen transitions
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
    
    /**
     * Zoom and fade animation for detail screens
     * Creates a zoom-in effect with fade when entering
     * Zoom-out with fade when exiting
     */
    fun zoomFadeAnimations(): AnimationSet {
        return AnimationSet(
            enter = fadeIn(animationSpec = tween(400)) + 
                    scaleIn(initialScale = 0.8f, animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(300)) + 
                   scaleOut(targetScale = 0.9f, animationSpec = tween(300)),
            popEnter = fadeIn(animationSpec = tween(300)) + 
                       scaleIn(initialScale = 0.9f, animationSpec = tween(300)),
            popExit = fadeOut(animationSpec = tween(400)) + 
                      scaleOut(targetScale = 0.8f, animationSpec = tween(400))
        )
    }
}

