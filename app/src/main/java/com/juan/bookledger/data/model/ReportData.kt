package com.juan.bookledger.data.model

import java.util.Date

data class ReportData(
    val generatedDate: Date,
    val totalExpenses: Double,
    val totalIncome: Double,
    val netProfit: Double,
    val expensesByCategory: List<CategoryTotal>,
    val salesBreakdown: SalesBreakdown,
    val summary: ReportSummary
)

data class SalesBreakdown(
    val publisherSales: Double,
    val directSales: Double,
    val donations: Double,
    val giveaways: Int,
    val totalSales: Double,
    val totalQuantity: Int
)

data class ReportSummary(
    val totalTransactions: Int,
    val averageExpense: Double,
    val averageSale: Double,
    val profitMargin: Double,
    val breakevenStatus: String
)
