package com.group22.weatherForecastApp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val isFavorite: Boolean = false,
    val isCurrentLocation: Boolean = false,
    val order: Int = 0, // For sorting favorites
    val createdAt: Long = System.currentTimeMillis()
)

