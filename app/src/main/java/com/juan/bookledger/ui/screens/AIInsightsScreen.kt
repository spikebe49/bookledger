package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.data.ai.GeminiAIService
import com.juan.bookledger.ui.viewmodel.AIInsightsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIInsightsScreen(
    navController: NavController,
    viewModel: AIInsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "AI Writing Assistant",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Chat Interface
            AIChatCard(
                onSendMessage = { message ->
                    coroutineScope.launch {
                        viewModel.sendMessage(message)
                    }
                },
                isLoading = uiState.isLoading,
                messages = uiState.messages
            )
            
            // Quick AI Actions
            QuickActionsCard(
                onActionClick = { action ->
                    coroutineScope.launch {
                        viewModel.performQuickAction(action)
                    }
                },
                isLoading = uiState.isLoading
            )
            
            // Writing Insights
            if (uiState.writingInsights.isNotEmpty()) {
                WritingInsightsCard(
                    insights = uiState.writingInsights,
                    onRefresh = {
                        coroutineScope.launch {
                            viewModel.refreshWritingInsights()
                        }
                    }
                )
            }
            
            // Marketing Suggestions
            if (uiState.marketingSuggestions.isNotEmpty()) {
                MarketingSuggestionsCard(
                    suggestions = uiState.marketingSuggestions,
                    onApplySuggestion = { suggestion ->
                        coroutineScope.launch {
                            viewModel.applyMarketingSuggestion(suggestion)
                        }
                    }
                )
            }
            
            // Financial Insights
            if (uiState.financialInsights.isNotEmpty()) {
                FinancialInsightsCard(
                    insights = uiState.financialInsights,
                    onViewDetails = {
                        // Navigate to detailed financial analysis
                    }
                )
            }
        }
    }
}

@Composable
private fun AIChatCard(
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
    messages: List<String>
) {
    var messageText by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "AI Writing Assistant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Messages display
            if (messages.isNotEmpty()) {
                messages.takeLast(3).forEach { message ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Message input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Ask AI anything about your writing...") },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                    enabled = !isLoading && messageText.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    onActionClick: (String) -> Unit,
    isLoading: Boolean
) {
    val quickActions = listOf(
        "Generate writing prompt" to Icons.Default.Edit,
        "Analyze manuscript" to Icons.Default.Info,
        "Marketing ideas" to Icons.Default.Info,
        "Financial insights" to Icons.Default.Info,
        "Social media post" to Icons.Default.Share,
        "Writing tips" to Icons.Default.Info
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickActions) { (action, icon) ->
                    Button(
                        onClick = { onActionClick(action) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                            Text(
                                text = action,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WritingInsightsCard(
    insights: String,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Writing Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
            
            Text(
                text = insights,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MarketingSuggestionsCard(
    suggestions: String,
    onApplySuggestion: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Marketing Suggestions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = suggestions,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(
                onClick = { onApplySuggestion(suggestions) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply Suggestions")
            }
        }
    }
}

@Composable
private fun FinancialInsightsCard(
    insights: String,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Financial Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = insights,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Detailed Analysis")
            }
        }
    }
}
