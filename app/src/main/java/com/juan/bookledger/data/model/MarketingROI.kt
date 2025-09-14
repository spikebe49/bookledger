package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

// MarketingCampaign is already defined in MarketingCampaign.kt

enum class MarketingType {
    SOCIAL_MEDIA,              // Social media advertising
    GOOGLE_ADS,                // Google Ads
    FACEBOOK_ADS,              // Facebook advertising
    AMAZON_ADS,                // Amazon advertising
    BOOK_TOUR,                 // Book tour events
    BLOG_TOUR,                 // Blog tour
    INFLUENCER,                // Influencer marketing
    EMAIL_MARKETING,           // Email campaigns
    PRESS_RELEASE,             // Press releases
    RADIO_TV,                  // Radio/TV appearances
    PODCAST,                   // Podcast appearances
    CONFERENCE,                // Conference appearances
    BOOKSTORE_EVENTS,          // Bookstore events
    LIBRARY_EVENTS,            // Library events
    SCHOOL_VISITS,             // School visits
    CONTEST_GIVEAWAY,          // Contests and giveaways
    PAID_REVIEWS,              // Paid book reviews
    NEWSLETTER,                // Newsletter advertising
    PRINT_ADS,                 // Print advertising
    OTHER                       // Other marketing types
}

enum class MarketingPlatform {
    FACEBOOK,                  // Facebook
    INSTAGRAM,                 // Instagram
    TWITTER,                   // Twitter/X
    TIKTOK,                    // TikTok
    LINKEDIN,                  // LinkedIn
    YOUTUBE,                   // YouTube
    GOOGLE,                    // Google Ads
    AMAZON,                    // Amazon Ads
    BARNES_NOBLE,              // Barnes & Noble
    KOBO,                      // Kobo
    APPLE,                     // Apple Books
    GOODREADS,                 // Goodreads
    BOOKBUB,                   // BookBub
    BOOKGORILLA,               // BookGorilla
    EREADER_NEWS,              // Ereader News Today
    FUSSY_LIBRARIAN,           // The Fussy Librarian
    READER_IQ,                 // ReaderIQ
    BOOKSEND,                  // BookSend
    PERSONAL_WEBSITE,          // Personal website
    EMAIL,                     // Email marketing
    PODCAST,                   // Podcast platforms
    RADIO,                     // Radio
    TV,                        // Television
    PRINT,                     // Print media
    OTHER                       // Other platforms
}

// CampaignStatus is already defined in MarketingCampaign.kt

data class MarketingROIAnalysis(
    val campaignId: Long,
    val campaignName: String,
    val totalSpent: Double,
    val totalRevenue: Double,
    val netProfit: Double,
    val roi: Double, // Return on investment percentage
    val costPerAcquisition: Double, // Cost to acquire each customer
    val costPerSale: Double, // Cost per book sold
    val revenuePerDollar: Double, // Revenue generated per dollar spent
    val breakEvenPoint: Double, // Number of sales needed to break even
    val conversionRate: Double, // Percentage of clicks that resulted in sales
    val clickThroughRate: Double, // Percentage of impressions that resulted in clicks
    val engagementRate: Double, // Percentage of reach that resulted in engagement
    val lifetimeValue: Double, // Average lifetime value of acquired customers
    val attributionWindow: Int, // Days to attribute sales to campaign
    val effectiveness: CampaignEffectiveness,
    val recommendations: List<String>
)

enum class CampaignEffectiveness {
    EXCELLENT,                 // ROI > 300%
    VERY_GOOD,                 // ROI 200-300%
    GOOD,                      // ROI 100-200%
    BREAK_EVEN,                // ROI 80-100%
    POOR,                      // ROI 50-80%
    VERY_POOR,                 // ROI < 50%
    LOSING_MONEY               // ROI < 0%
}

@Entity(tableName = "marketing_attributions")
data class MarketingAttribution(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val campaignId: Long,
    val saleId: Long,
    val attributionType: AttributionType,
    val attributionValue: Double, // Value attributed to this campaign
    val attributionDate: Date,
    val confidence: Double // Confidence level (0-1) of attribution
)

enum class AttributionType {
    DIRECT,                    // Direct attribution
    ASSISTED,                  // Assisted attribution
    VIEW_THROUGH,              // View-through attribution
    CLICK_THROUGH,             // Click-through attribution
    LAST_CLICK,                // Last-click attribution
    FIRST_CLICK,               // First-click attribution
    LINEAR,                    // Linear attribution
    TIME_DECAY,                // Time decay attribution
    POSITION_BASED             // Position-based attribution
}

@Entity(tableName = "marketing_budgets")
data class MarketingBudget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val totalBudget: Double,
    val allocatedBudget: Double,
    val spentBudget: Double,
    val remainingBudget: Double,
    val monthlyBudget: Double,
    val quarterlyBudget: Double,
    val yearlyBudget: Double,
    val budgetPeriod: BudgetPeriod,
    val startDate: Date,
    val endDate: Date,
    val isActive: Boolean = true
)

enum class BudgetPeriod {
    MONTHLY,                   // Monthly budget
    QUARTERLY,                 // Quarterly budget
    YEARLY,                    // Yearly budget
    CAMPAIGN,                  // Campaign-specific budget
    CUSTOM                     // Custom period
}

data class MarketingInsights(
    val bookId: Long,
    val totalMarketingSpend: Double,
    val totalMarketingRevenue: Double,
    val overallROI: Double,
    val bestPerformingCampaign: String,
    val worstPerformingCampaign: String,
    val bestPerformingPlatform: String,
    val worstPerformingPlatform: String,
    val averageCostPerAcquisition: Double,
    val averageConversionRate: Double,
    val totalReach: Int,
    val totalImpressions: Int,
    val totalClicks: Int,
    val totalEngagements: Int,
    val totalSalesAttributed: Int,
    val seasonalTrends: Map<String, Double>, // Month -> Performance
    val platformPerformance: Map<String, Double>, // Platform -> ROI
    val campaignTypePerformance: Map<String, Double>, // Type -> ROI
    val recommendations: List<String>,
    val nextBestActions: List<String>
)
