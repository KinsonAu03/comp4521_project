package com.group22.weatherForecastApp.data

import android.content.Context
import android.content.SharedPreferences

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
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
            TemperatureUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32.0
        }
    }
    
    fun getTemperatureUnitSymbol(unit: TemperatureUnit): String {
        return when (unit) {
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.FAHRENHEIT -> "°F"
        }
    }
}

