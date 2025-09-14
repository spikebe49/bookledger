package com.juan.bookledger.data.backup

import android.content.Context
import com.juan.bookledger.data.auth.UserPreferences
import com.juan.bookledger.data.remote.PocketBaseService
import com.juan.bookledger.data.remote.SyncData
import com.juan.bookledger.data.repository.BookRepository
import com.juan.bookledger.data.repository.ExpenseRepository
import com.juan.bookledger.data.repository.SaleRepository
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val context: Context,
    private val userPreferences: UserPreferences,
    private val pocketBaseService: PocketBaseService,
    private val bookRepository: BookRepository,
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository
) {
    
    suspend fun backupAllData(userId: String): BackupResult {
        return try {
            // Collect all local data
            val books = bookRepository.getAllBooks().first()
            val expenses = expenseRepository.getAllExpenses().first()
            val sales = saleRepository.getAllSales().first()
            
            val syncData = SyncData(
                books = books,
                expenses = expenses,
                sales = sales,
                lastSync = Date(),
                deviceId = getDeviceId()
            )
            
            // Upload to PocketBase
            val result = pocketBaseService.uploadData(syncData)
            
            if (result.success) {
                userPreferences.setLastSync(System.currentTimeMillis())
                BackupResult(
                    success = true,
                    message = "Backup completed successfully",
                    timestamp = Date(),
                    itemsBackedUp = books.size + expenses.size + sales.size
                )
            } else {
                BackupResult(
                    success = false,
                    message = result.message,
                    timestamp = Date(),
                    itemsBackedUp = 0
                )
            }
        } catch (e: Exception) {
            BackupResult(
                success = false,
                message = "Backup failed: ${e.message}",
                timestamp = Date(),
                itemsBackedUp = 0
            )
        }
    }
    
    suspend fun restoreAllData(userId: String): BackupResult {
        return try {
            // Download data from PocketBase
            val syncData = pocketBaseService.downloadData()
            
            if (syncData != null) {
                // Handle merge conflicts by preferring most recent timestamp
                val conflicts = handleMergeConflicts(syncData)
                
                // Restore books
                syncData.books.forEach { book ->
                    bookRepository.insertBook(book)
                }
                
                // Restore expenses
                syncData.expenses.forEach { expense ->
                    expenseRepository.insertExpense(expense)
                }
                
                // Restore sales
                syncData.sales.forEach { sale ->
                    saleRepository.insertSale(sale)
                }
                
                userPreferences.setLastSync(System.currentTimeMillis())
                
                BackupResult(
                    success = true,
                    message = "Restore completed successfully. ${conflicts.size} conflicts resolved.",
                    timestamp = Date(),
                    itemsBackedUp = syncData.books.size + syncData.expenses.size + syncData.sales.size
                )
            } else {
                BackupResult(
                    success = false,
                    message = "No backup data found",
                    timestamp = Date(),
                    itemsBackedUp = 0
                )
            }
        } catch (e: Exception) {
            BackupResult(
                success = false,
                message = "Restore failed: ${e.message}",
                timestamp = Date(),
                itemsBackedUp = 0
            )
        }
    }
    
    private suspend fun handleMergeConflicts(syncData: SyncData): List<ConflictResolution> {
        val conflicts = mutableListOf<ConflictResolution>()
        
        // Check for conflicts in books
        val localBooks = bookRepository.getAllBooks().first()
        syncData.books.forEach { remoteBook ->
            val localBook = localBooks.find { it.id == remoteBook.id }
            if (localBook != null) {
                val conflict = ConflictResolution(
                    type = "Book",
                    id = remoteBook.id.toString(),
                    localTimestamp = localBook.launchDate.time,
                    remoteTimestamp = remoteBook.launchDate.time,
                    resolution = if (remoteBook.launchDate.after(localBook.launchDate)) "Remote" else "Local"
                )
                conflicts.add(conflict)
            }
        }
        
        // Check for conflicts in expenses
        val localExpenses = expenseRepository.getAllExpenses().first()
        syncData.expenses.forEach { remoteExpense ->
            val localExpense = localExpenses.find { it.id == remoteExpense.id }
            if (localExpense != null) {
                val conflict = ConflictResolution(
                    type = "Expense",
                    id = remoteExpense.id.toString(),
                    localTimestamp = localExpense.date.time,
                    remoteTimestamp = remoteExpense.date.time,
                    resolution = if (remoteExpense.date.after(localExpense.date)) "Remote" else "Local"
                )
                conflicts.add(conflict)
            }
        }
        
        // Check for conflicts in sales
        val localSales = saleRepository.getAllSales().first()
        syncData.sales.forEach { remoteSale ->
            val localSale = localSales.find { it.id == remoteSale.id }
            if (localSale != null) {
                val conflict = ConflictResolution(
                    type = "Sale",
                    id = remoteSale.id.toString(),
                    localTimestamp = localSale.date.time,
                    remoteTimestamp = remoteSale.date.time,
                    resolution = if (remoteSale.date.after(localSale.date)) "Remote" else "Local"
                )
                conflicts.add(conflict)
            }
        }
        
        return conflicts
    }
    
    suspend fun backupToOneDrive(): BackupResult {
        // TODO: Implement OneDrive backup
        return BackupResult(
            success = false,
            message = "OneDrive backup not yet implemented",
            timestamp = Date(),
            itemsBackedUp = 0
        )
    }
    
    suspend fun backupToGoogleDrive(): BackupResult {
        // TODO: Implement Google Drive backup
        return BackupResult(
            success = false,
            message = "Google Drive backup not yet implemented",
            timestamp = Date(),
            itemsBackedUp = 0
        )
    }
    
    private fun getDeviceId(): String {
        return android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
    }
}

data class BackupResult(
    val success: Boolean,
    val message: String,
    val timestamp: Date,
    val itemsBackedUp: Int
)

data class ConflictResolution(
    val type: String,
    val id: String,
    val localTimestamp: Long,
    val remoteTimestamp: Long,
    val resolution: String
)

