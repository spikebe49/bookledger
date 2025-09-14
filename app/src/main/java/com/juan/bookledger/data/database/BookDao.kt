package com.juan.bookledger.data.database

import androidx.room.*
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.BookWithTotals
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun queryAll(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: Long): Book?

    @Query("SELECT * FROM books WHERE title LIKE :title ORDER BY title ASC")
    fun searchByTitle(title: String): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE launchDate BETWEEN :startDate AND :endDate ORDER BY launchDate DESC")
    fun getBooksByDateRange(startDate: Date, endDate: Date): Flow<List<Book>>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Insert
    suspend fun insert(book: Book): Long

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM books")
    suspend fun deleteAll()
    
    // Aggregated queries for book totals
    @Query("""
        SELECT 
            b.*,
            COALESCE(SUM(e.amount), 0) as totalExpenses,
            COALESCE(SUM(s.totalAmount), 0) as totalSales,
            COALESCE(SUM(s.totalAmount), 0) - COALESCE(SUM(e.amount), 0) as netProfit,
            COUNT(DISTINCT e.id) as expenseCount,
            COUNT(DISTINCT s.id) as saleCount,
            CASE 
                WHEN COUNT(DISTINCT s.id) > 0 THEN COALESCE(SUM(s.totalAmount), 0) / COUNT(DISTINCT s.id)
                ELSE 0 
            END as averageSalePrice,
            CASE 
                WHEN COALESCE(SUM(e.amount), 0) > 0 THEN 
                    ((COALESCE(SUM(s.totalAmount), 0) - COALESCE(SUM(e.amount), 0)) / COALESCE(SUM(e.amount), 0)) * 100
                ELSE 0 
            END as roiPercentage
        FROM books b
        LEFT JOIN expenses e ON b.id = e.bookId
        LEFT JOIN sales s ON b.id = s.bookId
        WHERE b.id = :bookId
        GROUP BY b.id, b.title, b.launchDate, b.description
    """)
    suspend fun getBookWithTotals(bookId: Long): BookWithTotals?
    
    @Query("""
        SELECT 
            b.*,
            COALESCE(SUM(e.amount), 0) as totalExpenses,
            COALESCE(SUM(s.totalAmount), 0) as totalSales,
            COALESCE(SUM(s.totalAmount), 0) - COALESCE(SUM(e.amount), 0) as netProfit,
            COUNT(DISTINCT e.id) as expenseCount,
            COUNT(DISTINCT s.id) as saleCount,
            CASE 
                WHEN COUNT(DISTINCT s.id) > 0 THEN COALESCE(SUM(s.totalAmount), 0) / COUNT(DISTINCT s.id)
                ELSE 0 
            END as averageSalePrice,
            CASE 
                WHEN COALESCE(SUM(e.amount), 0) > 0 THEN 
                    ((COALESCE(SUM(s.totalAmount), 0) - COALESCE(SUM(e.amount), 0)) / COALESCE(SUM(e.amount), 0)) * 100
                ELSE 0 
            END as roiPercentage
        FROM books b
        LEFT JOIN expenses e ON b.id = e.bookId
        LEFT JOIN sales s ON b.id = s.bookId
        GROUP BY b.id, b.title, b.launchDate, b.description
        ORDER BY b.title ASC
    """)
    fun getAllBooksWithTotals(): Flow<List<BookWithTotals>>
}
