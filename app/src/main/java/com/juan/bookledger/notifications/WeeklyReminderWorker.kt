package com.juan.bookledger.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.juan.bookledger.data.DashboardManager
import kotlinx.coroutines.flow.first

class WeeklyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Show weekly reminder notification
            NotificationHelper.showWeeklyReminder(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
