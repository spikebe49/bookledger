package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val totalExpenses: Double,
    val totalSales: Double,
    val netProfit: Double,
    val dateGenerated: Date
)
