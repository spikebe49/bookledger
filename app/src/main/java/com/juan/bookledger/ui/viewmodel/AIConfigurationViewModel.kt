package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.ai.AIConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIConfigurationViewModel @Inject constructor(
    private val aiConfig: AIConfig
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AIConfigurationUiState())
    val uiState: StateFlow<AIConfigurationUiState> = _uiState.asStateFlow()
    
    fun loadConfiguration() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val apiKey = aiConfig.getGeminiApiKey()
                val isAIEnabled = aiConfig.isAIEnabled()
                val hasValidApiKey = aiConfig.hasValidApiKey()
                
                _uiState.value = _uiState.value.copy(
                    apiKey = apiKey,
                    isAIEnabled = isAIEnabled,
                    hasValidApiKey = hasValidApiKey,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load configuration: ${e.message}"
                )
            }
        }
    }
    
    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                aiConfig.setGeminiApiKey(apiKey)
                val hasValidApiKey = aiConfig.hasValidApiKey()
                
                _uiState.value = _uiState.value.copy(
                    apiKey = apiKey,
                    hasValidApiKey = hasValidApiKey,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save API key: ${e.message}"
                )
            }
        }
    }
    
    fun clearApiKey() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                aiConfig.setGeminiApiKey("")
                
                _uiState.value = _uiState.value.copy(
                    apiKey = "",
                    hasValidApiKey = false,
                    isAIEnabled = false,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to clear API key: ${e.message}"
                )
            }
        }
    }
    
    fun setAIEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                aiConfig.setAIEnabled(enabled)
                
                _uiState.value = _uiState.value.copy(
                    isAIEnabled = enabled,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to update AI settings: ${e.message}"
                )
            }
        }
    }
}

data class AIConfigurationUiState(
    val apiKey: String = "",
    val isAIEnabled: Boolean = false,
    val hasValidApiKey: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
