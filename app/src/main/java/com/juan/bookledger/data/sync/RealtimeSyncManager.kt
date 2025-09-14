package com.juan.bookledger.data.sync

import android.content.Context
import com.juan.bookledger.data.auth.UserPreferences
import com.juan.bookledger.data.remote.PocketBaseService
import com.juan.bookledger.data.repository.BookRepository
import com.juan.bookledger.data.repository.ExpenseRepository
import com.juan.bookledger.data.repository.SaleRepository
import com.juan.bookledger.data.remote.SyncData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeSyncManager @Inject constructor(
    private val context: Context,
    private val userPreferences: UserPreferences,
    private val pocketBaseService: PocketBaseService,
    private val bookRepository: BookRepository,
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository
) {
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Date?>(null)
    val lastSyncTime: StateFlow<Date?> = _lastSyncTime.asStateFlow()
    
    private var syncJob: Job? = null
    private var isListening = false
    
    // Start real-time sync
    fun startRealtimeSync() {
        if (isListening) return
        
        isListening = true
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                _syncStatus.value = SyncStatus.SYNCING
                
                // Check if user is authenticated
                val user = userPreferences.getCurrentUser()
                if (user == null) {
                    _syncStatus.value = SyncStatus.ERROR
                    return@launch
                }
                
                _syncStatus.value = SyncStatus.SUCCESS
                
                // Start listening for changes
                startListeningForChanges(user.id)
                
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.ERROR
            }
        }
    }
    
    // Stop real-time sync
    fun stopRealtimeSync() {
        isListening = false
        syncJob?.cancel()
        syncJob = null
        _syncStatus.value = SyncStatus.IDLE
    }
    
    // Start listening for changes from PocketBase
    private suspend fun startListeningForChanges(userId: String) {
        try {
            // In a real implementation, this would use PocketBase real-time subscriptions
            // For now, we'll simulate periodic sync
            while (isListening) {
                delay(30000) // Check every 30 seconds
                
                if (isListening) {
                    performSync()
                }
            }
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.ERROR
        }
    }
    
    // Perform sync operation
    suspend fun performSync() {
        try {
            _syncStatus.value = SyncStatus.SYNCING
            
            // Download data from PocketBase
            val syncData = pocketBaseService.downloadData()
            if (syncData != null) {
                // Merge data with local database
                mergeData(syncData)
                _lastSyncTime.value = Date()
            }
            
            _syncStatus.value = SyncStatus.SUCCESS
            
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.ERROR
        }
    }
    
    // Merge downloaded data with local database
    private suspend fun mergeData(syncData: SyncData) {
        try {
            // Merge books
            syncData.books.forEach { book ->
                val existingBook = bookRepository.getBookById(book.id)
                if (existingBook == null) {
                    bookRepository.insertBook(book)
                } else if (book.updatedAt.after(existingBook.updatedAt)) {
                    bookRepository.updateBook(book)
                }
            }
            
            // Merge expenses
            syncData.expenses.forEach { expense ->
                val existingExpense = expenseRepository.getExpenseById(expense.id)
                if (existingExpense == null) {
                    expenseRepository.insertExpense(expense)
                } else if (expense.date.after(existingExpense.date)) {
                    expenseRepository.updateExpense(expense)
                }
            }
            
            // Merge sales
            syncData.sales.forEach { sale ->
                val existingSale = saleRepository.getSaleById(sale.id)
                if (existingSale == null) {
                    saleRepository.insertSale(sale)
                } else if (sale.date.after(existingSale.date)) {
                    saleRepository.updateSale(sale)
                }
            }
            
        } catch (e: Exception) {
            // Handle merge errors
        }
    }
    
    // Force sync
    suspend fun forceSync() {
        performSync()
    }
    
    // Check if sync is active
    fun isSyncActive(): Boolean {
        return isListening && _syncStatus.value != SyncStatus.IDLE
    }
}

