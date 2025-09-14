package com.juan.bookledger.data

import android.content.Context
import com.juan.bookledger.data.database.BookLedgerDatabase
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.ExpenseCategory
import com.juan.bookledger.notifications.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

object ExpenseManager {
    private var database: BookLedgerDatabase? = null
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context
        database = BookLedgerDatabase.getDatabase(context)
    }
    
    fun addExpense(
        category: ExpenseCategory,
        description: String,
        amount: Double,
        date: Date,
        bookId: Long = 0,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val db = database ?: return onError("Database not initialized")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expense = Expense(
                    category = category,
                    description = description,
                    amount = amount,
                    date = date,
                    categoryId = 0, // Default category ID
                    bookId = bookId
                )
                db.expenseDao().insert(expense)
                onSuccess()
                
                // Trigger breakeven alert check
                context?.let { ctx ->
                    NotificationManager.scheduleBreakevenAlert(ctx)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun getAllExpenses(): Flow<List<Expense>> {
        val db = database ?: throw IllegalStateException("Database not initialized")
        return db.expenseDao().queryAll()
    }
}
