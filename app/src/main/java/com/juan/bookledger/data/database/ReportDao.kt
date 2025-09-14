package com.juan.bookledger.data.database

import androidx.room.*
import com.juan.bookledger.data.model.Report
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY dateGenerated DESC")
    fun queryAll(): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE dateGenerated BETWEEN :startDate AND :endDate ORDER BY dateGenerated DESC")
    fun getReportsByDateRange(startDate: Date, endDate: Date): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: Long): Report?

    @Query("SELECT * FROM reports ORDER BY dateGenerated DESC LIMIT 1")
    suspend fun getLatestReport(): Report?

    @Query("SELECT * FROM reports ORDER BY dateGenerated DESC LIMIT :limit")
    fun getRecentReports(limit: Int): Flow<List<Report>>

    @Insert
    suspend fun insert(report: Report): Long

    @Update
    suspend fun update(report: Report)

    @Delete
    suspend fun delete(report: Report)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM reports WHERE dateGenerated < :cutoffDate")
    suspend fun deleteOldReports(cutoffDate: Date)
}
