package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.repository.ExpenseRepository
import com.juan.bookledger.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {
    
    private val _currentBookId = MutableStateFlow<Long?>(null)
    val currentBookId: StateFlow<Long?> = _currentBookId.asStateFlow()
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun setCurrentBook(bookId: Long) {
        _currentBookId.value = bookId
    }
    
    // Net Profit = totalSales - totalExpenses
    val netProfit: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            combine(
                saleRepository.getTotalSalesByBookId(bookId),
                expenseRepository.getTotalExpensesByBookId(bookId)
            ) { totalSales, totalExpenses ->
                totalSales - totalExpenses
            }
        }
    
    // ROI = (netProfit / totalExpenses) * 100 (if expenses > 0)
    val roi: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            combine(
                saleRepository.getTotalSalesByBookId(bookId),
                expenseRepository.getTotalExpensesByBookId(bookId)
            ) { totalSales, totalExpenses ->
                val netProfit = totalSales - totalExpenses
                if (totalExpenses > 0) {
                    (netProfit / totalExpenses) * 100
                } else {
                    0.0
                }
            }
        }
    
    // Breakeven Status: if totalSales >= totalExpenses → "Recouped" else "Still Negative"
    val breakevenStatus: Flow<String> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            combine(
                saleRepository.getTotalSalesByBookId(bookId),
                expenseRepository.getTotalExpensesByBookId(bookId)
            ) { totalSales, totalExpenses ->
                if (totalSales >= totalExpenses) {
                    "Recouped"
                } else {
                    "Still Negative"
                }
            }
        }
    
    // Sales Breakdown: percentages for Publisher vs Direct sales
    fun getSalesBreakdown(bookId: Long): Flow<Map<String, Double>> {
        return flow {
            try {
                val breakdown = saleRepository.getSalesBreakdownByBookId(bookId)
                val total = breakdown.sumOf { it.total }
                
                val percentages = if (total > 0) {
                    breakdown.associate { typeTotal ->
                        typeTotal.type to (typeTotal.total / total) * 100
                    }
                } else {
                    emptyMap()
                }
                
                emit(percentages)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load sales breakdown: ${e.message}"
                emit(emptyMap())
            }
        }
    }
    
    // Combined data for current book
    val bookReportData: StateFlow<BookReportData?> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            combine(
                saleRepository.getTotalSalesByBookId(bookId),
                expenseRepository.getTotalExpensesByBookId(bookId),
                saleRepository.getDirectSalesTotalByBookId(bookId),
                saleRepository.getPublisherSalesTotalByBookId(bookId),
                saleRepository.getDonationsTotalByBookId(bookId),
                saleRepository.getGiveawayCountByBookId(bookId)
            ) { values ->
                val totalSales = values[0] as Double
                val totalExpenses = values[1] as Double
                val directSales = values[2] as Double
                val publisherSales = values[3] as Double
                val donations = values[4] as Double
                val giveawayCount = values[5] as Int
                
                val netProfit = totalSales - totalExpenses
                val roi = if (totalExpenses > 0) {
                    (netProfit / totalExpenses) * 100
                } else {
                    0.0
                }
                val breakevenStatus = if (totalSales >= totalExpenses) {
                    "Recouped"
                } else {
                    "Still Negative"
                }
                
                BookReportData(
                    bookId = bookId,
                    totalSales = totalSales,
                    totalExpenses = totalExpenses,
                    netProfit = netProfit,
                    roi = roi,
                    breakevenStatus = breakevenStatus,
                    directSales = directSales,
                    publisherSales = publisherSales,
                    donations = donations,
                    giveawayCount = giveawayCount
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    fun clearError() {
        _errorMessage.value = null
    }
}

data class BookReportData(
    val bookId: Long,
    val totalSales: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val roi: Double,
    val breakevenStatus: String,
    val directSales: Double,
    val publisherSales: Double,
    val donations: Double,
    val giveawayCount: Int
) {
    val isProfitable: Boolean
        get() = netProfit > 0
    
    val roiFormatted: String
        get() = if (roi.isFinite() && !roi.isNaN()) {
            "${String.format("%.1f", roi)}%"
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
}
