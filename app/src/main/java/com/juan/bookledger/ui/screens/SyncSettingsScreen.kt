package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Date
import com.juan.bookledger.data.sync.SyncManager
import com.juan.bookledger.data.sync.SyncProvider
import com.juan.bookledger.data.sync.SyncSettings
import com.juan.bookledger.data.sync.SyncStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var syncSettings by remember { mutableStateOf(SyncSettings()) }
    var syncStatus by remember { mutableStateOf(SyncStatus.IDLE) }
    var lastSync by remember { mutableStateOf<Date?>(null) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        SyncManager.initialize(context)
        syncSettings = SyncManager.getSyncSettings()
        lastSync = syncSettings.lastSync
    }
    
    // Observe sync status
    LaunchedEffect(Unit) {
        SyncManager.syncStatus.collect { status ->
            syncStatus = status
        }
    }
    
    LaunchedEffect(Unit) {
        SyncManager.lastSync.collect { date ->
            lastSync = date
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Settings") },
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
            // Sync Status Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Sync Status",
                            tint = when (syncStatus) {
                                SyncStatus.SUCCESS -> MaterialTheme.colorScheme.primary
                                SyncStatus.ERROR -> MaterialTheme.colorScheme.error
                                SyncStatus.SYNCING -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = "Sync Status: ${syncStatus.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (lastSync != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Last Sync: ${lastSync.toString()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    if (syncMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = syncMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (syncStatus == SyncStatus.ERROR) 
                                MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // PocketBase Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "PocketBase Sync",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sync with your own PocketBase server for full control over your data.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable PocketBase Sync")
                        Switch(
                            checked = syncSettings.pocketbaseEnabled,
                            onCheckedChange = { enabled ->
                                syncSettings = syncSettings.copy(pocketbaseEnabled = enabled)
                                SyncManager.saveSyncSettings(syncSettings)
                            }
                        )
                    }
                }
            }
            
            // OneDrive Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "OneDrive Backup",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Backup your data to Microsoft OneDrive for easy access across devices.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable OneDrive Backup")
                        Switch(
                            checked = syncSettings.onedriveEnabled,
                            onCheckedChange = { enabled ->
                                syncSettings = syncSettings.copy(onedriveEnabled = enabled)
                                SyncManager.saveSyncSettings(syncSettings)
                            }
                        )
                    }
                }
            }
            
            // Google Drive Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Google Drive Backup",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Backup your data to Google Drive for seamless integration with Google services.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Google Drive Backup")
                        Switch(
                            checked = syncSettings.googledriveEnabled,
                            onCheckedChange = { enabled ->
                                syncSettings = syncSettings.copy(googledriveEnabled = enabled)
                                SyncManager.saveSyncSettings(syncSettings)
                            }
                        )
                    }
                }
            }
            
            // Auto Sync Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Auto Sync",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Automatically sync your data at regular intervals.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Auto Sync")
                        Switch(
                            checked = syncSettings.autoSync,
                            onCheckedChange = { enabled ->
                                syncSettings = syncSettings.copy(autoSync = enabled)
                                SyncManager.saveSyncSettings(syncSettings)
                            }
                        )
                    }
                    
                    if (syncSettings.autoSync) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sync Interval (hours):")
                        Slider(
                            value = syncSettings.syncInterval.toFloat(),
                            onValueChange = { value ->
                                syncSettings = syncSettings.copy(syncInterval = value.toInt())
                                SyncManager.saveSyncSettings(syncSettings)
                            },
                            valueRange = 1f..168f, // 1 hour to 1 week
                            steps = 23
                        )
                        Text("${syncSettings.syncInterval} hours")
                    }
                }
            }
            
            // Sync Actions
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sync Actions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                showSyncDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            enabled = syncStatus != SyncStatus.SYNCING
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync Now")
                        }
                        
                        Button(
                            onClick = {
                                showRestoreDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            enabled = syncStatus != SyncStatus.SYNCING
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restore")
                        }
                    }
                }
            }
        }
    }
    
    // Sync Confirmation Dialog
    if (showSyncDialog) {
        AlertDialog(
            onDismissRequest = { showSyncDialog = false },
            title = { Text("Sync Data") },
            text = { Text("This will upload your data to all enabled sync providers. Continue?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSyncDialog = false
                        scope.launch {
                            val enabledProviders = mutableListOf<SyncProvider>()
                            if (syncSettings.pocketbaseEnabled) enabledProviders.add(SyncProvider.POCKETBASE)
                            if (syncSettings.onedriveEnabled) enabledProviders.add(SyncProvider.ONEDRIVE)
                            if (syncSettings.googledriveEnabled) enabledProviders.add(SyncProvider.GOOGLE_DRIVE)
                            
                            val results = SyncManager.syncData(enabledProviders)
                            syncMessage = results.joinToString("\n") { "${it.message} (${if (it.success) "Success" else "Failed"})" }
                        }
                    }
                ) {
                    Text("Sync")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSyncDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Restore Confirmation Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore Data") },
            text = { Text("This will download and restore data from enabled sync providers. This may overwrite existing data. Continue?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        scope.launch {
                            val enabledProviders = mutableListOf<SyncProvider>()
                            if (syncSettings.pocketbaseEnabled) enabledProviders.add(SyncProvider.POCKETBASE)
                            if (syncSettings.onedriveEnabled) enabledProviders.add(SyncProvider.ONEDRIVE)
                            if (syncSettings.googledriveEnabled) enabledProviders.add(SyncProvider.GOOGLE_DRIVE)
                            
                            val results = SyncManager.restoreData(enabledProviders)
                            syncMessage = results.joinToString("\n") { "${it.message} (${if (it.success) "Success" else "Failed"})" }
                        }
                    }
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
