package com.juan.bookledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.data.model.*
import com.juan.bookledger.ui.viewmodel.EnhancedAuthorFinancialViewModel
import com.juan.bookledger.utils.CanadianCurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAuthorFinancialDashboard(
    bookId: Long,
    navController: NavController,
    viewModel: EnhancedAuthorFinancialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val book by viewModel.book.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val financialSummary by viewModel.financialSummary.collectAsState()
    val monthlyReports by viewModel.monthlyReports.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadBookData(bookId)
    }
    
    // Trigger calculations when data changes
    LaunchedEffect(expenses, sales) {
        viewModel.calculateFinancialSummary()
        viewModel.generateMonthlyReports()
        viewModel.generateAlerts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enhanced Financial Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Information
            item {
                BookInfoCard(book = book)
            }

            // Financial Summary
            item {
                FinancialSummaryCard(summary = financialSummary)
            }

            // Monthly Reports
            item {
                MonthlyReportsCard(reports = monthlyReports)
            }

            // Alerts
            item {
                AlertsCard(alerts = alerts)
            }

            // Quick Actions
            item {
                QuickActionsCard(
                    onAddExpense = { /* Navigate to add expense */ },
                    onAddSale = { /* Navigate to add sale */ },
                    onViewReports = { /* Navigate to reports */ }
                )
            }
        }
    }
}

@Composable
private fun BookInfoCard(book: Book?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Book Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (book != null) {
                Text(
                    text = "Title: ${book.title}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Author: ${book.authorName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "ISBN: ${book.isbn}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Loading book information...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FinancialSummaryCard(summary: AuthorFinancialAnalytics?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Financial Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (summary != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Revenue",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CanadianCurrencyFormatter.formatCurrency(summary.totalRevenue),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Total Expenses",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CanadianCurrencyFormatter.formatCurrency(summary.totalInvestment),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Net Profit",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val totalRevenue = summary.publisherRevenue + summary.directRevenue + summary.onlineStoreRevenue + summary.otherRevenue
                        val netProfit = totalRevenue - summary.totalInvestment
                        Text(
                            text = CanadianCurrencyFormatter.formatCurrency(netProfit),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (netProfit >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                    
                    Column {
                        Text(
                            text = "ROI",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${String.format("%.1f", summary.roiPercentage)}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "Loading financial summary...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MonthlyReportsCard(reports: List<MonthlyFinancialReport>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Monthly Reports",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (reports.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reports.take(6)) { report ->
                        MonthlyReportItem(report = report)
                    }
                }
            } else {
                Text(
                    text = "No monthly reports available",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MonthlyReportItem(report: MonthlyFinancialReport) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = report.month.toString(),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = CanadianCurrencyFormatter.formatCurrency(report.netProfit),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (report.netProfit >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

@Composable
private fun AlertsCard(alerts: List<FinancialAlert>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Financial Alerts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (alerts.isNotEmpty()) {
                alerts.take(3).forEach { alert ->
                    AlertItem(alert = alert)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            } else {
                Text(
                    text = "No alerts",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun AlertItem(alert: FinancialAlert) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Alert",
            tint =             when (alert.severity) {
                AlertSeverity.HIGH -> Color(0xFFF44336)
                AlertSeverity.MEDIUM -> Color(0xFFFF9800)
                AlertSeverity.LOW -> Color(0xFFFFC107)
                else -> Color(0xFFFFC107)
            },
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = alert.message,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun QuickActionsCard(
    onAddExpense: () -> Unit,
    onAddSale: () -> Unit,
    onViewReports: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onAddExpense,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Expense")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onAddSale,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Sale")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onViewReports,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reports")
                }
            }
        }
    }
}