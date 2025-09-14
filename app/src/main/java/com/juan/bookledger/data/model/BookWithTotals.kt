package com.juan.bookledger.data.model

import androidx.room.Embedded
import java.util.Date

data class BookWithTotals(
    // Book fields (embedded from query)
    val id: Long,
    val title: String,
    val launchDate: Date,
    val description: String?,
    
    // Aggregated totals
    val totalExpenses: Double,
    val totalSales: Double,
    val netProfit: Double,
    val expenseCount: Int,
    val saleCount: Int,
    val averageSalePrice: Double,
    val roiPercentage: Double
) {
    // Computed properties
    val isProfitable: Boolean
        get() = netProfit > 0
    
    val roiFormatted: String
        get() = if (roiPercentage.isFinite() && !roiPercentage.isNaN()) {
            "${String.format("%.1f", roiPercentage)}%"
        } else {
            "N/A"
        }
    
    val profitMargin: Double
        get() = if (totalSales > 0) {
            (netProfit / totalSales) * 100
        } else {
            0.0
        }
    
    val profitMarginFormatted: String
        get() = if (profitMargin.isFinite() && !profitMargin.isNaN()) {
            "${String.format("%.1f", profitMargin)}%"
        } else {
            "N/A"
        }
    
    // Helper method to create Book object
    fun toBook(): Book = Book(
        id = id,
        title = title,
        launchDate = launchDate,
        description = description
    )
}
