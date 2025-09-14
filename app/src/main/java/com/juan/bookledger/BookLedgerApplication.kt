package com.juan.bookledger

import android.app.Application
import com.juan.bookledger.data.BookManager
import com.juan.bookledger.data.DashboardManager
import com.juan.bookledger.data.ExpenseManager
import com.juan.bookledger.data.ReportGenerator
import com.juan.bookledger.data.SaleManager
import com.juan.bookledger.data.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BookLedgerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseInitializer(this).initializeDatabase()
        BookManager.initialize(this)
        ExpenseManager.initialize(this)
        SaleManager.initialize(this)
        DashboardManager.initialize(this)
        ReportGenerator.initialize(this)
    }
}
