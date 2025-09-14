package com.juan.bookledger.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.juan.bookledger.data.DashboardManager
import com.juan.bookledger.data.DashboardData
import com.juan.bookledger.utils.CurrencyFormatter
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val dashboardData by DashboardManager.getDashboardData().collectAsState(initial = DashboardData(0.0, 0.0, 0.0, "Still Negative", 0, 0))
    val chartData by DashboardManager.getExpensesAndSalesForChart().collectAsState(initial = Pair(emptyList(), emptyList()))
    
    val currencyFormatter = CurrencyFormatter
    
    LaunchedEffect(Unit) {
        DashboardManager.initialize(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BookLedger Dashboard") },
                actions = {
                    IconButton(onClick = { /* Refresh data */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("📊") },
                    label = { Text("Dashboard") },
                    selected = true,
                    onClick = { /* Already on dashboard */ }
                )
                NavigationBarItem(
                    icon = { Text("💸") },
                    label = { Text("Expenses") },
                    selected = false,
                    onClick = { navController.navigate("expenses") }
                )
                NavigationBarItem(
                    icon = { Text("💰") },
                    label = { Text("Sales") },
                    selected = false,
                    onClick = { navController.navigate("sales") }
                )
            }
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
            // Financial Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total Expenses Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Expenses",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currencyFormatter.formatCurrency(dashboardData.totalExpenses),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "${dashboardData.expenseCount} transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                // Total Income Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Income",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currencyFormatter.formatCurrency(dashboardData.totalIncome),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${dashboardData.saleCount} transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Net Profit and Breakeven Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Net Profit Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dashboardData.netProfit >= 0) 
                            MaterialTheme.colorScheme.tertiaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Net Profit",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (dashboardData.netProfit >= 0) 
                                MaterialTheme.colorScheme.onTertiaryContainer 
                            else 
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currencyFormatter.formatCurrency(dashboardData.netProfit),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (dashboardData.netProfit >= 0) 
                                MaterialTheme.colorScheme.onTertiaryContainer 
                            else 
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                // Breakeven Status Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dashboardData.breakevenStatus == "Recouped") 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (dashboardData.breakevenStatus == "Recouped") 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dashboardData.breakevenStatus,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (dashboardData.breakevenStatus == "Recouped") 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Chart Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Expenses vs Sales Comparison",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simple Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        SimpleBarChart(
                            expenses = chartData.first,
                            sales = chartData.second,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            
            // Quick Actions
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("add_expense") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Expense")
                        }
                        Button(
                            onClick = { navController.navigate("add_sale") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Sale")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("add_book") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Book")
                        }
                        Button(
                            onClick = { navController.navigate("report") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Generate Report")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("analytics") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Analytics")
                        }
                        Button(
                            onClick = { navController.navigate("export") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Settings")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("notification_settings") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Notifications")
                        }
                        Button(
                            onClick = { navController.navigate("sync_settings") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sync Settings")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleBarChart(
    expenses: List<Double>,
    sales: List<Double>,
    modifier: Modifier = Modifier
) {
    val expenseTotal = expenses.sum()
    val salesTotal = sales.sum()
    val maxValue = maxOf(expenseTotal, salesTotal, 1.0) // Avoid division by zero
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Chart Title
        Text(
            text = "Expenses vs Sales",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Chart Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawBarChart(
                    expenseTotal = expenseTotal,
                    salesTotal = salesTotal,
                    maxValue = maxValue,
                    canvasSize = size
                )
            }
        }
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFFFF6B6B))
                )
                Text(
                    text = "Expenses: ${NumberFormat.getCurrencyInstance().format(expenseTotal)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF4ECDC4))
                )
                Text(
                    text = "Sales: ${NumberFormat.getCurrencyInstance().format(salesTotal)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun DrawScope.drawBarChart(
    expenseTotal: Double,
    salesTotal: Double,
    maxValue: Double,
    canvasSize: androidx.compose.ui.geometry.Size
) {
    val barWidth = canvasSize.width * 0.3f
    val barSpacing = canvasSize.width * 0.1f
    val startX = (canvasSize.width - (2 * barWidth + barSpacing)) / 2
    
    val expenseHeight = (expenseTotal / maxValue * (canvasSize.height - 40)).toFloat()
    val salesHeight = (salesTotal / maxValue * (canvasSize.height - 40)).toFloat()
    
    val barY = canvasSize.height - 20f
    
    // Draw expenses bar
    drawRect(
        color = Color(0xFFFF6B6B),
        topLeft = Offset(startX, barY - expenseHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, expenseHeight)
    )
    
    // Draw sales bar
    drawRect(
        color = Color(0xFF4ECDC4),
        topLeft = Offset(startX + barWidth + barSpacing, barY - salesHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, salesHeight)
    )
    
    // Draw labels using Compose text (simplified approach)
    // Note: For proper text rendering in Canvas, we'd need to use drawIntoCanvas
    // For now, we'll rely on the legend below the chart
}
