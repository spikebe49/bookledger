package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "time_entries",
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
data class TimeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long = 0,
    val activityType: ActivityType,
    val description: String,
    val startTime: Date,
    val endTime: Date? = null,
    val duration: Long = 0, // Duration in minutes
    val hourlyRate: Double = 0.0, // Author's hourly rate
    val value: Double = 0.0, // Value of time spent (duration * hourlyRate)
    val productivity: Int = 5, // Productivity rating 1-10
    val mood: WritingMood? = null,
    val location: String? = null,
    val distractions: String? = null,
    val goals: String? = null,
    val achievements: String? = null,
    val notes: String? = null,
    val isBillable: Boolean = false, // Whether this time is billable
    val clientId: Long? = null, // If working for a client
    val projectId: Long? = null, // If part of a larger project
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class ActivityType {
    WRITING,                   // Writing/editing
    RESEARCH,                  // Research activities
    PLOTTING,                  // Plotting and outlining
    CHARACTER_DEVELOPMENT,     // Character development
    WORLD_BUILDING,            // World building
    REVISING,                  // Revising and editing
    PROOFREADING,              // Proofreading
    MARKETING,                 // Marketing activities
    SOCIAL_MEDIA,              // Social media management
    WEBSITE_MAINTENANCE,       // Website maintenance
    EMAIL_MARKETING,           // Email marketing
    BLOG_WRITING,              // Blog writing
    NEWSLETTER,                // Newsletter creation
    BOOK_TOUR,                 // Book tour activities
    INTERVIEWS,                // Media interviews
    PODCASTS,                  // Podcast appearances
    CONFERENCES,               // Conference attendance
    WORKSHOPS,                 // Workshop attendance
    NETWORKING,                // Networking events
    ADMINISTRATIVE,            // Administrative tasks
    ACCOUNTING,                // Accounting and finances
    LEGAL,                     // Legal matters
    CONTRACT_NEGOTIATION,      // Contract negotiations
    PUBLISHER_COMMUNICATION,   // Communication with publisher
    READER_COMMUNICATION,      // Communication with readers
    OTHER                      // Other activities
}

// WritingMood is already defined in Manuscript.kt

data class TimeAnalytics(
    val bookId: Long,
    val totalTimeSpent: Long, // Total time in minutes
    val totalValue: Double, // Total value of time
    val averageHourlyRate: Double,
    val mostProductiveTime: String, // Best time of day
    val mostProductiveDay: String, // Best day of week
    val averageProductivity: Double,
    val totalSessions: Int,
    val averageSessionLength: Double,
    val longestSession: Long,
    val shortestSession: Long,
    val productivityTrend: Double, // Improving or declining
    val moodAnalysis: Map<WritingMood, Long>, // Time spent in each mood
    val activityBreakdown: Map<ActivityType, Long>, // Time spent on each activity
    val locationAnalysis: Map<String, Long>, // Time spent in each location
    val distractionAnalysis: Map<String, Long>, // Common distractions
    val recommendations: List<String>
)

@Entity(tableName = "productivity_goals")
data class ProductivityGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val goalType: GoalType,
    val target: Double, // Target value
    val current: Double = 0.0, // Current progress
    val unit: String, // hours, words, pages, etc.
    val deadline: Date,
    val status: GoalStatus = GoalStatus.ACTIVE,
    val description: String? = null,
    val reward: String? = null,
    val createdAt: Date = Date()
)

// GoalType and GoalStatus are already defined in WritingAnalytics.kt

data class TimeValueAnalysis(
    val bookId: Long,
    val totalTimeValue: Double, // Total value of all time spent
    val timeInvestment: Double, // Time as investment
    val timeROI: Double, // Return on time investment
    val valuePerHour: Double, // Value generated per hour
    val mostValuableActivity: ActivityType,
    val leastValuableActivity: ActivityType,
    val timeToBreakEven: Long, // Hours needed to break even
    val timeToProfit: Long, // Hours needed to start profiting
    val hourlyRateTarget: Double, // Target hourly rate
    val currentHourlyRate: Double, // Current effective hourly rate
    val timeEfficiency: Double, // How efficiently time is used
    val recommendations: List<String>
)

// WritingStreak is already defined in WritingAnalytics.kt

data class ProductivityInsights(
    val bookId: Long,
    val peakProductivityHours: List<Int>, // Hours when most productive
    val peakProductivityDays: List<String>, // Days when most productive
    val averageFocusTime: Double, // Average time before distraction
    val commonDistractions: List<String>,
    val productivityFactors: Map<String, Double>, // Factors affecting productivity
    val energyLevels: Map<String, Double>, // Energy levels by time of day
    val moodImpact: Map<WritingMood, Double>, // How mood affects productivity
    val locationImpact: Map<String, Double>, // How location affects productivity
    val seasonalTrends: Map<String, Double>, // Productivity by season
    val recommendations: List<String>
)
