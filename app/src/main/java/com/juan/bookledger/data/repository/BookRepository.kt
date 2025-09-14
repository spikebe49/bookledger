package com.juan.bookledger.data.repository

import com.juan.bookledger.data.database.BookDao
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.BookWithTotals
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao
) {
    fun getAllBooks(): Flow<List<Book>> = bookDao.queryAll()
    
    fun getAllBooksWithTotals(): Flow<List<BookWithTotals>> = bookDao.getAllBooksWithTotals()
    
    suspend fun getBookById(id: Long): Book? = bookDao.getById(id)
    
    suspend fun getBookWithTotals(bookId: Long): BookWithTotals? = bookDao.getBookWithTotals(bookId)
    
    fun searchBooksByTitle(title: String): Flow<List<Book>> = bookDao.searchByTitle(title)
    
    fun getBooksByDateRange(startDate: Date, endDate: Date): Flow<List<Book>> = 
        bookDao.getBooksByDateRange(startDate, endDate)
    
    suspend fun getBookCount(): Int = bookDao.getBookCount()
    
    suspend fun insertBook(book: Book): Long = bookDao.insert(book)
    
    suspend fun updateBook(book: Book) = bookDao.update(book)
    
    suspend fun deleteBook(book: Book) = bookDao.delete(book)
    
    suspend fun deleteBookById(id: Long) = bookDao.deleteById(id)
    
    suspend fun deleteAllBooks() = bookDao.deleteAll()
    
    suspend fun addBook(
        title: String,
        launchDate: Date,
        description: String? = null
    ): Long {
        val book = Book(
            title = title,
            launchDate = launchDate,
            description = description
        )
        return insertBook(book)
    }
}
