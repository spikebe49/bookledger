package com.juan.bookledger.data.settings

import java.util.*

data class AppSettings(
    val currency: Currency,
    val expenseCategories: List<String>,
    val saleTypes: List<String>,
    val notificationsEnabled: Boolean,
    val weeklyReminderEnabled: Boolean,
    val breakevenAlertEnabled: Boolean
)

data class Currency(
    val code: String,
    val symbol: String,
    val locale: Locale,
    val displayName: String
) {
    override fun toString(): String = "$displayName ($code)"
}

object SupportedCurrencies {
    val CAD = Currency("CAD", "$", Locale.CANADA, "Canadian Dollar")
    val USD = Currency("USD", "$", Locale.US, "US Dollar")
    val GBP = Currency("GBP", "£", Locale.UK, "British Pound")
    val EUR = Currency("EUR", "€", Locale.GERMANY, "Euro")
    val AUD = Currency("AUD", "A$", Locale("en", "AU"), "Australian Dollar")
    val JPY = Currency("JPY", "¥", Locale.JAPAN, "Japanese Yen")
    val CHF = Currency("CHF", "CHF", Locale("de", "CH"), "Swiss Franc")
    val CNY = Currency("CNY", "¥", Locale.CHINA, "Chinese Yuan")
    
    val ALL = listOf(CAD, USD, GBP, EUR, AUD, JPY, CHF, CNY)
    
    fun getByCode(code: String): Currency? = ALL.find { it.code == code }
}

object DefaultCategories {
    val EXPENSE_CATEGORIES = listOf(
        "Printing",
        "Marketing",
        "Editing",
        "Cover Design",
        "ISBN",
        "Distribution",
        "Website",
        "Travel",
        "Office Supplies",
        "Software",
        "Other"
    )
    
    val SALE_TYPES = listOf(
        "PublisherSale",
        "DirectSale",
        "Online",
        "Bookstore",
        "Event",
        "Gift",
        "Other"
    )
}
