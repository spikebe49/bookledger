package com.juan.bookledger.data

import android.content.Context
import com.juan.bookledger.data.database.BookLedgerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardData(
    val totalExpenses: Double,
    val totalIncome: Double,
    val netProfit: Double,
    val breakevenStatus: String,
    val expenseCount: Int,
    val saleCount: Int
)

object DashboardManager {
    private var database: BookLedgerDatabase? = null
    
    fun initialize(context: Context) {
        database = BookLedgerDatabase.getDatabase(context)
    }
    
    fun getDashboardData(): Flow<DashboardData> {
        val db = database ?: throw IllegalStateException("Database not initialized")
        
        return combine(
            db.expenseDao().queryAll(),
            db.saleDao().queryAll()
        ) { expenses: List<com.juan.bookledger.data.model.Expense>, sales: List<com.juan.bookledger.data.model.Sale> ->
            val totalExpenses = expenses.sumOf { it.amount }
            val totalIncome = sales.sumOf { it.totalAmount }
            val netProfit = totalIncome - totalExpenses
            val breakevenStatus = if (totalIncome >= totalExpenses) "Recouped" else "Still Negative"
            
            DashboardData(
                totalExpenses = totalExpenses,
                totalIncome = totalIncome,
                netProfit = netProfit,
                breakevenStatus = breakevenStatus,
                expenseCount = expenses.size,
                saleCount = sales.size
            )
        }
    }
    
    fun getExpensesAndSalesForChart(): Flow<Pair<List<Double>, List<Double>>> {
        val db = database ?: throw IllegalStateException("Database not initialized")
        
        return combine(
            db.expenseDao().queryAll(),
            db.saleDao().queryAll()
        ) { expenses: List<com.juan.bookledger.data.model.Expense>, sales: List<com.juan.bookledger.data.model.Sale> ->
            // Group by month for chart data
            val expenseAmounts = expenses.map { it.amount }
            val saleAmounts = sales.map { it.totalAmount }
            
            Pair(expenseAmounts, saleAmounts)
        }
    }
}
