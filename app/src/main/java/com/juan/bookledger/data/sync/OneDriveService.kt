package com.juan.bookledger.data.sync

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Date

class OneDriveService(private val context: Context) {
    private val gson = Gson()
    private val fileName = "bookledger_backup.json"
    private var isAuthenticated = false
    
    suspend fun authenticate(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Simulate authentication delay
            delay(1000)
            isAuthenticated = true
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun uploadData(syncData: SyncData): SyncResult = withContext(Dispatchers.IO) {
        try {
            if (!isAuthenticated && !authenticate()) {
                return@withContext SyncResult(false, "OneDrive authentication failed")
            }
            
            // Simulate upload delay
            delay(2000)
            
            // For demo purposes, save to local storage
            val jsonData = gson.toJson(syncData)
            val prefs = context.getSharedPreferences("onedrive_backup", Context.MODE_PRIVATE)
            prefs.edit().putString("backup_data", jsonData).apply()
            
            SyncResult(true, "Data uploaded to OneDrive successfully (demo mode)", Date())
        } catch (e: Exception) {
            SyncResult(false, "OneDrive upload failed: ${e.message}")
        }
    }
    
    suspend fun downloadData(): SyncData? = withContext(Dispatchers.IO) {
        try {
            if (!isAuthenticated && !authenticate()) {
                return@withContext null
            }
            
            // Simulate download delay
            delay(1500)
            
            // For demo purposes, load from local storage
            val prefs = context.getSharedPreferences("onedrive_backup", Context.MODE_PRIVATE)
            val jsonData = prefs.getString("backup_data", null)
            if (jsonData != null) {
                gson.fromJson(jsonData, SyncData::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
