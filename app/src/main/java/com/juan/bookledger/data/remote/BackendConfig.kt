package com.juan.bookledger.data.remote

object BackendConfig {
    // Production backend URL (Render deployment)
    const val PRODUCTION_BASE_URL = "https://bookledger-backend.onrender.com"
    
    // Development backend URL (local PocketBase)
    const val DEVELOPMENT_BASE_URL = "http://127.0.0.1:8090"
    
    // Current backend URL - change this to switch between dev and production
    const val BASE_URL = PRODUCTION_BASE_URL
    
    // API endpoints
    const val API_BASE = "$BASE_URL/api"
    const val HEALTH_ENDPOINT = "$API_BASE/health"
    const val AUTH_ENDPOINT = "$API_BASE/collections/users/auth-with-password"
    const val REGISTER_ENDPOINT = "$API_BASE/collections/users"
    
    // Collection endpoints
    const val CATEGORIES_ENDPOINT = "$API_BASE/collections/categories"
    const val EXPENSES_ENDPOINT = "$API_BASE/collections/expenses"
    const val SALES_ENDPOINT = "$API_BASE/collections/sales"
    
    // Real-time endpoints
    const val REALTIME_ENDPOINT = "$BASE_URL/api/realtime"
    
    // Admin panel
    const val ADMIN_PANEL = "$BASE_URL/_/"
    
    // CORS settings
    val ALLOWED_ORIGINS = listOf(
        "https://bookledger-backend.onrender.com",
        "http://127.0.0.1:8090",
        "http://localhost:8090"
    )
}
