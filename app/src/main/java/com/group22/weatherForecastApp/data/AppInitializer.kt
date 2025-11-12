package com.group22.weatherForecastApp.data

import android.content.Context
import android.util.Log
import com.group22.weatherForecastApp.data.database.DemoData
import com.group22.weatherForecastApp.data.database.WeatherDatabase
import kotlinx.coroutines.delay

/**
 * Handles app initialization tasks that need to run on startup
 * All tasks run asynchronously and the app waits for completion
 */
class AppInitializer(private val context: Context) {
    private val tag = "AppInitializer"
    private lateinit var database: WeatherDatabase

    /**
     * Initialize the app with async tasks
     * This is a suspend function that completes when all initialization is done
     */
    suspend fun initialize() {
        Log.d(tag, "Starting app initialization...")
        
        try {
            // Run all initialization tasks sequentially
            initializeDatabase()
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
            database.weatherDataDao(),
            database.widgetLayoutDao()
        )
        
        // Populate demo data
        demoData.initializeDemoData()
        
        Log.d(tag, "Database initialization complete")
    }

    /**
     * Initialize weather data - fetch from API, etc.
     * This is where you would call your weather API
     */
    private suspend fun initializeWeatherData() {
        Log.d(tag, "Initializing weather data...")
        // TODO: Add your weather API fetching logic here
        // Example:
        // val weatherRepository = WeatherRepository()
        // weatherRepository.fetchCurrentWeather()
        
        // Simulate API call delay (remove this when you add real API calls)
        kotlinx.coroutines.delay(1000)
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

