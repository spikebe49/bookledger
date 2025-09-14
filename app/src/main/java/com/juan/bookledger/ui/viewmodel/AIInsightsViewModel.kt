package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.ai.GeminiAIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIInsightsViewModel @Inject constructor(
    private val geminiAIService: GeminiAIService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AIInsightsUiState())
    val uiState: StateFlow<AIInsightsUiState> = _uiState.asStateFlow()
    
    fun sendMessage(message: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val response = geminiAIService.askAIQuestion(message)
                val currentMessages = _uiState.value.messages.toMutableList()
                currentMessages.add("You: $message")
                currentMessages.add("AI: $response")
                
                _uiState.value = _uiState.value.copy(
                    messages = currentMessages,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun performQuickAction(action: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val response = when (action) {
                    "Generate writing prompt" -> {
                        geminiAIService.generateWritingPrompt("Fiction", "Just started writing")
                    }
                    "Analyze manuscript" -> {
                        geminiAIService.analyzeManuscriptStructure("Sample manuscript content...")
                    }
                    "Marketing ideas" -> {
                        geminiAIService.generateMarketingIdeas("My Book Title", "Fantasy", "Young Adults")
                    }
                    "Financial insights" -> {
                        geminiAIService.generateFinancialInsights("Sales: 100 books, Revenue: $500", "Expenses: $200")
                    }
                    "Social media post" -> {
                        geminiAIService.generateSocialMediaPost("My Book Title", "Fantasy", "Twitter")
                    }
                    "Writing tips" -> {
                        geminiAIService.askAIQuestion("Give me 5 writing tips for improving productivity")
                    }
                    else -> "Action not recognized"
                }
                
                val currentMessages = _uiState.value.messages.toMutableList()
                currentMessages.add("AI: $response")
                
                _uiState.value = _uiState.value.copy(
                    messages = currentMessages,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun refreshWritingInsights() {
        viewModelScope.launch {
            try {
                val insights = geminiAIService.analyzeWritingProductivity("""
                    Writing Data:
                    - Daily word count: 1000 words
                    - Writing streak: 15 days
                    - Peak writing time: 9-11 AM
                    - Average session: 2 hours
                    - Productivity rating: 8/10
                """.trimIndent())
                
                _uiState.value = _uiState.value.copy(writingInsights = insights)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error refreshing insights: ${e.message}")
            }
        }
    }
    
    fun applyMarketingSuggestion(suggestion: String) {
        viewModelScope.launch {
            // Here you would typically save the suggestion to a database
            // or trigger some action based on the suggestion
            val currentMessages = _uiState.value.messages.toMutableList()
            currentMessages.add("Applied marketing suggestion: $suggestion")
            
            _uiState.value = _uiState.value.copy(messages = currentMessages)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AIInsightsUiState(
    val isLoading: Boolean = false,
    val messages: List<String> = emptyList(),
    val writingInsights: String = "",
    val marketingSuggestions: String = "",
    val financialInsights: String = "",
    val error: String? = null
)
