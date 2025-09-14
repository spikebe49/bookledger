package com.juan.bookledger.utils

import com.juan.bookledger.data.settings.SettingsManager
import com.juan.bookledger.data.settings.SupportedCurrencies
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object CurrencyFormatter {
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val monthFormatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    
    fun formatCurrency(amount: Double): String {
        val settings = SettingsManager.settings.value
        val currency = settings?.currency ?: SupportedCurrencies.CAD
        
        val formatter = NumberFormat.getCurrencyInstance(currency.locale)
        return formatter.format(amount)
    }
    
    fun formatNumber(number: Double): String {
        val settings = SettingsManager.settings.value
        val currency = settings?.currency ?: SupportedCurrencies.CAD
        
        val formatter = NumberFormat.getNumberInstance(currency.locale)
        return formatter.format(number)
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
    
    fun getCurrencySymbol(): String {
        val settings = SettingsManager.settings.value
        return settings?.currency?.symbol ?: "$"
    }
    
    fun getCurrencyCode(): String {
        val settings = SettingsManager.settings.value
        return settings?.currency?.code ?: "CAD"
    }
}
