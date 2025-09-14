package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "collaborators",
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
data class Collaborator(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long = 0, // Foreign key to Book
    val name: String,
    val role: CollaboratorRole,
    val email: String? = null,
    val phone: String? = null,
    val company: String? = null,
    val hourlyRate: Double? = null,
    val totalCost: Double = 0.0,
    val status: CollaborationStatus = CollaborationStatus.ACTIVE,
    val startDate: Date,
    val endDate: Date? = null,
    val notes: String? = null,
    val communicationHistory: String? = null,
    val rating: Int? = null, // 1-5 stars
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class CollaboratorRole {
    EDITOR,
    PROOFREADER,
    COVER_DESIGNER,
    ILLUSTRATOR,
    TRANSLATOR,
    CO_AUTHOR,
    BETA_READER,
    AGENT,
    PUBLISHER,
    MARKETING_SPECIALIST,
    PUBLICIST,
    AUDIO_NARRATOR,
    FORMATTER,
    OTHER
}

enum class CollaborationStatus {
    ACTIVE,
    COMPLETED,
    ON_HOLD,
    CANCELLED,
    PENDING
}

@Entity(tableName = "collaboration_tasks")
data class CollaborationTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val collaboratorId: Long,
    val bookId: Long,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Date? = null,
    val completedDate: Date? = null,
    val estimatedHours: Double? = null,
    val actualHours: Double? = null,
    val cost: Double = 0.0,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    COMPLETED,
    CANCELLED,
    ON_HOLD
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

data class ProjectMilestone(
    val id: Long = 0,
    val bookId: Long,
    val title: String,
    val description: String? = null,
    val dueDate: Date,
    val completedDate: Date? = null,
    val status: MilestoneStatus = MilestoneStatus.PENDING,
    val dependencies: String? = null, // JSON string of milestone IDs
    val notes: String? = null
)

enum class MilestoneStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE,
    CANCELLED
}
