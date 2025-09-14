package com.juan.bookledger.data

import android.content.Context
import com.juan.bookledger.data.database.BookLedgerDatabase
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.SaleType
import com.juan.bookledger.notifications.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

object SaleManager {
    private var database: BookLedgerDatabase? = null
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context
        database = BookLedgerDatabase.getDatabase(context)
    }
    
    fun addSale(
        type: SaleType,
        platform: String,
        bookTitle: String,
        quantity: Int,
        unitPrice: Double,
        totalAmount: Double,
        donationAmount: Double = 0.0,
        isGiveaway: Boolean = false,
        bookId: Long = 0,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val db = database ?: return onError("Database not initialized")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sale = Sale(
                    type = type,
                    platform = platform,
                    bookTitle = bookTitle,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalAmount = totalAmount,
                    date = Date(),
                    donationAmount = donationAmount,
                    isGiveaway = isGiveaway,
                    bookId = bookId
                )
                db.saleDao().insert(sale)
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
    
    fun getAllSales(): Flow<List<Sale>> {
        val db = database ?: throw IllegalStateException("Database not initialized")
        return db.saleDao().queryAll()
    }
}
