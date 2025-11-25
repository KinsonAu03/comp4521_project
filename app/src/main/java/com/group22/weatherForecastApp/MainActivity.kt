package com.group22.weatherForecastApp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.group22.weatherForecastApp.data.AppInitializer
import com.group22.weatherForecastApp.data.LocationService
import com.group22.weatherForecastApp.navigation.NavAnimations
import com.group22.weatherForecastApp.navigation.NavRoutes
import com.group22.weatherForecastApp.ui.components.BottomNavigationBar
import com.group22.weatherForecastApp.ui.screens.*
import com.group22.weatherForecastApp.ui.theme.MyApplicationTheme
import com.group22.weatherForecastApp.ui.utils.LOCATION_PERMISSIONS
import com.group22.weatherForecastApp.ui.utils.rememberLocationPermissionLauncher

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
    var permissionsGranted by remember { mutableStateOf(false) }
    var showPermissionRequest by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    
    // Check if permissions are already granted on first composition
    LaunchedEffect(Unit) {
        permissionsGranted = locationService.hasLocationPermission()
        if (!permissionsGranted) {
            showPermissionRequest = true
        } else {
            // Permissions already granted, proceed with initialization
            Log.d("WeatherApp", "Location permissions already granted")
        }
    }
    
    // Permission launcher for location permissions
    val locationPermissionLauncher = rememberLocationPermissionLauncher { granted ->
        permissionsGranted = granted
        showPermissionRequest = false
        
        if (granted) {
            Log.d("WeatherApp", "Location permissions granted")
        } else {
            Log.d("WeatherApp", "Location permissions denied")
        }
    }

    // Initialize app after permissions are handled (either granted or denied)
    LaunchedEffect(showPermissionRequest, permissionsGranted) {
        if (!showPermissionRequest) {
            // Permissions have been handled (either granted or user dismissed)
            if (!isInitialized) {
                Log.d("WeatherApp", "Starting app initialization...")
                
                // Run all initialization tasks (database, demo data, API calls, etc.)
                appInitializer.initialize()
                
                Log.d("WeatherApp", "App initialization complete, showing main app")
                isInitialized = true
            }
        }
    }

    // Show permission request screen first
    if (showPermissionRequest) {
        PermissionRequestScreen(
            onRequestPermission = {
                locationPermissionLauncher.launch(LOCATION_PERMISSIONS)
            },
            onSkip = {
                // User can skip permission request and proceed
                showPermissionRequest = false
                permissionsGranted = false
            }
        )
        return
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
                    onNavigateToCurrentWeather = {
                        Log.d("WeatherApp", "HomeScreen: Navigating to CurrentWeather")
                        navController.navigate(NavRoutes.CurrentWeather.route)
                    },
                    onNavigateToAlerts = {
                        Log.d("WeatherApp", "HomeScreen: Navigating to WeatherAlerts")
                        navController.navigate(NavRoutes.WeatherAlerts.route)
                    },
                    onNavigateToDailyForecast = {
                        Log.d("WeatherApp", "HomeScreen: Navigating to DailyForecast")
                        navController.navigate(NavRoutes.DailyForecast.route)
                    }
                )
            }

            val defaultAnimations = NavAnimations.defaultAnimations()
            val zoomFadeAnimations = NavAnimations.zoomFadeAnimations()
            
            composable(
                route = NavRoutes.CurrentWeather.route,
                enterTransition = { zoomFadeAnimations.enter },
                exitTransition = { zoomFadeAnimations.exit },
                popEnterTransition = { zoomFadeAnimations.popEnter },
                popExitTransition = { zoomFadeAnimations.popExit }
            ) {
                Log.d("WeatherApp", "Rendering CurrentWeatherDetailScreen")
                CurrentWeatherDetailScreen(
                    onNavigateBack = {
                        Log.d("WeatherApp", "CurrentWeatherDetailScreen: Navigating back")
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.WeatherAlerts.route,
                enterTransition = { zoomFadeAnimations.enter },
                exitTransition = { zoomFadeAnimations.exit },
                popEnterTransition = { zoomFadeAnimations.popEnter },
                popExitTransition = { zoomFadeAnimations.popExit }
            ) {
                Log.d("WeatherApp", "Rendering WeatherAlertsDetailScreen")
                WeatherAlertsDetailScreen(
                    onNavigateBack = {
                        Log.d("WeatherApp", "WeatherAlertsDetailScreen: Navigating back")
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.DailyForecast.route,
                enterTransition = { zoomFadeAnimations.enter },
                exitTransition = { zoomFadeAnimations.exit },
                popEnterTransition = { zoomFadeAnimations.popEnter },
                popExitTransition = { zoomFadeAnimations.popExit }
            ) {
                Log.d("WeatherApp", "Rendering DailyForecastDetailScreen")
                DailyForecastDetailScreen(
                    onNavigateBack = {
                        Log.d("WeatherApp", "DailyForecastDetailScreen: Navigating back")
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
                    },
                    onNavigateToLocationManager = {
                        Log.d("WeatherApp", "SettingsScreen: Navigating to LocationManager")
                        navController.navigate(NavRoutes.LocationManager.route)
                    }
                )
            }

            composable(
                route = NavRoutes.LocationManager.route,
                enterTransition = { defaultAnimations.enter },
                exitTransition = { defaultAnimations.exit },
                popEnterTransition = { defaultAnimations.popEnter },
                popExitTransition = { defaultAnimations.popExit }
            ) {
                Log.d("WeatherApp", "Rendering LocationManagerScreen")
                LocationManagerScreen(
                    onNavigateBack = {
                        Log.d("WeatherApp", "LocationManagerScreen: Navigating back")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "This app needs location permission to provide accurate weather forecasts for your current location.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Location Permission")
            }
            
            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip for now")
            }
        }
    }
}