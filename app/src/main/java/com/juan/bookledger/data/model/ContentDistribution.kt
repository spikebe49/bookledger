package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "content_distributions",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bookId"])]
)
data class ContentDistribution(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long = 0, // Foreign key to Book
    val platform: DistributionPlatform,
    val format: ContentFormat,
    val status: DistributionStatus = DistributionStatus.PLANNING,
    val launchDate: Date? = null,
    val price: Double = 0.0,
    val royaltyRate: Double = 0.0,
    val isbn: String? = null,
    val asin: String? = null,
    val url: String? = null,
    val sales: Int = 0,
    val revenue: Double = 0.0,
    val reviews: Int = 0,
    val rating: Double = 0.0,
    val notes: String? = null,
    val aiStrategy: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class DistributionPlatform {
    AMAZON_KDP,
    INGRAMSPARK,
    DRAFT2DIGITAL,
    SMASHWORDS,
    APPLE_BOOKS,
    GOOGLE_PLAY,
    BARNES_NOBLE,
    KOBO,
    AUDIBLE,
    FINDWAY_VOICES,
    SPOTIFY,
    YOUTUBE,
    PERSONAL_WEBSITE,
    OTHER
}

enum class ContentFormat {
    EBOOK_PDF,
    EBOOK_EPUB,
    EBOOK_MOBI,
    PAPERBACK,
    HARDCOVER,
    AUDIOBOOK,
    PODCAST,
    VIDEO,
    INTERACTIVE,
    OTHER
}

enum class DistributionStatus {
    PLANNING,
    IN_PRODUCTION,
    SUBMITTED,
    LIVE,
    PAUSED,
    DISCONTINUED
}

data class PlatformPerformance(
    val id: Long = 0,
    val distributionId: Long,
    val date: Date,
    val sales: Int,
    val revenue: Double,
    val downloads: Int = 0,
    val pageReads: Int = 0,
    val reviews: Int = 0,
    val rating: Double = 0.0,
    val impressions: Int = 0,
    val clicks: Int = 0,
    val conversionRate: Double = 0.0
)

data class PricingStrategy(
    val id: Long = 0,
    val bookId: Long,
    val platform: DistributionPlatform,
    val format: ContentFormat,
    val suggestedPrice: Double,
    val currentPrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val competitorAnalysis: String? = null,
    val aiRecommendations: String? = null,
    val lastUpdated: Date = Date()
)
