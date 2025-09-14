package com.juan.bookledger.data.export

import android.content.Context
import com.juan.bookledger.data.BookManager
import com.juan.bookledger.data.ExpenseManager
import com.juan.bookledger.data.SaleManager
import com.juan.bookledger.utils.CurrencyFormatter
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CSVExporter {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
    private val timestampFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CANADA)
    
    suspend fun exportExpenses(context: Context): ExportResult {
        return try {
            ExpenseManager.initialize(context)
            val expenses = ExpenseManager.getAllExpenses().first()
            
            val fileName = "expenses_${timestampFormatter.format(Date())}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // Write header
                writer.write("Date,Category,Description,Amount,Book ID\n")
                
                // Write data
                expenses.forEach { expense ->
                    writer.write("${dateFormatter.format(expense.date)},")
                    writer.write("\"${expense.category}\",")
                    writer.write("\"${expense.description}\",")
                    writer.write("${CurrencyFormatter.formatCurrency(expense.amount).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${expense.bookId}\n")
                }
            }
            
            ExportResult(
                success = true,
                message = "Expenses exported successfully",
                filePath = file.absolutePath,
                fileSize = file.length()
            )
        } catch (e: Exception) {
            ExportResult(
                success = false,
                message = "Failed to export expenses: ${e.message}"
            )
        }
    }
    
    suspend fun exportSales(context: Context): ExportResult {
        return try {
            SaleManager.initialize(context)
            val sales = SaleManager.getAllSales().first()
            
            val fileName = "sales_${timestampFormatter.format(Date())}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // Write header
                writer.write("Date,Type,Book Title,Quantity,Unit Price,Total Amount,Donation Amount,Is Giveaway,Book ID\n")
                
                // Write data
                sales.forEach { sale ->
                    writer.write("${dateFormatter.format(sale.date)},")
                    writer.write("\"${sale.type}\",")
                    writer.write("\"${sale.bookTitle}\",")
                    writer.write("${sale.quantity},")
                    writer.write("${CurrencyFormatter.formatCurrency(sale.unitPrice).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${CurrencyFormatter.formatCurrency(sale.totalAmount).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${CurrencyFormatter.formatCurrency(sale.donationAmount).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${sale.isGiveaway},")
                    writer.write("${sale.bookId}\n")
                }
            }
            
            ExportResult(
                success = true,
                message = "Sales exported successfully",
                filePath = file.absolutePath,
                fileSize = file.length()
            )
        } catch (e: Exception) {
            ExportResult(
                success = false,
                message = "Failed to export sales: ${e.message}"
            )
        }
    }
    
    suspend fun exportAllData(context: Context): ExportResult {
        return try {
            BookManager.initialize(context)
            ExpenseManager.initialize(context)
            SaleManager.initialize(context)
            
            val books = BookManager.getAllBooks().first()
            val expenses = ExpenseManager.getAllExpenses().first()
            val sales = SaleManager.getAllSales().first()
            
            val totalExpenses = expenses.sumOf { it.amount }
            val totalSales = sales.sumOf { it.totalAmount }
            val netProfit = totalSales - totalExpenses
            
            val fileName = "bookledger_export_${timestampFormatter.format(Date())}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // Write summary
                writer.write("BookLedger Export Summary\n")
                writer.write("Export Date,${dateFormatter.format(Date())}\n")
                writer.write("Total Books,${books.size}\n")
                writer.write("Total Expenses,${CurrencyFormatter.formatCurrency(totalExpenses).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")}\n")
                writer.write("Total Sales,${CurrencyFormatter.formatCurrency(totalSales).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")}\n")
                writer.write("Net Profit,${CurrencyFormatter.formatCurrency(netProfit).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")}\n")
                writer.write("\n")
                
                // Write books
                writer.write("BOOKS\n")
                writer.write("ID,Title,Launch Date,Description\n")
                books.forEach { book ->
                    writer.write("${book.id},")
                    writer.write("\"${book.title}\",")
                    writer.write("${dateFormatter.format(book.launchDate)},")
                    writer.write("\"${book.description ?: ""}\"\n")
                }
                writer.write("\n")
                
                // Write expenses
                writer.write("EXPENSES\n")
                writer.write("Date,Category,Description,Amount,Book ID\n")
                expenses.forEach { expense ->
                    writer.write("${dateFormatter.format(expense.date)},")
                    writer.write("\"${expense.category}\",")
                    writer.write("\"${expense.description}\",")
                    writer.write("${CurrencyFormatter.formatCurrency(expense.amount).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${expense.bookId}\n")
                }
                writer.write("\n")
                
                // Write sales
                writer.write("SALES\n")
                writer.write("Date,Type,Book Title,Quantity,Unit Price,Total Amount,Donation Amount,Is Giveaway,Book ID\n")
                sales.forEach { sale ->
                    writer.write("${dateFormatter.format(sale.date)},")
                    writer.write("\"${sale.type}\",")
                    writer.write("\"${sale.bookTitle}\",")
                    writer.write("${sale.quantity},")
                    writer.write("${CurrencyFormatter.formatCurrency(sale.unitPrice).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${CurrencyFormatter.formatCurrency(sale.totalAmount).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${CurrencyFormatter.formatCurrency(sale.donationAmount).replace(CurrencyFormatter.getCurrencySymbol(), "").replace(",", "")},")
                    writer.write("${sale.isGiveaway},")
                    writer.write("${sale.bookId}\n")
                }
            }
            
            ExportResult(
                success = true,
                message = "All data exported successfully",
                filePath = file.absolutePath,
                fileSize = file.length()
            )
        } catch (e: Exception) {
            ExportResult(
                success = false,
                message = "Failed to export data: ${e.message}"
            )
        }
    }
}
