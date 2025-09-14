package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.SaleType
import com.juan.bookledger.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val saleRepository: SaleRepository
) : ViewModel() {
    
    private val _currentBookId = MutableStateFlow<Long?>(null)
    val currentBookId: StateFlow<Long?> = _currentBookId.asStateFlow()
    
    // Sales by bookId
    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()
    
    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Financial totals for current book
    val totalSales: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            saleRepository.getTotalSalesByBookId(bookId)
        }
    
    val directSalesTotal: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            saleRepository.getDirectSalesTotalByBookId(bookId)
        }
    
    val publisherSalesTotal: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            saleRepository.getPublisherSalesTotalByBookId(bookId)
        }
    
    val donationsTotal: Flow<Double> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            saleRepository.getDonationsTotalByBookId(bookId)
        }
    
    val giveawayCount: Flow<Int> = _currentBookId
        .filterNotNull()
        .flatMapLatest { bookId ->
            saleRepository.getGiveawayCountByBookId(bookId)
        }
    
    fun setCurrentBook(bookId: Long) {
        _currentBookId.value = bookId
        loadSalesForBook(bookId)
    }
    
    private fun loadSalesForBook(bookId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                saleRepository.getSalesByBookId(bookId).collect { salesList ->
                    _sales.value = salesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load sales: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addSale(
        type: SaleType,
        platform: String,
        bookTitle: String,
        quantity: Int,
        unitPrice: Double,
        totalAmount: Double,
        date: Date,
        donationAmount: Double = 0.0,
        isGiveaway: Boolean = false,
        bookId: Long
    ) {
        viewModelScope.launch {
            try {
                val sale = Sale(
                    type = type,
                    platform = platform,
                    bookTitle = bookTitle,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalAmount = totalAmount,
                    date = date,
                    donationAmount = donationAmount,
                    isGiveaway = isGiveaway,
                    bookId = bookId
                )
                saleRepository.insertSale(sale)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add sale: ${e.message}"
            }
        }
    }
    
    fun updateSale(sale: Sale) {
        viewModelScope.launch {
            try {
                saleRepository.updateSale(sale)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update sale: ${e.message}"
            }
        }
    }
    
    fun deleteSale(sale: Sale) {
        viewModelScope.launch {
            try {
                saleRepository.deleteSale(sale)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete sale: ${e.message}"
            }
        }
    }
    
    fun deleteSaleById(saleId: Long) {
        viewModelScope.launch {
            try {
                saleRepository.deleteSaleById(saleId)
                _errorMessage.value = null
                // The StateFlow will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete sale: ${e.message}"
            }
        }
    }
    
    fun refreshSales() {
        _currentBookId.value?.let { bookId ->
            loadSalesForBook(bookId)
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
