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
fun SimpleSaleScreen(navController: NavController) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Sales") }
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
                    selected = false,
                    onClick = { navController.navigate("expense") }
                )
                NavigationBarItem(
                    icon = { Text("📈") },
                    label = { Text("Sales") },
                    selected = true,
                    onClick = { /* Already on sales */ }
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
                text = "Sales Tracking",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("No sales recorded yet.")
                    Text("Add sales using the button below.")
                }
            }
            
            Button(
                onClick = { navController.navigate("add_sale") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Sale")
            }
        }
    }
}
