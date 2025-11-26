package com.group22.weatherForecastApp.data

import android.util.Log
import com.group22.weatherForecastApp.data.database.dao.WeatherDataDao
import com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity

class WeatherRepository(
    private val api: WeatherApi,
    private val dao: WeatherDataDao
) {
    private val tag = "WeatherRepository"

    suspend fun refreshWeather(locationId: Long, lat: Double, lon: Double): List<AlertDetail> {
        val response = api.getOneCallWeather(lat, lon)
        
        // Log the complete API response
        Log.d(tag, "========== OPENWEATHER API RESPONSE ==========")
        Log.d(tag, "Location ID: $locationId, Lat: $lat, Lon: $lon")
        Log.d(tag, "Response Object: $response")
        Log.d(tag, "-----------------------------------------------")
        
        // Log current weather details
        Log.d(tag, "CURRENT WEATHER:")
        Log.d(tag, "  Temperature: ${response.current.temp}°C")
        Log.d(tag, "  Feels Like: ${response.current.feels_like}°C")
        Log.d(tag, "  Condition: ${response.current.weather.firstOrNull()?.main}")
        Log.d(tag, "  Description: ${response.current.weather.firstOrNull()?.description}")
        Log.d(tag, "  Icon: ${response.current.weather.firstOrNull()?.icon}")
        Log.d(tag, "  Humidity: ${response.current.humidity}%")
        Log.d(tag, "  Wind Speed: ${response.current.wind_speed} m/s")
        Log.d(tag, "  Wind Direction: ${response.current.wind_deg}°")
        Log.d(tag, "  Pressure: ${response.current.pressure} hPa")
        Log.d(tag, "  UV Index: ${response.current.uvi}")
        Log.d(tag, "  Visibility: ${response.current.visibility} m")
        Log.d(tag, "  Timestamp: ${response.current.dt}")
        
        // Log hourly forecast
        Log.d(tag, "HOURLY FORECAST (${response.hourly.size} hours):")
        response.hourly.take(24).forEachIndexed { index, hourly ->
            Log.d(tag, "  Hour $index: ${hourly.temp}°C, ${hourly.weather.firstOrNull()?.main}, " +
                    "Pop: ${hourly.pop}, Wind: ${hourly.wind_speed} m/s")
        }
        
        // Log daily forecast
        Log.d(tag, "DAILY FORECAST (${response.daily.size} days):")
        response.daily.forEachIndexed { index, daily ->
            Log.d(tag, "  Day $index: Min ${daily.temp.min}°C, Max ${daily.temp.max}°C, " +
                    "${daily.weather.firstOrNull()?.main}, Pop: ${daily.pop}, " +
                    "UV: ${daily.uvi}, Wind: ${daily.wind_speed} m/s")
        }
        
        // Log alerts
        if (response.alerts != null && response.alerts.isNotEmpty()) {
            Log.d(tag, "WEATHER ALERTS (${response.alerts.size}):")
            response.alerts.forEachIndexed { index, alert ->
                Log.d(tag, "  Alert $index: ${alert.event}")
                Log.d(tag, "    Sender: ${alert.sender_name}")
                Log.d(tag, "    Start: ${alert.start}, End: ${alert.end}")
                Log.d(tag, "    Description: ${alert.description}")
            }
        } else {
            Log.d(tag, "WEATHER ALERTS: None")
        }
        
        Log.d(tag, "=============================================")

        // Clear previous data for this location BEFORE inserting new data
        // This prevents duplicates if multiple refresh calls happen
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
        
        // Return alerts from the response
        return response.alerts ?: emptyList()
    }
}