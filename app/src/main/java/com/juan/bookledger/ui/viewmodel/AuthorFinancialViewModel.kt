package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.model.*
import com.juan.bookledger.data.repository.BookRepository
import com.juan.bookledger.data.repository.ExpenseRepository
import com.juan.bookledger.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AuthorFinancialViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _financialAnalytics = MutableStateFlow<AuthorFinancialAnalytics?>(null)
    val financialAnalytics: StateFlow<AuthorFinancialAnalytics?> = _financialAnalytics.asStateFlow()
    
    fun loadFinancialAnalytics(bookId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val analytics = calculateFinancialAnalytics(bookId)
                _financialAnalytics.value = analytics
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getFinancialAnalytics(bookId: Long): StateFlow<AuthorFinancialAnalytics?> {
        return _financialAnalytics.asStateFlow()
    }
    
    fun refreshAnalytics(bookId: Long) {
        loadFinancialAnalytics(bookId)
    }
    
    private suspend fun calculateFinancialAnalytics(bookId: Long): AuthorFinancialAnalytics {
        // Get book details
        val book = bookRepository.getBookById(bookId) ?: throw Exception("Book not found")
        
        // Get all expenses for this book
        val expenses = expenseRepository.getExpensesByBookId(bookId).first()
        
        // Get all sales for this book
        val sales = saleRepository.getSalesByBookId(bookId).first()
        
        // Calculate investment breakdown
        val investmentBreakdown = calculateInvestmentBreakdown(expenses)
        
        // Calculate revenue analysis
        val revenueAnalysis = calculateRevenueAnalysis(sales)
        
        // Calculate break-even analysis
        val breakEvenAnalysis = calculateBreakEvenAnalysis(
            totalInvestment = investmentBreakdown.totalInvestment,
            totalRevenue = revenueAnalysis.totalRevenue,
            totalBooksSold = sales.sumOf { sale -> sale.quantity }
        )
        
        // Calculate profitability metrics
        val netProfit = revenueAnalysis.totalRevenue - investmentBreakdown.totalInvestment
        val roiPercentage = if (investmentBreakdown.totalInvestment > 0) {
            (netProfit / investmentBreakdown.totalInvestment) * 100
        } else 0.0
        
        val profitMargin = if (revenueAnalysis.totalRevenue > 0) {
            (netProfit / revenueAnalysis.totalRevenue) * 100
        } else 0.0
        
        // Calculate financial health
        val financialHealth = calculateFinancialHealth(netProfit, roiPercentage)
        
        // Generate AI recommendations
        val recommendations = generateAIRecommendations(
            financialHealth = financialHealth,
            netProfit = netProfit,
            roiPercentage = roiPercentage,
            breakEvenProgress = breakEvenAnalysis.breakEvenProgress
        )
        
        return AuthorFinancialAnalytics(
            bookId = bookId,
            bookTitle = book.title,
            totalInvestment = investmentBreakdown.totalInvestment,
            publisherFees = investmentBreakdown.publisherFees,
            illustratorFees = investmentBreakdown.illustratorFees,
            editingCosts = investmentBreakdown.editingCosts,
            marketingCosts = investmentBreakdown.marketingCosts,
            productionCosts = investmentBreakdown.productionCosts,
            otherCosts = investmentBreakdown.otherCosts,
            totalRevenue = revenueAnalysis.totalRevenue,
            publisherRevenue = revenueAnalysis.publisherRevenue,
            directRevenue = revenueAnalysis.directRevenue,
            onlineStoreRevenue = revenueAnalysis.onlineStoreRevenue,
            otherRevenue = revenueAnalysis.otherRevenue,
            averagePublisherRoyalty = revenueAnalysis.averagePublisherRoyalty,
            averageOnlineRoyalty = revenueAnalysis.averageOnlineRoyalty,
            totalRoyaltiesReceived = revenueAnalysis.totalRoyaltiesReceived,
            publisherCut = revenueAnalysis.publisherCut,
            platformFees = revenueAnalysis.platformFees,
            netProfit = netProfit,
            profitMargin = profitMargin,
            roiPercentage = roiPercentage,
            breakEvenPoint = breakEvenAnalysis,
            isBreakEven = breakEvenAnalysis.breakEvenProgress >= 100,
            breakEvenDate = if (breakEvenAnalysis.breakEvenProgress >= 100) Date() else null,
            profitStartDate = if (netProfit > 0) Date() else null,
            totalBooksSold = sales.sumOf { sale -> sale.quantity },
            averageSalePrice = if (sales.isNotEmpty()) sales.map { sale -> sale.unitPrice }.average() else 0.0,
            bestSellingPlatform = findBestSellingPlatform(sales),
            worstSellingPlatform = findWorstSellingPlatform(sales),
            daysToBreakEven = calculateDaysToBreakEven(book.launchDate, breakEvenAnalysis.breakEvenProgress >= 100),
            daysToProfit = calculateDaysToProfit(book.launchDate, netProfit > 0),
            monthsSinceLaunch = calculateMonthsSinceLaunch(book.launchDate),
            financialHealth = financialHealth,
            recommendations = recommendations
        )
    }
    
    private fun calculateInvestmentBreakdown(expenses: List<Expense>): InvestmentBreakdown {
        var totalInvestment = 0.0
        var publisherFees = 0.0
        var illustratorFees = 0.0
        var editingCosts = 0.0
        var marketingCosts = 0.0
        var productionCosts = 0.0
        var otherCosts = 0.0
        
        expenses.forEach { expense ->
            totalInvestment += expense.amount
            when (expense.category) {
                ExpenseCategory.PUBLISHER_FEES -> publisherFees += expense.amount
                ExpenseCategory.ILLUSTRATOR_FEES -> illustratorFees += expense.amount
                ExpenseCategory.EDITING_SERVICES, ExpenseCategory.PROOFREADING -> editingCosts += expense.amount
                ExpenseCategory.MARKETING, ExpenseCategory.ADVERTISING, ExpenseCategory.BOOK_TOURS, 
                ExpenseCategory.EVENTS, ExpenseCategory.PROMOTIONAL_MATERIALS, ExpenseCategory.WEBSITE -> marketingCosts += expense.amount
                ExpenseCategory.PRINTING_COSTS, ExpenseCategory.SHIPPING_COSTS, ExpenseCategory.INVENTORY, 
                ExpenseCategory.STORAGE -> productionCosts += expense.amount
                else -> otherCosts += expense.amount
            }
        }
        
        return InvestmentBreakdown(
            totalInvestment = totalInvestment,
            publisherFees = publisherFees,
            illustratorFees = illustratorFees,
            editingCosts = editingCosts,
            marketingCosts = marketingCosts,
            productionCosts = productionCosts,
            otherCosts = otherCosts
        )
    }
    
    private fun calculateRevenueAnalysis(sales: List<Sale>): RevenueAnalysis {
        var totalRevenue = 0.0
        var publisherRevenue = 0.0
        var directRevenue = 0.0
        var onlineStoreRevenue = 0.0
        var otherRevenue = 0.0
        var totalRoyaltiesReceived = 0.0
        var publisherCut = 0.0
        var platformFees = 0.0
        
        var publisherSalesCount = 0
        var onlineSalesCount = 0
        var totalPublisherRoyalty = 0.0
        var totalOnlineRoyalty = 0.0
        
        sales.forEach { sale ->
            totalRevenue += sale.totalAmount
            totalRoyaltiesReceived += sale.royaltyAmount
            publisherCut += sale.publisherCut
            platformFees += sale.platformFees
            
            when (sale.type) {
                SaleType.PUBLISHER_SALE -> {
                    publisherRevenue += sale.totalAmount
                    publisherSalesCount++
                    totalPublisherRoyalty += sale.royaltyRate
                }
                SaleType.DIRECT_SALE -> {
                    directRevenue += sale.totalAmount
                }
                SaleType.ONLINE_STORE -> {
                    onlineStoreRevenue += sale.totalAmount
                    onlineSalesCount++
                    totalOnlineRoyalty += sale.royaltyRate
                }
                else -> {
                    otherRevenue += sale.totalAmount
                }
            }
        }
        
        val averagePublisherRoyalty = if (publisherSalesCount > 0) totalPublisherRoyalty / publisherSalesCount else 0.0
        val averageOnlineRoyalty = if (onlineSalesCount > 0) totalOnlineRoyalty / onlineSalesCount else 0.0
        
        return RevenueAnalysis(
            totalRevenue = totalRevenue,
            publisherRevenue = publisherRevenue,
            directRevenue = directRevenue,
            onlineStoreRevenue = onlineStoreRevenue,
            otherRevenue = otherRevenue,
            averagePublisherRoyalty = averagePublisherRoyalty,
            averageOnlineRoyalty = averageOnlineRoyalty,
            totalRoyaltiesReceived = totalRoyaltiesReceived,
            publisherCut = publisherCut,
            platformFees = platformFees
        )
    }
    
    private fun calculateBreakEvenAnalysis(
        totalInvestment: Double,
        totalRevenue: Double,
        totalBooksSold: Int
    ): BreakEvenAnalysis {
        val averageSalePrice = if (totalBooksSold > 0) totalRevenue / totalBooksSold else 0.0
        val breakEvenQuantity = if (averageSalePrice > 0) (totalInvestment / averageSalePrice).toInt() else 0
        val breakEvenRevenue = totalInvestment
        val remainingToBreakEven = maxOf(0, breakEvenQuantity - totalBooksSold)
        val breakEvenProgress = if (breakEvenQuantity > 0) {
            minOf(100.0, (totalBooksSold.toDouble() / breakEvenQuantity) * 100)
        } else 100.0
        
        return BreakEvenAnalysis(
            breakEvenQuantity = breakEvenQuantity,
            breakEvenRevenue = breakEvenRevenue,
            booksSoldToBreakEven = totalBooksSold,
            remainingToBreakEven = remainingToBreakEven,
            breakEvenProgress = breakEvenProgress
        )
    }
    
    private fun calculateFinancialHealth(netProfit: Double, roiPercentage: Double): FinancialHealth {
        return when {
            netProfit > 0 && roiPercentage > 50 -> FinancialHealth.EXCELLENT
            netProfit > 0 && roiPercentage > 0 -> FinancialHealth.GOOD
            netProfit >= 0 -> FinancialHealth.BREAK_EVEN
            netProfit > -1000 -> FinancialHealth.LOSS
            else -> FinancialHealth.CRITICAL
        }
    }
    
    private fun generateAIRecommendations(
        financialHealth: FinancialHealth,
        netProfit: Double,
        roiPercentage: Double,
        breakEvenProgress: Double
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        when (financialHealth) {
            FinancialHealth.EXCELLENT -> {
                recommendations.add("Excellent performance! Consider expanding marketing to reach more readers.")
                recommendations.add("Your ROI is strong - consider investing in additional book projects.")
            }
            FinancialHealth.GOOD -> {
                recommendations.add("Good progress! Focus on increasing sales volume to improve ROI.")
                recommendations.add("Consider targeted marketing campaigns to boost visibility.")
            }
            FinancialHealth.BREAK_EVEN -> {
                recommendations.add("You've reached break-even! Focus on increasing profit margins.")
                recommendations.add("Consider optimizing your pricing strategy for better returns.")
            }
            FinancialHealth.LOSS -> {
                recommendations.add("Focus on reducing costs and increasing sales to reach break-even.")
                recommendations.add("Consider adjusting your marketing strategy to improve book visibility.")
            }
            FinancialHealth.CRITICAL -> {
                recommendations.add("Immediate action needed: review all expenses and focus on essential costs only.")
                recommendations.add("Consider pausing additional investments until sales improve.")
            }
        }
        
        if (breakEvenProgress < 50) {
            recommendations.add("You're less than halfway to break-even. Consider increasing marketing efforts.")
        }
        
        if (roiPercentage < 0) {
            recommendations.add("Negative ROI detected. Review your pricing and cost structure.")
        }
        
        return recommendations
    }
    
    private fun findBestSellingPlatform(sales: List<Sale>): String {
        return sales.groupBy { it.platform }
            .maxByOrNull { (_, sales) -> sales.sumOf { it.quantity } }
            ?.key ?: "N/A"
    }
    
    private fun findWorstSellingPlatform(sales: List<Sale>): String {
        return sales.groupBy { it.platform }
            .minByOrNull { (_, sales) -> sales.sumOf { it.quantity } }
            ?.key ?: "N/A"
    }
    
    private fun calculateDaysToBreakEven(launchDate: Date, hasBreakEven: Boolean): Int? {
        return if (hasBreakEven) {
            val now = Date()
            ((now.time - launchDate.time) / (1000 * 60 * 60 * 24)).toInt()
        } else null
    }
    
    private fun calculateDaysToProfit(launchDate: Date, hasProfit: Boolean): Int? {
        return if (hasProfit) {
            val now = Date()
            ((now.time - launchDate.time) / (1000 * 60 * 60 * 24)).toInt()
        } else null
    }
    
    private fun calculateMonthsSinceLaunch(launchDate: Date): Int {
        val now = Date()
        val diffInMonths = (now.time - launchDate.time) / (1000 * 60 * 60 * 24 * 30)
        return diffInMonths.toInt()
    }
}

// Data classes for internal calculations
private data class InvestmentBreakdown(
    val totalInvestment: Double,
    val publisherFees: Double,
    val illustratorFees: Double,
    val editingCosts: Double,
    val marketingCosts: Double,
    val productionCosts: Double,
    val otherCosts: Double
)

private data class RevenueAnalysis(
    val totalRevenue: Double,
    val publisherRevenue: Double,
    val directRevenue: Double,
    val onlineStoreRevenue: Double,
    val otherRevenue: Double,
    val averagePublisherRoyalty: Double,
    val averageOnlineRoyalty: Double,
    val totalRoyaltiesReceived: Double,
    val publisherCut: Double,
    val platformFees: Double
)
