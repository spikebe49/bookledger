package com.juan.bookledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juan.bookledger.data.sync.RealtimeSyncManager
import com.juan.bookledger.data.sync.SyncStatus
import com.juan.bookledger.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimpleSettingsViewModel @Inject constructor(
    private val realtimeSyncManager: RealtimeSyncManager,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _currency = MutableStateFlow("CAD")
    val currency: StateFlow<String> = _currency.asStateFlow()
    
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()
    
    private val _cloudBackupEnabled = MutableStateFlow(false)
    val cloudBackupEnabled: StateFlow<Boolean> = _cloudBackupEnabled.asStateFlow()
    
    private val _customCategories = MutableStateFlow<List<String>>(emptyList())
    val customCategories: StateFlow<List<String>> = _customCategories.asStateFlow()
    
    fun setCurrency(currency: String) {
        viewModelScope.launch {
            _currency.value = currency
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _notificationsEnabled.value = enabled
        }
    }
    
    fun setCloudBackupEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _cloudBackupEnabled.value = enabled
        }
    }
    
    fun addCustomCategory(category: String) {
        viewModelScope.launch {
            val current = _customCategories.value.toMutableList()
            if (!current.contains(category)) {
                current.add(category)
                _customCategories.value = current
            }
        }
    }
    
    fun removeCustomCategory(category: String) {
        viewModelScope.launch {
            val current = _customCategories.value.toMutableList()
            current.remove(category)
            _customCategories.value = current
        }
    }
    
    // Sync-related methods
    fun startRealtimeSync() {
        realtimeSyncManager.startRealtimeSync()
    }
    
    fun stopRealtimeSync() {
        realtimeSyncManager.stopRealtimeSync()
    }
    
    suspend fun forceSync() {
        realtimeSyncManager.forceSync()
    }
    
    fun getSyncStatus(): StateFlow<SyncStatus> {
        return realtimeSyncManager.syncStatus
    }
    
    fun getLastSyncTime(): StateFlow<java.util.Date?> {
        return realtimeSyncManager.lastSyncTime
    }
    
    fun isSyncActive(): Boolean {
        return realtimeSyncManager.isSyncActive()
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
