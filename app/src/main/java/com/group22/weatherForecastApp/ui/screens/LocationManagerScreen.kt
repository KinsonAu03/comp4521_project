package com.group22.weatherForecastApp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group22.weatherForecastApp.data.AppConstants
import com.group22.weatherForecastApp.data.GeocodingResponse
import com.group22.weatherForecastApp.data.database.entity.LocationEntity
import com.group22.weatherForecastApp.ui.components.ErrorDialog
import com.group22.weatherForecastApp.ui.components.ErrorType
import com.group22.weatherForecastApp.ui.utils.LOCATION_PERMISSIONS
import com.group22.weatherForecastApp.ui.utils.rememberLocationPermissionLauncher
import com.group22.weatherForecastApp.ui.viewmodel.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationManagerScreen(
    onNavigateBack: () -> Unit,
    viewModel: LocationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val allLocations by viewModel.allLocations.collectAsState(initial = emptyList())
    val usingLocation by viewModel.usingLocation.collectAsState(initial = null)
    val favoriteLocations by viewModel.favoriteLocations.collectAsState(initial = emptyList())
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.errorState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    
    // Permission launcher for location permissions
    val locationPermissionLauncher = rememberLocationPermissionLauncher { granted ->
        if (granted) {
            // Permission granted, automatically retry getting location
            viewModel.getCurrentDeviceLocation()
        } else {
            // Permission denied, clear error
            viewModel.clearError()
        }
    }
    
    // Check if error is related to permission
    val isPermissionError = errorState?.type == ErrorType.LOCATION_ERROR

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    if (query.isNotBlank()) {
                        viewModel.searchLocations(query)
                        showSearchResults = true
                    } else {
                        showSearchResults = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search location") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            showSearchResults = false
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show search results
            if (showSearchResults && searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(8.dp)
                        )
                        searchResults.forEach { result ->
                            SearchResultItem(
                                result = result,
                                onAdd = { setAsUsing ->
                                    viewModel.addLocationFromSearch(result, setAsUsing)
                                    searchQuery = ""
                                    showSearchResults = false
                                }
                            )
                            Divider()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Get current location button
            Button(
                onClick = { viewModel.getCurrentDeviceLocation() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get Current Location")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message card (for non-critical errors that can be shown inline)
            if (errorState != null && errorState.type != ErrorType.LOCATION_ERROR) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorState.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearError() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss")
                            }
                        }
                        errorState.details?.let { details ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = details,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Currently using location
            Text(
                text = "Currently Using",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (usingLocation != null) {
                LocationCard(
                    location = usingLocation!!,
                    isUsing = true,
                    onSetUsing = { viewModel.setUsingLocation(usingLocation!!.id) },
                    onDelete = { viewModel.deleteLocation(usingLocation!!.id) }
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "No location set",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Favorite locations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Favorites (${favoriteLocations.size}/${AppConstants.Location.MAX_FAVORITES})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (favoriteLocations.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "No favorite locations. Add locations using search above.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteLocations) { location ->
                        LocationCard(
                            location = location,
                            isUsing = false,
                            onSetUsing = { viewModel.setUsingLocation(location.id) },
                            onDelete = { viewModel.deleteLocation(location.id) }
                        )
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // Error Dialog for critical errors (location errors, network errors, etc.)
        ErrorDialog(
            error = errorState?.takeIf { it.type == ErrorType.LOCATION_ERROR || it.type == ErrorType.NETWORK_ERROR || it.type == ErrorType.API_ERROR },
            onDismiss = { viewModel.clearError() },
            onRetry = {
                when (errorState?.type) {
                    ErrorType.LOCATION_ERROR -> {
                        if (isPermissionError) {
                            locationPermissionLauncher.launch(LOCATION_PERMISSIONS)
                        } else {
                            viewModel.getCurrentDeviceLocation()
                        }
                    }
                    ErrorType.NETWORK_ERROR, ErrorType.API_ERROR -> {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchLocations(searchQuery)
                        }
                    }
                    else -> {}
                }
            },
            onContactSupport = {
                // TODO: Implement contact support
                viewModel.clearError()
            }
        )
    }
}

@Composable
fun LocationCard(
    location: LocationEntity,
    isUsing: Boolean,
    onSetUsing: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUsing) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (isUsing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isUsing) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (location.country != null) {
                    Text(
                        text = location.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                if (!isUsing) {
                    IconButton(onClick = onSetUsing) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Set as using",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (!isUsing) {
                            DropdownMenuItem(
                                text = { Text("Set as Using") },
                                onClick = {
                                    onSetUsing()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    result: GeocodingResponse,
    onAdd: (Boolean) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd(false) }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.name,
                style = MaterialTheme.typography.bodyLarge
            )
            if (result.state != null) {
                Text(
                    text = result.state,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (result.country != null) {
                Text(
                    text = result.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add location")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Add as Favorite") },
                    onClick = {
                        onAdd(false)
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Set as Using") },
                    onClick = {
                        onAdd(true)
                        showMenu = false
                    }
                )
            }
        }
    }
}

