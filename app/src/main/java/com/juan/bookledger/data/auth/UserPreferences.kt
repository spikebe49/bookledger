package com.juan.bookledger.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.userPreferencesDataStore
    
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val CLOUD_SYNC_ENABLED = booleanPreferencesKey("cloud_sync_enabled")
        private val LAST_SYNC = longPreferencesKey("last_sync")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
    }
    
    suspend fun getCurrentUser(): UserData? {
        val preferences = dataStore.data.first()
        val userId = preferences[USER_ID] ?: return null
        val email = preferences[USER_EMAIL] ?: return null
        val name = preferences[USER_NAME] ?: ""
        val token = preferences[AUTH_TOKEN] ?: ""
        
        return UserData(
            id = userId,
            email = email,
            name = name,
            authToken = token
        )
    }
    
    suspend fun saveUser(user: UserData) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_EMAIL] = user.email
            preferences[USER_NAME] = user.name
            preferences[AUTH_TOKEN] = user.authToken ?: ""
        }
    }
    
    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_NAME)
            preferences.remove(AUTH_TOKEN)
        }
    }
    
    suspend fun isBiometricEnabled(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[BIOMETRIC_ENABLED] ?: false
    }
    
    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }
    
    suspend fun isCloudSyncEnabled(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[CLOUD_SYNC_ENABLED] ?: false
    }
    
    suspend fun setCloudSyncEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CLOUD_SYNC_ENABLED] = enabled
        }
    }
    
    suspend fun getLastSync(): Long {
        val preferences = dataStore.data.first()
        return preferences[LAST_SYNC] ?: 0L
    }
    
    suspend fun setLastSync(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC] = timestamp
        }
    }
    
    val cloudSyncEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CLOUD_SYNC_ENABLED] ?: false
    }
    
    val biometricEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[BIOMETRIC_ENABLED] ?: false
    }
    
    suspend fun isRememberMeEnabled(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[REMEMBER_ME] ?: false
    }
    
    suspend fun setRememberMe(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMEMBER_ME] = enabled
        }
    }
    
    val rememberMeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[REMEMBER_ME] ?: false
    }
}

data class UserData(
    val id: String,
    val email: String,
    val name: String,
    val authToken: String?
)
