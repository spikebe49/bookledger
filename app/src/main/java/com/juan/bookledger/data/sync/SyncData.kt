package com.juan.bookledger.data.sync

import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.Expense
import com.juan.bookledger.data.model.Sale
import java.util.Date

data class SyncData(
    val books: List<Book>,
    val expenses: List<Expense>,
    val sales: List<Sale>,
    val lastSync: Date,
    val deviceId: String,
    val version: Int = 1
)

data class SyncResult(
    val success: Boolean,
    val message: String,
    val lastSync: Date? = null,
    val conflictCount: Int = 0
)

data class SyncSettings(
    val pocketbaseEnabled: Boolean = false,
    val onedriveEnabled: Boolean = false,
    val googledriveEnabled: Boolean = false,
    val autoSync: Boolean = false,
    val syncInterval: Int = 24, // hours
    val lastSync: Date? = null,
    val deviceId: String = ""
)

enum class SyncProvider {
    POCKETBASE,
    ONEDRIVE,
    GOOGLE_DRIVE
}

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR,
    CONFLICT
}
