package com.juan.bookledger.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.Category
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.Report
import com.juan.bookledger.data.model.Manuscript
import com.juan.bookledger.data.model.MarketingCampaign
import com.juan.bookledger.data.model.WritingAnalytics
import com.juan.bookledger.data.model.Collaborator
import com.juan.bookledger.data.model.ContentDistribution
// New Canadian Author Features
import com.juan.bookledger.data.model.BookSeries
import com.juan.bookledger.data.model.SeriesBook
import com.juan.bookledger.data.model.PublisherContract
import com.juan.bookledger.data.model.ContractAlert
import com.juan.bookledger.data.model.PaymentRecord
import com.juan.bookledger.data.model.MarketingAttribution
import com.juan.bookledger.data.model.MarketingBudget
import com.juan.bookledger.data.model.InventoryItem
import com.juan.bookledger.data.model.InventoryTransaction
import com.juan.bookledger.data.model.InventoryAlert
import com.juan.bookledger.data.model.TimeEntry
import com.juan.bookledger.data.model.ProductivityGoal
import com.juan.bookledger.data.model.WritingStreak
import com.juan.bookledger.data.model.FinancialAlert

@Database(
    entities = [
        Book::class, Category::class, Expense::class, Sale::class, Report::class,
        Manuscript::class, MarketingCampaign::class, WritingAnalytics::class,
        Collaborator::class, ContentDistribution::class,
        // New Canadian Author Features
        BookSeries::class, SeriesBook::class,
        PublisherContract::class, ContractAlert::class, PaymentRecord::class,
        MarketingAttribution::class, MarketingBudget::class,
        InventoryItem::class, InventoryTransaction::class, InventoryAlert::class,
        TimeEntry::class, ProductivityGoal::class,
        FinancialAlert::class
    ],
    version = 7, // Updated version for new features
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookLedgerDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun saleDao(): SaleDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: BookLedgerDatabase? = null

        fun getDatabase(context: Context): BookLedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookLedgerDatabase::class.java,
                    "bookledger_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
