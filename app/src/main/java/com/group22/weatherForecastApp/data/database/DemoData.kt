package com.group22.weatherForecastApp.data.database

import com.group22.weatherForecastApp.data.database.dao.LocationDao
import com.group22.weatherForecastApp.data.database.dao.WeatherDataDao
import com.group22.weatherForecastApp.data.database.entity.LocationEntity
import com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity
import kotlinx.coroutines.flow.first

class DemoData(
    private val locationDao: LocationDao,
    private val weatherDataDao: WeatherDataDao
) {
    suspend fun initializeDemoData() {
        // Check if data already exists
        val existingLocations = locationDao.getAllLocations().first()
        if (existingLocations.isNotEmpty()) {
            return // Data already initialized
        }

        // Insert demo locations
        val hongKong = LocationEntity(
            name = "Hong Kong",
            latitude = 22.3193,
            longitude = 114.1694,
            country = "HK",
            isFavorite = false,
            isCurrentLocation = true,
            isUsing = true, // Set as the location being used
            order = 0
        )
        val hongKongId = locationDao.insertLocation(hongKong)

        val london = LocationEntity(
            name = "London",
            latitude = 51.5074,
            longitude = -0.1278,
            country = "GB",
            isFavorite = true,
            isCurrentLocation = false,
            isUsing = false,
            order = 0
        )
        val londonId = locationDao.insertLocation(london)

        val tokyo = LocationEntity(
            name = "Tokyo",
            latitude = 35.6762,
            longitude = 139.6503,
            country = "JP",
            isFavorite = true,
            isCurrentLocation = false,
            isUsing = false,
            order = 1
        )
        val tokyoId = locationDao.insertLocation(tokyo)

        // Insert demo weather data for Hong Kong (current location)
        val currentWeather = WeatherDataEntity(
            locationId = hongKongId,
            temperature = 22.0,
            feelsLike = 20.0,
            condition = "Sunny",
            conditionIcon = "01d",
            humidity = 65,
            windSpeed = 15.0,
            windDirection = 180,
            pressure = 1013.0,
            uvIndex = 5.0,
            visibility = 10.0,
            forecastType = "current"
        )
        weatherDataDao.insertWeatherData(currentWeather)

        // Insert demo hourly forecast for Hong Kong
        val hourlyForecast = (0..23).map { hour ->
            WeatherDataEntity(
                locationId = hongKongId,
                temperature = 20.0 + (hour * 0.5),
                feelsLike = 18.0 + (hour * 0.5),
                condition = if (hour < 6 || hour > 18) "Clear" else "Sunny",
                conditionIcon = if (hour < 6 || hour > 18) "01n" else "01d",
                humidity = 60 + (hour % 10),
                windSpeed = 10.0 + (hour % 5),
                windDirection = 180,
                pressure = 1013.0,
                uvIndex = if (hour >= 10 && hour <= 16) 5.0 else 0.0,
                visibility = 10.0,
                timestamp = System.currentTimeMillis() + (hour * 3600000L),
                forecastType = "hourly"
            )
        }
        weatherDataDao.insertWeatherDataList(hourlyForecast)

        // Insert demo daily forecast for Hong Kong
        val dailyForecast = (0..6).map { day ->
            WeatherDataEntity(
                locationId = hongKongId,
                temperature = 22.0 + (day * 2),
                feelsLike = 20.0 + (day * 2),
                condition = when (day % 3) {
                    0 -> "Sunny"
                    1 -> "Partly Cloudy"
                    else -> "Rainy"
                },
                conditionIcon = when (day % 3) {
                    0 -> "01d"
                    1 -> "02d"
                    else -> "10d"
                },
                humidity = 65 + (day * 2),
                windSpeed = 12.0 + (day * 1.5),
                windDirection = 180,
                pressure = 1013.0,
                uvIndex = 5.0,
                visibility = 10.0,
                timestamp = System.currentTimeMillis() + (day * 86400000L),
                forecastType = "daily"
            )
        }
        weatherDataDao.insertWeatherDataList(dailyForecast)
    }
}

