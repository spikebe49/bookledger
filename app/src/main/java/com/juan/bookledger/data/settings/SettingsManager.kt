package com.juan.bookledger.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

object SettingsManager {
    private const val PREFS_NAME = "bookledger_settings"
    private const val KEY_CURRENCY = "currency_code"
    private const val KEY_EXPENSE_CATEGORIES = "expense_categories"
    private const val KEY_SALE_TYPES = "sale_types"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_WEEKLY_REMINDER_ENABLED = "weekly_reminder_enabled"
    private const val KEY_BREAKEVEN_ALERT_ENABLED = "breakeven_alert_enabled"
    
    private var context: Context? = null
    private var prefs: SharedPreferences? = null
    
    private val _settings = MutableStateFlow<AppSettings?>(null)
    val settings: StateFlow<AppSettings?> = _settings.asStateFlow()
    
    fun initialize(context: Context) {
        this.context = context
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadSettings()
    }
    
    private fun loadSettings() {
        val prefs = this.prefs ?: return
        
        val currencyCode = prefs.getString(KEY_CURRENCY, "CAD") ?: "CAD"
        val currency = SupportedCurrencies.getByCode(currencyCode) ?: SupportedCurrencies.CAD
        
        val expenseCategories = prefs.getStringSet(KEY_EXPENSE_CATEGORIES, null)?.toList()
            ?: DefaultCategories.EXPENSE_CATEGORIES
        
        val saleTypes = prefs.getStringSet(KEY_SALE_TYPES, null)?.toList()
            ?: DefaultCategories.SALE_TYPES
        
        val notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        val weeklyReminderEnabled = prefs.getBoolean(KEY_WEEKLY_REMINDER_ENABLED, true)
        val breakevenAlertEnabled = prefs.getBoolean(KEY_BREAKEVEN_ALERT_ENABLED, true)
        
        val appSettings = AppSettings(
            currency = currency,
            expenseCategories = expenseCategories,
            saleTypes = saleTypes,
            notificationsEnabled = notificationsEnabled,
            weeklyReminderEnabled = weeklyReminderEnabled,
            breakevenAlertEnabled = breakevenAlertEnabled
        )
        
        _settings.value = appSettings
    }
    
    fun updateCurrency(currency: Currency) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        prefs.edit()
            .putString(KEY_CURRENCY, currency.code)
            .apply()
        
        val updatedSettings = currentSettings.copy(currency = currency)
        _settings.value = updatedSettings
    }
    
    fun addExpenseCategory(category: String) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        val updatedCategories = (currentSettings.expenseCategories + category).distinct()
        
        prefs.edit()
            .putStringSet(KEY_EXPENSE_CATEGORIES, updatedCategories.toSet())
            .apply()
        
        val updatedSettings = currentSettings.copy(expenseCategories = updatedCategories)
        _settings.value = updatedSettings
    }
    
    fun removeExpenseCategory(category: String) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        val updatedCategories = currentSettings.expenseCategories.filter { it != category }
        
        prefs.edit()
            .putStringSet(KEY_EXPENSE_CATEGORIES, updatedCategories.toSet())
            .apply()
        
        val updatedSettings = currentSettings.copy(expenseCategories = updatedCategories)
        _settings.value = updatedSettings
    }
    
    fun addSaleType(saleType: String) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        val updatedTypes = (currentSettings.saleTypes + saleType).distinct()
        
        prefs.edit()
            .putStringSet(KEY_SALE_TYPES, updatedTypes.toSet())
            .apply()
        
        val updatedSettings = currentSettings.copy(saleTypes = updatedTypes)
        _settings.value = updatedSettings
    }
    
    fun removeSaleType(saleType: String) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        val updatedTypes = currentSettings.saleTypes.filter { it != saleType }
        
        prefs.edit()
            .putStringSet(KEY_SALE_TYPES, updatedTypes.toSet())
            .apply()
        
        val updatedSettings = currentSettings.copy(saleTypes = updatedTypes)
        _settings.value = updatedSettings
    }
    
    fun updateNotificationsEnabled(enabled: Boolean) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        prefs.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
            .apply()
        
        val updatedSettings = currentSettings.copy(notificationsEnabled = enabled)
        _settings.value = updatedSettings
    }
    
    fun updateWeeklyReminderEnabled(enabled: Boolean) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        prefs.edit()
            .putBoolean(KEY_WEEKLY_REMINDER_ENABLED, enabled)
            .apply()
        
        val updatedSettings = currentSettings.copy(weeklyReminderEnabled = enabled)
        _settings.value = updatedSettings
    }
    
    fun updateBreakevenAlertEnabled(enabled: Boolean) {
        val prefs = this.prefs ?: return
        val currentSettings = _settings.value ?: return
        
        prefs.edit()
            .putBoolean(KEY_BREAKEVEN_ALERT_ENABLED, enabled)
            .apply()
        
        val updatedSettings = currentSettings.copy(breakevenAlertEnabled = enabled)
        _settings.value = updatedSettings
    }
    
    fun resetToDefaults() {
        val prefs = this.prefs ?: return
        
        prefs.edit().clear().apply()
        loadSettings()
    }
}
