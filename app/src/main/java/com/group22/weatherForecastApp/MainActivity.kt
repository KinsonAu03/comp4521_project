package com.group22.weatherForecastApp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.group22.weatherForecastApp.data.AppInitializer
import com.group22.weatherForecastApp.navigation.NavAnimations
import com.group22.weatherForecastApp.navigation.NavRoutes
import com.group22.weatherForecastApp.ui.components.BottomNavigationBar
import com.group22.weatherForecastApp.ui.screens.HomeScreen
import com.group22.weatherForecastApp.ui.screens.LoadingScreen
import com.group22.weatherForecastApp.ui.screens.SettingsScreen
import com.group22.weatherForecastApp.ui.screens.WeatherDetailsScreen
import com.group22.weatherForecastApp.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize app initializer with context
        val appInitializer = AppInitializer(applicationContext)

        setContent {
            MyApplicationTheme {
                WeatherApp(appInitializer = appInitializer)
            }
        }
    }
}

@Composable
fun WeatherApp(appInitializer: AppInitializer) {
    var isInitialized by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Initialize app on first launch
    LaunchedEffect(Unit) {
        Log.d("WeatherApp", "Starting app initialization...")
        
        // Run all initialization tasks (database, demo data, API calls, etc.)
        appInitializer.initialize()
        
        Log.d("WeatherApp", "App initialization complete, showing main app")
        isInitialized = true
    }

    // Show loading screen until initialization is complete
    if (!isInitialized) {
        LoadingScreen(message = "Loading weather data...")
        return
    }

    // Log current route changes
    LaunchedEffect(currentRoute) {
        Log.d("WeatherApp", "Current route changed to: $currentRoute")
    }

    // Main app content
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    Log.d("WeatherApp", "BottomNav onNavigate called with route: $route")
                    Log.d("WeatherApp", "Current route before navigation: $currentRoute")
                    
                    // Only navigate if we're not already on that route
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                        Log.d("WeatherApp", "Navigation executed to: $route")
                    } else {
                        Log.d("WeatherApp", "Already on route $route, skipping navigation")
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            val homeAnimations = NavAnimations.defaultAnimations()
            composable(
                route = NavRoutes.Home.route,
                enterTransition = { homeAnimations.enter },
                exitTransition = { homeAnimations.exit },
                popEnterTransition = { homeAnimations.popEnter },
                popExitTransition = { homeAnimations.popExit }
            ) {
                Log.d("WeatherApp", "Rendering HomeScreen")
                HomeScreen(
                    onNavigateToWeatherDetails = {
                        Log.d("WeatherApp", "HomeScreen: Navigating to WeatherDetails")
                        navController.navigate(NavRoutes.WeatherDetails.route)
                    }
                )
            }

            val defaultAnimations = NavAnimations.defaultAnimations()
            composable(
                route = NavRoutes.WeatherDetails.route,
                enterTransition = { defaultAnimations.enter },
                exitTransition = { defaultAnimations.exit },
                popEnterTransition = { defaultAnimations.popEnter },
                popExitTransition = { defaultAnimations.popExit }
            ) {
                Log.d("WeatherApp", "Rendering WeatherDetailsScreen")
                WeatherDetailsScreen(
                    onNavigateBack = {
                        Log.d("WeatherApp", "WeatherDetailsScreen: Navigating back")
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.Settings.route,
                enterTransition = { defaultAnimations.enter },
                exitTransition = { defaultAnimations.exit },
                popEnterTransition = { defaultAnimations.popEnter },
                popExitTransition = { defaultAnimations.popExit }
            ) {
                Log.d("WeatherApp", "Rendering SettingsScreen")
                SettingsScreen(
                    onNavigateBack = {
                        Log.d("WeatherApp", "SettingsScreen: Navigating back")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}