package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.model.*
import com.juan.bookledger.data.repository.BookRepository
import com.juan.bookledger.data.repository.ExpenseRepository
import com.juan.bookledger.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EnhancedAuthorFinancialViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _bookId = MutableStateFlow(0L)
    val bookId: StateFlow<Long> = _bookId.asStateFlow()

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()

    private val _financialSummary = MutableStateFlow<AuthorFinancialAnalytics?>(null)
    val financialSummary: StateFlow<AuthorFinancialAnalytics?> = _financialSummary.asStateFlow()

    private val _monthlyReports = MutableStateFlow<List<MonthlyFinancialReport>>(emptyList())
    val monthlyReports: StateFlow<List<MonthlyFinancialReport>> = _monthlyReports.asStateFlow()

    private val _alerts = MutableStateFlow<List<FinancialAlert>>(emptyList())
    val alerts: StateFlow<List<FinancialAlert>> = _alerts.asStateFlow()

    private val _uiState = MutableStateFlow(EnhancedAuthorFinancialUiState())
    val uiState: StateFlow<EnhancedAuthorFinancialUiState> = _uiState.asStateFlow()

    fun loadBookData(bookId: Long) {
        _bookId.value = bookId
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Load book data (suspend function)
                _book.value = bookRepository.getBookById(bookId)
                
                // Set up data flows in separate coroutines
                launch {
                    expenseRepository.getExpensesByBookId(bookId).collect { expenses ->
                        _expenses.value = expenses
                    }
                }
                
                launch {
                    saleRepository.getSalesByBookId(bookId).collect { sales ->
                        _sales.value = sales
                    }
                }
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun calculateFinancialSummary() {
        val expenses = _expenses.value
        val sales = _sales.value
        
        val totalExpenses = expenses.sumOf { it.amount }
        val totalRevenue = sales.sumOf { it.totalAmount }
        val netProfit = totalRevenue - totalExpenses
        val roi = if (totalExpenses > 0) (netProfit / totalExpenses) * 100 else 0.0
        
        val financialHealth = when {
            roi >= 50 -> FinancialHealth.EXCELLENT
            roi >= 20 -> FinancialHealth.GOOD
            roi >= 0 -> FinancialHealth.BREAK_EVEN
            roi >= -20 -> FinancialHealth.LOSS
            else -> FinancialHealth.CRITICAL
        }
        
        val summary = AuthorFinancialAnalytics(
            bookId = _bookId.value,
            bookTitle = _book.value?.title ?: "Unknown",
            totalInvestment = totalExpenses,
            publisherFees = expenses.filter { it.category == ExpenseCategory.PUBLISHER_FEES }.sumOf { it.amount },
            illustratorFees = expenses.filter { it.category == ExpenseCategory.ILLUSTRATOR_FEES }.sumOf { it.amount },
            editingCosts = expenses.filter { it.category == ExpenseCategory.OTHER }.sumOf { it.amount },
            marketingCosts = expenses.filter { it.category == ExpenseCategory.MARKETING }.sumOf { it.amount },
            productionCosts = expenses.filter { it.category == ExpenseCategory.PRINTING_COSTS }.sumOf { it.amount },
            otherCosts = expenses.filter { it.category == ExpenseCategory.OTHER }.sumOf { it.amount },
            totalRevenue = totalRevenue,
            publisherRevenue = sales.filter { it.type == SaleType.PUBLISHER_SALE }.sumOf { it.totalAmount },
            directRevenue = sales.filter { it.type == SaleType.DIRECT_SALE }.sumOf { it.totalAmount },
            onlineStoreRevenue = sales.filter { it.platform.contains("Amazon") || it.platform.contains("Barnes") }.sumOf { it.totalAmount },
            otherRevenue = sales.filter { it.type == SaleType.OTHER }.sumOf { it.totalAmount },
            averagePublisherRoyalty = 0.0, // Simplified
            averageOnlineRoyalty = 0.0, // Simplified
            totalRoyaltiesReceived = sales.filter { it.type == SaleType.PUBLISHER_SALE }.sumOf { it.totalAmount },
            publisherCut = 0.0, // Simplified
            platformFees = 0.0, // Simplified
            netProfit = netProfit,
            profitMargin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0,
            roiPercentage = roi,
            breakEvenPoint = BreakEvenAnalysis(0, 0.0, 0, 0, 0.0), // Simplified
            isBreakEven = netProfit >= 0,
            breakEvenDate = null, // Simplified
            profitStartDate = null, // Simplified
            totalBooksSold = sales.sumOf { it.quantity },
            averageSalePrice = if (sales.isNotEmpty()) sales.sumOf { it.totalAmount } / sales.sumOf { it.quantity } else 0.0,
            bestSellingPlatform = if (sales.isNotEmpty()) {
                sales.groupBy { it.platform }.maxByOrNull { it.value.sumOf { sale -> sale.totalAmount } }?.key ?: "Unknown"
            } else "None",
            worstSellingPlatform = if (sales.isNotEmpty()) {
                sales.groupBy { it.platform }.minByOrNull { it.value.sumOf { sale -> sale.totalAmount } }?.key ?: "Unknown"
            } else "None",
            daysToBreakEven = 0, // Simplified
            daysToProfit = 0, // Simplified
            monthsSinceLaunch = 0, // Simplified
            financialHealth = financialHealth,
            recommendations = generateRecommendations(financialHealth, roi, netProfit)
        )
        
        _financialSummary.value = summary
    }

    fun generateMonthlyReports() {
        val expenses = _expenses.value
        val sales = _sales.value
        
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        val monthlyReports = mutableListOf<MonthlyFinancialReport>()
        
        // Generate reports for the last 12 months
        for (i in 11 downTo 0) {
            val reportMonth = if (currentMonth - i < 0) currentMonth - i + 12 else currentMonth - i
            val reportYear = if (currentMonth - i < 0) currentYear - 1 else currentYear
            
            val monthExpenses = expenses.filter { expense ->
                val expenseCalendar = Calendar.getInstance()
                expenseCalendar.time = expense.date
                expenseCalendar.get(Calendar.MONTH) == reportMonth && 
                expenseCalendar.get(Calendar.YEAR) == reportYear
            }
            
            val monthSales = sales.filter { sale ->
                val saleCalendar = Calendar.getInstance()
                saleCalendar.time = sale.date
                saleCalendar.get(Calendar.MONTH) == reportMonth && 
                saleCalendar.get(Calendar.YEAR) == reportYear
            }
            
            val monthName = when (reportMonth) {
                0 -> "Jan"
                1 -> "Feb"
                2 -> "Mar"
                3 -> "Apr"
                4 -> "May"
                5 -> "Jun"
                6 -> "Jul"
                7 -> "Aug"
                8 -> "Sep"
                9 -> "Oct"
                10 -> "Nov"
                11 -> "Dec"
                else -> "Unknown"
            }
            
            val monthlyReport = MonthlyFinancialReport(
                bookId = _bookId.value,
                month = reportMonth + 1, // Convert 0-based to 1-based
                year = reportYear,
                totalRevenue = monthSales.sumOf { it.totalAmount },
                totalExpenses = monthExpenses.sumOf { it.amount },
                netProfit = monthSales.sumOf { it.totalAmount } - monthExpenses.sumOf { it.amount },
                profitMargin = if (monthSales.sumOf { it.totalAmount } > 0) {
                    (monthSales.sumOf { it.totalAmount } - monthExpenses.sumOf { it.amount }) / monthSales.sumOf { it.totalAmount } * 100
                } else 0.0,
                roi = if (monthExpenses.sumOf { it.amount } > 0) {
                    (monthSales.sumOf { it.totalAmount } - monthExpenses.sumOf { it.amount }) / monthExpenses.sumOf { it.amount } * 100
                } else 0.0,
                booksSold = monthSales.sumOf { it.quantity },
                averageSalePrice = if (monthSales.isNotEmpty()) monthSales.sumOf { it.totalAmount } / monthSales.sumOf { it.quantity } else 0.0,
                totalRoyalties = monthSales.filter { it.type == SaleType.PUBLISHER_SALE }.sumOf { it.totalAmount },
                totalMarketingSpend = monthExpenses.filter { it.category == ExpenseCategory.MARKETING }.sumOf { it.amount },
                marketingROI = if (monthExpenses.filter { it.category == ExpenseCategory.MARKETING }.sumOf { it.amount } > 0) {
                    monthSales.sumOf { it.totalAmount } / monthExpenses.filter { it.category == ExpenseCategory.MARKETING }.sumOf { it.amount } * 100
                } else 0.0,
                topRevenueSource = if (monthSales.isNotEmpty()) {
                    monthSales.groupBy { it.type }.maxByOrNull { it.value.sumOf { sale -> sale.totalAmount } }?.key?.name ?: "Unknown"
                } else "None",
                topExpenseCategory = if (monthExpenses.isNotEmpty()) {
                    monthExpenses.groupBy { it.category }.maxByOrNull { it.value.sumOf { expense -> expense.amount } }?.key?.name ?: "Unknown"
                } else "None",
                financialHealth = FinancialHealth.GOOD, // Simplified
                recommendations = emptyList() // Simplified
            )
            
            monthlyReports.add(monthlyReport)
        }
        
        _monthlyReports.value = monthlyReports
    }

    fun generateAlerts() {
        val summary = _financialSummary.value ?: return
        val alerts = mutableListOf<FinancialAlert>()
        
        // Generate alerts based on financial health
        when (summary.financialHealth) {
            FinancialHealth.CRITICAL -> {
                alerts.add(
                    FinancialAlert(
                        bookId = _bookId.value,
                        alertType = FinancialAlertType.NEGATIVE_PROFIT,
                        severity = AlertSeverity.HIGH,
                        message = "Critical financial losses detected. Immediate action required.",
                        value = summary.totalInvestment - summary.publisherRevenue - summary.directRevenue - summary.onlineStoreRevenue - summary.otherRevenue,
                        threshold = 0.0
                    )
                )
            }
            FinancialHealth.LOSS -> {
                alerts.add(
                    FinancialAlert(
                        bookId = _bookId.value,
                        alertType = FinancialAlertType.NEGATIVE_PROFIT,
                        severity = AlertSeverity.MEDIUM,
                        message = "Book is operating at a loss. Consider cost reduction strategies.",
                        value = summary.totalInvestment - summary.publisherRevenue - summary.directRevenue - summary.onlineStoreRevenue - summary.otherRevenue,
                        threshold = 0.0
                    )
                )
            }
            FinancialHealth.BREAK_EVEN -> {
                alerts.add(
                    FinancialAlert(
                        bookId = _bookId.value,
                        alertType = FinancialAlertType.NEGATIVE_PROFIT,
                        severity = AlertSeverity.LOW,
                        message = "Book is breaking even. Focus on increasing revenue.",
                        value = summary.totalInvestment - summary.publisherRevenue - summary.directRevenue - summary.onlineStoreRevenue - summary.otherRevenue,
                        threshold = 0.0
                    )
                )
            }
            else -> {
                // No alerts for good financial health
            }
        }
        
        // ROI alert
        if (summary.roiPercentage < 10) {
            alerts.add(
                FinancialAlert(
                    bookId = _bookId.value,
                    alertType = FinancialAlertType.LOW_ROI,
                    severity = AlertSeverity.MEDIUM,
                    message = "Low ROI detected. Consider optimizing marketing spend.",
                    value = summary.roiPercentage,
                    threshold = 10.0
                )
            )
        }
        
        _alerts.value = alerts
    }

    private fun generateRecommendations(
        financialHealth: FinancialHealth,
        roi: Double,
        netProfit: Double
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        when (financialHealth) {
            FinancialHealth.CRITICAL -> {
                recommendations.add("Immediately review and reduce all non-essential expenses")
                recommendations.add("Consider pausing marketing campaigns until profitability improves")
                recommendations.add("Evaluate if the book should be discontinued")
            }
            FinancialHealth.LOSS -> {
                recommendations.add("Focus on cost reduction strategies")
                recommendations.add("Increase marketing efforts for better sales")
                recommendations.add("Consider price adjustments")
            }
            FinancialHealth.BREAK_EVEN -> {
                recommendations.add("Focus on increasing revenue through better marketing")
                recommendations.add("Consider expanding to new sales channels")
                recommendations.add("Optimize pricing strategy")
            }
            FinancialHealth.GOOD -> {
                recommendations.add("Maintain current strategies")
                recommendations.add("Consider scaling successful marketing campaigns")
                recommendations.add("Explore new revenue opportunities")
            }
            FinancialHealth.EXCELLENT -> {
                recommendations.add("Excellent performance! Consider replicating this success")
                recommendations.add("Scale up successful strategies")
                recommendations.add("Consider creating similar content")
            }
        }
        
        if (roi < 20) {
            recommendations.add("Focus on improving ROI through better marketing efficiency")
        }
        
        if (netProfit < 1000) {
            recommendations.add("Consider strategies to increase profit margins")
        }
        
        return recommendations
    }
}

data class EnhancedAuthorFinancialUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
