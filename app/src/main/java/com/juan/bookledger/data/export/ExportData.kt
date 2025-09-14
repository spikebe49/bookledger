package com.juan.bookledger.data.export

import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import com.juan.bookledger.data.model.Book
import java.util.Date

data class ExportResult(
    val success: Boolean,
    val message: String,
    val filePath: String? = null,
    val fileSize: Long = 0L
)

data class CSVExportData(
    val expenses: List<Expense>,
    val sales: List<Sale>,
    val books: List<Book>,
    val exportDate: Date,
    val totalExpenses: Double,
    val totalSales: Double,
    val netProfit: Double
)

data class PDFExportData(
    val expenses: List<Expense>,
    val sales: List<Sale>,
    val books: List<Book>,
    val analyticsData: com.juan.bookledger.data.analytics.AnalyticsData,
    val exportDate: Date,
    val reportTitle: String = "BookLedger Financial Report"
)

enum class ExportFormat {
    CSV,
    PDF
}

enum class ShareMethod {
    EMAIL,
    SMS,
    QUICKSHARE,
    GMAIL,
    PRINTER,
    OTHER
}
