package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Global stats across all books
    val totalExpensesAllBooks: Flow<Double> = dashboardRepository
        .getTotalExpensesAllBooks()
    
    val totalSalesAllBooks: Flow<Double> = dashboardRepository
        .getTotalSalesAllBooks()
    
    val netProfitAllBooks: Flow<Double> = dashboardRepository
        .getNetProfitAllBooks()
    
    val overallROI: Flow<Double> = dashboardRepository
        .getOverallROI()
    
    val topEarningBook: Flow<Book?> = dashboardRepository
        .getTopEarningBook()
    
    // Combined dashboard data
    val dashboardData: StateFlow<DashboardData> = combine(
        totalExpensesAllBooks,
        totalSalesAllBooks,
        netProfitAllBooks,
        overallROI,
        topEarningBook
    ) { totalExpenses, totalSales, netProfit, roi, topBook ->
        DashboardData(
            totalExpenses = totalExpenses,
            totalSales = totalSales,
            netProfit = netProfit,
            overallROI = roi,
            topEarningBook = topBook,
            isProfitable = netProfit > 0,
            breakevenStatus = if (totalSales >= totalExpenses) "Recouped" else "Still Negative"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardData()
    )
    
    fun refreshDashboard() {
        // The StateFlows will automatically refresh due to Flow collection
        // This method is here for manual refresh if needed
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

data class DashboardData(
    val totalExpenses: Double = 0.0,
    val totalSales: Double = 0.0,
    val netProfit: Double = 0.0,
    val overallROI: Double = 0.0,
    val topEarningBook: Book? = null,
    val isProfitable: Boolean = false,
    val breakevenStatus: String = "Still Negative"
) {
    val roiFormatted: String
        get() = if (overallROI.isFinite() && !overallROI.isNaN()) {
            "${String.format("%.1f", overallROI)}%"
        } else {
            "N/A"
        }
    
    val profitMargin: Double
        get() = if (totalSales > 0) {
            (netProfit / totalSales) * 100
        } else {
            0.0
        }
    
    val profitMarginFormatted: String
        get() = if (profitMargin.isFinite() && !profitMargin.isNaN()) {
            "${String.format("%.1f", profitMargin)}%"
        } else {
            "N/A"
        }
    
    val topEarningBookTitle: String
        get() = topEarningBook?.title ?: "No books yet"
    
    val topEarningBookProfit: String
        get() = if (topEarningBook != null) {
            String.format("%.2f", netProfit)
        } else {
            "0.00"
        }
}
