package com.juan.bookledger.data.ai

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIConfig @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences by lazy { 
        context.getSharedPreferences("ai_config", Context.MODE_PRIVATE) 
    }
    
    companion object {
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_AI_ENABLED = "ai_enabled"
        private const val DEFAULT_API_KEY = "" // Empty by default
    }
    
    suspend fun getGeminiApiKey(): String = withContext(Dispatchers.IO) {
        prefs.getString(KEY_GEMINI_API_KEY, DEFAULT_API_KEY) ?: DEFAULT_API_KEY
    }
    
    suspend fun setGeminiApiKey(apiKey: String) = withContext(Dispatchers.IO) {
        prefs.edit().putString(KEY_GEMINI_API_KEY, apiKey).apply()
    }
    
    suspend fun isAIEnabled(): Boolean = withContext(Dispatchers.IO) {
        prefs.getBoolean(KEY_AI_ENABLED, false)
    }
    
    suspend fun setAIEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        prefs.edit().putBoolean(KEY_AI_ENABLED, enabled).apply()
    }
    
    suspend fun hasValidApiKey(): Boolean = withContext(Dispatchers.IO) {
        val apiKey = prefs.getString(KEY_GEMINI_API_KEY, DEFAULT_API_KEY) ?: DEFAULT_API_KEY
        apiKey.isNotEmpty() && apiKey != DEFAULT_API_KEY
    }
}
