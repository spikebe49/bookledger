package com.juan.bookledger.data.repository

import com.juan.bookledger.data.model.DashboardStats
import com.juan.bookledger.data.model.ExpenseWithCategory
import com.juan.bookledger.data.model.SaleWithCategory
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.ExpenseCategory
import com.juan.bookledger.data.model.SaleType
import com.juan.bookledger.data.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository,
    private val bookRepository: BookRepository
) {
    fun getDashboardStats(): Flow<DashboardStats> {
        val calendar = Calendar.getInstance()
        val startOfMonth = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        
        val endOfMonth = calendar.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }.time

        return combine(
            expenseRepository.getAllExpenses(),
            saleRepository.getAllSales()
        ) { expenses, sales ->
            val totalExpenses = expenses.sumOf { it.amount }
            val totalSales = sales.sumOf { it.totalAmount }
            val netProfit = totalSales - totalExpenses
            
            val expensesByCategory = expenses.groupBy { it.category }
                .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
                .mapKeys { (category, _) -> category.name }
            
            val salesByCategory = sales.groupBy { it.type }
                .mapValues { (_, sales) -> sales.sumOf { it.totalAmount } }
                .mapKeys { (type, _) -> type.name }

            DashboardStats(
                totalExpenses = totalExpenses,
                totalSales = totalSales,
                netProfit = netProfit,
                expenseCount = expenses.size,
                saleCount = sales.size,
                recentExpenses = emptyList(),
                recentSales = emptyList(),
                expensesByCategory = expensesByCategory,
                salesByCategory = salesByCategory
            )
        }
    }
    
    // Global stats across all books
    fun getTotalExpensesAllBooks(): Flow<Double> {
        return expenseRepository.getAllExpenses().map { expenses ->
            expenses.sumOf { it.amount }
        }
    }
    
    fun getTotalSalesAllBooks(): Flow<Double> {
        return saleRepository.getAllSales().map { sales ->
            sales.sumOf { it.totalAmount }
        }
    }
    
    fun getNetProfitAllBooks(): Flow<Double> {
        return combine(
            getTotalSalesAllBooks(),
            getTotalExpensesAllBooks()
        ) { totalSales, totalExpenses ->
            totalSales - totalExpenses
        }
    }
    
    fun getOverallROI(): Flow<Double> {
        return combine(
            getTotalSalesAllBooks(),
            getTotalExpensesAllBooks()
        ) { totalSales, totalExpenses ->
            val netProfit = totalSales - totalExpenses
            if (totalExpenses > 0) {
                (netProfit / totalExpenses) * 100
            } else {
                0.0
            }
        }
    }
    
    fun getTopEarningBook(): Flow<Book?> {
        return bookRepository.getAllBooksWithTotals().map { booksWithTotals ->
            booksWithTotals.maxByOrNull { it.netProfit }?.toBook()
        }
    }
}
