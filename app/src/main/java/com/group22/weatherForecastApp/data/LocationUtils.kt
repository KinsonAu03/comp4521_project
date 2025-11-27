package com.group22.weatherForecastApp.data

import com.group22.weatherForecastApp.data.database.entity.LocationEntity

/**
 * Utility functions for location-related operations
 */
object LocationUtils {
    
    /**
     * Check if two locations are nearby based on proximity threshold
     * 
     * @param lat1 Latitude of first location
     * @param lon1 Longitude of first location
     * @param lat2 Latitude of second location
     * @param lon2 Longitude of second location
     * @param threshold Proximity threshold in degrees (default: ~100m)
     * @return true if locations are within threshold distance
     */
    fun areLocationsNearby(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        threshold: Double = AppConstants.Location.PROXIMITY_THRESHOLD
    ): Boolean {
        val latDiff = kotlin.math.abs(lat1 - lat2)
        val lonDiff = kotlin.math.abs(lon1 - lon2)
        return latDiff < threshold && lonDiff < threshold
    }
    
    /**
     * Find a location in a list that is nearby the target coordinates
     * 
     * @param locations List of locations to search
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param threshold Proximity threshold in degrees (default: ~100m)
     * @return The first nearby location found, or null if none found
     */
    fun findNearbyLocation(
        locations: List<LocationEntity>,
        targetLat: Double,
        targetLon: Double,
        threshold: Double = AppConstants.Location.PROXIMITY_THRESHOLD
    ): LocationEntity? {
        return locations.firstOrNull { location ->
            areLocationsNearby(
                location.latitude,
                location.longitude,
                targetLat,
                targetLon,
                threshold
            )
        }
    }
}

