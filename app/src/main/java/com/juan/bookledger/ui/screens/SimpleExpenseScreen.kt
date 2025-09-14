package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SimpleExpenseScreen(navController: NavController) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Expenses") }
            )
        },
        bottomBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("📊") },
                    label = { Text("Dashboard") },
                    selected = false,
                    onClick = { navController.navigate("dashboard") }
                )
                NavigationBarItem(
                    icon = { Text("💰") },
                    label = { Text("Expenses") },
                    selected = true,
                    onClick = { /* Already on expenses */ }
                )
                NavigationBarItem(
                    icon = { Text("📈") },
                    label = { Text("Sales") },
                    selected = false,
                    onClick = { navController.navigate("sale") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Expense Tracking",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("No expenses recorded yet.")
                    Text("Add expenses using the button below.")
                }
            }
            
            Button(
                onClick = { navController.navigate("add_expense") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Expense")
            }
        }
    }
}
