package com.juan.bookledger.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object CanadianFormatter {
    private val canadianLocale = Locale.CANADA
    private val currencyFormatter = NumberFormat.getCurrencyInstance(canadianLocale)
    private val numberFormatter = NumberFormat.getNumberInstance(canadianLocale)
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", canadianLocale)
    private val monthFormatter = SimpleDateFormat("MMM yyyy", canadianLocale)
    
    fun formatCurrency(amount: Double): String {
        return currencyFormatter.format(amount)
    }
    
    fun formatNumber(number: Double): String {
        return numberFormatter.format(number)
    }
    
    fun formatPercentage(percentage: Double): String {
        return "${String.format("%.1f", percentage)}%"
    }
    
    fun formatDate(date: Date): String {
        return dateFormatter.format(date)
    }
    
    fun formatMonth(date: Date): String {
        return monthFormatter.format(date)
    }
    
    fun formatROI(roi: Double): String {
        return if (roi.isFinite() && !roi.isNaN()) {
            "${String.format("%.1f", roi)}%"
        } else {
            "N/A"
        }
    }
    
    fun formatAveragePrice(price: Double): String {
        return if (price.isFinite() && !price.isNaN()) {
            formatCurrency(price)
        } else {
            "N/A"
        }
    }
}
