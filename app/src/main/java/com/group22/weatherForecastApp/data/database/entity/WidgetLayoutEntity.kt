package com.group22.weatherForecastApp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_layouts")
data class WidgetLayoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val widgetType: String, // "current_weather", "forecast", "alerts", etc.
    val position: Int, // Order/position on screen
    val size: String = "medium", // "small", "medium", "large"
    val isVisible: Boolean = true,
    val config: String? = null // JSON string for widget-specific config
)

