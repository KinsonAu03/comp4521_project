package com.group22.weatherForecastApp.ui.utils

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Utility composable for handling location permissions
 * Returns a launcher that can be used to request location permissions
 * 
 * @param onPermissionResult Callback that receives true if permission was granted, false otherwise
 * @return ActivityResultLauncher that can be called with launch() to request permissions
 */
@Composable
fun rememberLocationPermissionLauncher(
    onPermissionResult: (Boolean) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
    val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    
    val granted = fineLocationGranted || coarseLocationGranted
    onPermissionResult(granted)
}

/**
 * Location permissions array for easy reuse
 */
val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

