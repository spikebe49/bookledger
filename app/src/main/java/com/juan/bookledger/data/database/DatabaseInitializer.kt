package com.juan.bookledger.data.database

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseInitializer(private val context: Context) {
    
    fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = BookLedgerDatabase.getDatabase(context)
            val categoryDao = database.categoryDao()
            
            // Check if categories already exist
            val existingCategories = categoryDao.getAllCategories()
            
            // Insert default expense categories
            DefaultCategories.expenseCategories.forEach { category ->
                try {
                    categoryDao.insertCategory(category)
                } catch (e: Exception) {
                    // Category might already exist, ignore
                }
            }
            
            // Insert default income categories
            DefaultCategories.incomeCategories.forEach { category ->
                try {
                    categoryDao.insertCategory(category)
                } catch (e: Exception) {
                    // Category might already exist, ignore
                }
            }
        }
    }
}
