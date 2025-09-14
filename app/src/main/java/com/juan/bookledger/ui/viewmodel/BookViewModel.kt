package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.BookWithTotals
import com.juan.bookledger.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    // LiveData for all books
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books
    
    // LiveData for books with totals
    private val _booksWithTotals = MutableLiveData<List<BookWithTotals>>()
    val booksWithTotals: LiveData<List<BookWithTotals>> = _booksWithTotals
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // LiveData for success state
    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess
    
    // LiveData for single book with totals
    private val _bookWithTotals = MutableLiveData<BookWithTotals?>()
    val bookWithTotals: LiveData<BookWithTotals?> = _bookWithTotals
    
    init {
        loadAllBooks()
        loadAllBooksWithTotals()
    }
    
    fun loadAllBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                bookRepository.getAllBooks().collect { bookList ->
                    _books.value = bookList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load books: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadAllBooksWithTotals() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                bookRepository.getAllBooksWithTotals().collect { booksWithTotalsList ->
                    _booksWithTotals.value = booksWithTotalsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load books with totals: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun getBookWithTotals(bookId: Long) {
        viewModelScope.launch {
            try {
                val bookWithTotals = bookRepository.getBookWithTotals(bookId)
                _bookWithTotals.value = bookWithTotals
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load book totals: ${e.message}"
            }
        }
    }
    
    fun addBook(
        title: String,
        launchDate: Date,
        description: String? = null,
        authorName: String? = null,
        authorEmail: String? = null,
        authorPhone: String? = null,
        authorWebsite: String? = null,
        publisherName: String? = null,
        publisherEmail: String? = null,
        publisherPhone: String? = null,
        publisherWebsite: String? = null,
        publisherAddress: String? = null,
        illustratorName: String? = null,
        illustratorEmail: String? = null,
        illustratorPhone: String? = null,
        illustratorWebsite: String? = null,
        isbn: String? = null,
        genre: String? = null,
        targetAudience: String? = null,
        pageCount: Int? = null,
        language: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val book = Book(
                    title = title,
                    launchDate = launchDate,
                    description = description,
                    authorName = authorName,
                    authorEmail = authorEmail,
                    authorPhone = authorPhone,
                    authorWebsite = authorWebsite,
                    publisherName = publisherName,
                    publisherEmail = publisherEmail,
                    publisherPhone = publisherPhone,
                    publisherWebsite = publisherWebsite,
                    publisherAddress = publisherAddress,
                    illustratorName = illustratorName,
                    illustratorEmail = illustratorEmail,
                    illustratorPhone = illustratorPhone,
                    illustratorWebsite = illustratorWebsite,
                    isbn = isbn,
                    genre = genre,
                    targetAudience = targetAudience,
                    pageCount = pageCount,
                    language = language
                )
                val bookId = bookRepository.insertBook(book)
                _errorMessage.value = null
                _isLoading.value = false
                _isSuccess.value = true
                // The LiveData will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add book: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun updateBook(book: Book) {
        viewModelScope.launch {
            try {
                bookRepository.updateBook(book)
                _errorMessage.value = null
                // The LiveData will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update book: ${e.message}"
            }
        }
    }
    
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                bookRepository.deleteBook(book)
                _errorMessage.value = null
                // The LiveData will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete book: ${e.message}"
            }
        }
    }
    
    fun deleteBookById(bookId: Long) {
        viewModelScope.launch {
            try {
                bookRepository.deleteBookById(bookId)
                _errorMessage.value = null
                // The LiveData will automatically update due to Flow collection
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete book: ${e.message}"
            }
        }
    }
    
    fun searchBooksByTitle(title: String) {
        viewModelScope.launch {
            try {
                bookRepository.searchBooksByTitle(title).collect { bookList ->
                    _books.value = bookList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to search books: ${e.message}"
            }
        }
    }
    
    fun getBooksByDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                bookRepository.getBooksByDateRange(startDate, endDate).collect { bookList ->
                    _books.value = bookList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load books by date range: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearSuccess() {
        _isSuccess.value = false
    }
    
    fun refreshBooks() {
        loadAllBooks()
        loadAllBooksWithTotals()
    }
}
