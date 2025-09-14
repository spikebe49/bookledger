package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"]), Index(value = ["bookId"])]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: ExpenseCategory, // Author-specific expense categories
    val description: String,
    val amount: Double,
    val date: Date,
    val vendor: String? = null, // Who was paid (publisher, illustrator, etc.)
    val isRecurring: Boolean = false, // Recurring expense
    val paymentMethod: String? = null, // Payment method used
    val receiptNumber: String? = null, // Receipt/invoice number
    val notes: String? = null,
    val categoryId: Long = 0, // Keep for backward compatibility
    val bookId: Long = 0 // Foreign key to Book
)

enum class ExpenseCategory {
    // Publishing Costs
    PUBLISHER_FEES,        // Publisher fees and services
    ILLUSTRATOR_FEES,      // Illustrator/cover designer fees
    EDITING_SERVICES,      // Professional editing
    PROOFREADING,          // Proofreading services
    FORMATTING,            // Book formatting
    ISBN_COSTS,            // ISBN registration
    COPYRIGHT,             // Copyright registration
    
    // Production Costs
    PRINTING_COSTS,        // Physical book printing
    SHIPPING_COSTS,        // Shipping and handling
    INVENTORY,             // Book inventory purchases
    STORAGE,               // Storage costs
    
    // Marketing & Promotion
    MARKETING,             // Marketing campaigns
    ADVERTISING,           // Paid advertising
    BOOK_TOURS,            // Book tour expenses
    EVENTS,                // Book signing events
    PROMOTIONAL_MATERIALS, // Flyers, bookmarks, etc.
    WEBSITE,               // Website development/maintenance
    
    // Professional Services
    LEGAL_FEES,            // Legal services
    ACCOUNTING,            // Accounting services
    CONSULTING,            // Business consulting
    TRANSLATION,           // Translation services
    
    // Technology & Tools
    SOFTWARE,              // Writing/design software
    HARDWARE,              // Computer, tablet, etc.
    SUBSCRIPTIONS,         // Software subscriptions
    
    // Travel & Events
    TRAVEL,                // Travel for book events
    CONFERENCES,           // Writing conferences
    WORKSHOPS,             // Writing workshops
    
    // Other
    MISCELLANEOUS,         // Other expenses
    OTHER                  // Custom category
}
