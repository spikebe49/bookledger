package com.juan.bookledger.data.analytics

import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import java.util.Date

data class AnalyticsData(
    val roiPercentage: Double,
    val averageSalePrice: Double,
    val costPerBook: Double,
    val bestChannel: String,
    val publisherSalesTotal: Double,
    val directSalesTotal: Double,
    val totalExpenses: Double,
    val totalSales: Double,
    val netProfit: Double,
    val totalQuantity: Int,
    val printingCosts: Double,
    val booksCount: Int,
    val lastUpdated: Date,
    val expenses: List<com.juan.bookledger.data.model.Expense> = emptyList(),
    val sales: List<com.juan.bookledger.data.model.Sale> = emptyList()
)

data class ChartData(
    val labels: List<String>,
    val values: List<Double>,
    val colors: List<Int>
)

data class SalesChannelData(
    val channel: String,
    val totalAmount: Double,
    val percentage: Double,
    val color: Int
)

data class MonthlyTrendData(
    val month: String,
    val expenses: Double,
    val sales: Double,
    val profit: Double
)
