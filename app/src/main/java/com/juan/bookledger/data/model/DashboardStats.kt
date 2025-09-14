package com.juan.bookledger.data.model

import java.util.Date

data class DashboardStats(
    val totalExpenses: Double = 0.0,
    val totalSales: Double = 0.0,
    val netProfit: Double = 0.0,
    val expenseCount: Int = 0,
    val saleCount: Int = 0,
    val recentExpenses: List<ExpenseWithCategory> = emptyList(),
    val recentSales: List<SaleWithCategory> = emptyList(),
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val salesByCategory: Map<String, Double> = emptyMap()
)

data class MonthlyStats(
    val month: String,
    val year: Int,
    val totalExpenses: Double,
    val totalSales: Double,
    val netProfit: Double
)
