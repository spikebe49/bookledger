package com.juan.bookledger.data

import android.content.Context
import com.juan.bookledger.data.database.BookLedgerDatabase
import com.juan.bookledger.data.model.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

object BookManager {
    private var database: BookLedgerDatabase? = null
    
    fun initialize(context: Context) {
        database = BookLedgerDatabase.getDatabase(context)
    }
    
    fun getAllBooks(): Flow<List<Book>> {
        val db = database ?: throw IllegalStateException("Database not initialized")
        return db.bookDao().queryAll()
    }
    
    fun addBook(
        title: String,
        launchDate: Date,
        description: String? = null,
        onSuccess: (Long) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val db = database ?: return onError("Database not initialized")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val book = Book(
                    title = title,
                    launchDate = launchDate,
                    description = description
                )
                val bookId = db.bookDao().insert(book)
                onSuccess(bookId)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun updateBook(
        book: Book,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val db = database ?: return onError("Database not initialized")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.bookDao().update(book)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun deleteBook(
        bookId: Long,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val db = database ?: return onError("Database not initialized")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.bookDao().deleteById(bookId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    suspend fun getBookById(bookId: Long): Book? {
        val db = database ?: throw IllegalStateException("Database not initialized")
        return db.bookDao().getById(bookId)
    }
}
