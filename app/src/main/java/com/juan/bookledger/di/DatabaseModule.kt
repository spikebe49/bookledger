package com.juan.bookledger.di

import android.content.Context
import com.juan.bookledger.data.database.BookLedgerDatabase
import com.juan.bookledger.data.database.BookDao
import com.juan.bookledger.data.database.CategoryDao
import com.juan.bookledger.data.database.ExpenseDao
import com.juan.bookledger.data.database.SaleDao
import com.juan.bookledger.data.database.ReportDao
import com.juan.bookledger.data.auth.BiometricAuthManager
import com.juan.bookledger.data.ai.GeminiAIService
import com.juan.bookledger.data.ai.AIConfig
import com.juan.bookledger.data.remote.PocketBaseClient
import com.juan.bookledger.data.remote.PocketBaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideBookLedgerDatabase(@ApplicationContext context: Context): BookLedgerDatabase {
        return BookLedgerDatabase.getDatabase(context)
    }

    @Provides
    fun provideBookDao(database: BookLedgerDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    fun provideCategoryDao(database: BookLedgerDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideExpenseDao(database: BookLedgerDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideSaleDao(database: BookLedgerDatabase): SaleDao {
        return database.saleDao()
    }

    @Provides
    fun provideReportDao(database: BookLedgerDatabase): ReportDao {
        return database.reportDao()
    }
    
    @Provides
    @Singleton
    fun provideBiometricAuthManager(@ApplicationContext context: Context): BiometricAuthManager {
        return BiometricAuthManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAIConfig(@ApplicationContext context: Context): AIConfig {
        return AIConfig(context)
    }
    
    @Provides
    @Singleton
    fun provideGeminiAIService(@ApplicationContext context: Context, aiConfig: AIConfig): GeminiAIService {
        return GeminiAIService(context, aiConfig)
    }
    
    @Provides
    @Singleton
    fun providePocketBaseClient(@ApplicationContext context: Context): PocketBaseClient {
        return PocketBaseClient(context)
    }
    
    @Provides
    @Singleton
    fun providePocketBaseService(@ApplicationContext context: Context, pocketBaseClient: PocketBaseClient): PocketBaseService {
        return PocketBaseService(context, pocketBaseClient)
    }
}
