package com.group22.weatherForecastApp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group22.weatherForecastApp.data.GeocodingClient
import com.group22.weatherForecastApp.data.GeocodingResponse
import com.group22.weatherForecastApp.data.LocationService
import com.group22.weatherForecastApp.data.database.WeatherDatabase
import com.group22.weatherForecastApp.data.database.entity.LocationEntity
import com.group22.weatherForecastApp.ui.components.AppError
import com.group22.weatherForecastApp.ui.components.ErrorType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationDao = WeatherDatabase.getDatabase(application).locationDao()
    private val geocodingApi = GeocodingClient.api
    private val locationService = LocationService(application)

    // Flow of all locations
    val allLocations: Flow<List<LocationEntity>> = locationDao.getAllLocations()
    
    // Flow of currently using location
    val usingLocation: Flow<LocationEntity?> = locationDao.getUsingLocation()
    
    // Flow of favorite locations (is_using = false, max 5)
    val favoriteLocations: Flow<List<LocationEntity>> = locationDao.getFavoriteLocations()

    // Search results
    private val _searchResults = MutableStateFlow<List<GeocodingResponse>>(emptyList())
    val searchResults: StateFlow<List<GeocodingResponse>> = _searchResults.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state - using structured AppError instead of String
    private val _errorState = MutableStateFlow<AppError?>(null)
    val errorState: StateFlow<AppError?> = _errorState.asStateFlow()

    /**
     * Search for locations by name
     */
    fun searchLocations(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _errorState.value = null
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                val results = geocodingApi.searchLocations(query, limit = 5)
                _searchResults.value = results
                // Clear error on success
                if (results.isEmpty()) {
                    _errorState.value = AppError(
                        ErrorType.API_ERROR,
                        "No locations found",
                        "Try searching with a different name or check your spelling"
                    )
                }
            } catch (e: Exception) {
                handleError(e, "Failed to search locations")
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get current device location
     */
    fun getCurrentDeviceLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                if (!locationService.hasLocationPermission()) {
                    _errorState.value = AppError(
                        ErrorType.LOCATION_ERROR,
                        "Location permission not granted",
                        "Please grant location permission to use this feature"
                    )
                    return@launch
                }

                val locationResult = locationService.getCurrentLocation()
                if (locationResult != null) {
                    // Check if location already exists
                    val existingLocations = locationDao.getAllLocations().first()
                    val existing = existingLocations.firstOrNull { location ->
                        val latDiff = kotlin.math.abs(location.latitude - locationResult.latitude)
                        val lonDiff = kotlin.math.abs(location.longitude - locationResult.longitude)
                        latDiff < 0.001 && lonDiff < 0.001
                    }

                    if (existing == null) {
                        // Add new location
                        addLocation(
                            name = locationResult.name,
                            latitude = locationResult.latitude,
                            longitude = locationResult.longitude,
                            country = locationResult.country,
                            setAsUsing = true
                        )
                    } else {
                        // Set existing as using
                        setUsingLocation(existing.id)
                    }
                } else {
                    _errorState.value = AppError(
                        ErrorType.LOCATION_ERROR,
                        "Could not retrieve location",
                        "Please ensure location services are enabled and try again"
                    )
                }
            } catch (e: Exception) {
                handleError(e, "Failed to get current location")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add a location from search results
     */
    fun addLocationFromSearch(geocodingResponse: GeocodingResponse, setAsUsing: Boolean = false) {
        addLocation(
            name = geocodingResponse.name,
            latitude = geocodingResponse.lat,
            longitude = geocodingResponse.lon,
            country = geocodingResponse.country,
            state = geocodingResponse.state,
            setAsUsing = setAsUsing
        )
    }

    /**
     * Add a location to the database
     */
    private fun addLocation(
        name: String,
        latitude: Double,
        longitude: Double,
        country: String? = null,
        state: String? = null,
        setAsUsing: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                // Check if location already exists
                val existingLocations = locationDao.getAllLocations().first()
                val existing = existingLocations.firstOrNull { location ->
                    val latDiff = kotlin.math.abs(location.latitude - latitude)
                    val lonDiff = kotlin.math.abs(location.longitude - longitude)
                    latDiff < 0.001 && lonDiff < 0.001
                }

                if (existing != null) {
                    if (setAsUsing) {
                        setUsingLocation(existing.id)
                    }
                    return@launch
                }

                // If setting as using, clear current using location
                if (setAsUsing) {
                    locationDao.clearUsingLocation()
                } else {
                    // Check if we can add more favorites (max 5)
                    val favoriteCount = locationDao.getFavoriteLocationsCount()
                    if (favoriteCount >= 5) {
                        _errorState.value = AppError(
                            ErrorType.UNKNOWN_ERROR,
                            "Maximum favorite locations reached",
                            "You can have up to 5 favorite locations. Please remove one before adding another."
                        )
                        return@launch
                    }
                }

                val locationName = if (state != null && state.isNotBlank()) {
                    "$name, $state"
                } else {
                    name
                }

                val newLocation = LocationEntity(
                    name = locationName,
                    latitude = latitude,
                    longitude = longitude,
                    country = country,
                    isUsing = setAsUsing,
                    isFavorite = !setAsUsing,
                    isCurrentLocation = false,
                    order = if (setAsUsing) 0 else locationDao.getFavoriteLocationsCount()
                )

                locationDao.insertLocation(newLocation)
                _searchResults.value = emptyList() // Clear search results
            } catch (e: Exception) {
                handleError(e, "Failed to add location")
            }
        }
    }

    /**
     * Set a location as the one being used (is_using = true)
     */
    fun setUsingLocation(locationId: Long) {
        viewModelScope.launch {
            try {
                locationDao.clearUsingLocation()
                locationDao.setUsingLocation(locationId)
            } catch (e: Exception) {
                handleError(e, "Failed to set location")
            }
        }
    }

    /**
     * Delete a location
     */
    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            try {
                locationDao.deleteLocationById(locationId)
            } catch (e: Exception) {
                handleError(e, "Failed to delete location")
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _errorState.value = null
    }

    /**
     * Handle errors and convert them to structured AppError
     */
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

            is HttpException -> {
                when (e.code()) {
                    401 -> AppError(
                        ErrorType.API_ERROR,
                        "Invalid API key",
                        "Please check your API configuration"
                    )

                    429 -> AppError(
                        ErrorType.API_ERROR,
                        "API rate limit exceeded",
                        "Too many requests. Please wait a moment and try again"
                    )

                    404 -> AppError(
                        ErrorType.API_ERROR,
                        "Location not found",
                        "The location you searched for could not be found. Try a different search term."
                    )

                    500, 502, 503 -> AppError(
                        ErrorType.API_ERROR,
                        "Service unavailable",
                        "The location service is temporarily down. Please try again later"
                    )

                    else -> AppError(
                        ErrorType.API_ERROR,
                        "API error (${e.code()})",
                        e.message() ?: "An error occurred while processing your request"
                    )
                }
            }

            is android.database.sqlite.SQLiteException, is androidx.room.RoomDatabaseException -> {
                AppError(
                    ErrorType.DATABASE_ERROR,
                    "Database error",
                    "An error occurred while saving data. Please try again."
                )
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
}

