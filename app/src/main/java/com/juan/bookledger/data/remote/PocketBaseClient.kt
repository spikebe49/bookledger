package com.juan.bookledger.data.remote

import android.content.Context
import com.juan.bookledger.data.auth.UserData
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PocketBaseClient @Inject constructor(
    private val context: Context
) {
    private var authToken: String? = null
    private var currentUser: UserData? = null
    
    suspend fun authenticateWithEmail(email: String, password: String): Result<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(BackendConfig.AUTH_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                val requestBody = JSONObject().apply {
                    put("identity", email)
                    put("password", password)
                }
                
                val outputStream = connection.outputStream
                val writer = OutputStreamWriter(outputStream)
                writer.write(requestBody.toString())
                writer.flush()
                writer.close()
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    val token = jsonResponse.getString("token")
                    val record = jsonResponse.getJSONObject("record")
                    
                    val user = UserData(
                        id = record.getString("id"),
                        email = record.getString("email"),
                        name = record.optString("name", email.substringBefore("@")),
                        authToken = token
                    )
                    
                    authToken = token
                    currentUser = user
                    
                    Result.success(user)
                } else {
                    val errorResponse = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                    Result.failure(Exception("Authentication failed: $errorResponse"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createUser(email: String, password: String): Result<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(BackendConfig.REGISTER_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                val requestBody = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                    put("passwordConfirm", password)
                }
                
                val outputStream = connection.outputStream
                val writer = OutputStreamWriter(outputStream)
                writer.write(requestBody.toString())
                writer.flush()
                writer.close()
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    val user = UserData(
                        id = jsonResponse.getString("id"),
                        email = jsonResponse.getString("email"),
                        name = jsonResponse.optString("name", email.substringBefore("@")),
                        authToken = null // Will be set after login
                    )
                    
                    currentUser = user
                    Result.success(user)
                } else {
                    val errorResponse = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                    Result.failure(Exception("Registration failed: $errorResponse"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getCategories(): Result<List<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(BackendConfig.CATEGORIES_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")
                authToken?.let { token ->
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val items = jsonResponse.getJSONArray("items")
                    
                    val categories = mutableListOf<Map<String, Any>>()
                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        categories.add(mapOf(
                            "id" to item.getString("id"),
                            "name" to item.getString("name"),
                            "type" to item.getString("type"),
                            "color" to item.optString("color", "#2196F3")
                        ))
                    }
                    
                    Result.success(categories)
                } else {
                    Result.failure(Exception("Failed to fetch categories: HTTP $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createExpense(expense: Expense): Result<Expense> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(BackendConfig.EXPENSES_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                authToken?.let { token ->
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }
                connection.doOutput = true
                
                val requestBody = JSONObject().apply {
                    put("amount", expense.amount)
                    put("description", expense.description)
                    put("category_id", expense.categoryId)
                    put("date", expense.date.time) // Convert Date to timestamp
                    put("vendor", expense.vendor ?: "")
                    put("is_recurring", expense.isRecurring)
                    put("payment_method", expense.paymentMethod ?: "")
                    put("receipt_number", expense.receiptNumber ?: "")
                    put("user_id", currentUser?.id)
                }
                
                val outputStream = connection.outputStream
                val writer = OutputStreamWriter(outputStream)
                writer.write(requestBody.toString())
                writer.flush()
                writer.close()
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    val createdExpense = expense.copy(
                        id = jsonResponse.getString("id").toLong()
                    )
                    
                    Result.success(createdExpense)
                } else {
                    val errorResponse = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                    Result.failure(Exception("Failed to create expense: $errorResponse"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createSale(sale: Sale): Result<Sale> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(BackendConfig.SALES_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                authToken?.let { token ->
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }
                connection.doOutput = true
                
                val requestBody = JSONObject().apply {
                    put("type", sale.type.name)
                    put("platform", sale.platform)
                    put("book_title", sale.bookTitle)
                    put("quantity", sale.quantity)
                    put("unit_price", sale.unitPrice)
                    put("total_amount", sale.totalAmount)
                    put("royalty_rate", sale.royaltyRate)
                    put("royalty_amount", sale.royaltyAmount)
                    put("author_earnings", sale.authorEarnings)
                    put("publisher_cut", sale.publisherCut)
                    put("platform_fees", sale.platformFees)
                    put("date", sale.date.time) // Convert Date to timestamp
                    put("user_id", currentUser?.id)
                }
                
                val outputStream = connection.outputStream
                val writer = OutputStreamWriter(outputStream)
                writer.write(requestBody.toString())
                writer.flush()
                writer.close()
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    val createdSale = sale.copy(
                        id = jsonResponse.getString("id").toLong()
                    )
                    
                    Result.success(createdSale)
                } else {
                    val errorResponse = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                    Result.failure(Exception("Failed to create sale: $errorResponse"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun checkHealth(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(BackendConfig.HEALTH_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                
                val responseCode = connection.responseCode
                Result.success(responseCode == HttpURLConnection.HTTP_OK)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun logout() {
        authToken = null
        currentUser = null
    }
    
    fun isAuthenticated(): Boolean = authToken != null && currentUser != null
    
    fun getCurrentUser(): UserData? = currentUser
}
