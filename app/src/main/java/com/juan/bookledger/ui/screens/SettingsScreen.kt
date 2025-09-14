package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.juan.bookledger.data.settings.SettingsManager
import com.juan.bookledger.data.settings.SupportedCurrencies
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by SettingsManager.settings.collectAsState()
    
    var showAddExpenseCategoryDialog by remember { mutableStateOf(false) }
    var showAddSaleTypeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var newExpenseCategory by remember { mutableStateOf("") }
    var newSaleType by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        SettingsManager.initialize(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (settings == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Currency Settings
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Currency",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Current: ${settings!!.currency.displayName} (${settings!!.currency.symbol})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = false,
                            onExpandedChange = { }
                        ) {
                            OutlinedTextField(
                                value = settings!!.currency.displayName,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Select Currency") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                                modifier = Modifier.menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = false,
                                onDismissRequest = { }
                            ) {
                                SupportedCurrencies.ALL.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text(currency.displayName) },
                                        onClick = {
                                            scope.launch {
                                                SettingsManager.updateCurrency(currency)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Expense Categories
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
                            Text(
                                text = "Expense Categories",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showAddExpenseCategoryDialog = true }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Category")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        settings!!.expenseCategories.forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            SettingsManager.removeExpenseCategory(category)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }
                
                // Sale Types
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
                            Text(
                                text = "Sale Types",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { showAddSaleTypeDialog = true }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Type")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        settings!!.saleTypes.forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            SettingsManager.removeSaleType(type)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }
                
                // Notification Settings
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Enable Notifications",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Master switch for all notifications",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings!!.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch {
                                        SettingsManager.updateNotificationsEnabled(enabled)
                                    }
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Weekly Reminder",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Remind to log expenses and sales",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings!!.weeklyReminderEnabled && settings!!.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch {
                                        SettingsManager.updateWeeklyReminderEnabled(enabled)
                                    }
                                },
                                enabled = settings!!.notificationsEnabled
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Breakeven Alert",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Alert when you recoup costs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings!!.breakevenAlertEnabled && settings!!.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch {
                                        SettingsManager.updateBreakevenAlertEnabled(enabled)
                                    }
                                },
                                enabled = settings!!.notificationsEnabled
                            )
                        }
                    }
                }
                
                // Reset Settings
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Reset Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reset all settings to default values",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Reset to Defaults")
                        }
                    }
                }
            }
        }
    }
    
    // Add Expense Category Dialog
    if (showAddExpenseCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddExpenseCategoryDialog = false },
            title = { Text("Add Expense Category") },
            text = {
                OutlinedTextField(
                    value = newExpenseCategory,
                    onValueChange = { newExpenseCategory = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newExpenseCategory.isNotBlank()) {
                            scope.launch {
                                SettingsManager.addExpenseCategory(newExpenseCategory.trim())
                            }
                            newExpenseCategory = ""
                            showAddExpenseCategoryDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExpenseCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Add Sale Type Dialog
    if (showAddSaleTypeDialog) {
        AlertDialog(
            onDismissRequest = { showAddSaleTypeDialog = false },
            title = { Text("Add Sale Type") },
            text = {
                OutlinedTextField(
                    value = newSaleType,
                    onValueChange = { newSaleType = it },
                    label = { Text("Sale Type") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newSaleType.isNotBlank()) {
                            scope.launch {
                                SettingsManager.addSaleType(newSaleType.trim())
                            }
                            newSaleType = ""
                            showAddSaleTypeDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSaleTypeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Are you sure you want to reset all settings to default values? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            SettingsManager.resetToDefaults()
                        }
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
