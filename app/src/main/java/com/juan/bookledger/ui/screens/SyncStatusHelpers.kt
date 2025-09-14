package com.juan.bookledger.ui.screens

import androidx.compose.ui.graphics.Color
import com.juan.bookledger.data.sync.SyncStatus
import java.text.SimpleDateFormat
import java.util.*

fun getSyncStatusText(status: SyncStatus): String {
    return when (status) {
        SyncStatus.IDLE -> "Idle"
        SyncStatus.SYNCING -> "Syncing..."
        SyncStatus.SUCCESS -> "Synced"
        SyncStatus.ERROR -> "Error"
        SyncStatus.CONFLICT -> "Conflict"
    }
}

fun getSyncStatusColor(status: SyncStatus): Color {
    return when (status) {
        SyncStatus.SUCCESS -> Color.Green
        SyncStatus.SYNCING -> Color.Blue
        SyncStatus.ERROR -> Color.Red
        SyncStatus.CONFLICT -> Color.Red
        else -> Color.Gray
    }
}

fun formatTime(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return formatter.format(date)
}
