package com.juan.bookledger.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object NotificationManager {
    private const val WEEKLY_REMINDER_WORK_NAME = "weekly_reminder"
    private const val BREAKEVEN_ALERT_WORK_NAME = "breakeven_alert"
    
    fun scheduleWeeklyReminder(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val weeklyReminderRequest = PeriodicWorkRequestBuilder<WeeklyReminderWorker>(
            7, TimeUnit.DAYS,
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.DAYS) // Start after 1 day
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WEEKLY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            weeklyReminderRequest
        )
    }
    
    fun scheduleBreakevenAlert(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val breakevenAlertRequest = OneTimeWorkRequestBuilder<BreakevenAlertWorker>()
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // Check after 1 hour
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            BREAKEVEN_ALERT_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            breakevenAlertRequest
        )
    }
    
    fun cancelWeeklyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WEEKLY_REMINDER_WORK_NAME)
    }
    
    fun cancelBreakevenAlert(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(BREAKEVEN_ALERT_WORK_NAME)
    }
    
    fun cancelAllNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }
}
