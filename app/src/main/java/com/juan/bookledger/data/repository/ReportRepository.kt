package com.juan.bookledger.data.repository

import com.juan.bookledger.data.database.ReportDao
import com.juan.bookledger.data.model.Report
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {
    fun getAllReports(): Flow<List<Report>> = 
        reportDao.queryAll()

    fun getReportsByDateRange(startDate: Date, endDate: Date): Flow<List<Report>> = 
        reportDao.getReportsByDateRange(startDate, endDate)

    suspend fun getReportById(id: Long): Report? = 
        reportDao.getReportById(id)

    suspend fun getLatestReport(): Report? = 
        reportDao.getLatestReport()

    fun getRecentReports(limit: Int): Flow<List<Report>> = 
        reportDao.getRecentReports(limit)

    suspend fun insertReport(report: Report): Long = 
        reportDao.insert(report)

    suspend fun updateReport(report: Report) = 
        reportDao.update(report)

    suspend fun deleteReport(report: Report) = 
        reportDao.delete(report)

    suspend fun deleteReportById(id: Long) = 
        reportDao.deleteById(id)

    suspend fun deleteOldReports(cutoffDate: Date) = 
        reportDao.deleteOldReports(cutoffDate)

    suspend fun generateReport(
        totalExpenses: Double,
        totalSales: Double,
        netProfit: Double
    ): Long {
        val report = Report(
            totalExpenses = totalExpenses,
            totalSales = totalSales,
            netProfit = netProfit,
            dateGenerated = Date()
        )
        return insertReport(report)
    }
}
