package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.repository.ExpenseRepository
import com.juan.bookledger.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Example ViewModel demonstrating the ideal reactive UI pattern:
 * 
 * 1. DAOs expose Flow<T> - Raw database streams
 * 2. ViewModels transform with map/combine - Business logic
 * 3. UI collects Flow<T> - Reactive updates
 * 
 * This pattern provides:
 * - Better performance (no unnecessary StateFlow caching)
 * - Cleaner separation of concerns
 * - More flexible data transformation
 * - Easier testing
 */
@HiltViewModel
class ExampleReactiveViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {
    
    private val _currentBookId = MutableStateFlow<Long?>(null)
    val currentBookId: StateFlow<Long?> = _currentBookId.asStateFlow()
    
    // Error state (StateFlow is appropriate for UI state)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun setCurrentBook(bookId: Long) {
        _currentBookId.value = bookId
    }
    
    // ✅ BEST PRACTICE: Flow + Transform in ViewModel
    // Raw data from DAO
    val totalSales: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            saleRepository.getTotalSalesByBookId(bookId)
        }
    
    val totalExpenses: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            expenseRepository.getTotalExpensesByBookId(bookId)
        }
    
    // ✅ BEST PRACTICE: Business logic transformation
    val netProfit: Flow<Double> = combine(
        totalSales,
        totalExpenses
    ) { sales, expenses ->
        sales - expenses
    }
    
    val roi: Flow<Double> = combine(
        totalSales,
        totalExpenses
    ) { sales, expenses ->
        val netProfit = sales - expenses
        if (expenses > 0) {
            (netProfit / expenses) * 100
        } else {
            0.0
        }
    }
    
    val breakevenStatus: Flow<String> = combine(
        totalSales,
        totalExpenses
    ) { sales, expenses ->
        if (sales >= expenses) "Recouped" else "Still Negative"
    }
    
    // ✅ BEST PRACTICE: Complex data transformation
    val financialSummary: Flow<FinancialSummary> = combine(
        totalSales,
        totalExpenses,
        netProfit,
        roi,
        breakevenStatus
    ) { sales, expenses, profit, roiValue, status ->
        FinancialSummary(
            totalSales = sales,
            totalExpenses = expenses,
            netProfit = profit,
            roi = roiValue,
            breakevenStatus = status,
            isProfitable = profit > 0,
            roiFormatted = if (roiValue.isFinite()) "${String.format("%.1f", roiValue)}%" else "N/A"
        )
    }
    
    // ✅ BEST PRACTICE: Conditional flows
    val profitableBooks: Flow<Boolean> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            combine(
                saleRepository.getTotalSalesByBookId(bookId),
                expenseRepository.getTotalExpensesByBookId(bookId)
            ) { sales, expenses ->
                sales > expenses
            }
        }
    
    // ✅ BEST PRACTICE: Error handling in flows
    val safeFinancialData: Flow<Result<FinancialSummary>> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            combine(
                saleRepository.getTotalSalesByBookId(bookId).catch { emit(0.0) },
                expenseRepository.getTotalExpensesByBookId(bookId).catch { emit(0.0) }
            ) { sales, expenses ->
                try {
                    val netProfit = sales - expenses
                    val roi = if (expenses > 0) (netProfit / expenses) * 100 else 0.0
                    val status = if (sales >= expenses) "Recouped" else "Still Negative"
                    
                    Result.success(
                        FinancialSummary(
                            totalSales = sales,
                            totalExpenses = expenses,
                            netProfit = netProfit,
                            roi = roi,
                            breakevenStatus = status,
                            isProfitable = netProfit > 0,
                            roiFormatted = if (roi.isFinite()) "${String.format("%.1f", roi)}%" else "N/A"
                        )
                    )
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

data class FinancialSummary(
    val totalSales: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val roi: Double,
    val breakevenStatus: String,
    val isProfitable: Boolean,
    val roiFormatted: String
)
