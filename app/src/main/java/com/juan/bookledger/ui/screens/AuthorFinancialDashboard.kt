package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.data.model.AuthorFinancialAnalytics
import com.juan.bookledger.data.model.BreakEvenAnalysis
import com.juan.bookledger.data.model.FinancialHealth
import com.juan.bookledger.ui.viewmodel.AuthorFinancialViewModel
import com.juan.bookledger.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorFinancialDashboard(
    bookId: Long,
    navController: NavController,
    viewModel: AuthorFinancialViewModel = hiltViewModel()
) {
    val analytics by viewModel.getFinancialAnalytics(bookId).collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    
    LaunchedEffect(bookId) {
        viewModel.loadFinancialAnalytics(bookId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Financial Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAnalytics(bookId) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            analytics?.let { financialData ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Financial Health Overview
                    FinancialHealthCard(financialData = financialData)
                    
                    // Investment Breakdown
                    InvestmentBreakdownCard(financialData = financialData)
                    
                    // Revenue Analysis
                    RevenueAnalysisCard(financialData = financialData)
                    
                    // Break-even Analysis
                    BreakEvenAnalysisCard(breakEven = financialData.breakEvenPoint)
                    
                    // Profitability Metrics
                    ProfitabilityMetricsCard(financialData = financialData)
                    
                    // Platform Performance
                    PlatformPerformanceCard(financialData = financialData)
                    
                    // AI Recommendations
                    AIRecommendationsCard(recommendations = financialData.recommendations)
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No financial data available")
                }
            }
        }
    }
}

@Composable
private fun FinancialHealthCard(financialData: AuthorFinancialAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Financial Health",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = getFinancialHealthText(financialData.financialHealth),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getFinancialHealthColor(financialData.financialHealth)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Net Profit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.formatCurrency(financialData.netProfit),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (financialData.netProfit >= 0) Color.Green else Color.Red
                    )
                }
            }
            
            // Break-even status
            if (financialData.isBreakEven) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Break-even achieved!",
                        color = Color.Green,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Still working toward break-even",
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun InvestmentBreakdownCard(financialData: AuthorFinancialAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Investment Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Total Investment: ${CurrencyFormatter.formatCurrency(financialData.totalInvestment)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Investment categories
            InvestmentCategoryItem(
                label = "Publisher Fees",
                amount = financialData.publisherFees,
                total = financialData.totalInvestment
            )
            InvestmentCategoryItem(
                label = "Illustrator Fees",
                amount = financialData.illustratorFees,
                total = financialData.totalInvestment
            )
            InvestmentCategoryItem(
                label = "Editing Services",
                amount = financialData.editingCosts,
                total = financialData.totalInvestment
            )
            InvestmentCategoryItem(
                label = "Marketing Costs",
                amount = financialData.marketingCosts,
                total = financialData.totalInvestment
            )
            InvestmentCategoryItem(
                label = "Production Costs",
                amount = financialData.productionCosts,
                total = financialData.totalInvestment
            )
            InvestmentCategoryItem(
                label = "Other Costs",
                amount = financialData.otherCosts,
                total = financialData.totalInvestment
            )
        }
    }
}

@Composable
private fun InvestmentCategoryItem(
    label: String,
    amount: Double,
    total: Double
) {
    val percentage = if (total > 0) (amount / total) * 100 else 0.0
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = CurrencyFormatter.formatCurrency(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${String.format("%.1f", percentage)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RevenueAnalysisCard(financialData: AuthorFinancialAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Revenue Analysis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Total Revenue: ${CurrencyFormatter.formatCurrency(financialData.totalRevenue)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Revenue sources
            RevenueSourceItem(
                label = "Publisher Sales",
                revenue = financialData.publisherRevenue,
                total = financialData.totalRevenue,
                royaltyRate = financialData.averagePublisherRoyalty
            )
            RevenueSourceItem(
                label = "Online Stores",
                revenue = financialData.onlineStoreRevenue,
                total = financialData.totalRevenue,
                royaltyRate = financialData.averageOnlineRoyalty
            )
            RevenueSourceItem(
                label = "Direct Sales",
                revenue = financialData.directRevenue,
                total = financialData.totalRevenue,
                royaltyRate = 1.0 // 100% for direct sales
            )
            RevenueSourceItem(
                label = "Other Sources",
                revenue = financialData.otherRevenue,
                total = financialData.totalRevenue,
                royaltyRate = 0.0
            )
        }
    }
}

@Composable
private fun RevenueSourceItem(
    label: String,
    revenue: Double,
    total: Double,
    royaltyRate: Double
) {
    val percentage = if (total > 0) (revenue / total) * 100 else 0.0
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            if (royaltyRate > 0) {
                Text(
                    text = "Royalty: ${String.format("%.1f", royaltyRate * 100)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = CurrencyFormatter.formatCurrency(revenue),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${String.format("%.1f", percentage)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BreakEvenAnalysisCard(breakEven: BreakEvenAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Break-Even Analysis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress to Break-Even",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${String.format("%.1f", breakEven.breakEvenProgress)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = (breakEven.breakEvenProgress / 100).toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Break-even details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Books Sold",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${breakEven.booksSoldToBreakEven}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Needed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${breakEven.remainingToBreakEven}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfitabilityMetricsCard(financialData: AuthorFinancialAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Profitability Metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "ROI",
                    value = "${String.format("%.1f", financialData.roiPercentage)}%",
                    color = if (financialData.roiPercentage >= 0) Color.Green else Color.Red
                )
                MetricItem(
                    label = "Profit Margin",
                    value = "${String.format("%.1f", financialData.profitMargin)}%",
                    color = if (financialData.profitMargin >= 0) Color.Green else Color.Red
                )
                MetricItem(
                    label = "Books Sold",
                    value = "${financialData.totalBooksSold}",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
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
private fun PlatformPerformanceCard(financialData: AuthorFinancialAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Platform Performance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Best Platform: ${financialData.bestSellingPlatform}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Worst Platform: ${financialData.worstSellingPlatform}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AIRecommendationsCard(recommendations: List<String>) {
    if (recommendations.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "AI Recommendations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                recommendations.forEach { recommendation ->
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

private fun getFinancialHealthText(health: FinancialHealth): String {
    return when (health) {
        FinancialHealth.EXCELLENT -> "Excellent"
        FinancialHealth.GOOD -> "Good"
        FinancialHealth.BREAK_EVEN -> "Break-Even"
        FinancialHealth.LOSS -> "Loss"
        FinancialHealth.CRITICAL -> "Critical"
    }
}

private fun getFinancialHealthColor(health: FinancialHealth): Color {
    return when (health) {
        FinancialHealth.EXCELLENT -> Color.Green
        FinancialHealth.GOOD -> Color(0xFF4CAF50)
        FinancialHealth.BREAK_EVEN -> Color(0xFFFF9800)
        FinancialHealth.LOSS -> Color.Red
        FinancialHealth.CRITICAL -> Color(0xFFD32F2F)
    }
}
