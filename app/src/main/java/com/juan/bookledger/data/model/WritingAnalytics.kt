package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "writing_analytics")
data class WritingAnalytics(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,
    val manuscriptId: Long,
    val wordCount: Int,
    val writingTime: Long, // in minutes
    val productivity: Int, // 1-10 scale
    val mood: WritingMood? = null,
    val location: String? = null,
    val timeOfDay: Int, // hour of day (0-23)
    val dayOfWeek: Int, // 1-7 (Monday-Sunday)
    val weather: String? = null,
    val distractions: String? = null,
    val goals: String? = null,
    val achievements: String? = null,
    val notes: String? = null,
    val aiInsights: String? = null
)

data class WritingGoal(
    val id: Long = 0,
    val manuscriptId: Long,
    val type: GoalType,
    val target: Int,
    val current: Int = 0,
    val deadline: Date,
    val status: GoalStatus = GoalStatus.ACTIVE,
    val description: String? = null,
    val reward: String? = null,
    val createdAt: Date = Date()
)

enum class GoalType {
    DAILY_WORD_COUNT,
    WEEKLY_WORD_COUNT,
    MONTHLY_WORD_COUNT,
    TOTAL_WORD_COUNT,
    WRITING_STREAK,
    WRITING_TIME,
    CHAPTER_COUNT,
    PAGE_COUNT,
    OTHER
}

enum class GoalStatus {
    ACTIVE,
    COMPLETED,
    FAILED,
    PAUSED,
    CANCELLED
}

data class WritingStreak(
    val id: Long = 0,
    val manuscriptId: Long,
    val startDate: Date,
    val endDate: Date? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDays: Int = 0,
    val isActive: Boolean = true
)
