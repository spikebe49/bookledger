package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.juan.bookledger.data.export.CSVExporter
import com.juan.bookledger.data.export.ExportFormat
import com.juan.bookledger.data.export.PDFExporter
import com.juan.bookledger.utils.ShareUtility
import com.juan.bookledger.utils.ShareMethod
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var isExporting by remember { mutableStateOf(false) }
    var exportResult by remember { mutableStateOf<com.juan.bookledger.data.export.ExportResult?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }
    var availableShareMethods by remember { mutableStateOf<List<ShareMethod>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        availableShareMethods = ShareUtility.getAvailableShareMethods(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export Data") },
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
            // Export Options Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Export Options",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // CSV Export Buttons
                    Text(
                        text = "CSV Export",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isExporting = true
                                    exportResult = CSVExporter.exportExpenses(context)
                                    isExporting = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isExporting
                        ) {
                            Text("Expenses")
                        }
                        
                        Button(
                            onClick = {
                                scope.launch {
                                    isExporting = true
                                    exportResult = CSVExporter.exportSales(context)
                                    isExporting = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isExporting
                        ) {
                            Text("Sales")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            scope.launch {
                                isExporting = true
                                exportResult = CSVExporter.exportAllData(context)
                                isExporting = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isExporting
                    ) {
                        Text("All Data (CSV)")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // PDF Export Button
                    Text(
                        text = "PDF Export",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            scope.launch {
                                isExporting = true
                                exportResult = PDFExporter.exportFullReport(context)
                                isExporting = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isExporting
                    ) {
                        Text("Full Report (PDF)")
                    }
                }
            }
            
            // Export Status Card
            if (isExporting) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Exporting...")
                    }
                }
            }
            
            // Export Result Card
            exportResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.success) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (result.success) "Export Successful" else "Export Failed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (result.success) 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        if (result.success && result.filePath != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "File: ${result.filePath.split("/").last()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Size: ${formatFileSize(result.fileSize)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { showShareDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share File")
                            }
                        }
                    }
                }
            }
            
            // Share Methods Card
            if (availableShareMethods.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Available Share Methods",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        availableShareMethods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = method.icon,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = method.displayName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Share Dialog
    if (showShareDialog && exportResult?.success == true && exportResult?.filePath != null) {
        ShareDialog(
            filePath = exportResult!!.filePath!!,
            onDismiss = { showShareDialog = false },
            availableMethods = availableShareMethods
        )
    }
}

@Composable
private fun ShareDialog(
    filePath: String,
    onDismiss: () -> Unit,
    availableMethods: List<ShareMethod>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share File") },
        text = { Text("Choose how you'd like to share the exported file:") },
        confirmButton = {
            TextButton(
                onClick = {
                    val shareIntent = ShareUtility.getShareChooserIntent(
                        context = context,
                        filePath = filePath,
                        mimeType = if (filePath.endsWith(".pdf")) "application/pdf" else "text/csv"
                    )
                    context.startActivity(shareIntent)
                    onDismiss()
                }
            ) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
