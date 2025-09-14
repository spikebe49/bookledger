package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudConfigurationDialog(
    onDismiss: () -> Unit
) {
    var selectedProvider by remember { mutableStateOf("PocketBase") }
    var pocketBaseUrl by remember { mutableStateOf("http://127.0.0.1:8090") }
    var oneDriveToken by remember { mutableStateOf("") }
    var googleDriveToken by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Cloud Storage") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Provider Selection
                Text(
                    text = "Select Cloud Provider:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                ExposedDropdownMenuBox(
                    expanded = showDropdown,
                    onExpandedChange = { showDropdown = !showDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedProvider,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Provider") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        listOf("PocketBase", "OneDrive", "Google Drive").forEach { provider ->
                            DropdownMenuItem(
                                text = { Text(provider) },
                                onClick = { 
                                    selectedProvider = provider
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }
                
                // Configuration based on provider
                when (selectedProvider) {
                    "PocketBase" -> {
                        OutlinedTextField(
                            value = pocketBaseUrl,
                            onValueChange = { pocketBaseUrl = it },
                            label = { Text("PocketBase URL") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("http://127.0.0.1:8090") }
                        )
                        Text(
                            text = "Enter your PocketBase server URL",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    "OneDrive" -> {
                        OutlinedTextField(
                            value = oneDriveToken,
                            onValueChange = { oneDriveToken = it },
                            label = { Text("Access Token") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter OneDrive access token") }
                        )
                        Text(
                            text = "Get your access token from OneDrive Developer Console",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    "Google Drive" -> {
                        OutlinedTextField(
                            value = googleDriveToken,
                            onValueChange = { googleDriveToken = it },
                            label = { Text("Access Token") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter Google Drive access token") }
                        )
                        Text(
                            text = "Get your access token from Google Cloud Console",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // TODO: Save cloud configuration
                    onDismiss()
                }
            ) {
                Text("Save Configuration")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
