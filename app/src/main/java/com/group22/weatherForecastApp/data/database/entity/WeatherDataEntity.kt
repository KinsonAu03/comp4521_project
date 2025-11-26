package com.group22.weatherForecastApp.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_data",
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["locationId"]),
        Index(value = ["locationId", "timestamp", "forecastType"], unique = true)
    ]
)
data class WeatherDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locationId: Long,
    val temperature: Double,
    val feelsLike: Double,
    val condition: String, // e.g., "Sunny", "Rainy"
    val conditionIcon: String? = null, // Icon code from API
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int? = null,
    val pressure: Double? = null,
    val uvIndex: Double? = null,
    val visibility: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val forecastType: String = "current" // "current", "hourly", "daily"
)

