package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "inventory_items",
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
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long = 0,
    val itemType: InventoryType,
    val format: BookFormat,
    val quantityOnHand: Int = 0,
    val quantityReserved: Int = 0, // Reserved for orders
    val quantityAvailable: Int = 0, // Available for sale
    val reorderPoint: Int = 0, // When to reorder
    val reorderQuantity: Int = 0, // How many to reorder
    val unitCost: Double = 0.0, // Cost per unit
    val unitPrice: Double = 0.0, // Selling price per unit
    val totalValue: Double = 0.0, // Total inventory value
    val supplier: String? = null,
    val supplierContact: String? = null,
    val lastRestocked: Date? = null,
    val nextRestockDate: Date? = null,
    val storageLocation: String? = null,
    val condition: ItemCondition = ItemCondition.NEW,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class InventoryType {
    PHYSICAL_BOOKS,            // Physical book inventory
    EBOOKS,                    // Digital book inventory
    AUDIOBOOKS,                // Audiobook inventory
    MERCHANDISE,               // Book-related merchandise
    PROMOTIONAL_ITEMS,         // Promotional materials
    SUPPLIES,                  // Office supplies
    EQUIPMENT,                 // Equipment inventory
    OTHER                      // Other inventory types
}

enum class BookFormat {
    HARDCOVER,                 // Hardcover books
    PAPERBACK,                 // Paperback books
    MASS_MARKET,               // Mass market paperback
    LARGE_PRINT,               // Large print books
    EBOOK_PDF,                 // PDF ebooks
    EBOOK_EPUB,                // EPUB ebooks
    EBOOK_MOBI,                // MOBI ebooks
    AUDIOBOOK_CD,              // CD audiobooks
    AUDIOBOOK_DOWNLOAD,        // Downloadable audiobooks
    AUDIOBOOK_STREAMING,       // Streaming audiobooks
    OTHER                      // Other formats
}

enum class ItemCondition {
    NEW,                       // New condition
    LIKE_NEW,                  // Like new condition
    VERY_GOOD,                 // Very good condition
    GOOD,                      // Good condition
    FAIR,                      // Fair condition
    POOR,                      // Poor condition
    DAMAGED,                   // Damaged
    DEFECTIVE,                 // Defective
    OTHER                      // Other condition
}

@Entity(tableName = "inventory_transactions")
data class InventoryTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val inventoryItemId: Long,
    val transactionType: TransactionType,
    val quantity: Int,
    val unitCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val transactionDate: Date,
    val reference: String? = null, // PO number, invoice number, etc.
    val supplier: String? = null,
    val customer: String? = null,
    val reason: String? = null, // Reason for adjustment
    val notes: String? = null,
    val createdBy: String? = null
)

enum class TransactionType {
    PURCHASE,                  // Purchase from supplier
    SALE,                      // Sale to customer
    RETURN,                    // Return from customer
    ADJUSTMENT,                // Inventory adjustment
    TRANSFER,                  // Transfer between locations
    DAMAGE,                    // Damaged goods write-off
    THEFT,                     // Theft/loss
    GIVEAWAY,                  // Free giveaway
    SAMPLE,                    // Sample distribution
    OTHER                      // Other transaction types
}

data class InventorySummary(
    val bookId: Long,
    val bookTitle: String,
    val totalItems: Int,
    val totalValue: Double,
    val totalCost: Double,
    val totalProfit: Double,
    val profitMargin: Double,
    val turnoverRate: Double, // How quickly inventory turns over
    val daysInInventory: Double, // Average days items stay in inventory
    val reorderAlerts: Int, // Number of items below reorder point
    val slowMovingItems: Int, // Items that haven't moved in 90+ days
    val fastMovingItems: Int, // Items that move quickly
    val inventoryHealth: InventoryHealth,
    val recommendations: List<String>
)

enum class InventoryHealth {
    EXCELLENT,                 // Optimal inventory levels
    GOOD,                      // Good inventory management
    FAIR,                      // Some issues to address
    POOR,                      // Significant inventory problems
    CRITICAL                   // Critical inventory issues
}

data class PrintOnDemandInfo(
    val bookId: Long,
    val platform: PODPlatform,
    val setupCost: Double = 0.0,
    val printingCost: Double = 0.0, // Cost per book
    val shippingCost: Double = 0.0, // Cost per shipment
    val platformFee: Double = 0.0, // Platform fee per sale
    val royaltyRate: Double = 0.0, // Royalty rate
    val minimumOrder: Int = 1,
    val maximumOrder: Int = 1000,
    val printTime: Int = 0, // Days to print
    val shippingTime: Int = 0, // Days to ship
    val quality: PODQuality = PODQuality.STANDARD,
    val isActive: Boolean = true
)

enum class PODPlatform {
    AMAZON_KDP,                // Amazon KDP Print
    INGRAMSPARK,               // IngramSpark
    LULU,                      // Lulu
    BLURB,                     // Blurb
    BOOKBABY,                  // BookBaby
    SELF_PUBLISHING_HUB,       // Self Publishing Hub
    OTHER                      // Other POD platforms
}

enum class PODQuality {
    PREMIUM,                   // Premium quality
    STANDARD,                  // Standard quality
    ECONOMY,                   // Economy quality
    CUSTOM                     // Custom quality
}

@Entity(tableName = "inventory_alerts")
data class InventoryAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val inventoryItemId: Long,
    val alertType: InventoryAlertType,
    val message: String,
    val priority: AlertPriority = AlertPriority.MEDIUM,
    val isRead: Boolean = false,
    val isResolved: Boolean = false,
    val alertDate: Date = Date()
)

enum class InventoryAlertType {
    LOW_STOCK,                 // Stock below reorder point
    OUT_OF_STOCK,              // Completely out of stock
    OVERSTOCK,                 // Too much inventory
    SLOW_MOVING,               // Items not selling
    EXPIRING,                  // Items expiring soon
    DAMAGED,                   // Damaged items
    THEFT,                     // Potential theft
    OTHER                      // Other alerts
}

data class InventoryReport(
    val bookId: Long,
    val reportDate: Date,
    val totalInventoryValue: Double,
    val totalInventoryCost: Double,
    val totalProfit: Double,
    val profitMargin: Double,
    val turnoverRate: Double,
    val averageDaysInInventory: Double,
    val reorderAlerts: List<InventoryAlert>,
    val topSellingItems: List<InventoryItem>,
    val slowMovingItems: List<InventoryItem>,
    val recommendations: List<String>
)
