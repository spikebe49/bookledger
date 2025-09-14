package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.auth.AuthRepository
import com.juan.bookledger.data.auth.UserData
import com.juan.bookledger.data.auth.BiometricAuthManager
import com.juan.bookledger.data.auth.BiometricResult
import com.juan.bookledger.data.auth.BiometricStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {
    
    // Authentication state
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()
    
    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Biometric authentication state
    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                _isAuthenticated.value = user != null
                _biometricEnabled.value = authRepository.isBiometricEnabled()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to check authentication state: ${e.message}"
            }
        }
    }
    
    suspend fun signInWithEmail(email: String, password: String, rememberMe: Boolean = false): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null
        
        return try {
            val result = authRepository.signInWithEmail(email, password, rememberMe)
            if (result.isSuccess) {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                _isAuthenticated.value = true
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Sign in failed: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null
        
        return try {
            val result = authRepository.signUpWithEmail(email, password)
            if (result.isSuccess) {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                _isAuthenticated.value = true
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Sign up failed: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun signInWithGoogle(): Result<Unit> {
        _isLoading.value = true
        _errorMessage.value = null
        
        return try {
            val result = authRepository.signInWithGoogle()
            if (result.isSuccess) {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                _isAuthenticated.value = true
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Google sign in failed: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun signOut() {
        _isLoading.value = true
        try {
            authRepository.signOut()
            _currentUser.value = null
            _isAuthenticated.value = false
            _biometricEnabled.value = false
        } catch (e: Exception) {
            _errorMessage.value = "Sign out failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun enableBiometric(): Result<Unit> {
        return try {
            val result = authRepository.enableBiometric()
            if (result.isSuccess) {
                _biometricEnabled.value = true
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Failed to enable biometric: ${e.message}"
            Result.failure(e)
        }
    }
    
    suspend fun disableBiometric() {
        try {
            authRepository.disableBiometric()
            _biometricEnabled.value = false
        } catch (e: Exception) {
            _errorMessage.value = "Failed to disable biometric: ${e.message}"
        }
    }
    
    suspend fun authenticateWithBiometric(): Result<Unit> {
        return try {
            authRepository.authenticateWithBiometric()
        } catch (e: Exception) {
            _errorMessage.value = "Biometric authentication failed: ${e.message}"
            Result.failure(e)
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    // Biometric Authentication
    fun isBiometricAvailable(): BiometricStatus {
        return biometricAuthManager.isBiometricAvailable()
    }
    
    fun canUseBiometric(): Boolean {
        return biometricAuthManager.canUseBiometric()
    }
    
    suspend fun authenticateWithBiometric(activity: androidx.fragment.app.FragmentActivity): BiometricResult {
        return try {
            val result = biometricAuthManager.authenticateWithBiometric(activity)
            when (result) {
                is BiometricResult.Success -> {
                    // Check if user is already authenticated, if not, try to get stored credentials
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        _currentUser.value = user
                        _isAuthenticated.value = true
                    }
                    result
                }
                else -> result
            }
        } catch (e: Exception) {
            _errorMessage.value = "Biometric authentication failed: ${e.message}"
            BiometricResult.Error(e.message ?: "Unknown error")
        }
    }
    
}
