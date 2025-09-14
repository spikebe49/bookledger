package com.juan.bookledger.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.ui.viewmodel.SimpleSettingsViewModel
import com.juan.bookledger.ui.screens.CloudConfigurationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSettingsScreen(
    navController: NavController,
    viewModel: SimpleSettingsViewModel = hiltViewModel()
) {
    val currency by viewModel.currency.collectAsState(initial = "CAD")
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState(initial = true)
    val cloudBackupEnabled by viewModel.cloudBackupEnabled.collectAsState(initial = false)
    val customCategories by viewModel.customCategories.collectAsState(initial = emptyList())
    val syncStatus by viewModel.getSyncStatus().collectAsState(initial = com.juan.bookledger.data.sync.SyncStatus.IDLE)
    val lastSyncTime by viewModel.getLastSyncTime().collectAsState(initial = null)
    
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var newCategory by remember { mutableStateOf("") }
    var showSyncDialog by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }
    var showCloudConfigDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Currency Settings
            CurrencySettingsCard(
                currency = currency,
                onCurrencyClick = { showCurrencyDialog = true }
            )
            
            // Notification Settings
            NotificationSettingsCard(
                notificationsEnabled = notificationsEnabled,
                onToggleNotifications = { viewModel.setNotificationsEnabled(it) }
            )
            
            // Cloud Backup Settings
            CloudBackupSettingsCard(
                cloudBackupEnabled = cloudBackupEnabled,
                onToggleCloudBackup = { 
                    viewModel.setCloudBackupEnabled(it)
                    if (it) {
                        viewModel.startRealtimeSync()
                    } else {
                        viewModel.stopRealtimeSync()
                    }
                },
                onForceSync = { showSyncDialog = true },
                isSyncing = isSyncing,
                onConfigureCloudStorage = { navController.navigate("sync_settings") },
                syncStatus = syncStatus,
                lastSyncTime = lastSyncTime
            )
            
            // AI Configuration
            AIConfigurationCard(
                onConfigureAI = { navController.navigate("ai_configuration") }
            )
            
            // Additional Settings
            AdditionalSettingsCard(
                onNotificationSettings = { navController.navigate("notification_settings") },
                onAnalytics = { navController.navigate("analytics") },
                onExport = { navController.navigate("export") }
            )
            
            // Account Settings
            AccountSettingsCard(
                onLogout = { viewModel.logout() }
            )
            
            // Custom Categories
            CustomCategoriesCard(
                categories = customCategories,
                onAddCategory = { showCategoryDialog = true },
                onRemoveCategory = { viewModel.removeCustomCategory(it) }
            )
            
            // App Information
            AppInfoCard()
        }
    }
    
    // Currency Selection Dialog
    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentCurrency = currency,
            onCurrencySelected = { 
                viewModel.setCurrency(it)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }
    
    // Add Category Dialog
    if (showCategoryDialog) {
        AddCategoryDialog(
            onCategoryAdded = { category ->
                if (category.isNotBlank()) {
                    viewModel.addCustomCategory(category)
                    newCategory = ""
                }
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false }
        )
    }
    
    // Sync Dialog
    if (showSyncDialog) {
        SyncDialog(
            onSync = { 
                isSyncing = true
                // TODO: Implement actual sync logic
                isSyncing = false
                showSyncDialog = false
            },
            onDismiss = { showSyncDialog = false }
        )
    }
    
    // Cloud Configuration Dialog
    if (showCloudConfigDialog) {
        CloudConfigurationDialog(
            onDismiss = { showCloudConfigDialog = false }
        )
    }
}

@Composable
private fun CurrencySettingsCard(
    currency: String,
    onCurrencyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Currency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Default Currency",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currency,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onCurrencyClick) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Change Currency")
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsCard(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Enable Notifications",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Weekly reminders and breakeven alerts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )
            }
        }
    }
}

@Composable
private fun CloudBackupSettingsCard(
    cloudBackupEnabled: Boolean,
    onToggleCloudBackup: (Boolean) -> Unit,
    onForceSync: () -> Unit,
    isSyncing: Boolean,
    onConfigureCloudStorage: () -> Unit,
    syncStatus: com.juan.bookledger.data.sync.SyncStatus,
    lastSyncTime: java.util.Date?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Cloud Backup",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Enable Cloud Backup",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Sync data across devices with PocketBase, OneDrive, and Google Drive",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = cloudBackupEnabled,
                    onCheckedChange = onToggleCloudBackup
                )
            }
            
            if (cloudBackupEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sync Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status: ${getSyncStatusText(syncStatus)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = getSyncStatusColor(syncStatus)
                    )
                    
                    lastSyncTime?.let { time ->
                        Text(
                            text = "Last sync: ${formatTime(time)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onForceSync,
                        enabled = !isSyncing,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Syncing...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Force Sync")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onConfigureCloudStorage,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync Settings")
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomCategoriesCard(
    categories: List<String>,
    onAddCategory: () -> Unit,
    onRemoveCategory: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Custom Categories",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onAddCategory) {
                    Icon(Icons.Default.Add, contentDescription = "Add Category")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (categories.isEmpty()) {
                Text(
                    text = "No custom categories yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                categories.forEach { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        IconButton(onClick = { onRemoveCategory(category) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Category")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "App Information",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AppInfoItem(
                label = "Version",
                value = "1.0.0"
            )
            
            AppInfoItem(
                label = "Build",
                value = "Debug"
            )
            
            AppInfoItem(
                label = "Developer",
                value = "Juan"
            )
        }
    }
}

@Composable
private fun AppInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CurrencySelectionDialog(
    currentCurrency: String,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currencies = listOf("CAD", "USD", "EUR", "GBP", "JPY", "AUD")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency") },
        text = {
            Column {
                currencies.forEach { currency ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currency,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        RadioButton(
                            selected = currency == currentCurrency,
                            onClick = { onCurrencySelected(currency) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
private fun AddCategoryDialog(
    onCategoryAdded: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Category") },
        text = {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category Name") },
                placeholder = { Text("Enter category name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCategoryAdded(category) },
                enabled = category.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SyncDialog(
    onSync: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Force Sync") },
        text = {
            Column {
                Text("This will sync all your data with the cloud backup service.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Upload local changes to cloud\n• Download remote changes\n• Resolve any conflicts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSync) {
                Text("Sync Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AIConfigurationCard(
    onConfigureAI: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Configuration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Configure Gemini AI for enhanced insights",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onConfigureAI) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Configure AI",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun AdditionalSettingsCard(
    onNotificationSettings: () -> Unit,
    onAnalytics: () -> Unit,
    onExport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Additional Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notification Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNotificationSettings() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Notification Settings")
                }
                Icon(Icons.Default.ArrowForward, contentDescription = "Go to notifications")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Analytics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnalytics() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Analytics",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Analytics")
                }
                Icon(Icons.Default.ArrowForward, contentDescription = "Go to analytics")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Export
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExport() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Export",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Export Data")
                }
                Icon(Icons.Default.ArrowForward, contentDescription = "Go to export")
            }
        }
    }
}

@Composable
private fun AccountSettingsCard(
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logout Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Logout",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
