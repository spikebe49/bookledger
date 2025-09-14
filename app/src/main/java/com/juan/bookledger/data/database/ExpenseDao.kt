package com.juan.bookledger.data.database

import androidx.room.*
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.CategoryTotal
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun queryAll(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): Expense?
    
    @Query("SELECT * FROM expenses WHERE bookId = :bookId")
    suspend fun getByBookId(bookId: Long): List<Expense>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpensesByDateRange(startDate: Date, endDate: Date): Double?

    @Query("SELECT COUNT(*) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getExpenseCountByDateRange(startDate: Date, endDate: Date): Int

    @Query("""
        SELECT category, SUM(amount) as total
        FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY category
        ORDER BY total DESC
    """)
    suspend fun getExpensesByCategoryGrouped(startDate: Date, endDate: Date): List<CategoryTotal>

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // BookId filtering methods for ViewModels
    @Query("SELECT * FROM expenses WHERE bookId = :bookId ORDER BY date DESC")
    fun getExpensesByBookId(bookId: Long): Flow<List<Expense>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE bookId = :bookId")
    fun getTotalExpensesByBookId(bookId: Long): Flow<Double>
    
    @Query("""
        SELECT category, SUM(amount) as total
        FROM expenses 
        WHERE bookId = :bookId
        GROUP BY category
        ORDER BY total DESC
    """)
    suspend fun getExpensesByCategoryByBookId(bookId: Long): List<CategoryTotal>
}
