package com.group22.weatherForecastApp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group22.weatherForecastApp.data.RetrofitClient
import com.group22.weatherForecastApp.data.WeatherRepository
import com.group22.weatherForecastApp.data.database.WeatherDatabase
import com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherDao = WeatherDatabase.getDatabase(application).weatherDataDao()
    private val locationDao = WeatherDatabase.getDatabase(application).locationDao()
    private val weatherApi = RetrofitClient.api
    private val weatherRepository = WeatherRepository(weatherApi, weatherDao)
    
    // Get current location
    private val currentLocation = locationDao.getUsingLocation()
    
    // Current weather
    val currentWeather: Flow<WeatherDataEntity?> = currentLocation
        .flatMapLatest { location ->
            if (location != null) {
                weatherDao.getCurrentWeather(location.id)
            } else {
                flowOf(null)
            }
        }
    
    // Hourly forecast (24 hours)
    val hourlyForecast: Flow<List<WeatherDataEntity>> = currentLocation
        .flatMapLatest { location ->
            if (location != null) {
                weatherDao.getHourlyForecast(location.id)
            } else {
                flowOf(emptyList())
            }
        }
    
    // Daily forecast (7 days)
    val dailyForecast: Flow<List<WeatherDataEntity>> = currentLocation
        .flatMapLatest { location ->
            if (location != null) {
                weatherDao.getDailyForecast(location.id)
            } else {
                flowOf(emptyList())
            }
        }
    
    // Weather alerts - need to fetch from API since not stored in DB
    private val _alerts = MutableStateFlow<List<com.group22.weatherForecastApp.data.AlertDetail>>(emptyList())
    val alerts: StateFlow<List<com.group22.weatherForecastApp.data.AlertDetail>> = _alerts.asStateFlow()
    
    // Refresh weather data
    fun refreshWeather() {
        viewModelScope.launch {
            try {
                val location = currentLocation.first()
                if (location != null) {
                    weatherRepository.refreshWeather(location.id, location.latitude, location.longitude)
                    // Fetch alerts from API
                    try {
                        val response = weatherApi.getOneCallWeather(location.latitude, location.longitude)
                        _alerts.value = response.alerts ?: emptyList()
                    } catch (e: Exception) {
                        _alerts.value = emptyList()
                    }
                } else {
                    _alerts.value = emptyList()
                }
            } catch (e: Exception) {
                // Handle any errors silently
                _alerts.value = emptyList()
            }
        }
    }
    
    init {
        // Refresh on init
        refreshWeather()
    }
}

