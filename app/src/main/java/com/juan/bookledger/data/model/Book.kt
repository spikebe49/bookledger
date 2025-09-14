package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val launchDate: Date,
    val description: String? = null,
    
    // Author Information
    val authorName: String? = null,
    val authorEmail: String? = null,
    val authorPhone: String? = null,
    val authorWebsite: String? = null,
    
    // Publisher Information
    val publisherName: String? = null,
    val publisherEmail: String? = null,
    val publisherPhone: String? = null,
    val publisherWebsite: String? = null,
    val publisherAddress: String? = null,
    
    // Illustrator Information
    val illustratorName: String? = null,
    val illustratorEmail: String? = null,
    val illustratorPhone: String? = null,
    val illustratorWebsite: String? = null,
    
    // Additional Book Details
    val isbn: String? = null,
    val genre: String? = null,
    val targetAudience: String? = null,
    val pageCount: Int? = null,
    val language: String? = null,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
