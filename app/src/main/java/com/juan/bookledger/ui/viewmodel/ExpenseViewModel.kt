package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.CategoryTotal
import com.juan.bookledger.data.model.ExpenseCategory
import com.juan.bookledger.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    
    private val _currentBookId = MutableStateFlow<Long?>(null)
    val currentBookId: StateFlow<Long?> = _currentBookId.asStateFlow()
    
    // Expenses by bookId
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Financial totals for current book
    val totalExpenses: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            expenseRepository.getTotalExpensesByBookId(bookId)
        }
    
    fun setCurrentBook(bookId: Long) {
        _currentBookId.value = bookId
        loadExpensesForBook(bookId)
    }
    
    private fun loadExpensesForBook(bookId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                expenseRepository.getExpensesByBookId(bookId).collect { expensesList ->
                    _expenses.value = expensesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load expenses: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Legacy method for backward compatibility
    private fun loadExpenses() {
        // This method is kept for backward compatibility but should not be used
        // Use setCurrentBook(bookId) instead
    }
    
    fun addExpense(
        category: ExpenseCategory,
        description: String,
        amount: Double,
        date: Date,
        bookId: Long
    ) {
        viewModelScope.launch {
            try {
                val expense = Expense(
                    category = category,
                    description = description,
                    amount = amount,
                    date = date,
                    categoryId = 0, // Will be set based on category lookup if needed
                    bookId = bookId
                )
                expenseRepository.insertExpense(expense)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add expense: ${e.message}"
            }
        }
    }
    
    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                expenseRepository.updateExpense(expense)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update expense: ${e.message}"
            }
        }
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpense(expense)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete expense: ${e.message}"
            }
        }
    }
    
    fun deleteExpenseById(expenseId: Long) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpenseById(expenseId)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete expense: ${e.message}"
            }
        }
    }
    
    fun getExpensesByCategory(bookId: Long): Flow<List<CategoryTotal>> {
        return flow {
            try {
                val categories = expenseRepository.getExpensesByCategoryByBookId(bookId)
                emit(categories)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load expenses by category: ${e.message}"
                emit(emptyList())
            }
        }
    }
    
    fun refreshExpenses() {
        _currentBookId.value?.let { bookId ->
            loadExpensesForBook(bookId)
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
