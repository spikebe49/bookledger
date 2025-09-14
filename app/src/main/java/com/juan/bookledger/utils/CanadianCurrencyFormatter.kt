package com.juan.bookledger.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CanadianCurrencyFormatter {
    private val canadianLocale = Locale.CANADA
    private val canadianDollar = Currency.getInstance("CAD")
    
    private val currencyFormatter = NumberFormat.getCurrencyInstance(canadianLocale).apply {
        currency = canadianDollar
    }
    
    private val numberFormatter = NumberFormat.getNumberInstance(canadianLocale)
    
    fun formatCurrency(amount: Double): String {
        return currencyFormatter.format(amount)
    }
    
    fun formatCurrency(amount: Double, showSymbol: Boolean = true): String {
        return if (showSymbol) {
            currencyFormatter.format(amount)
        } else {
            numberFormatter.format(amount)
        }
    }
    
    fun formatCurrency(amount: Double, decimals: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(canadianLocale).apply {
            currency = canadianDollar
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        return formatter.format(amount)
    }
    
    fun formatNumber(amount: Double): String {
        return numberFormatter.format(amount)
    }
    
    fun formatNumber(amount: Double, decimals: Int): String {
        val formatter = NumberFormat.getNumberInstance(canadianLocale).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        return formatter.format(amount)
    }
    
    fun formatPercentage(value: Double): String {
        val formatter = NumberFormat.getPercentInstance(canadianLocale).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 1
        }
        return formatter.format(value / 100.0)
    }
    
    fun formatPercentage(value: Double, decimals: Int): String {
        val formatter = NumberFormat.getPercentInstance(canadianLocale).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        return formatter.format(value / 100.0)
    }
    
    fun formatLargeNumber(amount: Double): String {
        return when {
            amount >= 1_000_000 -> "${formatNumber(amount / 1_000_000, 1)}M"
            amount >= 1_000 -> "${formatNumber(amount / 1_000, 1)}K"
            else -> formatNumber(amount, 0)
        }
    }
    
    fun formatLargeCurrency(amount: Double): String {
        return when {
            amount >= 1_000_000 -> "${formatCurrency(amount / 1_000_000, 1)}M"
            amount >= 1_000 -> "${formatCurrency(amount / 1_000, 1)}K"
            else -> formatCurrency(amount)
        }
    }
    
    fun parseCurrency(currencyString: String): Double? {
        return try {
            currencyString.replace("$", "").replace(",", "").toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }
    
    fun parseNumber(numberString: String): Double? {
        return try {
            numberString.replace(",", "").toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }
    
    fun formatTaxRate(rate: Double): String {
        return formatPercentage(rate, 2)
    }
    
    fun formatGSTHST(amount: Double, isHST: Boolean = true): String {
        val rate = if (isHST) 0.13 else 0.05
        val tax = amount * rate
        return formatCurrency(tax)
    }
    
    fun calculateGSTHST(amount: Double, isHST: Boolean = true): Double {
        val rate = if (isHST) 0.13 else 0.05
        return amount * rate
    }
    
    fun formatWithGSTHST(amount: Double, isHST: Boolean = true): String {
        val tax = calculateGSTHST(amount, isHST)
        val total = amount + tax
        return "${formatCurrency(amount)} + ${formatCurrency(tax)} (${if (isHST) "HST" else "GST"}) = ${formatCurrency(total)}"
    }
    
    fun formatCanadianAddress(
        street: String,
        city: String,
        province: String,
        postalCode: String,
        country: String = "Canada"
    ): String {
        return "$street\n$city, $province $postalCode\n$country"
    }
    
    fun formatCanadianPhone(phone: String): String {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return when {
            cleaned.length == 10 -> "(${cleaned.substring(0, 3)}) ${cleaned.substring(3, 6)}-${cleaned.substring(6)}"
            cleaned.length == 11 && cleaned.startsWith("1") -> "+1 (${cleaned.substring(1, 4)}) ${cleaned.substring(4, 7)}-${cleaned.substring(7)}"
            else -> phone
        }
    }
    
    fun formatCanadianPostalCode(postalCode: String): String {
        val cleaned = postalCode.replace(Regex("[^A-Za-z0-9]"), "").uppercase()
        return if (cleaned.length == 6) {
            "${cleaned.substring(0, 3)} ${cleaned.substring(3)}"
        } else {
            postalCode
        }
    }
    
    fun getCanadianProvinces(): List<String> {
        return listOf(
            "Alberta", "British Columbia", "Manitoba", "New Brunswick",
            "Newfoundland and Labrador", "Northwest Territories", "Nova Scotia",
            "Nunavut", "Ontario", "Prince Edward Island", "Quebec",
            "Saskatchewan", "Yukon"
        )
    }
    
    fun getOntarioCities(): List<String> {
        return listOf(
            "Toronto", "Ottawa", "Mississauga", "Brampton", "Hamilton",
            "London", "Markham", "Vaughan", "Kitchener", "Windsor",
            "Richmond Hill", "Oakville", "Burlington", "Greater Sudbury",
            "Oshawa", "Barrie", "St. Catharines", "Cambridge", "Kingston",
            "Guelph", "Thunder Bay", "Waterloo", "Chatham-Kent", "Sarnia"
        )
    }
}
