package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "sales",
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
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: SaleType, // Sale type (Publisher, Direct, Online Store, etc.)
    val platform: String, // Platform (Amazon, Barnes & Noble, Publisher, etc.)
    val bookTitle: String,
    val quantity: Int,
    val unitPrice: Double, // Retail price
    val totalAmount: Double, // Total revenue before royalties
    val royaltyRate: Double = 0.0, // Royalty percentage (e.g., 0.10 for 10%)
    val royaltyAmount: Double = 0.0, // Actual royalty received
    val authorEarnings: Double = 0.0, // Net amount author receives
    val publisherCut: Double = 0.0, // Amount publisher keeps
    val platformFees: Double = 0.0, // Platform fees (Amazon, etc.)
    val date: Date,
    val donationAmount: Double = 0.0,
    val isGiveaway: Boolean = false,
    val notes: String? = null,
    val bookId: Long = 0 // Foreign key to Book
)

enum class SaleType {
    PUBLISHER_SALE,      // Sold through publisher
    DIRECT_SALE,         // Direct sales (website, events)
    ONLINE_STORE,        // Amazon, Barnes & Noble, etc.
    BOOKSTORE,           // Physical bookstore
    LIBRARY,             // Library sales
    BULK_SALE,           // Bulk orders
    GIVEAWAY,            // Free copies
    OTHER
}
