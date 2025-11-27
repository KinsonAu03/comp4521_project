package com.group22.weatherForecastApp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.group22.weatherForecastApp.data.AppConstants
import com.group22.weatherForecastApp.data.database.DemoData
import com.group22.weatherForecastApp.data.database.WeatherDatabase
import com.group22.weatherForecastApp.data.database.entity.LocationEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * Handles app initialization tasks that need to run on startup
 * All tasks run asynchronously and the app waits for completion
 */
class AppInitializer(private val context: Context) {
    private val tag = "AppInitializer"
    private lateinit var database: WeatherDatabase
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val KEY_FIRST_LAUNCH = "first_launch"
    private val KEY_LOCATION_FETCHED = "location_fetched"

    /**
     * Initialize the app with async tasks
     * This is a suspend function that completes when all initialization is done
     */
    suspend fun initialize() {
        Log.d(tag, "Starting app initialization...")

        try {
            // Run all initialization tasks sequentially
            initializeDatabase()
            initializeLocation()
            initializeWeatherData()
            customInitialization()
            Log.d(tag, "App initialization complete")
        } catch (e: Exception) {
            Log.e(tag, "Error during app initialization", e)
        }
    }

    /**
     * Get the database instance (call after initialize())
     */
    fun getDatabase(): WeatherDatabase {
        return database
    }

    /**
     * Initialize database and demo data
     */
    private suspend fun initializeDatabase() {
        Log.d(tag, "Initializing database...")

        // Initialize database
        database = WeatherDatabase.getDatabase(context)

        // Initialize demo data
        val demoData = DemoData(
            database.locationDao(),
            database.weatherDataDao()
        )

        // Populate demo data
        demoData.initializeDemoData()

        Log.d(tag, "Database initialization complete")
    }

    /**
     * Initialize location - get current device location only on first launch
     * After first launch, user can manage locations via LocationManager screen
     */
    private suspend fun initializeLocation() {
        Log.d(tag, "Initializing location...")
        
        // Check if location was already fetched on first launch
        val locationFetched = prefs.getBoolean(KEY_LOCATION_FETCHED, false)
        if (locationFetched) {
            Log.d(tag, "Location already fetched on first launch, skipping auto-fetch")
            return
        }
        
        val locationService = LocationService(context)
        val locationDao = database.locationDao()
        
        // Check if location permission is granted
        if (!locationService.hasLocationPermission()) {
            Log.w(tag, "Location permission not granted, skipping location initialization")
            return
        }
        
        // Check if there's already a location set as is_using
        val usingLocation = locationDao.getUsingLocation().first()
        if (usingLocation != null) {
            Log.d(tag, "Location already set as is_using, skipping auto-fetch")
            prefs.edit().putBoolean(KEY_LOCATION_FETCHED, true).apply()
            return
        }
        
        try {
            // Get current location
            val locationResult = locationService.getCurrentLocation()
            
            if (locationResult != null) {
                Log.d(tag, "Current location retrieved: ${locationResult.name} (${locationResult.latitude}, ${locationResult.longitude})")
                
                // Clear any existing is_using flags
                locationDao.clearUsingLocation()
                
                // Check if this location already exists in database
                val existingLocations = locationDao.getAllLocations().first()
                val existingLocation = existingLocations.firstOrNull { location ->
                    // Check if location is within proximity threshold (~100m)
                    val latDiff = kotlin.math.abs(location.latitude - locationResult.latitude)
                    val lonDiff = kotlin.math.abs(location.longitude - locationResult.longitude)
                    latDiff < AppConstants.Location.PROXIMITY_THRESHOLD && 
                    lonDiff < AppConstants.Location.PROXIMITY_THRESHOLD
                }
                
                if (existingLocation != null) {
                    // Update existing location to be is_using
                    val updatedLocation = existingLocation.copy(isUsing = true, isCurrentLocation = true)
                    locationDao.updateLocation(updatedLocation)
                    Log.d(tag, "Updated existing location to is_using")
                } else {
                    // Insert new location as is_using
                    val newLocation = LocationEntity(
                        name = locationResult.name,
                        latitude = locationResult.latitude,
                        longitude = locationResult.longitude,
                        country = locationResult.country,
                        isCurrentLocation = true,
                        isUsing = true,
                        isFavorite = false,
                        order = 0
                    )
                    locationDao.insertLocation(newLocation)
                    Log.d(tag, "Inserted new location as is_using")
                }
                
                // Mark location as fetched
                prefs.edit().putBoolean(KEY_LOCATION_FETCHED, true).apply()
            } else {
                Log.w(tag, "Could not retrieve current location")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error initializing location", e)
        }
        
        Log.d(tag, "Location initialization complete")
    }

    /**
     * Initialize weather data - fetch from API
     * This is the only place where weather data is automatically fetched on app startup
     * Screens will read from the database, and users can manually refresh via refresh button/pull-to-refresh
     */
    private suspend fun initializeWeatherData() {
        Log.d(tag, "Initializing weather data...")
        val locationDao = database.locationDao()
        val weatherDao = database.weatherDataDao()

        val repository = WeatherRepository(RetrofitClient.api, weatherDao)

        // Fetch weather data for all saved locations
        val locations = locationDao.getAllLocations().first()
        
        if (locations.isNotEmpty()) {
            locations.forEach { location ->
                try {
                    repository.refreshWeather(
                        locationId = location.id,
                        lat = location.latitude,
                        lon = location.longitude
                    )
                    Log.d(tag, "Weather data loaded for location: ${location.name}")
                } catch (e: Exception) {
                    Log.e(tag, "Failed to load weather data for location: ${location.name}", e)
                    // Continue with other locations even if one fails
                }
            }
        } else {
            Log.d(tag, "No locations found, skipping weather data initialization")
        }

        Log.d(tag, "Weather data initialization complete")
    }


    /**
     * Add custom initialization tasks here
     * Call this from initialize() if you need additional setup
     */
    suspend fun customInitialization() {
        // Add your custom initialization logic here
        // For example:
        // - Fetch user preferences
        // - Initialize location services
        // - Load cached data
        // - Setup notifications
    }
}

