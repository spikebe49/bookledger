package com.juan.bookledger.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.juan.bookledger.ui.screens.SimpleDashboardScreen
import com.juan.bookledger.ui.screens.SimpleBookListScreen
import com.juan.bookledger.ui.screens.SimpleBookDetailScreen
import com.juan.bookledger.ui.screens.SimpleAddExpenseScreen
import com.juan.bookledger.ui.screens.SimpleAddSaleScreen
import com.juan.bookledger.ui.screens.ComprehensiveAddBookScreen
import com.juan.bookledger.ui.screens.SimpleAddBookScreen
import com.juan.bookledger.ui.screens.SimpleReportScreen
import com.juan.bookledger.ui.screens.SimpleSettingsScreen
import com.juan.bookledger.ui.screens.SyncSettingsScreen
import com.juan.bookledger.ui.screens.NotificationSettingsScreen
import com.juan.bookledger.ui.screens.AnalyticsScreen
import com.juan.bookledger.ui.screens.ExportScreen
import com.juan.bookledger.ui.screens.SimpleExpenseScreen
import com.juan.bookledger.ui.screens.SimpleSaleScreen
import com.juan.bookledger.ui.screens.AIInsightsScreen
import com.juan.bookledger.ui.screens.AIConfigurationScreen
import com.juan.bookledger.ui.screens.AuthorFinancialDashboard
import com.juan.bookledger.ui.screens.EnhancedAuthorFinancialDashboard
import androidx.navigation.NavType
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookLedgerAppNavigation(navController: NavController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Define bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem(
            route = AppDestinations.Dashboard.route,
            icon = Icons.Default.Home,
            label = "Dashboard"
        ),
        BottomNavItem(
            route = AppDestinations.BookList.route,
            icon = Icons.Default.Menu,
            label = "Books"
        ),
        BottomNavItem(
            route = AppDestinations.AIInsights.route,
            icon = Icons.Default.Info,
            label = "AI Assistant"
        ),
        BottomNavItem(
            route = AppDestinations.Report.route,
            icon = Icons.Default.Info,
            label = "Reports"
        ),
        BottomNavItem(
            route = AppDestinations.Settings.route,
            icon = Icons.Default.Settings,
            label = "Settings"
        )
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(text = item.label)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Dashboard Screen
            composable(
                route = AppDestinations.Dashboard.route
            ) {
                SimpleDashboardScreen(navController = navController)
            }
            
            // Books Screen
            composable(
                route = AppDestinations.BookList.route
            ) {
                SimpleBookListScreen(navController = navController)
            }
            
            // Book Detail Screen
            composable(
                route = AppDestinations.BookDetail.route,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                val bookIdString = backStackEntry.arguments?.getString("bookId") ?: "0"
                val bookId = bookIdString.toLongOrNull() ?: 0L
                SimpleBookDetailScreen(
                    bookId = bookId,
                    navController = navController
                )
            }
            
            // Add Expense Screen
            composable(
                route = AppDestinations.AddExpense.route,
                arguments = listOf(
                    navArgument("bookId") { 
                        type = NavType.StringType
                        defaultValue = "0"
                        nullable = true
                    }
                ),
            ) { backStackEntry ->
                val bookIdString = backStackEntry.arguments?.getString("bookId")
                val bookId = bookIdString?.toLongOrNull()
                SimpleAddExpenseScreen(
                    bookId = bookId,
                    navController = navController
                )
            }
            
            // Add Sale Screen
            composable(
                route = AppDestinations.AddSale.route,
                arguments = listOf(
                    navArgument("bookId") { 
                        type = NavType.StringType
                        defaultValue = "0"
                        nullable = true
                    }
                ),
            ) { backStackEntry ->
                val bookIdString = backStackEntry.arguments?.getString("bookId")
                val bookId = bookIdString?.toLongOrNull()
                SimpleAddSaleScreen(
                    bookId = bookId,
                    navController = navController
                )
            }
            
            // Add Book Screen
            composable(
                route = AppDestinations.AddBook.route
            ) {
                ComprehensiveAddBookScreen(navController = navController)
            }
            
            // Reports Screen
            composable(
                route = AppDestinations.Report.route
            ) {
                SimpleReportScreen(navController = navController)
            }
            
            // Settings Screen
            composable(
                route = AppDestinations.Settings.route,
            ) {
                SimpleSettingsScreen(navController = navController)
            }
            
            // Sync Settings Screen
            composable(
                route = AppDestinations.SyncSettings.route,
            ) {
                SyncSettingsScreen(navController = navController)
            }
            
            // Notification Settings Screen
            composable(
                route = AppDestinations.NotificationSettings.route,
            ) {
                NotificationSettingsScreen(navController = navController)
            }
            
            // Analytics Screen
            composable(
                route = AppDestinations.Analytics.route,
            ) {
                AnalyticsScreen(navController = navController)
            }
            
            // Export Screen
            composable(
                route = AppDestinations.Export.route,
            ) {
                ExportScreen(navController = navController)
            }
            
            // Expenses Screen
            composable(
                route = AppDestinations.Expenses.route,
            ) {
                SimpleExpenseScreen(navController = navController)
            }
            
            // Sales Screen
            composable(
                route = AppDestinations.Sales.route,
            ) {
                SimpleSaleScreen(navController = navController)
            }
            
            // AI Insights Screen
            composable(
                route = AppDestinations.AIInsights.route
            ) {
                AIInsightsScreen(navController = navController)
            }
            
            // AI Configuration Screen
            composable(
                route = AppDestinations.AIConfiguration.route
            ) {
                AIConfigurationScreen(navController = navController)
            }
            
       // Author Financial Dashboard
       composable(
           route = AppDestinations.AuthorFinancialDashboard.route,
           arguments = listOf(
               navArgument("bookId") { type = NavType.LongType }
           )
       ) { backStackEntry ->
           val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
           AuthorFinancialDashboard(
               bookId = bookId,
               navController = navController
           )
       }
       
       // Enhanced Author Financial Dashboard
       composable(
           route = AppDestinations.EnhancedAuthorFinancialDashboard.route,
           arguments = listOf(
               navArgument("bookId") { type = NavType.LongType }
           )
       ) { backStackEntry ->
           val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
           EnhancedAuthorFinancialDashboard(
               bookId = bookId,
               navController = navController
           )
       }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
