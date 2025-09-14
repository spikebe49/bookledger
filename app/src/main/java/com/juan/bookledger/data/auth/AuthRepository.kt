package com.juan.bookledger.data.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.juan.bookledger.data.remote.PocketBaseService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthRepository @Inject constructor(
    private val context: Context,
    private val userPreferences: UserPreferences,
    private val pocketBaseService: PocketBaseService
) {
    
    suspend fun getCurrentUser(): UserData? {
        return userPreferences.getCurrentUser()
    }
    
    suspend fun signInWithEmail(email: String, password: String, rememberMe: Boolean = false): Result<Unit> {
        return try {
            val result = pocketBaseService.authenticateWithEmail(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                user?.let { 
                    userPreferences.saveUser(it)
                    if (rememberMe) {
                        userPreferences.setRememberMe(true)
                    }
                }
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return try {
            val result = pocketBaseService.createUser(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                user?.let { userPreferences.saveUser(it) }
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithGoogle(): Result<Unit> {
        return try {
            // TODO: Implement Google Sign-In with PocketBase
            // This would require OAuth2 integration
            Result.failure(Exception("Google Sign-In not yet implemented"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        try {
            pocketBaseService.signOut()
            userPreferences.clearUser()
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun isBiometricEnabled(): Boolean {
        return userPreferences.isBiometricEnabled()
    }
    
    suspend fun enableBiometric(): Result<Unit> {
        return try {
            if (isBiometricAvailable()) {
                userPreferences.setBiometricEnabled(true)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Biometric authentication not available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun disableBiometric() {
        userPreferences.setBiometricEnabled(false)
    }
    
    suspend fun authenticateWithBiometric(): Result<Unit> {
        return if (isBiometricEnabled()) {
            try {
                // This would need to be called from a Fragment/Activity context
                // For now, return success - actual implementation would require UI context
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("Biometric authentication not enabled"))
        }
    }
    
    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return try {
            userPreferences.clearUser()
            userPreferences.setRememberMe(false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
