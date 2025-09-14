package com.juan.bookledger.data.remote

import android.content.Context
import com.juan.bookledger.data.auth.UserData
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PocketBaseService @Inject constructor(
    private val context: Context,
    private val pocketBaseClient: PocketBaseClient
) {
    // Real PocketBase integration
    private var currentUser: UserData? = null
    
    suspend fun authenticateWithEmail(email: String, password: String): Result<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val result = pocketBaseClient.authenticateWithEmail(email, password)
                if (result.isSuccess) {
                    currentUser = result.getOrNull()
                }
                result
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createUser(email: String, password: String): Result<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val result = pocketBaseClient.createUser(email, password)
                if (result.isSuccess) {
                    currentUser = result.getOrNull()
                }
                result
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            try {
                pocketBaseClient.logout()
                currentUser = null
            } catch (e: Exception) {
                // Ignore errors during sign out
            }
        }
    }
    
    suspend fun uploadData(syncData: SyncData): SyncResult {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUser?.id ?: return@withContext SyncResult(false, "Not authenticated")
                
                // Upload expenses
                syncData.expenses.forEach { expense ->
                    pocketBaseClient.createExpense(expense)
                }
                
                // Upload sales
                syncData.sales.forEach { sale ->
                    pocketBaseClient.createSale(sale)
                }
                
                SyncResult(true, "Data uploaded successfully to user: $userId")
            } catch (e: Exception) {
                SyncResult(false, "Upload failed: ${e.message}")
            }
        }
    }
    
    suspend fun downloadData(): SyncData? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUser?.id ?: return@withContext null
                
                // Download categories
                val categoriesResult = pocketBaseClient.getCategories()
                if (categoriesResult.isFailure) {
                    return@withContext null
                }
                
                // For now, return empty data - in a full implementation,
                // you would download expenses and sales from PocketBase
                SyncData(
                    books = emptyList(),
                    expenses = emptyList(),
                    sales = emptyList(),
                    lastSync = java.util.Date(),
                    deviceId = getDeviceId()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun checkBackendHealth(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                pocketBaseClient.checkHealth()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun isAuthenticated(): Boolean = pocketBaseClient.isAuthenticated()
    
    fun getCurrentUser(): UserData? = pocketBaseClient.getCurrentUser()
    
    private fun getDeviceId(): String {
        return android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
    }
}

data class SyncData(
    val books: List<Book>,
    val expenses: List<Expense>,
    val sales: List<Sale>,
    val lastSync: java.util.Date,
    val deviceId: String
)

data class SyncResult(
    val success: Boolean,
    val message: String,
    val timestamp: java.util.Date = java.util.Date()
)