package com.juan.bookledger.navigation

sealed class AppDestinations(val route: String) {
    object Dashboard : AppDestinations("dashboard")
    object BookList : AppDestinations("book_list")
    object BookDetail : AppDestinations("book_detail/{bookId}") {
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    object AddExpense : AppDestinations("add_expense") {
        fun createRoute(bookId: Long? = null) = if (bookId != null) "add_expense?bookId=$bookId" else "add_expense"
    }
    object AddSale : AppDestinations("add_sale") {
        fun createRoute(bookId: Long? = null) = if (bookId != null) "add_sale?bookId=$bookId" else "add_sale"
    }
    object AddBook : AppDestinations("add_book")
    object Report : AppDestinations("report")
    object Settings : AppDestinations("settings")
    object SyncSettings : AppDestinations("sync_settings")
    object NotificationSettings : AppDestinations("notification_settings")
    object Analytics : AppDestinations("analytics")
    object Export : AppDestinations("export")
    object Expenses : AppDestinations("expenses")
    object Sales : AppDestinations("sales")
    object AIInsights : AppDestinations("ai_insights")
    object AIConfiguration : AppDestinations("ai_configuration")
    object AuthorFinancialDashboard : AppDestinations("author_financial_dashboard/{bookId}") {
        fun createRoute(bookId: Long) = "author_financial_dashboard/$bookId"
    }

    object EnhancedAuthorFinancialDashboard : AppDestinations("enhanced_author_financial_dashboard/{bookId}") {
        fun createRoute(bookId: Long) = "enhanced_author_financial_dashboard/$bookId"
    }
}
