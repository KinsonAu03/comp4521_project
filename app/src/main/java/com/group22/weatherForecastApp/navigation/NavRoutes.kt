package com.group22.weatherForecastApp.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object CurrentWeather : NavRoutes("current_weather")
    object WeatherAlerts : NavRoutes("weather_alerts")
    object DailyForecast : NavRoutes("daily_forecast")
    object Settings : NavRoutes("settings")
    object LocationManager : NavRoutes("location_manager")
}

