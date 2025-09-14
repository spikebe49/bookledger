package com.juan.bookledger.data.sync

import android.content.Context
import com.juan.bookledger.data.BookManager
import com.juan.bookledger.data.ExpenseManager
import com.juan.bookledger.data.SaleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

object SyncManager {
    private var context: Context? = null
    private var pocketbaseService: PocketBaseService? = null
    private var oneDriveService: OneDriveService? = null
    private var googleDriveService: GoogleDriveService? = null
    
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    private val _lastSync = MutableStateFlow<Date?>(null)
    val lastSync: StateFlow<Date?> = _lastSync.asStateFlow()
    
    fun initialize(context: Context) {
        this.context = context
        pocketbaseService = PocketBaseService(context)
        oneDriveService = OneDriveService(context)
        googleDriveService = GoogleDriveService(context)
    }
    
    suspend fun syncData(providers: List<SyncProvider>): List<SyncResult> {
        val results = mutableListOf<SyncResult>()
        _syncStatus.value = SyncStatus.SYNCING
        
        try {
            // Collect local data
            val localData = collectLocalData()
            
            // Upload to enabled providers
            providers.forEach { provider ->
                val result = when (provider) {
                    SyncProvider.POCKETBASE -> pocketbaseService?.uploadData(localData)
                    SyncProvider.ONEDRIVE -> oneDriveService?.uploadData(localData)
                    SyncProvider.GOOGLE_DRIVE -> googleDriveService?.uploadData(localData)
                }
                result?.let { results.add(it) }
            }
            
            // Check if any upload was successful
            val hasSuccess = results.any { it.success }
            if (hasSuccess) {
                _lastSync.value = Date()
                _syncStatus.value = SyncStatus.SUCCESS
            } else {
                _syncStatus.value = SyncStatus.ERROR
            }
            
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.ERROR
            results.add(SyncResult(false, "Sync failed: ${e.message}"))
        }
        
        return results
    }
    
    suspend fun restoreData(providers: List<SyncProvider>): List<SyncResult> {
        val results = mutableListOf<SyncResult>()
        _syncStatus.value = SyncStatus.SYNCING
        
        try {
            // Try to download from providers in order
            var restoredData: SyncData? = null
            for (provider in providers) {
                val data = when (provider) {
                    SyncProvider.POCKETBASE -> pocketbaseService?.downloadData()
                    SyncProvider.ONEDRIVE -> oneDriveService?.downloadData()
                    SyncProvider.GOOGLE_DRIVE -> googleDriveService?.downloadData()
                }
                if (data != null) {
                    restoredData = data
                    break
                }
            }
            
            if (restoredData != null) {
                // Restore data to local database
                restoreLocalData(restoredData)
                _lastSync.value = Date()
                _syncStatus.value = SyncStatus.SUCCESS
                results.add(SyncResult(true, "Data restored successfully", Date()))
            } else {
                _syncStatus.value = SyncStatus.ERROR
                results.add(SyncResult(false, "No backup data found"))
            }
            
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.ERROR
            results.add(SyncResult(false, "Restore failed: ${e.message}"))
        }
        
        return results
    }
    
    private suspend fun collectLocalData(): SyncData {
        val context = this.context ?: throw IllegalStateException("SyncManager not initialized")
        
        // Initialize managers if needed
        BookManager.initialize(context)
        ExpenseManager.initialize(context)
        SaleManager.initialize(context)
        
        // Collect all data
        val books = BookManager.getAllBooks().first()
        val expenses = ExpenseManager.getAllExpenses().first()
        val sales = SaleManager.getAllSales().first()
        
        return SyncData(
            books = books,
            expenses = expenses,
            sales = sales,
            lastSync = Date(),
            deviceId = getDeviceId()
        )
    }
    
    private suspend fun restoreLocalData(syncData: SyncData) {
        val context = this.context ?: throw IllegalStateException("SyncManager not initialized")
        
        // Initialize managers if needed
        BookManager.initialize(context)
        ExpenseManager.initialize(context)
        SaleManager.initialize(context)
        
        // Clear existing data (optional - could merge instead)
        // For now, we'll just add the restored data
        
        // Restore books
        syncData.books.forEach { book ->
            BookManager.addBook(
                title = book.title,
                launchDate = book.launchDate,
                description = book.description ?: ""
            )
        }
        
        // Restore expenses
        syncData.expenses.forEach { expense ->
            ExpenseManager.addExpense(
                category = expense.category,
                description = expense.description,
                amount = expense.amount,
                date = expense.date,
                bookId = expense.bookId
            )
        }
        
        // Restore sales
        syncData.sales.forEach { sale ->
            SaleManager.addSale(
                type = sale.type,
                platform = sale.platform,
                bookTitle = sale.bookTitle,
                quantity = sale.quantity,
                unitPrice = sale.unitPrice,
                totalAmount = sale.totalAmount,
                donationAmount = sale.donationAmount,
                isGiveaway = sale.isGiveaway,
                bookId = sale.bookId
            )
        }
    }
    
    private fun getDeviceId(): String {
        val context = this.context ?: return "unknown"
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        var deviceId = prefs.getString("device_id", null)
        if (deviceId == null) {
            deviceId = java.util.UUID.randomUUID().toString()
            prefs.edit().putString("device_id", deviceId).apply()
        }
        return deviceId
    }
    
    fun getSyncSettings(): SyncSettings {
        val context = this.context ?: return SyncSettings()
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        
        return SyncSettings(
            pocketbaseEnabled = prefs.getBoolean("pocketbase_enabled", false),
            onedriveEnabled = prefs.getBoolean("onedrive_enabled", false),
            googledriveEnabled = prefs.getBoolean("googledrive_enabled", false),
            autoSync = prefs.getBoolean("auto_sync", false),
            syncInterval = prefs.getInt("sync_interval", 24),
            lastSync = if (prefs.contains("last_sync")) {
                Date(prefs.getLong("last_sync", 0))
            } else null,
            deviceId = prefs.getString("device_id", "") ?: ""
        )
    }
    
    fun saveSyncSettings(settings: SyncSettings) {
        val context = this.context ?: return
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        
        prefs.edit().apply {
            putBoolean("pocketbase_enabled", settings.pocketbaseEnabled)
            putBoolean("onedrive_enabled", settings.onedriveEnabled)
            putBoolean("googledrive_enabled", settings.googledriveEnabled)
            putBoolean("auto_sync", settings.autoSync)
            putInt("sync_interval", settings.syncInterval)
            settings.lastSync?.let { putLong("last_sync", it.time) }
            putString("device_id", settings.deviceId)
            apply()
        }
    }
}
