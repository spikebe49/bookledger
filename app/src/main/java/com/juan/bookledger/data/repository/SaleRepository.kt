package com.juan.bookledger.data.repository

import com.juan.bookledger.data.database.SaleDao
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.TypeTotal
import com.juan.bookledger.data.model.BookTotal
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleRepository @Inject constructor(
    private val saleDao: SaleDao
) {
    fun getAllSales(): Flow<List<Sale>> = 
        saleDao.queryAll()

    fun getSalesByDateRange(startDate: Date, endDate: Date): Flow<List<Sale>> = 
        saleDao.getSalesByDateRange(startDate, endDate)

    fun getSalesByType(type: String): Flow<List<Sale>> = 
        saleDao.getSalesByType(type)

    fun getSalesByBookTitle(bookTitle: String): Flow<List<Sale>> = 
        saleDao.getSalesByBookTitle(bookTitle)

    fun getSalesByGiveawayStatus(isGiveaway: Boolean): Flow<List<Sale>> = 
        saleDao.getSalesByGiveawayStatus(isGiveaway)

    suspend fun getTotalSalesByDateRange(startDate: Date, endDate: Date): Double = 
        saleDao.getTotalSalesByDateRange(startDate, endDate) ?: 0.0

    suspend fun getTotalDonationsByDateRange(startDate: Date, endDate: Date): Double = 
        saleDao.getTotalDonationsByDateRange(startDate, endDate) ?: 0.0

    suspend fun getSaleCountByDateRange(startDate: Date, endDate: Date): Int = 
        saleDao.getSaleCountByDateRange(startDate, endDate)

    suspend fun getTotalQuantitySoldByDateRange(startDate: Date, endDate: Date): Int = 
        saleDao.getTotalQuantitySoldByDateRange(startDate, endDate) ?: 0

    suspend fun getSalesByTypeGrouped(startDate: Date, endDate: Date): List<TypeTotal> = 
        saleDao.getSalesByTypeGrouped(startDate, endDate)

    suspend fun getSalesByBookTitleGrouped(startDate: Date, endDate: Date): List<BookTotal> = 
        saleDao.getSalesByBookTitleGrouped(startDate, endDate)

    suspend fun getSaleById(id: Long): Sale? =
        saleDao.getById(id)
    
    
    suspend fun insertSale(sale: Sale): Long = 
        saleDao.insert(sale)

    suspend fun updateSale(sale: Sale) = 
        saleDao.update(sale)

    suspend fun deleteSale(sale: Sale) = 
        saleDao.delete(sale)

    suspend fun deleteSaleById(id: Long) = 
        saleDao.deleteById(id)
    
    // BookId filtering methods for ViewModels
    fun getSalesByBookId(bookId: Long): Flow<List<Sale>> = 
        saleDao.getSalesByBookId(bookId)
    
    fun getTotalSalesByBookId(bookId: Long): Flow<Double> = 
        saleDao.getTotalSalesByBookId(bookId)
    
    fun getDirectSalesTotalByBookId(bookId: Long): Flow<Double> = 
        saleDao.getDirectSalesTotalByBookId(bookId)
    
    fun getPublisherSalesTotalByBookId(bookId: Long): Flow<Double> = 
        saleDao.getPublisherSalesTotalByBookId(bookId)
    
    fun getDonationsTotalByBookId(bookId: Long): Flow<Double> = 
        saleDao.getDonationsTotalByBookId(bookId)
    
    fun getGiveawayCountByBookId(bookId: Long): Flow<Int> = 
        saleDao.getGiveawayCountByBookId(bookId)
    
    suspend fun getSalesBreakdownByBookId(bookId: Long): List<TypeTotal> = 
        saleDao.getSalesBreakdownByBookId(bookId)
}
