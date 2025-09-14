package com.juan.bookledger.data.database

import androidx.room.*
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.TypeTotal
import com.juan.bookledger.data.model.BookTotal
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun queryAll(): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getById(id: Long): Sale?
    
    @Query("SELECT * FROM sales WHERE bookId = :bookId")
    suspend fun getByBookId(bookId: Long): List<Sale>

    @Query("SELECT * FROM sales WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSalesByDateRange(startDate: Date, endDate: Date): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE type = :type ORDER BY date DESC")
    fun getSalesByType(type: String): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE bookTitle LIKE :bookTitle ORDER BY date DESC")
    fun getSalesByBookTitle(bookTitle: String): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE isGiveaway = :isGiveaway ORDER BY date DESC")
    fun getSalesByGiveawayStatus(isGiveaway: Boolean): Flow<List<Sale>>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSalesByDateRange(startDate: Date, endDate: Date): Double?

    @Query("SELECT SUM(donationAmount) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalDonationsByDateRange(startDate: Date, endDate: Date): Double?

    @Query("SELECT COUNT(*) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getSaleCountByDateRange(startDate: Date, endDate: Date): Int

    @Query("SELECT SUM(quantity) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalQuantitySoldByDateRange(startDate: Date, endDate: Date): Int?

    @Query("""
        SELECT type, SUM(totalAmount) as total
        FROM sales 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY type
        ORDER BY total DESC
    """)
    suspend fun getSalesByTypeGrouped(startDate: Date, endDate: Date): List<TypeTotal>

    @Query("""
        SELECT bookTitle, SUM(totalAmount) as total, SUM(quantity) as quantity
        FROM sales 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY bookTitle
        ORDER BY total DESC
    """)
    suspend fun getSalesByBookTitleGrouped(startDate: Date, endDate: Date): List<BookTotal>

    @Insert
    suspend fun insert(sale: Sale): Long

    @Update
    suspend fun update(sale: Sale)

    @Delete
    suspend fun delete(sale: Sale)

    @Query("DELETE FROM sales WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // BookId filtering methods for ViewModels
    @Query("SELECT * FROM sales WHERE bookId = :bookId ORDER BY date DESC")
    fun getSalesByBookId(bookId: Long): Flow<List<Sale>>
    
    @Query("SELECT SUM(totalAmount) FROM sales WHERE bookId = :bookId")
    fun getTotalSalesByBookId(bookId: Long): Flow<Double>
    
    @Query("SELECT SUM(totalAmount) FROM sales WHERE bookId = :bookId AND type = 'DirectSale'")
    fun getDirectSalesTotalByBookId(bookId: Long): Flow<Double>
    
    @Query("SELECT SUM(totalAmount) FROM sales WHERE bookId = :bookId AND type = 'PublisherSale'")
    fun getPublisherSalesTotalByBookId(bookId: Long): Flow<Double>
    
    @Query("SELECT SUM(donationAmount) FROM sales WHERE bookId = :bookId")
    fun getDonationsTotalByBookId(bookId: Long): Flow<Double>
    
    @Query("SELECT COUNT(*) FROM sales WHERE bookId = :bookId AND isGiveaway = 1")
    fun getGiveawayCountByBookId(bookId: Long): Flow<Int>
    
    @Query("""
        SELECT type, SUM(totalAmount) as total
        FROM sales 
        WHERE bookId = :bookId
        GROUP BY type
        ORDER BY total DESC
    """)
    suspend fun getSalesBreakdownByBookId(bookId: Long): List<TypeTotal>
}
