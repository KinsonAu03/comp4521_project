package com.group22.weatherForecastApp.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

/**
 * Service for handling location-related operations
 * Uses FusedLocationProviderClient from Google Play Services
 */
class LocationService(private val context: Context) {
    private val tag = "LocationService"
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get the current device location
     * Returns null if permission is not granted or location cannot be retrieved
     */
    suspend fun getCurrentLocation(): LocationResult? {
        if (!hasLocationPermission()) {
            Log.w(tag, "Location permission not granted")
            return null
        }

        return try {
            val cancellationTokenSource = CancellationTokenSource()
            
            val locationResult = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (locationResult != null) {
                Log.d(tag, "Location retrieved: lat=${locationResult.latitude}, lon=${locationResult.longitude}")
                
                // Get address using Geocoder
                val address = getAddressFromLocation(locationResult.latitude, locationResult.longitude)
                
                LocationResult(
                    latitude = locationResult.latitude,
                    longitude = locationResult.longitude,
                    name = address?.locality ?: "Current Location",
                    country = address?.countryCode
                )
            } else {
                Log.w(tag, "Location result is null")
                null
            }
        } catch (e: SecurityException) {
            Log.e(tag, "Security exception while getting location", e)
            null
        } catch (e: Exception) {
            Log.e(tag, "Error getting location", e)
            null
        }
    }

    /**
     * Get address from coordinates using Geocoder
     */
    private fun getAddressFromLocation(latitude: Double, longitude: Double): android.location.Address? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()
        } catch (e: Exception) {
            Log.e(tag, "Error getting address from location", e)
            null
        }
    }

    /**
     * Data class to hold location result
     */
    data class LocationResult(
        val latitude: Double,
        val longitude: Double,
        val name: String,
        val country: String? = null
    )
}

