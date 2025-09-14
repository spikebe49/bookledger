package com.juan.bookledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.juan.bookledger.navigation.BookLedgerAppNavigation
import com.juan.bookledger.notifications.NotificationHelper
import com.juan.bookledger.notifications.NotificationManager
import com.juan.bookledger.ui.screens.AuthScreen
import com.juan.bookledger.ui.theme.BookLedgerTheme
import com.juan.bookledger.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize notifications
        NotificationHelper.createNotificationChannel(this)
        NotificationManager.scheduleWeeklyReminder(this)
        NotificationManager.scheduleBreakevenAlert(this)
        
        setContent {
            BookLedgerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookLedgerApp()
                }
            }
        }
    }
}

@Composable
fun BookLedgerApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    
    // Check authentication status on app start
    LaunchedEffect(Unit) {
        // This will trigger the auth check in AuthViewModel
    }
    
    if (isAuthenticated && currentUser != null) {
        // User is authenticated, show main app
        BookLedgerAppNavigation(navController = navController)
    } else {
        // User is not authenticated, show auth screen
        AuthScreen(
            onAuthSuccess = {
                // Auth success is handled by the LaunchedEffect in AuthScreen
            }
        )
    }
}