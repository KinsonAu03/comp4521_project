package com.group22.weatherForecastApp.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object WeatherDetails : NavRoutes("weather_details")
    object Settings : NavRoutes("settings")
}

