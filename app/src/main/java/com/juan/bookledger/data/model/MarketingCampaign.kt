package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "marketing_campaigns",
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
data class MarketingCampaign(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long = 0, // Foreign key to Book
    val name: String,
    val type: CampaignType,
    val status: CampaignStatus = CampaignStatus.PLANNING,
    val startDate: Date,
    val endDate: Date? = null,
    val budget: Double = 0.0,
    val spent: Double = 0.0,
    val targetAudience: String? = null,
    val goals: String? = null,
    val results: String? = null,
    val roi: Double = 0.0,
    val notes: String? = null,
    val aiSuggestions: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class CampaignType {
    BOOK_LAUNCH,
    SOCIAL_MEDIA,
    BLOG_TOUR,
    PAID_ADS,
    INFLUENCER_OUTREACH,
    EVENT_MARKETING,
    EMAIL_MARKETING,
    PRESS_RELEASE,
    CONTEST_GIVEAWAY,
    OTHER
}

enum class CampaignStatus {
    PLANNING,
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED
}

data class MarketingEvent(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val type: EventType,
    val date: Date,
    val location: String? = null,
    val cost: Double = 0.0,
    val attendees: Int? = null,
    val notes: String? = null,
    val followUpRequired: Boolean = false
)

enum class EventType {
    BOOK_SIGNING,
    READING,
    CONFERENCE,
    WORKSHOP,
    INTERVIEW,
    PODCAST,
    LIVE_STREAM,
    NETWORKING,
    OTHER
}
