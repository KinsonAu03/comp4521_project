package com.group22.weatherForecastApp.data

/**
 * Application-wide constants
 * Centralizes magic numbers and configuration values
 */
object AppConstants {
    
    /**
     * Location-related constants
     */
    object Location {
        /**
         * Proximity threshold for comparing locations (in degrees)
         * Approximately 100 meters
         */
        const val PROXIMITY_THRESHOLD = 0.001
        
        /**
         * Maximum number of favorite locations allowed
         */
        const val MAX_FAVORITES = 5
        
        /**
         * Default geocoding search result limit
         */
        const val GEOCODING_SEARCH_LIMIT = 5
    }
    
    /**
     * Network-related constants
     */
    object Network {
        /**
         * Connection timeout in seconds
         */
        const val CONNECT_TIMEOUT_SECONDS = 30L
        
        /**
         * Read timeout in seconds
         */
        const val READ_TIMEOUT_SECONDS = 30L
        
        /**
         * Write timeout in seconds
         */
        const val WRITE_TIMEOUT_SECONDS = 30L
    }
    
    /**
     * Weather forecast constants
     */
    object Weather {
        /**
         * Number of hours in hourly forecast
         */
        const val HOURLY_FORECAST_HOURS = 24
        
        /**
         * Number of days in daily forecast
         */
        const val DAILY_FORECAST_DAYS = 7
    }
    
    /**
     * ViewModel polling constants
     */
    object ViewModel {
        /**
         * Delay in milliseconds for polling preferences
         * Note: This should be replaced with proper StateFlow updates
         */
        const val PREFERENCE_POLLING_DELAY_MS = 100L
    }
    
    /**
     * Unit conversion constants
     */
    object Conversion {
        /**
         * Celsius to Fahrenheit conversion multiplier
         */
        const val CELSIUS_TO_FAHRENHEIT_MULTIPLIER = 9.0 / 5.0
        
        /**
         * Celsius to Fahrenheit offset
         */
        const val CELSIUS_TO_FAHRENHEIT_OFFSET = 32.0
        
        /**
         * Meters per second to kilometers per hour multiplier
         */
        const val MS_TO_KMH_MULTIPLIER = 3.6
        
        /**
         * Meters per second to miles per hour multiplier
         */
        const val MS_TO_MPH_MULTIPLIER = 2.23694
    }
    
    /**
     * UI constants
     */
    object UI {
        /**
         * Default padding in dp
         */
        const val DEFAULT_PADDING_DP = 24
    }
}

