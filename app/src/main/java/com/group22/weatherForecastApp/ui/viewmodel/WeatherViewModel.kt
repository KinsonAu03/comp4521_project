package com.group22.weatherForecastApp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group22.weatherForecastApp.data.PreferencesManager
import com.group22.weatherForecastApp.data.RetrofitClient
import com.group22.weatherForecastApp.data.TemperatureUnit
import com.group22.weatherForecastApp.data.ThemeMode
import com.group22.weatherForecastApp.data.WindSpeedUnit
import com.group22.weatherForecastApp.data.WeatherRepository
import com.group22.weatherForecastApp.data.database.WeatherDatabase
import com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity
import com.group22.weatherForecastApp.ui.components.AppError
import com.group22.weatherForecastApp.ui.components.ErrorType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import androidx.compose.runtime.mutableStateOf

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherDao = WeatherDatabase.getDatabase(application).weatherDataDao()
    private val locationDao = WeatherDatabase.getDatabase(application).locationDao()
    private val weatherApi = RetrofitClient.api
    private val weatherRepository = WeatherRepository(weatherApi, weatherDao)
    private val preferencesManager = PreferencesManager(application)

    // Get current location
    val currentLocation: Flow<com.group22.weatherForecastApp.data.database.entity.LocationEntity?> =
        locationDao.getUsingLocation()

    // Error state
    private val _errorState = MutableStateFlow<AppError?>(null)
    val errorState: StateFlow<AppError?> = _errorState.asStateFlow()

    // Loading state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    // Guard to prevent concurrent refresh calls
    private var isRefreshingInProgress = false

    // Temperature unit
    val temperatureUnit: StateFlow<TemperatureUnit> = flow {
        while (true) {
            emit(preferencesManager.getTemperatureUnit())
            kotlinx.coroutines.delay(100) // Check periodically
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        preferencesManager.getTemperatureUnit()
    )

    val themeMode : StateFlow<ThemeMode> = flow {
        while (true) {
            emit(preferencesManager.getThemeMode())
            kotlinx.coroutines.delay(100) // Check periodically
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        preferencesManager.getThemeMode()
    )
    val windUnit : StateFlow<WindSpeedUnit> = flow {
        while (true) {
            emit(preferencesManager.getWindSpeedUnit())
            kotlinx.coroutines.delay(100) // Check periodically
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        preferencesManager.getWindSpeedUnit()
    )

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
    private val _alerts =
        MutableStateFlow<List<com.group22.weatherForecastApp.data.AlertDetail>>(emptyList())
    val alerts: StateFlow<List<com.group22.weatherForecastApp.data.AlertDetail>> =
        _alerts.asStateFlow()

    fun setTemperatureUnit(unit: TemperatureUnit) {
        preferencesManager.setTemperatureUnit(unit)
    }

    fun convertTemperature(celsius: Double): Double {
        return preferencesManager.convertTemperature(
            celsius,
            preferencesManager.getTemperatureUnit()
        )
    }

    fun getTemperatureUnitSymbol(): String {
        return preferencesManager.getTemperatureUnitSymbol(preferencesManager.getTemperatureUnit())
    }

    fun setThemeMode(mode: ThemeMode) {
        preferencesManager.setThemeMode(mode)
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        preferencesManager.setWindSpeedUnit(unit)
    }

    fun convertWindSpeed(MS:Double):Double{
        return preferencesManager.convertWindSpeed(
            MS,preferencesManager.getWindSpeedUnit()
        )
    }

    fun getWindSpeedUnitSymbol(): String {
        return preferencesManager.getWindSpeedUnitSymbol(preferencesManager.getWindSpeedUnit())
    }

    fun clearError() {
        _errorState.value = null
    }

    // Refresh weather data
    fun refreshWeather() {
        // Prevent concurrent refresh calls
        if (isRefreshingInProgress) {
            return
        }
        
        viewModelScope.launch {
            isRefreshingInProgress = true
            _isRefreshing.value = true
            _errorState.value = null
            try {
                val location = currentLocation.first()
                if (location != null) {
                    try {
                        // Refresh weather data and get alerts from the same API call
                        val alerts = weatherRepository.refreshWeather(
                            location.id,
                            location.latitude,
                            location.longitude
                        )
                        // Set alerts from the response (no duplicate API call needed)
                        _alerts.value = alerts
                    } catch (e: Exception) {
                        handleError(e, "Failed to refresh weather data")
                    }
                } else {
                    _errorState.value = AppError(
                        ErrorType.LOCATION_ERROR,
                        "No location selected",
                        "Please select a location in Settings to view weather data"
                    )
                }
            } catch (e: Exception) {
                handleError(e, "Failed to refresh weather data")
            } finally {
                _isRefreshing.value = false
                isRefreshingInProgress = false
            }
        }
    }

    private fun handleError(e: Exception, defaultMessage: String) {
        val error = when (e) {
            is UnknownHostException, is IOException -> {
                AppError(
                    ErrorType.NETWORK_ERROR,
                    "No internet connection",
                    "Please check your internet connection and try again"
                )
            }

            is SocketTimeoutException -> {
                AppError(
                    ErrorType.NETWORK_ERROR,
                    "Connection timeout",
                    "The request took too long. Please try again"
                )
            }

            is retrofit2.HttpException -> {
                when (e.code()) {
                    401 -> AppError(
                        ErrorType.API_ERROR,
                        "Invalid API key",
                        "Please check your API configuration"
                    )

                    429 -> AppError(
                        ErrorType.API_ERROR,
                        "API rate limit exceeded",
                        "Too many requests. Please try again later"
                    )

                    500, 502, 503 -> AppError(
                        ErrorType.API_ERROR,
                        "Weather service unavailable",
                        "The weather service is temporarily down. Please try again later"
                    )

                    else -> AppError(
                        ErrorType.API_ERROR,
                        "API error (${e.code()})",
                        e.message()
                    )
                }
            }

            else -> {
                AppError(
                    ErrorType.UNKNOWN_ERROR,
                    defaultMessage,
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
        _errorState.value = error
    }

    // No automatic refresh in init - data is loaded by AppInitializer on app startup
    // Users can manually refresh via refresh button or pull-to-refresh
}

