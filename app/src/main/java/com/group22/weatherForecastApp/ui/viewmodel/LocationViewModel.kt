package com.group22.weatherForecastApp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group22.weatherForecastApp.data.GeocodingClient
import com.group22.weatherForecastApp.data.GeocodingResponse
import com.group22.weatherForecastApp.data.LocationService
import com.group22.weatherForecastApp.data.database.WeatherDatabase
import com.group22.weatherForecastApp.data.database.entity.LocationEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Search for locations by name
     */
    fun searchLocations(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val results = geocodingApi.searchLocations(query, limit = 5)
                _searchResults.value = results
            } catch (e: Exception) {
                _errorMessage.value = "Error searching locations: ${e.message}"
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
            _errorMessage.value = null
            try {
                if (!locationService.hasLocationPermission()) {
                    _errorMessage.value = "Location permission not granted"
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
                    _errorMessage.value = "Could not retrieve location"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error getting location: ${e.message}"
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
                        _errorMessage.value = "Maximum 5 favorite locations allowed"
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
                _errorMessage.value = "Error adding location: ${e.message}"
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
                _errorMessage.value = "Error setting location: ${e.message}"
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
                _errorMessage.value = "Error deleting location: ${e.message}"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

