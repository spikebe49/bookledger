package com.juan.bookledger.data.repository

import com.juan.bookledger.data.database.ExpenseDao
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.CategoryTotal
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun getAllExpenses(): Flow<List<Expense>> = 
        expenseDao.queryAll()

    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>> = 
        expenseDao.getExpensesByDateRange(startDate, endDate)

    fun getExpensesByCategory(category: String): Flow<List<Expense>> = 
        expenseDao.getExpensesByCategory(category)

    suspend fun getTotalExpensesByDateRange(startDate: Date, endDate: Date): Double = 
        expenseDao.getTotalExpensesByDateRange(startDate, endDate) ?: 0.0

    suspend fun getExpenseCountByDateRange(startDate: Date, endDate: Date): Int = 
        expenseDao.getExpenseCountByDateRange(startDate, endDate)

    suspend fun getExpensesByCategoryGrouped(startDate: Date, endDate: Date): List<CategoryTotal> = 
        expenseDao.getExpensesByCategoryGrouped(startDate, endDate)

    suspend fun getExpenseById(id: Long): Expense? =
        expenseDao.getById(id)
    
    
    suspend fun insertExpense(expense: Expense): Long = 
        expenseDao.insert(expense)

    suspend fun updateExpense(expense: Expense) = 
        expenseDao.update(expense)

    suspend fun deleteExpense(expense: Expense) = 
        expenseDao.delete(expense)

    suspend fun deleteExpenseById(id: Long) = 
        expenseDao.deleteById(id)
    
    // BookId filtering methods for ViewModels
    fun getExpensesByBookId(bookId: Long): Flow<List<Expense>> = 
        expenseDao.getExpensesByBookId(bookId)
    
    fun getTotalExpensesByBookId(bookId: Long): Flow<Double> = 
        expenseDao.getTotalExpensesByBookId(bookId)
    
    suspend fun getExpensesByCategoryByBookId(bookId: Long): List<CategoryTotal> = 
        expenseDao.getExpensesByCategoryByBookId(bookId)
}
