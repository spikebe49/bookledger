package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.juan.bookledger.data.BookManager
import com.juan.bookledger.data.SaleManager
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.SaleType
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var type by remember { mutableStateOf("") }
    var bookTitle by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var donationAmount by remember { mutableStateOf("") }
    var isGiveaway by remember { mutableStateOf(false) }
    var showTypeDropdown by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var showBookDropdown by remember { mutableStateOf(false) }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    
    val saleTypes = SaleType.values().map { it.name }
    
    LaunchedEffect(Unit) {
        BookManager.initialize(context)
        BookManager.getAllBooks().collect { bookList ->
            books = bookList
        }
    }
    
    // Calculate total amount
    val totalAmount = remember(quantity, unitPrice, donationAmount) {
        try {
            val qty = quantity.toDoubleOrNull() ?: 0.0
            val price = unitPrice.toDoubleOrNull() ?: 0.0
            val donation = donationAmount.toDoubleOrNull() ?: 0.0
            (qty * price) - donation
        } catch (e: Exception) {
            0.0
        }
    }
    
    val currencyFormatter = NumberFormat.getCurrencyInstance()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Sale") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Dropdown
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sale Type *",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = showTypeDropdown,
                        onExpandedChange = { showTypeDropdown = !showTypeDropdown }
                    ) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Select Sale Type") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showTypeDropdown
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showTypeDropdown,
                            onDismissRequest = { showTypeDropdown = false }
                        ) {
                            saleTypes.forEach { saleType ->
                                DropdownMenuItem(
                                    text = { Text(saleType) },
                                    onClick = {
                                        type = saleType
                                        showTypeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Book Selection Dropdown
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Book (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = showBookDropdown,
                        onExpandedChange = { showBookDropdown = !showBookDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedBook?.title ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Select Book") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showBookDropdown
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showBookDropdown,
                            onDismissRequest = { showBookDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("No Book Selected") },
                                onClick = {
                                    selectedBook = null
                                    showBookDropdown = false
                                }
                            )
                            books.forEach { book ->
                                DropdownMenuItem(
                                    text = { Text(book.title) },
                                    onClick = {
                                        selectedBook = book
                                        showBookDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Book Title Field
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Book Title *",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bookTitle,
                        onValueChange = { bookTitle = it },
                        label = { Text("Enter book title") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
            
            // Quantity and Unit Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quantity Field
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Quantity *",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { newValue ->
                                // Allow only positive integers
                                if (newValue.matches(Regex("^\\d*$"))) {
                                    quantity = newValue
                                }
                            },
                            label = { Text("Qty") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                
                // Unit Price Field
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Unit Price *",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = unitPrice,
                            onValueChange = { newValue ->
                                // Allow only numbers and decimal point
                                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    unitPrice = newValue
                                }
                            },
                            label = { Text("Price") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            prefix = { Text("$") }
                        )
                    }
                }
            }
            
            // Donation Amount Field
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Donation Amount (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = donationAmount,
                        onValueChange = { newValue ->
                            // Allow only numbers and decimal point
                            if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                donationAmount = newValue
                            }
                        },
                        label = { Text("Enter donation amount") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text("$") }
                    )
                }
            }
            
            // Giveaway Checkbox
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isGiveaway,
                        onCheckedChange = { isGiveaway = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "This is a giveaway (free book)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            // Total Amount Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currencyFormatter.format(totalAmount),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (isGiveaway) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Giveaway - No payment required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Save Button
            Button(
                onClick = {
                    if (type.isNotEmpty() && bookTitle.isNotEmpty() && quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                        try {
                            val qty = quantity.toInt()
                            val price = unitPrice.toDouble()
                            val donation = donationAmount.toDoubleOrNull() ?: 0.0
                            
                            SaleManager.addSale(
                                type = SaleType.valueOf(type),
                                platform = "Unknown", // Default platform
                                bookTitle = bookTitle,
                                quantity = qty,
                                unitPrice = price,
                                totalAmount = totalAmount,
                                donationAmount = donation,
                                isGiveaway = isGiveaway,
                                bookId = selectedBook?.id ?: 0,
                                onSuccess = {
                                    navController.popBackStack()
                                },
                                onError = { error ->
                                    // Handle error - could show a toast or error message
                                    println("Error saving sale: $error")
                                }
                            )
                        } catch (e: NumberFormatException) {
                            // Handle invalid number format
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = type.isNotEmpty() && bookTitle.isNotEmpty() && quantity.isNotEmpty() && unitPrice.isNotEmpty()
            ) {
                Text(
                    text = "Save Sale",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
