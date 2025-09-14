package com.juan.bookledger.data.model

import java.util.Date

data class AuthorFinancialAnalytics(
    val bookId: Long,
    val bookTitle: String,
    
    // Investment Tracking
    val totalInvestment: Double, // Total money spent developing the book
    val publisherFees: Double,   // Total paid to publisher
    val illustratorFees: Double, // Total paid to illustrator
    val editingCosts: Double,    // Editing and proofreading costs
    val marketingCosts: Double,  // Marketing and promotion costs
    val productionCosts: Double, // Printing, shipping, inventory
    val otherCosts: Double,      // All other expenses
    
    // Revenue Tracking
    val totalRevenue: Double,    // Total revenue from all sources
    val publisherRevenue: Double, // Revenue from publisher sales
    val directRevenue: Double,   // Direct sales revenue
    val onlineStoreRevenue: Double, // Amazon, B&N, etc.
    val otherRevenue: Double,    // Other revenue sources
    
    // Royalty Analysis
    val averagePublisherRoyalty: Double, // Average royalty rate from publisher
    val averageOnlineRoyalty: Double,    // Average royalty rate from online stores
    val totalRoyaltiesReceived: Double,  // Total royalties actually received
    val publisherCut: Double,            // Total amount publisher kept
    val platformFees: Double,           // Total platform fees paid
    
    // Profitability Analysis
    val netProfit: Double,               // Total profit (revenue - investment)
    val profitMargin: Double,            // Profit margin percentage
    val roiPercentage: Double,           // Return on investment percentage
    
    // Break-even Analysis
    val breakEvenPoint: BreakEvenAnalysis,
    val isBreakEven: Boolean,            // Has the book broken even?
    val breakEvenDate: Date?,            // When break-even was achieved
    val profitStartDate: Date?,          // When profit started
    
    // Sales Performance
    val totalBooksSold: Int,
    val averageSalePrice: Double,
    val bestSellingPlatform: String,
    val worstSellingPlatform: String,
    
    // Time-based Analysis
    val daysToBreakEven: Int?,           // Days from launch to break-even
    val daysToProfit: Int?,              // Days from launch to profit
    val monthsSinceLaunch: Int,          // Months since book launch
    
    // Financial Health Indicators
    val financialHealth: FinancialHealth,
    val recommendations: List<String>    // AI-generated recommendations
)

data class BreakEvenAnalysis(
    val breakEvenQuantity: Int,          // Number of books needed to break even
    val breakEvenRevenue: Double,        // Revenue needed to break even
    val booksSoldToBreakEven: Int,       // How many books sold so far
    val remainingToBreakEven: Int,       // Books still needed to break even
    val breakEvenProgress: Double        // Progress percentage (0-100)
)

enum class FinancialHealth {
    EXCELLENT,    // High profit, good ROI
    GOOD,         // Profitable, decent ROI
    BREAK_EVEN,   // Just breaking even
    LOSS,         // Losing money
    CRITICAL      // Significant losses
}

data class AuthorPlatformPerformance(
    val platform: String,
    val totalSales: Int,
    val totalRevenue: Double,
    val averageRoyaltyRate: Double,
    val totalRoyalties: Double,
    val platformFees: Double,
    val netEarnings: Double,
    val roi: Double
)

data class MonthlyFinancialSummary(
    val month: String,
    val year: Int,
    val totalExpenses: Double,
    val totalRevenue: Double,
    val netProfit: Double,
    val booksSold: Int,
    val averageSalePrice: Double
)
