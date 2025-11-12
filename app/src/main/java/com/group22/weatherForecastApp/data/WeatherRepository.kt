package com.group22.weatherForecastApp.data

import com.group22.weatherForecastApp.data.database.dao.WeatherDataDao
import com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity

class WeatherRepository(
    private val api: WeatherApi,
    private val dao: WeatherDataDao
) {

    suspend fun refreshWeather(locationId: Long, lat: Double, lon: Double) {
        val response = api.getOneCallWeather(lat, lon)

        // Clear previous data for this location
        dao.deleteWeatherDataForLocation(locationId)

        // Insert current weather
        dao.insertWeatherData(
            WeatherDataEntity(
                locationId = locationId,
                temperature = response.current.temp,
                feelsLike = response.current.feels_like,
                condition = response.current.weather.firstOrNull()?.main ?: "",
                conditionIcon = response.current.weather.firstOrNull()?.icon,
                humidity = response.current.humidity,
                windSpeed = response.current.wind_speed,
                windDirection = response.current.wind_deg,
                pressure = response.current.pressure,
                uvIndex = response.current.uvi,
                visibility = response.current.visibility,
                timestamp = response.current.dt * 1000L,
                forecastType = "current"
            )
        )

        // Insert hourly forecast
        dao.insertWeatherDataList(
            response.hourly.map {
                WeatherDataEntity(
                    locationId = locationId,
                    temperature = it.temp,
                    feelsLike = it.feels_like,
                    condition = it.weather.firstOrNull()?.main ?: "",
                    conditionIcon = it.weather.firstOrNull()?.icon,
                    humidity = it.humidity,
                    windSpeed = it.wind_speed,
                    windDirection = it.wind_deg,
                    pressure = it.pressure,
                    uvIndex = it.uvi,
                    visibility = it.visibility,
                    timestamp = it.dt * 1000L,
                    forecastType = "hourly"
                )
            }
        )

        // Insert daily forecast
        dao.insertWeatherDataList(
            response.daily.map {
                WeatherDataEntity(
                    locationId = locationId,
                    temperature = it.temp.day,
                    feelsLike = it.feels_like.day,
                    condition = it.weather.firstOrNull()?.main ?: "",
                    conditionIcon = it.weather.firstOrNull()?.icon,
                    humidity = it.humidity,
                    windSpeed = it.wind_speed,
                    windDirection = it.wind_deg,
                    pressure = it.pressure,
                    uvIndex = it.uvi,
                    visibility = it.visibility,
                    timestamp = it.dt * 1000L,
                    forecastType = "daily"
                )
            }
        )
    }
}