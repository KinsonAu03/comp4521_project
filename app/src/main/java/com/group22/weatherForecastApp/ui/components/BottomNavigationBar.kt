package com.group22.weatherForecastApp.ui.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.group22.weatherForecastApp.navigation.NavRoutes

data class BottomNavItem(
        val route: String,
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BottomNavigationBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    val items =
            listOf(
                    BottomNavItem(
                            route = NavRoutes.Home.route,
                            label = "Home",
                            icon = Icons.Default.Home
                    ),
                    BottomNavItem(
                            route = NavRoutes.WeatherDetails.route,
                            label = "Details",
                            icon = Icons.Default.Info
                    ),
                    BottomNavItem(
                            route = NavRoutes.Settings.route,
                            label = "Settings",
                            icon = Icons.Default.Settings
                    )
            )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {
                        Log.d("BottomNav", "Clicked on: ${item.label} (route: ${item.route})")
                        Log.d("BottomNav", "Current route before navigation: $currentRoute")
                        onNavigate(item.route)
                        Log.d("BottomNav", "Navigation triggered for: ${item.route}")
                    }
            )
        }
    }
}
