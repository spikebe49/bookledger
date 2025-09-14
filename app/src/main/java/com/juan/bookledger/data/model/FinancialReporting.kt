package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

data class MonthlyFinancialReport(
    val bookId: Long,
    val month: Int,
    val year: Int,
    val totalRevenue: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val profitMargin: Double,
    val roi: Double,
    val booksSold: Int,
    val averageSalePrice: Double,
    val totalRoyalties: Double,
    val totalMarketingSpend: Double,
    val marketingROI: Double,
    val topRevenueSource: String,
    val topExpenseCategory: String,
    val financialHealth: FinancialHealth,
    val recommendations: List<String>
)

data class QuarterlyFinancialReport(
    val bookId: Long,
    val quarter: Int,
    val year: Int,
    val totalRevenue: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val profitMargin: Double,
    val roi: Double,
    val booksSold: Int,
    val averageSalePrice: Double,
    val totalRoyalties: Double,
    val totalMarketingSpend: Double,
    val marketingROI: Double,
    val quarterlyGrowth: Double, // Growth compared to previous quarter
    val seasonalTrends: Map<String, Double>, // Performance by month
    val financialHealth: FinancialHealth,
    val recommendations: List<String>
)

data class AnnualFinancialReport(
    val bookId: Long,
    val year: Int,
    val totalRevenue: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val profitMargin: Double,
    val roi: Double,
    val booksSold: Int,
    val averageSalePrice: Double,
    val totalRoyalties: Double,
    val totalMarketingSpend: Double,
    val marketingROI: Double,
    val yearOverYearGrowth: Double, // Growth compared to previous year
    val quarterlyBreakdown: Map<Int, Double>, // Performance by quarter
    val monthlyBreakdown: Map<Int, Double>, // Performance by month
    val seasonalAnalysis: Map<String, Double>, // Performance by season
    val financialHealth: FinancialHealth,
    val taxImplications: TaxSummary,
    val recommendations: List<String>
)

data class TaxSummary(
    val totalIncome: Double,
    val totalDeductibleExpenses: Double,
    val netBusinessIncome: Double,
    val gstHstCollected: Double,
    val gstHstPaid: Double,
    val netGstHst: Double,
    val estimatedFederalTax: Double,
    val estimatedProvincialTax: Double,
    val totalEstimatedTax: Double,
    val cppContributions: Double,
    val eiPremiums: Double,
    val totalPayrollTaxes: Double,
    val taxSavings: Double, // Money saved through deductions
    val taxDeadlines: List<TaxDeadline>,
    val recommendations: List<String>
)

data class TaxDeadline(
    val deadlineType: TaxDeadlineType,
    val dueDate: Date,
    val description: String,
    val amount: Double? = null,
    val isPaid: Boolean = false,
    val paymentDate: Date? = null
)

enum class TaxDeadlineType {
    GST_HST_RETURN,            // GST/HST return
    INCOME_TAX_RETURN,         // Income tax return
    PAYROLL_DEDUCTIONS,        // Payroll deductions
    INSTALMENT_PAYMENTS,       // Instalment payments
    CORPORATE_TAX,             // Corporate tax return
    OTHER                      // Other tax deadlines
}

data class ProfitLossStatement(
    val bookId: Long,
    val period: String, // "2024-01" for January 2024
    val revenue: RevenueBreakdown,
    val expenses: ExpenseBreakdown,
    val grossProfit: Double,
    val operatingExpenses: Double,
    val operatingProfit: Double,
    val netProfit: Double,
    val profitMargin: Double,
    val ebitda: Double, // Earnings Before Interest, Taxes, Depreciation, Amortization
    val recommendations: List<String>
)

data class RevenueBreakdown(
    val bookSales: Double,
    val royalties: Double,
    val speakingFees: Double,
    val teachingIncome: Double,
    val consultingFees: Double,
    val otherIncome: Double,
    val totalRevenue: Double
)

data class ExpenseBreakdown(
    val publishingCosts: Double,
    val marketingCosts: Double,
    val professionalServices: Double,
    val officeExpenses: Double,
    val travelExpenses: Double,
    val equipmentDepreciation: Double,
    val otherExpenses: Double,
    val totalExpenses: Double
)

data class CashFlowStatement(
    val bookId: Long,
    val period: String,
    val operatingActivities: OperatingCashFlow,
    val investingActivities: InvestingCashFlow,
    val financingActivities: FinancingCashFlow,
    val netCashFlow: Double,
    val beginningCash: Double,
    val endingCash: Double,
    val recommendations: List<String>
)

data class OperatingCashFlow(
    val netIncome: Double,
    val depreciation: Double,
    val accountsReceivable: Double,
    val accountsPayable: Double,
    val inventory: Double,
    val otherOperating: Double,
    val totalOperating: Double
)

data class InvestingCashFlow(
    val equipmentPurchases: Double,
    val equipmentSales: Double,
    val otherInvesting: Double,
    val totalInvesting: Double
)

data class FinancingCashFlow(
    val loansReceived: Double,
    val loansRepaid: Double,
    val ownerDraws: Double,
    val ownerContributions: Double,
    val otherFinancing: Double,
    val totalFinancing: Double
)

data class FinancialRatioAnalysis(
    val bookId: Long,
    val period: String,
    val profitabilityRatios: ProfitabilityRatios,
    val liquidityRatios: LiquidityRatios,
    val efficiencyRatios: EfficiencyRatios,
    val leverageRatios: LeverageRatios,
    val marketRatios: MarketRatios,
    val recommendations: List<String>
)

data class ProfitabilityRatios(
    val grossProfitMargin: Double,
    val operatingProfitMargin: Double,
    val netProfitMargin: Double,
    val returnOnInvestment: Double,
    val returnOnEquity: Double,
    val returnOnAssets: Double
)

data class LiquidityRatios(
    val currentRatio: Double,
    val quickRatio: Double,
    val cashRatio: Double,
    val operatingCashFlowRatio: Double
)

data class EfficiencyRatios(
    val inventoryTurnover: Double,
    val receivablesTurnover: Double,
    val payablesTurnover: Double,
    val assetTurnover: Double
)

data class LeverageRatios(
    val debtToEquity: Double,
    val debtToAssets: Double,
    val interestCoverage: Double,
    val debtServiceCoverage: Double
)

data class MarketRatios(
    val priceToEarnings: Double,
    val priceToSales: Double,
    val priceToBook: Double,
    val dividendYield: Double
)

data class FinancialForecast(
    val bookId: Long,
    val forecastPeriod: Int, // Months ahead
    val projectedRevenue: Double,
    val projectedExpenses: Double,
    val projectedProfit: Double,
    val projectedROI: Double,
    val projectedBooksSold: Int,
    val confidenceLevel: Double, // 0-1 confidence in forecast
    val assumptions: List<String>,
    val risks: List<String>,
    val recommendations: List<String>
)

@Entity(tableName = "financial_alerts")
data class FinancialAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val alertType: FinancialAlertType,
    val severity: AlertSeverity,
    val message: String,
    val value: Double? = null,
    val threshold: Double? = null,
    val alertDate: Date = Date(),
    val isRead: Boolean = false,
    val isResolved: Boolean = false,
    val resolutionDate: Date? = null,
    val resolutionNotes: String? = null
)

enum class FinancialAlertType {
    LOW_CASH_FLOW,             // Cash flow below threshold
    HIGH_EXPENSES,             // Expenses above threshold
    LOW_REVENUE,               // Revenue below threshold
    NEGATIVE_PROFIT,           // Negative profit
    LOW_ROI,                   // ROI below threshold
    PAYMENT_OVERDUE,           // Payment overdue
    CONTRACT_EXPIRY,           // Contract expiring
    TAX_DEADLINE,              // Tax deadline approaching
    BUDGET_EXCEEDED,           // Budget exceeded
    GOAL_NOT_MET,              // Financial goal not met
    OTHER                      // Other alerts
}

enum class AlertSeverity {
    LOW,                       // Low severity
    MEDIUM,                    // Medium severity
    HIGH,                      // High severity
    CRITICAL                   // Critical severity
}
