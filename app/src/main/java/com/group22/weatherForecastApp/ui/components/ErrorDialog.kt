package com.group22.weatherForecastApp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ErrorType {
    NETWORK_ERROR,
    API_ERROR,
    LOCATION_ERROR,
    DATABASE_ERROR,
    UNKNOWN_ERROR
}

data class AppError(
    val type: ErrorType,
    val message: String,
    val details: String? = null
)

@Composable
fun ErrorDialog(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    if (error != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = when (error.type) {
                        ErrorType.NETWORK_ERROR -> "Connection Error"
                        ErrorType.API_ERROR -> "API Error"
                        ErrorType.LOCATION_ERROR -> "Location Error"
                        ErrorType.DATABASE_ERROR -> "Database Error"
                        ErrorType.UNKNOWN_ERROR -> "Error"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = error.message)
                    error.details?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Troubleshooting:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (error.type) {
                            ErrorType.NETWORK_ERROR -> 
                                "• Check your internet connection\n" +
                                "• Try again later\n" +
                                "• Restart the app if problem persists"
                            ErrorType.API_ERROR -> 
                                "• The weather service may be temporarily unavailable\n" +
                                "• Try again in a few minutes\n" +
                                "• Restart the app if problem persists"
                            ErrorType.LOCATION_ERROR -> 
                                "• Check location permissions in Settings\n" +
                                "• Enable location services\n" +
                                "• Try adding location manually"
                            ErrorType.DATABASE_ERROR -> 
                                "• Try restarting the app\n" +
                                "• If problem persists, reinstall the app\n" +
                                "• Contact support if issue continues"
                            ErrorType.UNKNOWN_ERROR -> 
                                "• Try restarting the app\n" +
                                "• If problem persists, reinstall the app\n" +
                                "• Contact support if issue continues"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    if (error.type == ErrorType.NETWORK_ERROR || error.type == ErrorType.API_ERROR) {
                        Button(onClick = {
                            onRetry()
                            onDismiss()
                        }) {
                            Text("Try Again")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onContactSupport) {
                    Text("Contact Support")
                }
            }
        )
    }
}

