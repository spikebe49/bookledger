package com.juan.bookledger.data

import android.content.Context
import com.juan.bookledger.data.database.BookLedgerDatabase
import com.juan.bookledger.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object ReportGenerator {
    private var database: BookLedgerDatabase? = null
    
    fun initialize(context: Context) {
        database = BookLedgerDatabase.getDatabase(context)
    }
    
    suspend fun generateReport(): ReportData = withContext(Dispatchers.IO) {
        val db = database ?: throw IllegalStateException("Database not initialized")
        
        // Get all data
        val expenses = db.expenseDao().queryAll()
        val sales = db.saleDao().queryAll()
        
        // Collect data
        val expenseList = mutableListOf<Expense>()
        val saleList = mutableListOf<Sale>()
        
        expenses.collect { expenseList.addAll(it) }
        sales.collect { saleList.addAll(it) }
        
        // Calculate totals
        val totalExpenses = expenseList.sumOf { it.amount }
        val totalIncome = saleList.sumOf { it.totalAmount }
        val netProfit = totalIncome - totalExpenses
        
        // Group expenses by category
        val expensesByCategory = expenseList.groupBy { it.category }
            .map { (category, expenses) -> 
                CategoryTotal(category.name, expenses.sumOf { it.amount })
            }
            .sortedByDescending { it.total }
        
        // Calculate sales breakdown
        val publisherSales = saleList.filter { it.type == SaleType.PUBLISHER_SALE }.sumOf { it.totalAmount }
        val directSales = saleList.filter { it.type == SaleType.DIRECT_SALE }.sumOf { it.totalAmount }
        val donations = saleList.sumOf { it.donationAmount }
        val giveaways = saleList.count { it.isGiveaway }
        val totalQuantity = saleList.sumOf { it.quantity }
        
        val salesBreakdown = SalesBreakdown(
            publisherSales = publisherSales,
            directSales = directSales,
            donations = donations,
            giveaways = giveaways,
            totalSales = totalIncome,
            totalQuantity = totalQuantity
        )
        
        // Calculate summary metrics
        val totalTransactions = expenseList.size + saleList.size
        val averageExpense = if (expenseList.isNotEmpty()) totalExpenses / expenseList.size else 0.0
        val averageSale = if (saleList.isNotEmpty()) totalIncome / saleList.size else 0.0
        val profitMargin = if (totalIncome > 0) (netProfit / totalIncome) * 100 else 0.0
        val breakevenStatus = if (totalIncome >= totalExpenses) "Recouped" else "Still Negative"
        
        val summary = ReportSummary(
            totalTransactions = totalTransactions,
            averageExpense = averageExpense,
            averageSale = averageSale,
            profitMargin = profitMargin,
            breakevenStatus = breakevenStatus
        )
        
        ReportData(
            generatedDate = Date(),
            totalExpenses = totalExpenses,
            totalIncome = totalIncome,
            netProfit = netProfit,
            expensesByCategory = expensesByCategory,
            salesBreakdown = salesBreakdown,
            summary = summary
        )
    }
    
    suspend fun exportToCSV(context: Context, reportData: ReportData): File = withContext(Dispatchers.IO) {
        val fileName = "BookLedger_Report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(reportData.generatedDate)}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        FileWriter(file).use { writer ->
            val currencyFormatter = NumberFormat.getCurrencyInstance()
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            
            // Header
            writer.appendLine("BookLedger Financial Report")
            writer.appendLine("Generated: ${dateFormatter.format(reportData.generatedDate)}")
            writer.appendLine()
            
            // Summary
            writer.appendLine("=== FINANCIAL SUMMARY ===")
            writer.appendLine("Total Expenses,${currencyFormatter.format(reportData.totalExpenses)}")
            writer.appendLine("Total Income,${currencyFormatter.format(reportData.totalIncome)}")
            writer.appendLine("Net Profit,${currencyFormatter.format(reportData.netProfit)}")
            writer.appendLine("Breakeven Status,${reportData.summary.breakevenStatus}")
            writer.appendLine()
            
            // Expenses by Category
            writer.appendLine("=== EXPENSES BY CATEGORY ===")
            writer.appendLine("Category,Amount")
            reportData.expensesByCategory.forEach { category ->
                writer.appendLine("${category.category},${currencyFormatter.format(category.total)}")
            }
            writer.appendLine()
            
            // Sales Breakdown
            writer.appendLine("=== SALES BREAKDOWN ===")
            writer.appendLine("Publisher Sales,${currencyFormatter.format(reportData.salesBreakdown.publisherSales)}")
            writer.appendLine("Direct Sales,${currencyFormatter.format(reportData.salesBreakdown.directSales)}")
            writer.appendLine("Donations,${currencyFormatter.format(reportData.salesBreakdown.donations)}")
            writer.appendLine("Giveaways,${reportData.salesBreakdown.giveaways}")
            writer.appendLine("Total Sales,${currencyFormatter.format(reportData.salesBreakdown.totalSales)}")
            writer.appendLine("Total Quantity,${reportData.salesBreakdown.totalQuantity}")
            writer.appendLine()
            
            // Summary Metrics
            writer.appendLine("=== SUMMARY METRICS ===")
            writer.appendLine("Total Transactions,${reportData.summary.totalTransactions}")
            writer.appendLine("Average Expense,${currencyFormatter.format(reportData.summary.averageExpense)}")
            writer.appendLine("Average Sale,${currencyFormatter.format(reportData.summary.averageSale)}")
            writer.appendLine("Profit Margin,${String.format("%.2f", reportData.summary.profitMargin)}%")
        }
        
        file
    }
    
    suspend fun exportToPDF(context: Context, reportData: ReportData): File = withContext(Dispatchers.IO) {
        val fileName = "BookLedger_Report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(reportData.generatedDate)}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        // Note: This is a simplified PDF generation
        // In a production app, you'd want to use a more robust PDF library
        // For now, we'll create a basic text-based PDF structure
        
        val currencyFormatter = NumberFormat.getCurrencyInstance()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        // Create a simple text file that can be opened as PDF
        // In a real implementation, you'd use iText or similar library
        FileWriter(file).use { writer ->
            writer.appendLine("BookLedger Financial Report")
            writer.appendLine("Generated: ${dateFormatter.format(reportData.generatedDate)}")
            writer.appendLine()
            writer.appendLine("=== FINANCIAL SUMMARY ===")
            writer.appendLine("Total Expenses: ${currencyFormatter.format(reportData.totalExpenses)}")
            writer.appendLine("Total Income: ${currencyFormatter.format(reportData.totalIncome)}")
            writer.appendLine("Net Profit: ${currencyFormatter.format(reportData.netProfit)}")
            writer.appendLine("Breakeven Status: ${reportData.summary.breakevenStatus}")
            writer.appendLine()
            writer.appendLine("=== EXPENSES BY CATEGORY ===")
            reportData.expensesByCategory.forEach { category ->
                writer.appendLine("${category.category}: ${currencyFormatter.format(category.total)}")
            }
            writer.appendLine()
            writer.appendLine("=== SALES BREAKDOWN ===")
            writer.appendLine("Publisher Sales: ${currencyFormatter.format(reportData.salesBreakdown.publisherSales)}")
            writer.appendLine("Direct Sales: ${currencyFormatter.format(reportData.salesBreakdown.directSales)}")
            writer.appendLine("Donations: ${currencyFormatter.format(reportData.salesBreakdown.donations)}")
            writer.appendLine("Giveaways: ${reportData.salesBreakdown.giveaways}")
            writer.appendLine("Total Sales: ${currencyFormatter.format(reportData.salesBreakdown.totalSales)}")
            writer.appendLine("Total Quantity: ${reportData.salesBreakdown.totalQuantity}")
            writer.appendLine()
            writer.appendLine("=== SUMMARY METRICS ===")
            writer.appendLine("Total Transactions: ${reportData.summary.totalTransactions}")
            writer.appendLine("Average Expense: ${currencyFormatter.format(reportData.summary.averageExpense)}")
            writer.appendLine("Average Sale: ${currencyFormatter.format(reportData.summary.averageSale)}")
            writer.appendLine("Profit Margin: ${String.format("%.2f", reportData.summary.profitMargin)}%")
        }
        
        file
    }
}

