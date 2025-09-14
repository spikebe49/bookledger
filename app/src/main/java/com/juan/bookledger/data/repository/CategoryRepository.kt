package com.juan.bookledger.data.repository

import com.juan.bookledger.data.database.CategoryDao
import com.juan.bookledger.data.model.Category
import com.juan.bookledger.data.model.CategoryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getExpenseCategories(): Flow<List<Category>> = 
        categoryDao.getCategoriesByType(CategoryType.EXPENSE)

    fun getIncomeCategories(): Flow<List<Category>> = 
        categoryDao.getCategoriesByType(CategoryType.INCOME)

    fun getAllCategories(): Flow<List<Category>> = 
        categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): Category? = 
        categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: Category): Long = 
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = 
        categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) = 
        categoryDao.deleteCategory(category)

    suspend fun deleteCategoryById(id: Long) = 
        categoryDao.deleteCategoryById(id)
}
