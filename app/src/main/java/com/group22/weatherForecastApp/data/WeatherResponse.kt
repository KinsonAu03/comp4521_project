package com.group22.weatherForecastApp.data

data class OneCallResponse(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val alerts: List<AlertDetail>? = null
)

data class CurrentWeather(
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Double,
    val uvi: Double,
    val humidity: Int,
    val visibility: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDescription>
)

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)

data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Double,
    val uvi: Double,
    val humidity: Int,
    val visibility: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDescription>,
    val pop: Double
)

data class DailyWeather(
    val dt: Long,
    val temp: TempDetail,
    val feels_like: FeelsLikeDetail,
    val pressure: Double,
    val uvi: Double,
    val humidity: Int,
    val visibility: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDescription>,
    val pop: Double,
    val alert: List<AlertDetail>
)

data class TempDetail(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double,
)

data class FeelsLikeDetail(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double,
)

data class AlertDetail(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)




