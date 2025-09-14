package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "book_series")
data class BookSeries(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val genre: String? = null,
    val targetAudience: String? = null,
    val totalBooksPlanned: Int? = null,
    val booksPublished: Int = 0,
    val seriesStatus: SeriesStatus = SeriesStatus.ACTIVE,
    val launchDate: Date? = null,
    val completionDate: Date? = null,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class SeriesStatus {
    PLANNING,       // In planning phase
    ACTIVE,         // Currently being written/published
    COMPLETED,      // Series finished
    ON_HOLD,        // Temporarily paused
    CANCELLED       // Series cancelled
}

@Entity(tableName = "series_books")
data class SeriesBook(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val seriesId: Long,
    val bookId: Long,
    val bookNumber: Int, // Book 1, 2, 3, etc.
    val isMainBook: Boolean = false, // Main book in series
    val isPrequel: Boolean = false,
    val isSequel: Boolean = false,
    val isSpinOff: Boolean = false,
    val addedDate: Date = Date()
)

data class SeriesFinancialSummary(
    val seriesId: Long,
    val seriesName: String,
    val totalInvestment: Double,
    val totalRevenue: Double,
    val netProfit: Double,
    val totalBooksSold: Int,
    val averageBookProfit: Double,
    val bestPerformingBook: String,
    val worstPerformingBook: String,
    val seriesROI: Double,
    val isSeriesProfitable: Boolean,
    val breakEvenBook: Int?, // Which book number broke even
    val totalRoyalties: Double,
    val totalMarketingCosts: Double,
    val averageRoyaltyRate: Double,
    val crossBookSales: Int, // Sales attributed to series effect
    val seriesGrowthRate: Double, // Month-over-month growth
    val completionPercentage: Double, // How much of series is complete
    val estimatedCompletionValue: Double // Projected value when complete
)

data class SeriesAnalytics(
    val seriesId: Long,
    val totalBooks: Int,
    val publishedBooks: Int,
    val totalWordCount: Long,
    val averageWordCount: Long,
    val totalPages: Int,
    val averagePages: Int,
    val totalWritingTime: Long, // in hours
    val averageWritingTime: Long,
    val seriesStartDate: Date,
    val lastBookDate: Date?,
    val averageTimeBetweenBooks: Long, // in days
    val readerRetentionRate: Double, // % of readers who buy next book
    val seriesMomentum: Double, // Sales velocity trend
    val crossPromotionEffectiveness: Double // How well books promote each other
)
