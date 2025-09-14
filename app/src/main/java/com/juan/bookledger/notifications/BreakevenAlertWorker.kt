package com.juan.bookledger.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.juan.bookledger.data.DashboardManager
import kotlinx.coroutines.flow.first

class BreakevenAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Initialize DashboardManager
            DashboardManager.initialize(applicationContext)
            
            // Get current financial data
            val dashboardData = DashboardManager.getDashboardData().first()
            
            // Check if we've reached breakeven (Net Profit >= 0)
            if (dashboardData.netProfit >= 0) {
                // Show breakeven alert notification
                NotificationHelper.showBreakevenAlert(applicationContext)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
