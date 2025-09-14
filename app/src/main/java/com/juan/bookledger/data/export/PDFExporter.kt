package com.juan.bookledger.data.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.juan.bookledger.data.analytics.AnalyticsData
import com.juan.bookledger.data.analytics.SalesChannelData
import com.juan.bookledger.data.analytics.MonthlyTrendData
import com.juan.bookledger.data.analytics.AnalyticsManager
import com.juan.bookledger.utils.CurrencyFormatter
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PDFExporter {
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.CANADA)
    private val timestampFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CANADA)
    
    suspend fun exportFullReport(context: Context): ExportResult {
        return try {
            val analyticsData = AnalyticsManager.getAnalyticsData()
            val salesChannelData = AnalyticsManager.getSalesChannelData()
            val monthlyTrendData = AnalyticsManager.getMonthlyTrendData()
            
            val fileName = "bookledger_report_${timestampFormatter.format(Date())}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val document = Document(PageSize.A4)
            val writer = PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()
            
            // Add title
            val titleFont = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, BaseColor.BLACK)
            val title = Paragraph("BookLedger Financial Report", titleFont)
            title.alignment = Element.ALIGN_CENTER
            document.add(title)
            
            // Add date
            val dateFont = Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL, BaseColor.GRAY)
            val dateParagraph = Paragraph("Generated on ${dateFormatter.format(Date())}", dateFont)
            dateParagraph.alignment = Element.ALIGN_CENTER
            document.add(dateParagraph)
            
            document.add(Chunk.NEWLINE)
            
            // Add summary section
            addSummarySection(document, analyticsData)
            
            // Add analytics section
            addAnalyticsSection(document, analyticsData)
            
            // Add charts section
            addChartsSection(document, salesChannelData, monthlyTrendData)
            
            // Add detailed data section
            addDetailedDataSection(document, analyticsData)
            
            document.close()
            
            ExportResult(
                success = true,
                message = "PDF report exported successfully",
                filePath = file.absolutePath,
                fileSize = file.length()
            )
        } catch (e: Exception) {
            ExportResult(
                success = false,
                message = "Failed to export PDF: ${e.message}"
            )
        }
    }
    
    private fun addSummarySection(document: Document, analyticsData: AnalyticsData) {
        val sectionFont = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, BaseColor.BLACK)
        val sectionTitle = Paragraph("Financial Summary", sectionFont)
        document.add(sectionTitle)
        document.add(Chunk.NEWLINE)
        
        val dataFont = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
        
        val summaryData = listOf(
            "Total Revenue" to CurrencyFormatter.formatCurrency(analyticsData.totalSales),
            "Total Expenses" to CurrencyFormatter.formatCurrency(analyticsData.totalExpenses),
            "Net Profit" to CurrencyFormatter.formatCurrency(analyticsData.netProfit),
            "ROI" to CurrencyFormatter.formatROI(analyticsData.roiPercentage),
            "Average Sale Price" to CurrencyFormatter.formatAveragePrice(analyticsData.averageSalePrice),
            "Cost per Book" to CurrencyFormatter.formatCurrency(analyticsData.costPerBook),
            "Best Channel" to analyticsData.bestChannel,
            "Books Published" to analyticsData.booksCount.toString()
        )
        
        summaryData.forEach { (label, value) ->
            val paragraph = Paragraph("$label: $value", dataFont)
            document.add(paragraph)
        }
        
        document.add(Chunk.NEWLINE)
    }
    
    private fun addAnalyticsSection(document: Document, analyticsData: AnalyticsData) {
        val sectionFont = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, BaseColor.BLACK)
        val sectionTitle = Paragraph("Analytics", sectionFont)
        document.add(sectionTitle)
        document.add(Chunk.NEWLINE)
        
        val dataFont = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
        
        // Publisher vs Direct Sales
        val publisherPercentage = if (analyticsData.totalSales > 0) {
            (analyticsData.publisherSalesTotal / analyticsData.totalSales) * 100
        } else 0.0
        
        val directPercentage = if (analyticsData.totalSales > 0) {
            (analyticsData.directSalesTotal / analyticsData.totalSales) * 100
        } else 0.0
        
        val analyticsData = listOf(
            "Publisher Sales" to "${CurrencyFormatter.formatCurrency(analyticsData.publisherSalesTotal)} (${String.format("%.1f", publisherPercentage)}%)",
            "Direct Sales" to "${CurrencyFormatter.formatCurrency(analyticsData.directSalesTotal)} (${String.format("%.1f", directPercentage)}%)",
            "Total Quantity Sold" to analyticsData.totalQuantity.toString(),
            "Printing Costs" to CurrencyFormatter.formatCurrency(analyticsData.printingCosts)
        )
        
        analyticsData.forEach { (label, value) ->
            val paragraph = Paragraph("$label: $value", dataFont)
            document.add(paragraph)
        }
        
        document.add(Chunk.NEWLINE)
    }
    
    private fun addChartsSection(document: Document, salesChannelData: List<SalesChannelData>, monthlyTrendData: List<MonthlyTrendData>) {
        val sectionFont = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, BaseColor.BLACK)
        val sectionTitle = Paragraph("Charts", sectionFont)
        document.add(sectionTitle)
        document.add(Chunk.NEWLINE)
        
        // Sales Channel Chart
        if (salesChannelData.isNotEmpty()) {
            val chartTitle = Paragraph("Sales by Channel", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))
            document.add(chartTitle)
            
            val dataFont = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
            val total = salesChannelData.sumOf { it.totalAmount }
            
            salesChannelData.forEach { data ->
                val percentage = if (total > 0) (data.totalAmount / total) * 100 else 0.0
                val paragraph = Paragraph("${data.channel}: ${CurrencyFormatter.formatCurrency(data.totalAmount)} (${String.format("%.1f", percentage)}%)", dataFont)
                document.add(paragraph)
            }
            
            document.add(Chunk.NEWLINE)
        }
        
        // Monthly Trends Chart
        if (monthlyTrendData.isNotEmpty()) {
            val chartTitle = Paragraph("Monthly Trends", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))
            document.add(chartTitle)
            
            val dataFont = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
            
            monthlyTrendData.forEach { data ->
                val paragraph = Paragraph("${data.month}: Expenses ${CurrencyFormatter.formatCurrency(data.expenses)}, Sales ${CurrencyFormatter.formatCurrency(data.sales)}, Profit ${CurrencyFormatter.formatCurrency(data.profit)}", dataFont)
                document.add(paragraph)
            }
            
            document.add(Chunk.NEWLINE)
        }
    }
    
    private fun addDetailedDataSection(document: Document, analyticsData: AnalyticsData) {
        val sectionFont = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, BaseColor.BLACK)
        val sectionTitle = Paragraph("Detailed Data", sectionFont)
        document.add(sectionTitle)
        document.add(Chunk.NEWLINE)
        
        val dataFont = Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL, BaseColor.BLACK)
        
        // Recent Expenses (last 10)
        val recentExpenses = analyticsData.expenses.takeLast(10)
        if (recentExpenses.isNotEmpty()) {
            val expensesTitle = Paragraph("Recent Expenses", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))
            document.add(expensesTitle)
            
            recentExpenses.forEach { expense ->
                val paragraph = Paragraph("${dateFormatter.format(expense.date)} - ${expense.category}: ${expense.description} - ${CurrencyFormatter.formatCurrency(expense.amount)}", dataFont)
                document.add(paragraph)
            }
            
            document.add(Chunk.NEWLINE)
        }
        
        // Recent Sales (last 10)
        val recentSales = analyticsData.sales.takeLast(10)
        if (recentSales.isNotEmpty()) {
            val salesTitle = Paragraph("Recent Sales", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))
            document.add(salesTitle)
            
            recentSales.forEach { sale ->
                val paragraph = Paragraph("${dateFormatter.format(sale.date)} - ${sale.type}: ${sale.bookTitle} - ${sale.quantity} units - ${CurrencyFormatter.formatCurrency(sale.totalAmount)}", dataFont)
                document.add(paragraph)
            }
        }
    }
}
