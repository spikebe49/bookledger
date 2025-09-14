package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.ui.viewmodel.DashboardViewModel
import com.juan.bookledger.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val totalExpenses by viewModel.totalExpensesAllBooks.collectAsState(initial = 0.0)
    val totalSales by viewModel.totalSalesAllBooks.collectAsState(initial = 0.0)
    val netProfit by viewModel.netProfitAllBooks.collectAsState(initial = 0.0)
    val overallROI by viewModel.overallROI.collectAsState(initial = 0.0)
    val topEarningBook by viewModel.topEarningBook.collectAsState(initial = null)
    val dashboardData by viewModel.dashboardData.collectAsState(initial = null)
    
    val currencyFormatter = CurrencyFormatter
    
    LaunchedEffect(Unit) {
        viewModel.refreshDashboard()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "BookLedger Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshDashboard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Financial Overview
            FinancialOverviewCard(
                totalExpenses = totalExpenses,
                totalSales = totalSales,
                netProfit = netProfit,
                overallROI = overallROI,
                breakevenStatus = dashboardData?.breakevenStatus ?: "Still Negative",
                currencyFormatter = currencyFormatter
            )
            
            // Top Earning Book
            TopEarningBookCard(
                topEarningBook = topEarningBook,
                netProfit = netProfit,
                currencyFormatter = currencyFormatter
            )
            
            // Quick Actions
            QuickActionsCard(navController = navController)
        }
    }
}

@Composable
private fun FinancialOverviewCard(
    totalExpenses: Double,
    totalSales: Double,
    netProfit: Double,
    overallROI: Double,
    breakevenStatus: String,
    currencyFormatter: CurrencyFormatter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Financial Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Financial metrics in a grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinancialMetric(
                    title = "Total Expenses",
                    value = currencyFormatter.formatCurrency(totalExpenses),
                    color = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.Info
                )
                
                FinancialMetric(
                    title = "Total Sales",
                    value = currencyFormatter.formatCurrency(totalSales),
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Add
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinancialMetric(
                    title = "Net Profit",
                    value = currencyFormatter.formatCurrency(netProfit),
                    color = if (netProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    icon = if (netProfit >= 0) Icons.Default.Add else Icons.Default.Info
                )
                
                FinancialMetric(
                    title = "ROI",
                    value = if (overallROI.isFinite()) "${String.format("%.1f", overallROI)}%" else "N/A",
                    color = if (overallROI >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    icon = if (overallROI >= 0) Icons.Default.Add else Icons.Default.Info
                )
            }
            
            // Breakeven Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (breakevenStatus == "Recouped") 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (breakevenStatus == "Recouped") Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (breakevenStatus == "Recouped") 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Status: $breakevenStatus",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (breakevenStatus == "Recouped") 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancialMetric(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.width(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun TopEarningBookCard(
    topEarningBook: Book?,
    netProfit: Double,
    currencyFormatter: CurrencyFormatter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Top Earning Book",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (topEarningBook != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = topEarningBook.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Profit: ${currencyFormatter.formatCurrency(netProfit)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "No books yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickActionsCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    text = "View Books",
                    icon = Icons.Default.Menu,
                    onClick = { navController.navigate("book_list") }
                )
                
                QuickActionButton(
                    text = "Add Expense",
                    icon = Icons.Default.Add,
                    onClick = { navController.navigate("add_expense") }
                )
                
                QuickActionButton(
                    text = "Add Sale",
                    icon = Icons.Default.ShoppingCart,
                    onClick = { navController.navigate("add_sale") }
                )
                
                QuickActionButton(
                    text = "Reports",
                    icon = Icons.Default.Info,
                    onClick = { navController.navigate("report") }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    text = "Analytics",
                    icon = Icons.Default.Info,
                    onClick = { navController.navigate("analytics") }
                )
                
                QuickActionButton(
                    text = "Export",
                    icon = Icons.Default.Info,
                    onClick = { navController.navigate("export") }
                )
                
                QuickActionButton(
                    text = "Expenses",
                    icon = Icons.Default.Info,
                    onClick = { navController.navigate("expenses") }
                )
                
                QuickActionButton(
                    text = "Sales",
                    icon = Icons.Default.ShoppingCart,
                    onClick = { navController.navigate("sales") }
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
