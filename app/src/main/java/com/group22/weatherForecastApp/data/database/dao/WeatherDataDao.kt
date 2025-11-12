package com.group22.weatherForecastApp.data.database.dao

import androidx.room.*
import com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDataDao {
    @Query("SELECT * FROM weather_data WHERE locationId = :locationId AND forecastType = 'current' ORDER BY timestamp DESC LIMIT 1")
    fun getCurrentWeather(locationId: Long): Flow<WeatherDataEntity?>

    @Query("SELECT * FROM weather_data WHERE locationId = :locationId AND forecastType = 'hourly' ORDER BY timestamp ASC")
    fun getHourlyForecast(locationId: Long): Flow<List<WeatherDataEntity>>

    @Query("SELECT * FROM weather_data WHERE locationId = :locationId AND forecastType = 'daily' ORDER BY timestamp ASC")
    fun getDailyForecast(locationId: Long): Flow<List<WeatherDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherDataList(weatherDataList: List<WeatherDataEntity>)

    @Query("DELETE FROM weather_data WHERE locationId = :locationId")
    suspend fun deleteWeatherDataForLocation(locationId: Long)

    @Query("DELETE FROM weather_data WHERE locationId = :locationId AND forecastType = :forecastType")
    suspend fun deleteWeatherDataByType(locationId: Long, forecastType: String)
}

