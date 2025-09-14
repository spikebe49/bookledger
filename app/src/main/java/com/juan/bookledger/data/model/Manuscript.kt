package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "manuscripts",
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
data class Manuscript(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long = 0, // Foreign key to Book
    val title: String,
    val genre: String,
    val targetWordCount: Int,
    val currentWordCount: Int = 0,
    val currentDraft: Int = 1,
    val status: ManuscriptStatus = ManuscriptStatus.PLANNING,
    val startDate: Date,
    val targetCompletionDate: Date? = null,
    val actualCompletionDate: Date? = null,
    val lastWritingDate: Date? = null,
    val writingStreak: Int = 0,
    val totalWritingTime: Long = 0, // in minutes
    val notes: String? = null,
    val aiInsights: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class ManuscriptStatus {
    PLANNING,
    FIRST_DRAFT,
    REVISION,
    EDITING,
    PROOFREADING,
    COMPLETED,
    PUBLISHED,
    ON_HOLD
}

data class WritingSession(
    val id: Long = 0,
    val manuscriptId: Long,
    val date: Date,
    val wordCount: Int,
    val duration: Long, // in minutes
    val notes: String? = null,
    val mood: WritingMood? = null,
    val productivity: Int = 5 // 1-10 scale
)

enum class WritingMood {
    EXCITED,
    FOCUSED,
    STRUGGLING,
    INSPIRED,
    TIRED,
    FRUSTRATED,
    CONFIDENT
}
