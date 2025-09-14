package com.juan.bookledger.data.analytics

import android.content.Context
import com.juan.bookledger.data.BookManager
import com.juan.bookledger.data.DashboardManager
import com.juan.bookledger.data.ExpenseManager
import com.juan.bookledger.data.SaleManager
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.ExpenseCategory
import com.juan.bookledger.data.model.SaleType
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

object AnalyticsManager {
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context
    }
    
    suspend fun getAnalyticsData(): AnalyticsData {
        val ctx = context ?: throw IllegalStateException("AnalyticsManager not initialized")
        
        // Initialize managers if needed
        BookManager.initialize(ctx)
        ExpenseManager.initialize(ctx)
        SaleManager.initialize(ctx)
        DashboardManager.initialize(ctx)
        
        // Get all data
        val expenses = ExpenseManager.getAllExpenses().first()
        val sales = SaleManager.getAllSales().first()
        val books = BookManager.getAllBooks().first()
        
        // Calculate metrics
        val totalExpenses = expenses.sumOf { it.amount }
        val totalSales = sales.sumOf { it.totalAmount }
        val netProfit = totalSales - totalExpenses
        
        // ROI calculation
        val roiPercentage = if (totalExpenses > 0) {
            (netProfit / totalExpenses) * 100
        } else {
            0.0
        }
        
        // Average sale price
        val totalQuantity = sales.sumOf { it.quantity }
        val averageSalePrice = if (totalQuantity > 0) {
            totalSales / totalQuantity
        } else {
            0.0
        }
        
        // Cost per book (assuming printing costs are in expenses)
        val printingCosts = expenses.filter { 
            it.category == ExpenseCategory.PRINTING_COSTS ||
            it.description.lowercase().contains("printing") ||
            it.description.lowercase().contains("print")
        }.sumOf { it.amount }
        
        val costPerBook = if (books.isNotEmpty()) {
            printingCosts / books.size
        } else {
            0.0
        }
        
        // Best channel analysis
        val publisherSales = sales.filter { it.type == SaleType.PUBLISHER_SALE }.sumOf { it.totalAmount }
        val directSales = sales.filter { it.type == SaleType.DIRECT_SALE }.sumOf { it.totalAmount }
        
        val bestChannel = when {
            publisherSales > directSales -> "Publisher Sales"
            directSales > publisherSales -> "Direct Sales"
            else -> "Tie"
        }
        
        return AnalyticsData(
            roiPercentage = roiPercentage,
            averageSalePrice = averageSalePrice,
            costPerBook = costPerBook,
            bestChannel = bestChannel,
            publisherSalesTotal = publisherSales,
            directSalesTotal = directSales,
            totalExpenses = totalExpenses,
            totalSales = totalSales,
            netProfit = netProfit,
            totalQuantity = totalQuantity,
            printingCosts = printingCosts,
            booksCount = books.size,
            lastUpdated = Date(),
            expenses = expenses,
            sales = sales
        )
    }
    
    suspend fun getSalesChannelData(): List<SalesChannelData> {
        val ctx = context ?: throw IllegalStateException("AnalyticsManager not initialized")
        SaleManager.initialize(ctx)
        
        val sales = SaleManager.getAllSales().first()
        val totalSales = sales.sumOf { it.totalAmount }
        
        val publisherSales = sales.filter { it.type == SaleType.PUBLISHER_SALE }.sumOf { it.totalAmount }
        val directSales = sales.filter { it.type == SaleType.DIRECT_SALE }.sumOf { it.totalAmount }
        val donations = sales.sumOf { it.donationAmount }
        val giveaways = sales.filter { it.isGiveaway }.sumOf { it.totalAmount }
        
        val channelData = mutableListOf<SalesChannelData>()
        
        if (publisherSales > 0) {
            channelData.add(SalesChannelData(
                channel = "Publisher Sales",
                totalAmount = publisherSales,
                percentage = (publisherSales / totalSales) * 100,
                color = 0xFF4CAF50.toInt()
            ))
        }
        
        if (directSales > 0) {
            channelData.add(SalesChannelData(
                channel = "Direct Sales",
                totalAmount = directSales,
                percentage = (directSales / totalSales) * 100,
                color = 0xFF2196F3.toInt()
            ))
        }
        
        if (donations > 0) {
            channelData.add(SalesChannelData(
                channel = "Donations",
                totalAmount = donations,
                percentage = (donations / totalSales) * 100,
                color = 0xFFFF9800.toInt()
            ))
        }
        
        if (giveaways > 0) {
            channelData.add(SalesChannelData(
                channel = "Giveaways",
                totalAmount = giveaways,
                percentage = (giveaways / totalSales) * 100,
                color = 0xFF9C27B0.toInt()
            ))
        }
        
        return channelData
    }
    
    suspend fun getMonthlyTrendData(): List<MonthlyTrendData> {
        val ctx = context ?: throw IllegalStateException("AnalyticsManager not initialized")
        ExpenseManager.initialize(ctx)
        SaleManager.initialize(ctx)
        
        val expenses = ExpenseManager.getAllExpenses().first()
        val sales = SaleManager.getAllSales().first()
        
        // Group by month
        val monthlyData = mutableMapOf<String, Triple<Double, Double, Double>>()
        
        expenses.forEach { expense ->
            val month = SimpleDateFormat("MMM yyyy", Locale.CANADA).format(expense.date)
            val current = monthlyData[month] ?: Triple(0.0, 0.0, 0.0)
            monthlyData[month] = Triple(current.first + expense.amount, current.second, current.third)
        }
        
        sales.forEach { sale ->
            val month = SimpleDateFormat("MMM yyyy", Locale.CANADA).format(sale.date)
            val current = monthlyData[month] ?: Triple(0.0, 0.0, 0.0)
            val newSales = current.second + sale.totalAmount
            val newProfit = newSales - current.first
            monthlyData[month] = Triple(current.first, newSales, newProfit)
        }
        
        return monthlyData.map { (month, data) ->
            MonthlyTrendData(
                month = month,
                expenses = data.first,
                sales = data.second,
                profit = data.third
            )
        }.sortedBy { 
            try {
                SimpleDateFormat("MMM yyyy", Locale.CANADA).parse(it.month)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
    }
}
