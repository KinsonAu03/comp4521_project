package com.group22.weatherForecastApp.data

import android.content.Context
import android.content.SharedPreferences
import com.group22.weatherForecastApp.data.AppConstants

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

enum class WindSpeedUnit {
    KMH, MS, MPH
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_WIND_UNIT = "wind_speed_unit"
        private const val KEY_THEME_MODE = "theme_mode"
    }
    
    fun getTemperatureUnit(): TemperatureUnit {
        val unitString = prefs.getString(KEY_TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name) ?: TemperatureUnit.CELSIUS.name
        return try {
            TemperatureUnit.valueOf(unitString)
        } catch (e: Exception) {
            TemperatureUnit.CELSIUS
        }
    }
    
    fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit().putString(KEY_TEMPERATURE_UNIT, unit.name).apply()
    }
    
    fun convertTemperature(celsius: Double, unit: TemperatureUnit): Double {
        return when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * AppConstants.Conversion.CELSIUS_TO_FAHRENHEIT_MULTIPLIER) + 
                    AppConstants.Conversion.CELSIUS_TO_FAHRENHEIT_OFFSET
        }
    }
    
    fun getTemperatureUnitSymbol(unit: TemperatureUnit): String {
        return when (unit) {
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.FAHRENHEIT -> "°F"
        }
    }

    fun getWindSpeedUnit(): WindSpeedUnit {
        val value = prefs.getString(KEY_WIND_UNIT, WindSpeedUnit.KMH.name) ?: WindSpeedUnit.KMH.name
        return try {
            WindSpeedUnit.valueOf(value)
        } catch (e: IllegalArgumentException) {
            // If stored value is invalid, return default
            WindSpeedUnit.KMH
        }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        prefs.edit().putString(KEY_WIND_UNIT, unit.name).apply()
    }

    fun convertWindSpeed(speedInMs: Double, unit: WindSpeedUnit): Double {
        return when (unit) {
            WindSpeedUnit.MS -> speedInMs
            WindSpeedUnit.KMH -> speedInMs * AppConstants.Conversion.MS_TO_KMH_MULTIPLIER
            WindSpeedUnit.MPH -> speedInMs * AppConstants.Conversion.MS_TO_MPH_MULTIPLIER
        }
    }

    fun getWindSpeedUnitSymbol(unit: WindSpeedUnit): String {
        return when (unit) {
            WindSpeedUnit.MS -> "m/s"
            WindSpeedUnit.KMH -> "km/h"
            WindSpeedUnit.MPH -> "mph"
        }
    }

    fun getThemeMode(): ThemeMode {
        val value = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(value)
        } catch (e: IllegalArgumentException) {
            // If stored value is invalid, return default
            ThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }
}

