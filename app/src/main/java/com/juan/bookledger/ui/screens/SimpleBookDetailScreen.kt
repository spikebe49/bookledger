package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.BookWithTotals
import com.juan.bookledger.ui.viewmodel.BookViewModel
import com.juan.bookledger.ui.viewmodel.ExpenseViewModel
import com.juan.bookledger.ui.viewmodel.SaleViewModel
import com.juan.bookledger.ui.viewmodel.ReportViewModel
import com.juan.bookledger.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBookDetailScreen(
    bookId: Long,
    navController: NavController,
    bookViewModel: BookViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    saleViewModel: SaleViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val bookWithTotals by bookViewModel.bookWithTotals.observeAsState(initial = null)
    val expenses by expenseViewModel.expenses.collectAsState(initial = emptyList<Expense>())
    val sales by saleViewModel.sales.collectAsState(initial = emptyList<Sale>())
    val reportData by reportViewModel.bookReportData.collectAsState(initial = null)
    
    val currencyFormatter = CurrencyFormatter
    var selectedTab by remember { mutableStateOf(0) }
    
    // Set current book for all ViewModels
    LaunchedEffect(bookId) {
        bookViewModel.getBookWithTotals(bookId)
        expenseViewModel.setCurrentBook(bookId)
        saleViewModel.setCurrentBook(bookId)
        reportViewModel.setCurrentBook(bookId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        bookWithTotals?.title ?: "Book Details",
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Book")
                    }
                }
            )
        },
        floatingActionButton = {
            when (selectedTab) {
                0 -> FloatingActionButton(
                    onClick = { navController.navigate("add_expense?bookId=$bookId") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
                1 -> FloatingActionButton(
                    onClick = { navController.navigate("add_sale?bookId=$bookId") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Sale")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Book header with financial summary
            BookHeaderCard(
                bookWithTotals = bookWithTotals,
                currencyFormatter = currencyFormatter
            )
            
            // Tab row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Expenses") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Sales") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Reports") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
            }
            
            // Tab content
            when (selectedTab) {
                0 -> ExpensesTabContent(
                    expenses = expenses,
                    currencyFormatter = currencyFormatter
                )
                1 -> SalesTabContent(
                    sales = sales,
                    currencyFormatter = currencyFormatter
                )
                2 -> ReportsTabContent(
                    reportData = reportData,
                    currencyFormatter = currencyFormatter
                )
            }
        }
    }
}

@Composable
private fun BookHeaderCard(
    bookWithTotals: BookWithTotals?,
    currencyFormatter: CurrencyFormatter
) {
    if (bookWithTotals == null) return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Book title and description
            Text(
                text = bookWithTotals.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            bookWithTotals.description?.let { description ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Financial summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinancialSummaryItem(
                    label = "Total Expenses",
                    value = currencyFormatter.formatCurrency(bookWithTotals.totalExpenses),
                    color = MaterialTheme.colorScheme.error
                )
                
                FinancialSummaryItem(
                    label = "Total Sales",
                    value = currencyFormatter.formatCurrency(bookWithTotals.totalSales),
                    color = MaterialTheme.colorScheme.primary
                )
                
                FinancialSummaryItem(
                    label = "Net Profit",
                    value = currencyFormatter.formatCurrency(bookWithTotals.netProfit),
                    color = if (bookWithTotals.isProfitable) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Breakeven status
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (bookWithTotals.isProfitable) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (bookWithTotals.isProfitable) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (bookWithTotals.isProfitable) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (bookWithTotals.isProfitable) "Recouped" else "Still Negative",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (bookWithTotals.isProfitable) 
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
private fun FinancialSummaryItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ExpensesTabContent(
    expenses: List<Expense>,
    currencyFormatter: CurrencyFormatter
) {
    if (expenses.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Info,
            title = "No Expenses",
            description = "Add your first expense to start tracking costs"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(expenses) { expense ->
                ExpenseCard(
                    expense = expense,
                    currencyFormatter = currencyFormatter
                )
            }
        }
    }
}

@Composable
private fun SalesTabContent(
    sales: List<Sale>,
    currencyFormatter: CurrencyFormatter
) {
    if (sales.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Add,
            title = "No Sales",
            description = "Add your first sale to start tracking revenue"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sales) { sale ->
                SaleCard(
                    sale = sale,
                    currencyFormatter = currencyFormatter
                )
            }
        }
    }
}

@Composable
private fun ReportsTabContent(
    reportData: com.juan.bookledger.ui.viewmodel.BookReportData?,
    currencyFormatter: CurrencyFormatter
) {
    if (reportData == null) {
        EmptyState(
            icon = Icons.Default.Info,
            title = "No Report Data",
            description = "Add expenses and sales to generate reports"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ReportSummaryCard(
                    reportData = reportData,
                    currencyFormatter = currencyFormatter
                )
            }
            
            item {
                SalesBreakdownCard(
                    reportData = reportData,
                    currencyFormatter = currencyFormatter
                )
            }
        }
    }
}

@Composable
private fun ExpenseCard(
    expense: Expense,
    currencyFormatter: CurrencyFormatter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDate(expense.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = currencyFormatter.formatCurrency(expense.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SaleCard(
    sale: Sale,
    currencyFormatter: CurrencyFormatter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sale.type.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Qty: ${sale.quantity} × ${currencyFormatter.formatCurrency(sale.unitPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDate(sale.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = currencyFormatter.formatCurrency(sale.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (sale.isGiveaway) {
                    Text(
                        text = "Giveaway",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportSummaryCard(
    reportData: com.juan.bookledger.ui.viewmodel.BookReportData,
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
                text = "Report Summary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReportMetric(
                    label = "ROI",
                    value = reportData.roiFormatted,
                    color = if (reportData.roi >= 0) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                
                ReportMetric(
                    label = "Profit Margin",
                    value = reportData.profitMarginFormatted,
                    color = if (reportData.profitMargin >= 0) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SalesBreakdownCard(
    reportData: com.juan.bookledger.ui.viewmodel.BookReportData,
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
                text = "Sales Breakdown",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SalesBreakdownItem(
                label = "Direct Sales",
                value = currencyFormatter.formatCurrency(reportData.directSales),
                color = MaterialTheme.colorScheme.primary
            )
            
            SalesBreakdownItem(
                label = "Publisher Sales",
                value = currencyFormatter.formatCurrency(reportData.publisherSales),
                color = MaterialTheme.colorScheme.secondary
            )
            
            SalesBreakdownItem(
                label = "Donations",
                value = currencyFormatter.formatCurrency(reportData.donations),
                color = MaterialTheme.colorScheme.tertiary
            )
            
            SalesBreakdownItem(
                label = "Giveaways",
                value = "${reportData.giveawayCount} items",
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ReportMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun SalesBreakdownItem(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}
